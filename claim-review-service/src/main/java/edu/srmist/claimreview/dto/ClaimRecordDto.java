package edu.srmist.claimreview.dto;

import edu.srmist.claimreview.domain.ClaimRecord;
import edu.srmist.claimreview.domain.ClaimStatus;
import java.time.LocalDateTime;

public class ClaimRecordDto {
    private Long id;
    private Long itemId;
    private Long claimantUserId;
    private String claimantName;
    private String claimantEmail;
    private String claimantPhone;
    private String claimantStudentId;
    private String claimExplanation;
    private String distinguishingFeatures;
    private String proofDescription;
    private ClaimStatus status;
    private String adminNotes;
    private Long reviewedByAdminId;
    private String reviewedByAdminName;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ClaimRecordDto() {}

    public static ClaimRecordDto fromEntity(ClaimRecord entity) {
        ClaimRecordDto dto = new ClaimRecordDto();
        dto.setId(entity.getId());
        dto.setItemId(entity.getItemId());
        dto.setClaimantUserId(entity.getClaimantUserId());
        dto.setClaimantName(entity.getClaimantName());
        dto.setClaimantEmail(entity.getClaimantEmail());
        dto.setClaimantPhone(entity.getClaimantPhone());
        dto.setClaimantStudentId(entity.getClaimantStudentId());
        dto.setClaimExplanation(entity.getClaimExplanation());
        dto.setDistinguishingFeatures(entity.getDistinguishingFeatures());
        dto.setProofDescription(entity.getProofDescription());
        dto.setStatus(entity.getStatus());
        dto.setAdminNotes(entity.getAdminNotes());
        dto.setReviewedByAdminId(entity.getReviewedByAdminId());
        dto.setReviewedByAdminName(entity.getReviewedByAdminName());
        dto.setSubmittedAt(entity.getSubmittedAt());
        dto.setReviewedAt(entity.getReviewedAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Long getClaimantUserId() { return claimantUserId; }
    public void setClaimantUserId(Long claimantUserId) { this.claimantUserId = claimantUserId; }

    public String getClaimantName() { return claimantName; }
    public void setClaimantName(String claimantName) { this.claimantName = claimantName; }

    public String getClaimantEmail() { return claimantEmail; }
    public void setClaimantEmail(String claimantEmail) { this.claimantEmail = claimantEmail; }

    public String getClaimantPhone() { return claimantPhone; }
    public void setClaimantPhone(String claimantPhone) { this.claimantPhone = claimantPhone; }

    public String getClaimantStudentId() { return claimantStudentId; }
    public void setClaimantStudentId(String claimantStudentId) { this.claimantStudentId = claimantStudentId; }

    public String getClaimExplanation() { return claimExplanation; }
    public void setClaimExplanation(String claimExplanation) { this.claimExplanation = claimExplanation; }

    public String getDistinguishingFeatures() { return distinguishingFeatures; }
    public void setDistinguishingFeatures(String distinguishingFeatures) { this.distinguishingFeatures = distinguishingFeatures; }

    public String getProofDescription() { return proofDescription; }
    public void setProofDescription(String proofDescription) { this.proofDescription = proofDescription; }

    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public Long getReviewedByAdminId() { return reviewedByAdminId; }
    public void setReviewedByAdminId(Long reviewedByAdminId) { this.reviewedByAdminId = reviewedByAdminId; }

    public String getReviewedByAdminName() { return reviewedByAdminName; }
    public void setReviewedByAdminName(String reviewedByAdminName) { this.reviewedByAdminName = reviewedByAdminName; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
