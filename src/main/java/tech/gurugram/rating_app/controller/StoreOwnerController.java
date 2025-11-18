package tech.gurugram.rating_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.gurugram.rating_app.config.SecurityUtil;
import tech.gurugram.rating_app.dto.StoreOwnerDashboardDto;
import tech.gurugram.rating_app.service.StoreOwnerService;
import java.util.List;

@RestController
@RequestMapping("/api/owner")
@PreAuthorize("hasRole('STORE_OWNER')")
public class StoreOwnerController {

    private final StoreOwnerService storeOwnerService;

    public StoreOwnerController(StoreOwnerService storeOwnerService) {
        this.storeOwnerService = storeOwnerService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        Long ownerId = SecurityUtil.currentUserId();
        List<StoreOwnerDashboardDto> dashboardData = storeOwnerService.getOwnerDashboard(ownerId);
        return ResponseEntity.ok(dashboardData);
    }
}