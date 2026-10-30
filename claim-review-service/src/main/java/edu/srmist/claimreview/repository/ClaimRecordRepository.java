package edu.srmist.claimreview.repository;

import edu.srmist.claimreview.domain.ClaimRecord;
import edu.srmist.claimreview.domain.ClaimStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRecordRepository extends JpaRepository<ClaimRecord, Long> {
    List<ClaimRecord> findByItemId(Long itemId, Sort sort);
    List<ClaimRecord> findByClaimantUserId(Long claimantUserId, Sort sort);
    List<ClaimRecord> findByStatus(ClaimStatus status, Sort sort);
    boolean existsByItemIdAndClaimantUserIdAndStatus(Long itemId, Long claimantUserId, ClaimStatus status);
    Optional<ClaimRecord> findFirstByItemIdAndStatus(Long itemId, ClaimStatus status);
    long countByStatus(ClaimStatus status);
}
