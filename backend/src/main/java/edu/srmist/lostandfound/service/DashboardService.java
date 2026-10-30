package edu.srmist.lostandfound.service;

import edu.srmist.lostandfound.domain.User;
import edu.srmist.lostandfound.domain.enums.ItemStatus;
import edu.srmist.lostandfound.domain.enums.ReportType;
import edu.srmist.lostandfound.domain.enums.Role;
import edu.srmist.lostandfound.dto.claim.ClaimResponseDto;
import edu.srmist.lostandfound.dto.dashboard.AdminDashboardDto;
import edu.srmist.lostandfound.dto.dashboard.UserDashboardDto;
import edu.srmist.lostandfound.dto.item.ItemResponseDto;
import edu.srmist.lostandfound.repository.CollectionRecordRepository;
import edu.srmist.lostandfound.repository.ItemRepository;
import edu.srmist.lostandfound.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final ItemRepository itemRepository;
    private final CollectionRecordRepository collectionRepository;
    private final UserRepository userRepository;
    private final ClaimClientService claimClientService;
    private final UserService userService;

    public DashboardService(ItemRepository itemRepository,
                            CollectionRecordRepository collectionRepository,
                            UserRepository userRepository,
                            ClaimClientService claimClientService,
                            UserService userService) {
        this.itemRepository = itemRepository;
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
        this.claimClientService = claimClientService;
        this.userService = userService;
    }

    public UserDashboardDto getUserDashboard() {
        User user = userService.getCurrentAuthenticatedUser();
        UserDashboardDto dto = new UserDashboardDto();

        List<ItemResponseDto> myItems = itemRepository.findByReporterId(user.getId(), PageRequest.of(0, 10))
                .getContent().stream().map(ItemResponseDto::fromEntity).collect(Collectors.toList());

        long myTotal = itemRepository.findByReporterId(user.getId(), PageRequest.of(0, 1)).getTotalElements();
        dto.setTotalMyReports(myTotal);

        long myLost = myItems.stream().filter(i -> i.getReportType() == ReportType.LOST).count();
        long myFound = myItems.stream().filter(i -> i.getReportType() == ReportType.FOUND).count();
        dto.setTotalMyLostReports(myLost);
        dto.setTotalMyFoundReports(myFound);
        dto.setRecentReports(myItems);

        List<ClaimResponseDto> myClaims = claimClientService.getMyClaims();
        dto.setTotalMyClaims(myClaims.size());
        dto.setPendingClaims(myClaims.stream().filter(c -> "PENDING".equalsIgnoreCase(c.getStatus())).count());
        dto.setApprovedClaims(myClaims.stream().filter(c -> "APPROVED".equalsIgnoreCase(c.getStatus())).count());
        dto.setRejectedClaims(myClaims.stream().filter(c -> "REJECTED".equalsIgnoreCase(c.getStatus())).count());

        long myCollectionsCount = collectionRepository.findByClaimantId(user.getId(), PageRequest.of(0, 1)).getTotalElements();
        dto.setCollectedItems(myCollectionsCount);

        return dto;
    }

    public AdminDashboardDto getAdminDashboard() {
        User admin = userService.getCurrentAuthenticatedUser();
        if (admin.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Administrator access required");
        }

        AdminDashboardDto dto = new AdminDashboardDto();
        dto.setTotalItems(itemRepository.count());
        dto.setLostItems(itemRepository.countByReportType(ReportType.LOST));
        dto.setFoundItems(itemRepository.countByReportType(ReportType.FOUND));
        dto.setActiveItems(itemRepository.countByStatus(ItemStatus.ACTIVE));
        dto.setResolvedItems(itemRepository.countByStatus(ItemStatus.RESOLVED));
        dto.setTotalCollections(collectionRepository.count());
        dto.setTotalUsers(userRepository.count());

        Map<String, Long> categoryMap = new HashMap<>();
        for (Object[] row : itemRepository.countItemsByCategory()) {
            categoryMap.put(row[0].toString(), (Long) row[1]);
        }
        dto.setItemsByCategory(categoryMap);

        dto.setRecentItems(itemRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(ItemResponseDto::fromEntity)
                .collect(Collectors.toList()));

        Map<String, Object> claimStats = claimClientService.getClaimStats();
        dto.setTotalClaims(toNumber(claimStats.get("totalClaims")));
        dto.setPendingClaims(toNumber(claimStats.get("pendingClaims")));
        dto.setApprovedClaims(toNumber(claimStats.get("approvedClaims")));
        dto.setRejectedClaims(toNumber(claimStats.get("rejectedClaims")));

        List<ClaimResponseDto> pendingClaims = claimClientService.getAllClaimsForAdmin("PENDING");
        dto.setPendingClaimsQueue(pendingClaims);

        return dto;
    }

    public Map<String, Object> getPublicStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalItemsReported", itemRepository.count());
        stats.put("activeListings", itemRepository.countByStatus(ItemStatus.ACTIVE));
        stats.put("resolvedReunions", itemRepository.countByStatus(ItemStatus.RESOLVED));
        stats.put("successfulHandouts", collectionRepository.count());
        return stats;
    }

    private long toNumber(Object obj) {
        if (obj instanceof Number number) {
            return number.longValue();
        }
        return 0L;
    }
}
