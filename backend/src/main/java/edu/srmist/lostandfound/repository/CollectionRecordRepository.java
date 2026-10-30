package edu.srmist.lostandfound.repository;

import edu.srmist.lostandfound.domain.CollectionRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionRecordRepository extends JpaRepository<CollectionRecord, Long> {

    @Query("SELECT c FROM CollectionRecord c JOIN FETCH c.item i JOIN FETCH c.verifiedByAdmin WHERE c.item.id = :itemId")
    Optional<CollectionRecord> findByItemId(@Param("itemId") Long itemId);

    Optional<CollectionRecord> findByClaimId(Long claimId);

    @Query("SELECT c FROM CollectionRecord c JOIN FETCH c.item i JOIN FETCH c.verifiedByAdmin WHERE c.claimantId = :claimantId ORDER BY c.collectedAt DESC")
    Page<CollectionRecord> findByClaimantId(@Param("claimantId") Long claimantId, Pageable pageable);

    @Query("SELECT c FROM CollectionRecord c JOIN FETCH c.item i JOIN FETCH c.verifiedByAdmin ORDER BY c.collectedAt DESC")
    Page<CollectionRecord> findAllWithDetails(Pageable pageable);

    boolean existsByItemId(Long itemId);
    boolean existsByClaimId(Long claimId);
}
