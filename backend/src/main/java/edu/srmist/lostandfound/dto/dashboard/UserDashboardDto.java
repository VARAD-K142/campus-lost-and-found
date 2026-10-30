package edu.srmist.lostandfound.dto.dashboard;

import edu.srmist.lostandfound.dto.item.ItemResponseDto;
import java.util.List;

public class UserDashboardDto {
    private long totalMyReports;
    private long totalMyLostReports;
    private long totalMyFoundReports;
    private long totalMyClaims;
    private long pendingClaims;
    private long approvedClaims;
    private long rejectedClaims;
    private long collectedItems;
    private List<ItemResponseDto> recentReports;

    public UserDashboardDto() {}

    public long getTotalMyReports() { return totalMyReports; }
    public void setTotalMyReports(long totalMyReports) { this.totalMyReports = totalMyReports; }
    public long getTotalMyLostReports() { return totalMyLostReports; }
    public void setTotalMyLostReports(long totalMyLostReports) { this.totalMyLostReports = totalMyLostReports; }
    public long getTotalMyFoundReports() { return totalMyFoundReports; }
    public void setTotalMyFoundReports(long totalMyFoundReports) { this.totalMyFoundReports = totalMyFoundReports; }
    public long getTotalMyClaims() { return totalMyClaims; }
    public void setTotalMyClaims(long totalMyClaims) { this.totalMyClaims = totalMyClaims; }
    public long getPendingClaims() { return pendingClaims; }
    public void setPendingClaims(long pendingClaims) { this.pendingClaims = pendingClaims; }
    public long getApprovedClaims() { return approvedClaims; }
    public void setApprovedClaims(long approvedClaims) { this.approvedClaims = approvedClaims; }
    public long getRejectedClaims() { return rejectedClaims; }
    public void setRejectedClaims(long rejectedClaims) { this.rejectedClaims = rejectedClaims; }
    public long getCollectedItems() { return collectedItems; }
    public void setCollectedItems(long collectedItems) { this.collectedItems = collectedItems; }
    public List<ItemResponseDto> getRecentReports() { return recentReports; }
    public void setRecentReports(List<ItemResponseDto> recentReports) { this.recentReports = recentReports; }
}
