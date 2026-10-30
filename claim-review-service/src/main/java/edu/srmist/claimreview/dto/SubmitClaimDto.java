package edu.srmist.claimreview.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SubmitClaimDto {

    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotNull(message = "Claimant User ID is required")
    private Long claimantUserId;

    @NotBlank(message = "Claimant name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String claimantName;

    @NotBlank(message = "Claimant email is required")
    @Email(message = "Invalid email format")
    private String claimantEmail;

    private String claimantPhone;
    private String claimantStudentId;

    @NotBlank(message = "Claim explanation is required")
    @Size(min = 10, max = 2000, message = "Explanation must be between 10 and 2000 characters")
    private String claimExplanation;

    @NotBlank(message = "Distinguishing features are required")
    @Size(min = 5, max = 2000, message = "Distinguishing features must be between 5 and 2000 characters")
    private String distinguishingFeatures;

    private String proofDescription;

    public SubmitClaimDto() {}

    // Getters and Setters
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
}
