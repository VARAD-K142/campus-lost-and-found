package edu.srmist.lostandfound.dto.collection;

import edu.srmist.lostandfound.domain.CollectionRecord;
import java.time.LocalDateTime;

public class CollectionRecordDto {
    private Long id;
    private Long itemId;
    private String itemTitle;
    private Long claimId;
    private Long claimantId;
    private String claimantName;
    private String claimantStudentId;
    private String handoverNotes;
    private String collectorVerificationId;
    private LocalDateTime collectedAt;
    private Long verifiedByAdminId;
    private String verifiedByAdminName;
    private LocalDateTime createdAt;

    public CollectionRecordDto() {}

    public static CollectionRecordDto fromEntity(CollectionRecord entity) {
        CollectionRecordDto dto = new CollectionRecordDto();
        dto.setId(entity.getId());
        dto.setItemId(entity.getItem().getId());
        dto.setItemTitle(entity.getItem().getTitle());
        dto.setClaimId(entity.getClaimId());
        dto.setClaimantId(entity.getClaimantId());
        dto.setClaimantName(entity.getClaimantName());
        dto.setClaimantStudentId(entity.getClaimantStudentId());
        dto.setHandoverNotes(entity.getHandoverNotes());
        dto.setCollectorVerificationId(entity.getCollectorVerificationId());
        dto.setCollectedAt(entity.getCollectedAt());
        if (entity.getVerifiedByAdmin() != null) {
            dto.setVerifiedByAdminId(entity.getVerifiedByAdmin().getId());
            dto.setVerifiedByAdminName(entity.getVerifiedByAdmin().getFullName());
        }
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public String getItemTitle() { return itemTitle; }
    public void setItemTitle(String itemTitle) { this.itemTitle = itemTitle; }
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
    public Long getVerifiedByAdminId() { return verifiedByAdminId; }
    public void setVerifiedByAdminId(Long verifiedByAdminId) { this.verifiedByAdminId = verifiedByAdminId; }
    public String getVerifiedByAdminName() { return verifiedByAdminName; }
    public void setVerifiedByAdminName(String verifiedByAdminName) { this.verifiedByAdminName = verifiedByAdminName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
