package edu.srmist.lostandfound.dto.collection;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RecordCollectionRequest {

    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotNull(message = "Approved Claim ID is required")
    private Long claimId;

    @NotBlank(message = "Claimant Collector Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String claimantName;

    private String claimantStudentId;

    @NotBlank(message = "Handover and identity verification notes are required")
    @Size(min = 5, max = 2000, message = "Notes must be between 5 and 2000 characters")
    private String handoverNotes;

    @NotBlank(message = "Verification ID (e.g. SRM Student ID card number / Aadhaar) is required")
    @Size(max = 100, message = "Verification ID must not exceed 100 characters")
    private String collectorVerificationId;

    public RecordCollectionRequest() {}

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }
    public String getClaimantName() { return claimantName; }
    public void setClaimantName(String claimantName) { this.claimantName = claimantName; }
    public String getClaimantStudentId() { return claimantStudentId; }
    public void setClaimantStudentId(String claimantStudentId) { this.claimantStudentId = claimantStudentId; }
    public String getHandoverNotes() { return handoverNotes; }
    public void setHandoverNotes(String handoverNotes) { this.handoverNotes = handoverNotes; }
    public String getCollectorVerificationId() { return collectorVerificationId; }
    public void setCollectorVerificationId(String collectorVerificationId) { this.collectorVerificationId = collectorVerificationId; }
}
