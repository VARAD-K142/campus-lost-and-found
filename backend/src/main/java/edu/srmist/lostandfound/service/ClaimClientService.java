package edu.srmist.lostandfound.service;

import edu.srmist.lostandfound.domain.Item;
import edu.srmist.lostandfound.domain.User;
import edu.srmist.lostandfound.domain.enums.ItemStatus;
import edu.srmist.lostandfound.domain.enums.ReportType;
import edu.srmist.lostandfound.domain.enums.Role;
import edu.srmist.lostandfound.dto.claim.ClaimResponseDto;
import edu.srmist.lostandfound.dto.claim.ClaimReviewRequest;
import edu.srmist.lostandfound.dto.claim.SubmitClaimRequest;
import edu.srmist.lostandfound.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@Transactional
public class ClaimClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClaimClientService.class);

    private final RestTemplate restTemplate;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final AuditService auditService;

    @Value("${app.claim-service.base-url}")
    private String claimServiceBaseUrl;

    @Value("${app.claim-service.api-key}")
    private String serviceApiKey;

    public ClaimClientService(RestTemplate restTemplate,
                              ItemRepository itemRepository,
                              UserService userService,
                              AuditService auditService) {
        this.restTemplate = restTemplate;
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.auditService = auditService;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Service-API-Key", serviceApiKey);
        return headers;
    }

    public ClaimResponseDto submitClaim(Long itemId, SubmitClaimRequest request) {
        User claimant = userService.getCurrentAuthenticatedUser();

        Item item = itemRepository.findByIdWithReporter(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found with ID: " + itemId));

        // Business Rule: Claims can only be filed against FOUND items
        if (item.getReportType() != ReportType.FOUND) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Claims can only be submitted against FOUND belongings");
        }

        // Business Rule: Protection against claiming one's own found-item report
        if (item.getReporter().getId().equals(claimant.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot claim an item that you reported as found");
        }

        // Business Rule: Protection against claims on ineligible or already resolved items
        if (item.getStatus() == ItemStatus.RESOLVED || item.getStatus() == ItemStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This item has already been resolved or collected");
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("itemId", itemId);
        payload.put("claimantUserId", claimant.getId());
        payload.put("claimantName", claimant.getFullName());
        payload.put("claimantEmail", claimant.getEmail());
        payload.put("claimantPhone", claimant.getPhoneNumber());
        payload.put("claimantStudentId", claimant.getStudentId());
        payload.put("claimExplanation", request.getClaimExplanation().trim());
        payload.put("distinguishingFeatures", request.getDistinguishingFeatures().trim());
        payload.put("proofDescription", request.getProofDescription());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, createHeaders());

        try {
            ResponseEntity<ClaimResponseDto> response = restTemplate.exchange(
                    claimServiceBaseUrl + "/api/v1/claims",
                    HttpMethod.POST,
                    entity,
                    ClaimResponseDto.class
            );

            ClaimResponseDto claim = response.getBody();
            if (claim != null) {
                claim.setItemTitle(item.getTitle());
            }

            // Update item workflow state to CLAIM_IN_PROGRESS
            if (item.getStatus() == ItemStatus.ACTIVE) {
                item.setStatus(ItemStatus.CLAIM_IN_PROGRESS);
                itemRepository.save(item);
            }

            auditService.logAction(claimant, "CLAIM_SUBMITTED", "Claim", claim != null ? claim.getId() : 0L,
                    "Submitted ownership claim for item: '" + item.getTitle() + "'", null);

            return claim;
        } catch (HttpStatusCodeException ex) {
            logger.error("Error communicating with claim-review-service: {}", ex.getResponseBodyAsString());
            throw new ResponseStatusException(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getResponseBodyAsString());
        } catch (Exception ex) {
            logger.error("Failed to connect to claim-review microservice at {}", claimServiceBaseUrl, ex);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Claim Review Microservice is currently unavailable");
        }
    }

    @Transactional(readOnly = true)
    public ClaimResponseDto getClaimById(Long claimId) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        try {
            ResponseEntity<ClaimResponseDto> response = restTemplate.exchange(
                    claimServiceBaseUrl + "/api/v1/claims/" + claimId,
                    HttpMethod.GET,
                    entity,
                    ClaimResponseDto.class
            );

            ClaimResponseDto claim = response.getBody();
            if (claim == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found with ID: " + claimId);
            }

            // Privacy Check: Only claimant or ADMIN can view claim details
            boolean isClaimant = claim.getClaimantUserId().equals(currentUser.getId());
            boolean isAdmin = currentUser.getRole() == Role.ADMIN;
            if (!isClaimant && !isAdmin) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to view this claim");
            }

            itemRepository.findById(claim.getItemId()).ifPresent(it -> claim.setItemTitle(it.getTitle()));
            return claim;
        } catch (HttpStatusCodeException ex) {
            throw new ResponseStatusException(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getResponseBodyAsString());
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception ex) {
            logger.error("Failed to fetch claim from microservice", ex);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Claim Review Microservice is unavailable");
        }
    }

    @Transactional(readOnly = true)
    public List<ClaimResponseDto> getClaimsByItem(Long itemId) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only administrators can view all claims for an item");
        }

        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        try {
            ResponseEntity<List<ClaimResponseDto>> response = restTemplate.exchange(
                    claimServiceBaseUrl + "/api/v1/claims/item/" + itemId,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<ClaimResponseDto>>() {}
            );

            List<ClaimResponseDto> list = response.getBody();
            if (list != null) {
                itemRepository.findById(itemId).ifPresent(it -> list.forEach(c -> c.setItemTitle(it.getTitle())));
                return list;
            }
            return Collections.emptyList();
        } catch (Exception ex) {
            logger.error("Failed to fetch claims for item from microservice", ex);
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public List<ClaimResponseDto> getMyClaims() {
        User currentUser = userService.getCurrentAuthenticatedUser();
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        try {
            ResponseEntity<List<ClaimResponseDto>> response = restTemplate.exchange(
                    claimServiceBaseUrl + "/api/v1/claims/claimant/" + currentUser.getId(),
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<ClaimResponseDto>>() {}
            );

            List<ClaimResponseDto> list = response.getBody();
            if (list != null) {
                enrichItemTitles(list);
                return list;
            }
            return Collections.emptyList();
        } catch (Exception ex) {
            logger.error("Failed to fetch user claims from microservice", ex);
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public List<ClaimResponseDto> getAllClaimsForAdmin(String status) {
        User admin = userService.getCurrentAuthenticatedUser();
        if (admin.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Administrator access required");
        }

        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        String url = claimServiceBaseUrl + "/api/v1/claims/admin/all" +
                (status != null && !status.isEmpty() ? "?status=" + status : "");

        try {
            ResponseEntity<List<ClaimResponseDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<ClaimResponseDto>>() {}
            );

            List<ClaimResponseDto> list = response.getBody();
            if (list != null) {
                enrichItemTitles(list);
                return list;
            }
            return Collections.emptyList();
        } catch (Exception ex) {
            logger.error("Failed to fetch admin claims from microservice", ex);
            return Collections.emptyList();
        }
    }

    public ClaimResponseDto reviewClaim(Long claimId, ClaimReviewRequest request) {
        User admin = userService.getCurrentAuthenticatedUser();
        if (admin.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only administrators can review claims");
        }

        String action = request.getAction().toUpperCase().trim();
        String targetStatus = "APPROVE".equals(action) || "APPROVED".equals(action) ? "APPROVED" : "REJECTED";

        Map<String, Object> payload = new HashMap<>();
        payload.put("status", targetStatus);
        payload.put("adminNotes", request.getAdminNotes());
        payload.put("reviewedByAdminId", admin.getId());
        payload.put("reviewedByAdminName", admin.getFullName());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, createHeaders());

        try {
            ResponseEntity<ClaimResponseDto> response = restTemplate.exchange(
                    claimServiceBaseUrl + "/api/v1/claims/" + claimId + "/review",
                    HttpMethod.POST,
                    entity,
                    ClaimResponseDto.class
            );

            ClaimResponseDto reviewed = response.getBody();
            if (reviewed != null) {
                Item item = itemRepository.findById(reviewed.getItemId()).orElse(null);
                if (item != null) {
                    reviewed.setItemTitle(item.getTitle());

                    if ("APPROVED".equals(targetStatus)) {
                        item.setStatus(ItemStatus.CLAIMED);
                        itemRepository.save(item);
                        auditService.logAction(admin, "CLAIM_APPROVED", "Claim", claimId,
                                "Admin approved claim #" + claimId + " for item: '" + item.getTitle() + "'", null);
                    } else {
                        // Check if other claims are pending
                        List<ClaimResponseDto> otherClaims = getClaimsByItem(item.getId());
                        boolean hasOtherPending = otherClaims.stream()
                                .anyMatch(c -> !c.getId().equals(claimId) && "PENDING".equalsIgnoreCase(c.getStatus()));
                        if (!hasOtherPending && item.getStatus() == ItemStatus.CLAIM_IN_PROGRESS) {
                            item.setStatus(ItemStatus.ACTIVE);
                            itemRepository.save(item);
                        }
                        auditService.logAction(admin, "CLAIM_REJECTED", "Claim", claimId,
                                "Admin rejected claim #" + claimId + " for item: '" + item.getTitle() + "'", null);
                    }
                }
            }

            return reviewed;
        } catch (HttpStatusCodeException ex) {
            throw new ResponseStatusException(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getResponseBodyAsString());
        } catch (Exception ex) {
            logger.error("Failed to review claim in microservice", ex);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Claim Review Microservice is unavailable");
        }
    }

    @Transactional(readOnly = true)
    public ClaimResponseDto getApprovedClaimForItem(Long itemId) {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        try {
            ResponseEntity<ClaimResponseDto> response = restTemplate.exchange(
                    claimServiceBaseUrl + "/api/v1/claims/item/" + itemId + "/approved",
                    HttpMethod.GET,
                    entity,
                    ClaimResponseDto.class
            );
            return response.getBody();
        } catch (Exception ex) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getClaimStats() {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    claimServiceBaseUrl + "/api/v1/claims/stats",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            return response.getBody();
        } catch (Exception ex) {
            logger.warn("Could not retrieve claim stats from microservice", ex);
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("totalClaims", 0L);
            fallback.put("pendingClaims", 0L);
            fallback.put("approvedClaims", 0L);
            fallback.put("rejectedClaims", 0L);
            return fallback;
        }
    }

    private void enrichItemTitles(List<ClaimResponseDto> list) {
        Set<Long> itemIds = new HashSet<>();
        list.forEach(c -> itemIds.add(c.getItemId()));
        Map<Long, String> titles = new HashMap<>();
        itemRepository.findAllById(itemIds).forEach(i -> titles.put(i.getId(), i.getTitle()));
        list.forEach(c -> c.setItemTitle(titles.getOrDefault(c.getItemId(), "Item #" + c.getItemId())));
    }
}
