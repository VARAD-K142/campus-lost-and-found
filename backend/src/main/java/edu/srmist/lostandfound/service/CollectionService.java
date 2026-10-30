package edu.srmist.lostandfound.service;

import edu.srmist.lostandfound.domain.CollectionRecord;
import edu.srmist.lostandfound.domain.Item;
import edu.srmist.lostandfound.domain.User;
import edu.srmist.lostandfound.domain.enums.ItemStatus;
import edu.srmist.lostandfound.domain.enums.Role;
import edu.srmist.lostandfound.dto.claim.ClaimResponseDto;
import edu.srmist.lostandfound.dto.collection.CollectionRecordDto;
import edu.srmist.lostandfound.dto.collection.RecordCollectionRequest;
import edu.srmist.lostandfound.repository.CollectionRecordRepository;
import edu.srmist.lostandfound.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@Transactional
public class CollectionService {

    private final CollectionRecordRepository collectionRepository;
    private final ItemRepository itemRepository;
    private final ClaimClientService claimClientService;
    private final UserService userService;
    private final AuditService auditService;

    public CollectionService(CollectionRecordRepository collectionRepository,
                             ItemRepository itemRepository,
                             ClaimClientService claimClientService,
                             UserService userService,
                             AuditService auditService) {
        this.collectionRepository = collectionRepository;
        this.itemRepository = itemRepository;
        this.claimClientService = claimClientService;
        this.userService = userService;
        this.auditService = auditService;
    }

    public CollectionRecordDto recordCollection(RecordCollectionRequest request) {
        User admin = userService.getCurrentAuthenticatedUser();
        if (admin.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only administrators can record item handover and collection");
        }

        Item item = itemRepository.findByIdWithReporter(request.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found with ID: " + request.getItemId()));

        // Business Rule: Check if item already collected
        if (collectionRepository.existsByItemId(request.getItemId()) || item.getStatus() == ItemStatus.RESOLVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Collection has already been recorded for this item");
        }

        // Business Rule: Verify claim from claim-review-service
        ClaimResponseDto claim = claimClientService.getClaimById(request.getClaimId());
        if (claim == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Associated claim not found with ID: " + request.getClaimId());
        }

        if (!claim.getItemId().equals(item.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Claim #" + claim.getId() + " is not associated with Item #" + item.getId());
        }

        // Business Rule: Attempts to collect an unapproved item must be rejected
        if (!"APPROVED".equalsIgnoreCase(claim.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot record collection: Claim #" + claim.getId() + " is currently " + claim.getStatus() + ". Only APPROVED claims are eligible for collection.");
        }

        CollectionRecord record = new CollectionRecord();
        record.setItem(item);
        record.setClaimId(claim.getId());
        record.setClaimantId(claim.getClaimantUserId());
        record.setClaimantName(request.getClaimantName().trim());
        record.setClaimantStudentId(request.getClaimantStudentId());
        record.setHandoverNotes(request.getHandoverNotes().trim());
        record.setCollectorVerificationId(request.getCollectorVerificationId().trim());
        record.setCollectedAt(LocalDateTime.now());
        record.setVerifiedByAdmin(admin);

        CollectionRecord savedRecord = collectionRepository.save(record);

        // Update item status to RESOLVED
        item.setStatus(ItemStatus.RESOLVED);
        itemRepository.save(item);

        auditService.logAction(admin, "ITEM_COLLECTED", "CollectionRecord", savedRecord.getId(),
                "Handover recorded for item: '" + item.getTitle() + "' to claimant: " + savedRecord.getClaimantName() + " (Verification ID: " + savedRecord.getCollectorVerificationId() + ")", null);

        return CollectionRecordDto.fromEntity(savedRecord);
    }

    @Transactional(readOnly = true)
    public CollectionRecordDto getCollectionByItemId(Long itemId) {
        CollectionRecord record = collectionRepository.findByItemId(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No collection record found for item #" + itemId));
        return CollectionRecordDto.fromEntity(record);
    }

    @Transactional(readOnly = true)
    public Page<CollectionRecordDto> getMyCollections(int page, int size) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        Pageable pageable = PageRequest.of(page, Math.min(size, 50));
        return collectionRepository.findByClaimantId(currentUser.getId(), pageable)
                .map(CollectionRecordDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<CollectionRecordDto> getAllCollections(int page, int size) {
        User admin = userService.getCurrentAuthenticatedUser();
        if (admin.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Administrator access required");
        }
        Pageable pageable = PageRequest.of(page, Math.min(size, 50));
        return collectionRepository.findAllWithDetails(pageable)
                .map(CollectionRecordDto::fromEntity);
    }
}
