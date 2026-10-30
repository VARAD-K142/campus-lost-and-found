package edu.srmist.lostandfound.controller;

import edu.srmist.lostandfound.dto.common.ApiResponse;
import edu.srmist.lostandfound.dto.dashboard.AdminDashboardDto;
import edu.srmist.lostandfound.dto.dashboard.UserDashboardDto;
import edu.srmist.lostandfound.service.DashboardService;
import edu.srmist.lostandfound.service.JdbcConnectivityDemo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final JdbcConnectivityDemo jdbcConnectivityDemo;

    public DashboardController(DashboardService dashboardService, JdbcConnectivityDemo jdbcConnectivityDemo) {
        this.dashboardService = dashboardService;
        this.jdbcConnectivityDemo = jdbcConnectivityDemo;
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<UserDashboardDto>> getUserDashboard() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getUserDashboard()));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<AdminDashboardDto>> getAdminDashboard() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getAdminDashboard()));
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPublicStats() {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.getPublicStats()));
    }

    @GetMapping("/jdbc-test")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getJdbcVerification() {
        return ResponseEntity.ok(ApiResponse.ok("CO-3 JDBC Verification Results", jdbcConnectivityDemo.runJdbcVerification()));
    }
}
