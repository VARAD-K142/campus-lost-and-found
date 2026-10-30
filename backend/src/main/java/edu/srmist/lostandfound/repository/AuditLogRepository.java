package edu.srmist.lostandfound.repository;

import edu.srmist.lostandfound.domain.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("SELECT a FROM AuditLog a LEFT JOIN FETCH a.actor ORDER BY a.createdAt DESC")
    Page<AuditLog> findAllWithActor(Pageable pageable);

    @Query("SELECT a FROM AuditLog a LEFT JOIN FETCH a.actor WHERE a.entityType = :entityType AND a.entityId = :entityId ORDER BY a.createdAt DESC")
    List<AuditLog> findByEntityTypeAndEntityId(@Param("entityType") String entityType, @Param("entityId") Long entityId);

    List<AuditLog> findTop10ByOrderByCreatedAtDesc();
}
