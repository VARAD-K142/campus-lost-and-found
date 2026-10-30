package edu.srmist.lostandfound.service;

import edu.srmist.lostandfound.domain.CollectionRecord;
import edu.srmist.lostandfound.domain.Item;
import edu.srmist.lostandfound.domain.User;
import edu.srmist.lostandfound.domain.enums.ItemStatus;
import edu.srmist.lostandfound.domain.enums.ReportType;
import edu.srmist.lostandfound.domain.enums.Role;
import edu.srmist.lostandfound.dto.claim.ClaimResponseDto;
import edu.srmist.lostandfound.dto.collection.CollectionRecordDto;
import edu.srmist.lostandfound.dto.collection.RecordCollectionRequest;
import edu.srmist.lostandfound.repository.CollectionRecordRepository;
import edu.srmist.lostandfound.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollectionServiceTest {

    @Mock
    private CollectionRecordRepository collectionRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ClaimClientService claimClientService;

    @Mock
    private UserService userService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private CollectionService collectionService;

    private User adminUser;
    private Item foundItem;
    private RecordCollectionRequest request;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(Role.ADMIN);
        adminUser.setFullName("Security Admin");

        foundItem = new Item();
        foundItem.setId(20L);
        foundItem.setTitle("Titan Watch");
        foundItem.setReportType(ReportType.FOUND);
        foundItem.setStatus(ItemStatus.CLAIMED);

        request = new RecordCollectionRequest();
        request.setItemId(20L);
        request.setClaimId(100L);
        request.setClaimantName("Varadharajan S");
        request.setClaimantStudentId("RA2311003010012");
        request.setCollectorVerificationId("SRM-ID-2026-999");
        request.setHandoverNotes("Verified matching serial number and student ID physically");
    }

    @Test
    @DisplayName("Should successfully record collection for an approved claim and update item to RESOLVED")
    void shouldRecordCollectionSuccessfully() {
        ClaimResponseDto approvedClaim = new ClaimResponseDto();
        approvedClaim.setId(100L);
        approvedClaim.setItemId(20L);
        approvedClaim.setClaimantUserId(5L);
        approvedClaim.setStatus("APPROVED");

        when(userService.getCurrentAuthenticatedUser()).thenReturn(adminUser);
        when(itemRepository.findByIdWithReporter(20L)).thenReturn(Optional.of(foundItem));
        when(collectionRepository.existsByItemId(20L)).thenReturn(false);
        when(claimClientService.getClaimById(100L)).thenReturn(approvedClaim);
        when(collectionRepository.save(any(CollectionRecord.class))).thenAnswer(inv -> {
            CollectionRecord cr = inv.getArgument(0);
            cr.setId(500L);
            return cr;
        });

        CollectionRecordDto result = collectionService.recordCollection(request);

        assertThat(result).isNotNull();
        assertThat(foundItem.getStatus()).isEqualTo(ItemStatus.RESOLVED);
        verify(itemRepository).save(foundItem);
        verify(collectionRepository).save(any(CollectionRecord.class));
    }

    @Test
    @DisplayName("Should reject collection when associated claim is not APPROVED")
    void shouldRejectCollectionForUnapprovedClaim() {
        ClaimResponseDto pendingClaim = new ClaimResponseDto();
        pendingClaim.setId(100L);
        pendingClaim.setItemId(20L);
        pendingClaim.setClaimantUserId(5L);
        pendingClaim.setStatus("PENDING");

        when(userService.getCurrentAuthenticatedUser()).thenReturn(adminUser);
        when(itemRepository.findByIdWithReporter(20L)).thenReturn(Optional.of(foundItem));
        when(collectionRepository.existsByItemId(20L)).thenReturn(false);
        when(claimClientService.getClaimById(100L)).thenReturn(pendingClaim);

        assertThatThrownBy(() -> collectionService.recordCollection(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Only APPROVED claims are eligible for collection");

        verify(collectionRepository, never()).save(any(CollectionRecord.class));
    }

    @Test
    @DisplayName("Should reject duplicate collection attempt on already collected item")
    void shouldRejectDuplicateCollection() {
        when(userService.getCurrentAuthenticatedUser()).thenReturn(adminUser);
        when(itemRepository.findByIdWithReporter(20L)).thenReturn(Optional.of(foundItem));
        when(collectionRepository.existsByItemId(20L)).thenReturn(true);

        assertThatThrownBy(() -> collectionService.recordCollection(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Collection has already been recorded for this item");

        verify(collectionRepository, never()).save(any(CollectionRecord.class));
    }
}
