package edu.srmist.lostandfound.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "collection_records", indexes = {
    @Index(name = "idx_collection_claimant", columnList = "claimant_id"),
    @Index(name = "idx_collection_claim", columnList = "claim_id")
})
public class CollectionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false, unique = true)
    private Item item;

    @Column(name = "claim_id", nullable = false)
    private Long claimId;

    @Column(name = "claimant_id", nullable = false)
    private Long claimantId;

    @Column(name = "claimant_name", nullable = false, length = 100)
    private String claimantName;

    @Column(name = "claimant_student_id", length = 50)
    private String claimantStudentId;

    @Column(name = "handover_notes", nullable = false, columnDefinition = "TEXT")
    private String handoverNotes;

    @Column(name = "collector_verification_id", nullable = false, length = 100)
    private String collectorVerificationId;

    @Column(name = "collected_at", nullable = false)
    private LocalDateTime collectedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "verified_by_admin_id", nullable = false)
    private User verifiedByAdmin;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        if (this.collectedAt == null) {
            this.collectedAt = now;
        }
    }

    public CollectionRecord() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }

    public Long getClaimantId() { return claimantId; }
    public void setClaimantId(Long claimantId) { this.claimantId = claimantId; }

    public String getClaimantName() { return claimantName; }
    public void setClaimantName(String claimantName) { this.claimantName = claimantName; }

    public String getClaimantStudentId() { return claimantStudentId; }
    public void setClaimantStudentId(String claimantStudentId) { this.claimantStudentId = claimantStudentId; }

    public String getHandoverNotes() { return handoverNotes; }
    public void setHandoverNotes(String handoverNotes) { this.handoverNotes = handoverNotes; }

    public String getCollectorVerificationId() { return collectorVerificationId; }
    public void setCollectorVerificationId(String collectorVerificationId) { this.collectorVerificationId = collectorVerificationId; }

    public LocalDateTime getCollectedAt() { return collectedAt; }
    public void setCollectedAt(LocalDateTime collectedAt) { this.collectedAt = collectedAt; }

    public User getVerifiedByAdmin() { return verifiedByAdmin; }
    public void setVerifiedByAdmin(User verifiedByAdmin) { this.verifiedByAdmin = verifiedByAdmin; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
