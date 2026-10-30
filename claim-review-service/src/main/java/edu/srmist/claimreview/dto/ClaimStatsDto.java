package edu.srmist.claimreview.dto;

public class ClaimStatsDto {
    private long totalClaims;
    private long pendingClaims;
    private long approvedClaims;
    private long rejectedClaims;

    public ClaimStatsDto(long totalClaims, long pendingClaims, long approvedClaims, long rejectedClaims) {
        this.totalClaims = totalClaims;
        this.pendingClaims = pendingClaims;
        this.approvedClaims = approvedClaims;
        this.rejectedClaims = rejectedClaims;
    }

    public long getTotalClaims() { return totalClaims; }
    public long getPendingClaims() { return pendingClaims; }
    public long getApprovedClaims() { return approvedClaims; }
    public long getRejectedClaims() { return rejectedClaims; }
}
