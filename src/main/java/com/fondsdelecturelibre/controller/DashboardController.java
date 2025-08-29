package com.fondsdelecturelibre.controller;

import com.fondsdelecturelibre.dto.DashboardDTO;
import com.fondsdelecturelibre.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping
    public ResponseEntity<DashboardDTO> getUserDashboard(Authentication authentication) {
        String username = authentication.getName();
        DashboardDTO dashboard = dashboardService.getUserDashboard(username);
        return ResponseEntity.ok(dashboard);
    }
}
