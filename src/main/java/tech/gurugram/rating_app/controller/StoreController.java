package tech.gurugram.rating_app.controller;

import tech.gurugram.rating_app.config.SecurityUtil;
import tech.gurugram.rating_app.dto.StoreListingDto;
import tech.gurugram.rating_app.service.StoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stores")
@PreAuthorize("isAuthenticated()")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }
    @GetMapping
    public ResponseEntity<?> listStores(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address
    ) {
        Long userId = SecurityUtil.currentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        List<StoreListingDto> stores = storeService.getStoreListings(userId, name, address);
        return ResponseEntity.ok(stores);
    }
}