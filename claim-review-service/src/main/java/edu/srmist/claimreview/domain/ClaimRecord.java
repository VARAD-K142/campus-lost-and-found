package edu.srmist.claimreview.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "claim_records", indexes = {
    @Index(name = "idx_claims_item", columnList = "item_id"),
    @Index(name = "idx_claims_claimant", columnList = "claimant_user_id"),
    @Index(name = "idx_claims_status", columnList = "status"),
    @Index(name = "idx_claims_submitted_at", columnList = "submitted_at")
})
public class ClaimRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "claimant_user_id", nullable = false)
    private Long claimantUserId;

    @Column(name = "claimant_name", nullable = false, length = 100)
    private String claimantName;

    @Column(name = "claimant_email", nullable = false, length = 150)
    private String claimantEmail;

    @Column(name = "claimant_phone", length = 25)
    private String claimantPhone;

    @Column(name = "claimant_student_id", length = 50)
    private String claimantStudentId;

    @Column(name = "claim_explanation", nullable = false, columnDefinition = "TEXT")
    private String claimExplanation;

    @Column(name = "distinguishing_features", nullable = false, columnDefinition = "TEXT")
    private String distinguishingFeatures;

    @Column(name = "proof_description", columnDefinition = "TEXT")
    private String proofDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ClaimStatus status = ClaimStatus.PENDING;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @Column(name = "reviewed_by_admin_id")
    private Long reviewedByAdminId;

    @Column(name = "reviewed_by_admin_name", length = 100)
    private String reviewedByAdminName;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.submittedAt = now;
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = ClaimStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public ClaimRecord() {}

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
