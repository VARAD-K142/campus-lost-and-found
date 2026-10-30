package edu.srmist.claimreview.controller;

import edu.srmist.claimreview.domain.ClaimStatus;
import edu.srmist.claimreview.dto.ClaimRecordDto;
import edu.srmist.claimreview.dto.ClaimStatsDto;
import edu.srmist.claimreview.dto.ReviewClaimDto;
import edu.srmist.claimreview.dto.SubmitClaimDto;
import edu.srmist.claimreview.service.ClaimReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/claims")
@CrossOrigin(origins = "*")
public class ClaimReviewController {

    private final ClaimReviewService claimService;

    @Value("${service.api-key}")
    private String expectedApiKey;

    public ClaimReviewController(ClaimReviewService claimService) {
        this.claimService = claimService;
    }

    private void validateApiKey(String apiKey) {
        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing inter-service API key");
        }
    }

    @PostMapping
    public ResponseEntity<ClaimRecordDto> submitClaim(
            @RequestHeader(value = "X-Service-API-Key", required = false) String apiKey,
            @Valid @RequestBody SubmitClaimDto dto) {
        validateApiKey(apiKey);
        ClaimRecordDto created = claimService.submitClaim(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimRecordDto> getClaimById(
            @RequestHeader(value = "X-Service-API-Key", required = false) String apiKey,
            @PathVariable Long id) {
        validateApiKey(apiKey);
        return ResponseEntity.ok(claimService.getClaimById(id));
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<ClaimRecordDto>> getClaimsByItem(
            @RequestHeader(value = "X-Service-API-Key", required = false) String apiKey,
            @PathVariable Long itemId) {
        validateApiKey(apiKey);
        return ResponseEntity.ok(claimService.getClaimsByItem(itemId));
    }

    @GetMapping("/item/{itemId}/approved")
    public ResponseEntity<ClaimRecordDto> getApprovedClaimForItem(
            @RequestHeader(value = "X-Service-API-Key", required = false) String apiKey,
            @PathVariable Long itemId) {
        validateApiKey(apiKey);
        ClaimRecordDto approved = claimService.getApprovedClaimForItem(itemId);
        if (approved == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(approved);
    }

    @GetMapping("/claimant/{userId}")
    public ResponseEntity<List<ClaimRecordDto>> getClaimsByClaimant(
            @RequestHeader(value = "X-Service-API-Key", required = false) String apiKey,
            @PathVariable Long userId) {
        validateApiKey(apiKey);
        return ResponseEntity.ok(claimService.getClaimsByClaimant(userId));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<ClaimRecordDto>> getAllClaims(
            @RequestHeader(value = "X-Service-API-Key", required = false) String apiKey,
            @RequestParam(required = false) ClaimStatus status) {
        validateApiKey(apiKey);
        return ResponseEntity.ok(claimService.getAllClaims(status));
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<ClaimRecordDto> reviewClaim(
            @RequestHeader(value = "X-Service-API-Key", required = false) String apiKey,
            @PathVariable Long id,
            @Valid @RequestBody ReviewClaimDto reviewDto) {
        validateApiKey(apiKey);
        return ResponseEntity.ok(claimService.reviewClaim(id, reviewDto));
    }

    @GetMapping("/stats")
    public ResponseEntity<ClaimStatsDto> getStats(
            @RequestHeader(value = "X-Service-API-Key", required = false) String apiKey) {
        validateApiKey(apiKey);
        return ResponseEntity.ok(claimService.getStats());
    }
}
