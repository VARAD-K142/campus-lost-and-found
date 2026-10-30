
package edu.srmist.lostandfound.service;

import edu.srmist.lostandfound.domain.AuditLog;
import edu.srmist.lostandfound.domain.User;
import edu.srmist.lostandfound.dto.audit.AuditLogDto;
import edu.srmist.lostandfound.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void logAction(
            User actor,
            String action,
            String entityType,
            Long entityId,
            String details,
            String ipAddress) {

        String actorName = (actor != null)
                ? actor.getFullName()
                : "SYSTEM";

        AuditLog log = new AuditLog(
                actor,
                actorName,
                action,
                entityType,
                entityId,
                details,
                ipAddress
        );

        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogDto> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllWithActor(pageable)
                .map(AuditLogDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<AuditLogDto> getAuditLogsForEntity(
            String entityType,
            Long entityId) {

        return auditLogRepository
                .findByEntityTypeAndEntityId(entityType, entityId)
                .stream()
                .map(AuditLogDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AuditLogDto> getRecentAuditLogs() {
        return auditLogRepository
                .findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(AuditLogDto::fromEntity)
                .collect(Collectors.toList());
    }
}
