package edu.srmist.claimreview.dto;

import edu.srmist.claimreview.domain.ClaimStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewClaimDto {

    @NotNull(message = "Verdict status is required (APPROVED or REJECTED)")
    private ClaimStatus status;

    @Size(max = 2000, message = "Admin notes must not exceed 2000 characters")
    private String adminNotes;

    @NotNull(message = "Reviewer Admin ID is required")
    private Long reviewedByAdminId;

    private String reviewedByAdminName;

    public ReviewClaimDto() {}

    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public Long getReviewedByAdminId() { return reviewedByAdminId; }
    public void setReviewedByAdminId(Long reviewedByAdminId) { this.reviewedByAdminId = reviewedByAdminId; }

    public String getReviewedByAdminName() { return reviewedByAdminName; }
    public void setReviewedByAdminName(String reviewedByAdminName) { this.reviewedByAdminName = reviewedByAdminName; }
}
