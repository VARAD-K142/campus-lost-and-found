package edu.srmist.lostandfound.dto.claim;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SubmitClaimRequest {

    @NotBlank(message = "Claim explanation is required")
    @Size(min = 10, max = 2000, message = "Explanation must be between 10 and 2000 characters")
    private String claimExplanation;

    @NotBlank(message = "Distinguishing characteristics or features are required")
    @Size(min = 5, max = 2000, message = "Distinguishing features must be between 5 and 2000 characters")
    private String distinguishingFeatures;

    private String proofDescription;

    public SubmitClaimRequest() {}

    public String getClaimExplanation() { return claimExplanation; }
    public void setClaimExplanation(String claimExplanation) { this.claimExplanation = claimExplanation; }
    public String getDistinguishingFeatures() { return distinguishingFeatures; }
    public void setDistinguishingFeatures(String distinguishingFeatures) { this.distinguishingFeatures = distinguishingFeatures; }
    public String getProofDescription() { return proofDescription; }
    public void setProofDescription(String proofDescription) { this.proofDescription = proofDescription; }
}
