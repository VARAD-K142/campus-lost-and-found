package edu.srmist.claimreview.service;

import edu.srmist.claimreview.domain.ClaimRecord;
import edu.srmist.claimreview.domain.ClaimStatus;
import edu.srmist.claimreview.dto.ClaimRecordDto;
import edu.srmist.claimreview.dto.ClaimStatsDto;
import edu.srmist.claimreview.dto.ReviewClaimDto;
import edu.srmist.claimreview.dto.SubmitClaimDto;
import edu.srmist.claimreview.repository.ClaimRecordRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClaimReviewService {

    private final ClaimRecordRepository claimRepository;

    public ClaimReviewService(ClaimRecordRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public ClaimRecordDto submitClaim(SubmitClaimDto dto) {
        // Business Rule: Check for duplicate pending claim on the same item by the same claimant
        boolean alreadyPending = claimRepository.existsByItemIdAndClaimantUserIdAndStatus(
                dto.getItemId(), dto.getClaimantUserId(), ClaimStatus.PENDING);
        if (alreadyPending) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You already have a pending claim submitted for this item. Please wait for administrator review.");
        }

        ClaimRecord record = new ClaimRecord();
        record.setItemId(dto.getItemId());
        record.setClaimantUserId(dto.getClaimantUserId());
        record.setClaimantName(dto.getClaimantName());
        record.setClaimantEmail(dto.getClaimantEmail());
        record.setClaimantPhone(dto.getClaimantPhone());
        record.setClaimantStudentId(dto.getClaimantStudentId());
        record.setClaimExplanation(dto.getClaimExplanation());
        record.setDistinguishingFeatures(dto.getDistinguishingFeatures());
        record.setProofDescription(dto.getProofDescription());
        record.setStatus(ClaimStatus.PENDING);

        ClaimRecord saved = claimRepository.save(record);
        return ClaimRecordDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public ClaimRecordDto getClaimById(Long id) {
        ClaimRecord record = claimRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim record not found with ID: " + id));
        return ClaimRecordDto.fromEntity(record);
    }

    @Transactional(readOnly = true)
    public List<ClaimRecordDto> getClaimsByItem(Long itemId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "submittedAt");
        return claimRepository.findByItemId(itemId, sort).stream()
                .map(ClaimRecordDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClaimRecordDto> getClaimsByClaimant(Long claimantUserId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "submittedAt");
        return claimRepository.findByClaimantUserId(claimantUserId, sort).stream()
                .map(ClaimRecordDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClaimRecordDto> getAllClaims(ClaimStatus statusFilter) {
        Sort sort = Sort.by(Sort.Direction.DESC, "submittedAt");
        List<ClaimRecord> list = (statusFilter != null)
                ? claimRepository.findByStatus(statusFilter, sort)
                : claimRepository.findAll(sort);
        return list.stream().map(ClaimRecordDto::fromEntity).collect(Collectors.toList());
    }

    public ClaimRecordDto reviewClaim(Long id, ReviewClaimDto reviewDto) {
        ClaimRecord record = claimRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim record not found with ID: " + id));

        // Business Rule: Only PENDING claims can transition to APPROVED or REJECTED
        if (record.getStatus() != ClaimStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid state transition: Claim #" + id + " has already been reviewed as " + record.getStatus());
        }

        if (reviewDto.getStatus() != ClaimStatus.APPROVED && reviewDto.getStatus() != ClaimStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Review status must be either APPROVED or REJECTED");
        }

        record.setStatus(reviewDto.getStatus());
        record.setAdminNotes(reviewDto.getAdminNotes());
        record.setReviewedByAdminId(reviewDto.getReviewedByAdminId());
        record.setReviewedByAdminName(reviewDto.getReviewedByAdminName());
        record.setReviewedAt(LocalDateTime.now());

        ClaimRecord updated = claimRepository.save(record);
        return ClaimRecordDto.fromEntity(updated);
    }

    @Transactional(readOnly = true)
    public ClaimRecordDto getApprovedClaimForItem(Long itemId) {
        return claimRepository.findFirstByItemIdAndStatus(itemId, ClaimStatus.APPROVED)
                .map(ClaimRecordDto::fromEntity)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public ClaimStatsDto getStats() {
        long total = claimRepository.count();
        long pending = claimRepository.countByStatus(ClaimStatus.PENDING);
        long approved = claimRepository.countByStatus(ClaimStatus.APPROVED);
        long rejected = claimRepository.countByStatus(ClaimStatus.REJECTED);
        return new ClaimStatsDto(total, pending, approved, rejected);
    }
}
