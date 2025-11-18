package tech.gurugram.rating_app.controller;

import jakarta.persistence.criteria.Join;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import tech.gurugram.rating_app.model.Role;
import tech.gurugram.rating_app.model.Store;
import tech.gurugram.rating_app.model.User;
import tech.gurugram.rating_app.repository.RoleRepository;
import tech.gurugram.rating_app.repository.StoreRepository;
import tech.gurugram.rating_app.repository.UserRepository;
import tech.gurugram.rating_app.repository.RatingRepository;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class AdminController {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final StoreRepository storeRepo;
    private final RatingRepository ratingRepo;
    private final BCryptPasswordEncoder encoder;

    public AdminController(UserRepository userRepo,
                           RoleRepository roleRepo,
                           StoreRepository storeRepo,
                           RatingRepository ratingRepo,
                           BCryptPasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.storeRepo = storeRepo;
        this.ratingRepo = ratingRepo;
        this.encoder = encoder;
    }

    @GetMapping("/users")
    public ResponseEntity<?> listUsers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String role
    ) {
        Specification<User> spec = (root, q, cb) -> cb.conjunction();

        if (StringUtils.hasText(query)) {
            spec = spec.and((root, q, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), "%" + query.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("email")), "%" + query.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("address")), "%" + query.toLowerCase() + "%")
            ));
        }

        if (StringUtils.hasText(role)) {
            spec = spec.and((root, q, cb) -> {
                Join<User, Role> roleJoin = root.join("roles");
                return cb.equal(roleJoin.get("name"), role);
            });
        }

        List<User> users = userRepo.findAll(spec);

        List<Map<String, Object>> enriched = users.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("name", u.getName());
            map.put("email", u.getEmail());
            map.put("address", u.getAddress());
            map.put("roles", u.getRoles());

            boolean isOwner = u.getRoles().stream()
                    .anyMatch(r -> "STORE_OWNER".equals(r.getName()));
            if (isOwner) {
                List<Store> ownedStores = storeRepo.findByOwnerId(u.getId());
                map.put("stores", ownedStores.stream().map(Store::getName).collect(Collectors.toList()));
            }
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(enriched);
    }

    @GetMapping("/stores")
    public ResponseEntity<?> listStores(
            @RequestParam(required = false) String query
    ) {
        Specification<Store> spec = (root, q, cb) -> cb.conjunction();

        if (StringUtils.hasText(query)) {
            spec = spec.and((root, q, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), "%" + query.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("email")), "%" + query.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("address")), "%" + query.toLowerCase() + "%")
            ));
        }

        List<Store> stores = storeRepo.findAll(spec);

        List<Map<String, Object>> enriched = stores.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("name", s.getName());
            map.put("address", s.getAddress());
            map.put("email", s.getEmail());

            if(s.getOwner() != null) {
                map.put("owner", Map.of("id", s.getOwner().getId(), "name", s.getOwner().getName()));
            }

            Double avg = ratingRepo.getAverageRatingByStoreId(s.getId());
            Long count = ratingRepo.getRatingCountByStoreId(s.getId());
            map.put("ratingCount", count);
            map.put("avgRating", avg != null ? (Math.round(avg * 100.0) / 100.0) : null);

            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(enriched);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", userRepo.count());
        stats.put("totalStores", storeRepo.count());
        stats.put("totalRatings", ratingRepo.count());
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest req) {

        if (userRepo.existsByEmail(req.email)) {
            return ResponseEntity.badRequest().body("email_already_exists");
        }

        User u = new User();
        u.setName(req.name);
        u.setEmail(req.email);
        u.setAddress(req.address);
        u.setPassword(encoder.encode(req.password));

        Set<Role> userRoles = new HashSet<>();
        if (req.roleName != null && !req.roleName.isBlank()) {
            Role role = roleRepo.findByName(req.roleName)
                    .orElseGet(() -> roleRepo.findByName("USER").orElseThrow());
            userRoles.add(role);
        } else {
            Role defaultRole = roleRepo.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("USER role not found"));
            userRoles.add(defaultRole);
        }
        u.setRoles(userRoles);
        User saved = userRepo.save(u);

        return ResponseEntity.ok(saved);
    }

    @PostMapping("/create-store")
    public ResponseEntity<?> createStore(@Valid @RequestBody CreateStoreRequest req) {

        Store s = new Store();
        s.setName(req.name);
        s.setAddress(req.address);
        s.setEmail(req.email);

        if (req.ownerId != null) {
            Optional<User> ownerOpt = userRepo.findById(req.ownerId);
            if (ownerOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("owner_not_found");
            }
            s.setOwner(ownerOpt.get());
        }

        Store saved = storeRepo.save(s);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/assign-role")
    public ResponseEntity<?> assignRole(@RequestBody AssignRoleRequest req) {
        if (req == null || req.userId == null || req.roleName == null) {
            return ResponseEntity.badRequest().body("userId_and_roleName_required");
        }

        User u = userRepo.findById(req.userId).orElse(null);
        if (u == null) return ResponseEntity.badRequest().body("user_not_found");

        Role r = roleRepo.findByName(req.roleName).orElse(null);
        if (r == null) return ResponseEntity.badRequest().body("role_not_found");

        boolean already = u.getRoles().stream()
                .anyMatch(existing -> Objects.equals(existing.getName(), req.roleName));
        if (already) return ResponseEntity.ok("user_already_has_role");

        u.getRoles().add(r);
        userRepo.save(u);
        return ResponseEntity.ok("role_assigned");
    }

    public static class AssignRoleRequest {
        public Long userId;
        public String roleName;
    }

    public static class CreateUserRequest {
        @NotBlank @Size(min = 20, max = 60, message = "Name must be 20-60 chars")
        public String name;
        @NotBlank @Email
        public String email;
        @NotBlank
        @Size(min = 8, max = 16, message = "Password must be 8-16 characters")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$&*]).*$", message = "Password must have one uppercase letter and one special character")
        public String password;
        @Size(max = 400, message = "Address max 400 chars")
        public String address;
        public String roleName;
    }

    public static class CreateStoreRequest {
        @NotBlank
        public String name;
        @Size(max = 400, message = "Address max 400 chars")
        public String address;
        @Email(message = "Must be a valid email")
        public String email;
        public Long ownerId;
    }
}