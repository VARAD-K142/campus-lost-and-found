package edu.srmist.lostandfound.dto.dashboard;

import edu.srmist.lostandfound.dto.claim.ClaimResponseDto;
import edu.srmist.lostandfound.dto.item.ItemResponseDto;
import java.util.List;
import java.util.Map;

public class AdminDashboardDto {
    private long totalItems;
    private long lostItems;
    private long foundItems;
    private long activeItems;
    private long resolvedItems;
    private long totalClaims;
    private long pendingClaims;
    private long approvedClaims;
    private long rejectedClaims;
    private long totalCollections;
    private long totalUsers;
    private Map<String, Long> itemsByCategory;
    private List<ItemResponseDto> recentItems;
    private List<ClaimResponseDto> pendingClaimsQueue;

    public AdminDashboardDto() {}

    public long getTotalItems() { return totalItems; }
    public void setTotalItems(long totalItems) { this.totalItems = totalItems; }
    public long getLostItems() { return lostItems; }
    public void setLostItems(long lostItems) { this.lostItems = lostItems; }
    public long getFoundItems() { return foundItems; }
    public void setFoundItems(long foundItems) { this.foundItems = foundItems; }
    public long getActiveItems() { return activeItems; }
    public void setActiveItems(long activeItems) { this.activeItems = activeItems; }
    public long getResolvedItems() { return resolvedItems; }
    public void setResolvedItems(long resolvedItems) { this.resolvedItems = resolvedItems; }
    public long getTotalClaims() { return totalClaims; }
    public void setTotalClaims(long totalClaims) { this.totalClaims = totalClaims; }
    public long getPendingClaims() { return pendingClaims; }
    public void setPendingClaims(long pendingClaims) { this.pendingClaims = pendingClaims; }
    public long getApprovedClaims() { return approvedClaims; }
    public void setApprovedClaims(long approvedClaims) { this.approvedClaims = approvedClaims; }
    public long getRejectedClaims() { return rejectedClaims; }
    public void setRejectedClaims(long rejectedClaims) { this.rejectedClaims = rejectedClaims; }
    public long getTotalCollections() { return totalCollections; }
    public void setTotalCollections(long totalCollections) { this.totalCollections = totalCollections; }
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public Map<String, Long> getItemsByCategory() { return itemsByCategory; }
    public void setItemsByCategory(Map<String, Long> itemsByCategory) { this.itemsByCategory = itemsByCategory; }
    public List<ItemResponseDto> getRecentItems() { return recentItems; }
    public void setRecentItems(List<ItemResponseDto> recentItems) { this.recentItems = recentItems; }
    public List<ClaimResponseDto> getPendingClaimsQueue() { return pendingClaimsQueue; }
    public void setPendingClaimsQueue(List<ClaimResponseDto> pendingClaimsQueue) { this.pendingClaimsQueue = pendingClaimsQueue; }
}
