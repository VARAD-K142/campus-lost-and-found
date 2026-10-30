package edu.srmist.claimreview.service;

import edu.srmist.claimreview.domain.ClaimRecord;
import edu.srmist.claimreview.domain.ClaimStatus;
import edu.srmist.claimreview.dto.ClaimRecordDto;
import edu.srmist.claimreview.dto.ReviewClaimDto;
import edu.srmist.claimreview.dto.SubmitClaimDto;
import edu.srmist.claimreview.repository.ClaimRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimReviewServiceTest {

    @Mock
    private ClaimRecordRepository claimRepository;

    @InjectMocks
    private ClaimReviewService claimService;

    private SubmitClaimDto submitDto;
    private ClaimRecord pendingRecord;

    @BeforeEach
    void setUp() {
        submitDto = new SubmitClaimDto();
        submitDto.setItemId(10L);
        submitDto.setClaimantUserId(5L);
        submitDto.setClaimantName("Varadharajan S");
        submitDto.setClaimantEmail("vs7890@srmist.edu.in");
        submitDto.setClaimExplanation("I lost my black HP mouse in Tech Park Lab 3 yesterday afternoon.");
        submitDto.setDistinguishingFeatures("Has a small scratch near the scroll wheel and serial no sticker underneath.");

        pendingRecord = new ClaimRecord();
        pendingRecord.setId(101L);
        pendingRecord.setItemId(10L);
        pendingRecord.setClaimantUserId(5L);
        pendingRecord.setClaimantName("Varadharajan S");
        pendingRecord.setClaimantEmail("vs7890@srmist.edu.in");
        pendingRecord.setClaimExplanation(submitDto.getClaimExplanation());
        pendingRecord.setDistinguishingFeatures(submitDto.getDistinguishingFeatures());
        pendingRecord.setStatus(ClaimStatus.PENDING);
        pendingRecord.setSubmittedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should submit claim successfully when no duplicate pending exists")
    void shouldSubmitClaimSuccessfully() {
        when(claimRepository.existsByItemIdAndClaimantUserIdAndStatus(10L, 5L, ClaimStatus.PENDING))
                .thenReturn(false);
        when(claimRepository.save(any(ClaimRecord.class))).thenReturn(pendingRecord);

        ClaimRecordDto result = claimService.submitClaim(submitDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(101L);
        assertThat(result.getStatus()).isEqualTo(ClaimStatus.PENDING);
        verify(claimRepository).save(any(ClaimRecord.class));
    }

    @Test
    @DisplayName("Should reject duplicate pending claim for same item and claimant")
    void shouldRejectDuplicatePendingClaim() {
        when(claimRepository.existsByItemIdAndClaimantUserIdAndStatus(10L, 5L, ClaimStatus.PENDING))
                .thenReturn(true);

        assertThatThrownBy(() -> claimService.submitClaim(submitDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("You already have a pending claim submitted");

        verify(claimRepository, never()).save(any(ClaimRecord.class));
    }

    @Test
    @DisplayName("Should approve a pending claim with admin review notes")
    void shouldApprovePendingClaim() {
        ReviewClaimDto reviewDto = new ReviewClaimDto();
        reviewDto.setStatus(ClaimStatus.APPROVED);
        reviewDto.setAdminNotes("Serial number matches verification sheet in Security Office.");
        reviewDto.setReviewedByAdminId(1L);
        reviewDto.setReviewedByAdminName("Admin Officer");

        when(claimRepository.findById(101L)).thenReturn(Optional.of(pendingRecord));
        when(claimRepository.save(any(ClaimRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        ClaimRecordDto reviewed = claimService.reviewClaim(101L, reviewDto);

        assertThat(reviewed.getStatus()).isEqualTo(ClaimStatus.APPROVED);
        assertThat(reviewed.getAdminNotes()).isEqualTo("Serial number matches verification sheet in Security Office.");
        assertThat(reviewed.getReviewedByAdminId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should prevent state transition if claim is already resolved")
    void shouldPreventInvalidStateTransition() {
        pendingRecord.setStatus(ClaimStatus.APPROVED);

        ReviewClaimDto reviewDto = new ReviewClaimDto();
        reviewDto.setStatus(ClaimStatus.REJECTED);
        reviewDto.setAdminNotes("Attempting double review");
        reviewDto.setReviewedByAdminId(1L);

        when(claimRepository.findById(101L)).thenReturn(Optional.of(pendingRecord));

        assertThatThrownBy(() -> claimService.reviewClaim(101L, reviewDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid state transition");
    }
}
