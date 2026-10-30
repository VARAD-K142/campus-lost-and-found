package edu.srmist.lostandfound.controller;

import edu.srmist.lostandfound.dto.audit.AuditLogDto;
import edu.srmist.lostandfound.dto.common.ApiResponse;
import edu.srmist.lostandfound.dto.common.PagedResponse;
import edu.srmist.lostandfound.service.AuditService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/audit-logs")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<AuditLogDto>>> getAllAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 50));
        Page<AuditLogDto> pageResult = auditService.getAllAuditLogs(pageable);
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.fromPage(pageResult)));
    }
}
