package edu.srmist.lostandfound.dto.claim;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClaimReviewRequest {

    @NotBlank(message = "Review action must be APPROVE or REJECT")
    private String action; // "APPROVE" or "REJECT"

    @Size(max = 2000, message = "Admin notes must not exceed 2000 characters")
    private String adminNotes;

    public ClaimReviewRequest() {}

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
}
