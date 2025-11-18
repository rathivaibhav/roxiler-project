package tech.gurugram.rating_app.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import tech.gurugram.rating_app.config.SecurityUtil;
import tech.gurugram.rating_app.model.Role;
import tech.gurugram.rating_app.model.User;
import tech.gurugram.rating_app.repository.RoleRepository;
import tech.gurugram.rating_app.repository.UserRepository;
import tech.gurugram.rating_app.config.JwtUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepo,
                          RoleRepository roleRepo,
                          BCryptPasswordEncoder encoder,
                          JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterReq req) {

        if (userRepo.existsByEmail(req.email)) {
            return ResponseEntity.badRequest().body("email_taken");
        }

        Role defaultRole = roleRepo.findByName("USER")
                .orElseThrow(() -> new RuntimeException("role_user_missing"));

        User u = new User();
        u.setName(req.name);
        u.setEmail(req.email);
        u.setAddress(req.address);
        u.setPassword(encoder.encode(req.password));
        u.setRoles(java.util.Set.of(defaultRole));
        userRepo.save(u);

        return ResponseEntity.ok("registered_successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReq req) {

        var opt = userRepo.findByEmail(req.email);
        if (opt.isEmpty()) return ResponseEntity.status(401).body("invalid_credentials");

        var u = opt.get();
        if (!encoder.matches(req.password, u.getPassword())) {
            return ResponseEntity.status(401).body("invalid_credentials");
        }

        List<String> roles = u.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        String token = jwtUtil.generateToken(String.valueOf(u.getId()), roles);

        return ResponseEntity.ok(new LoginResp(token, u.getId(), u.getName(), u.getEmail(), roles));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Long userId = SecurityUtil.currentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body("not_authenticated");
        }

        User u = userRepo.findById(userId).orElse(null);
        if (u == null) {
            return ResponseEntity.status(404).body("user_not_found");
        }

        List<String> roles = u.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new UserInfo(u.getId(), u.getName(), u.getEmail(), u.getAddress(), roles));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordReq req) {

        Long userId = SecurityUtil.currentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body("not_authenticated");
        }

        User u = userRepo.findById(userId).orElse(null);
        if (u == null) {
            return ResponseEntity.status(404).body("user_not_found");
        }

        if (!encoder.matches(req.oldPassword, u.getPassword())) {
            return ResponseEntity.status(400).body("old_password_incorrect");
        }

        u.setPassword(encoder.encode(req.newPassword));
        userRepo.save(u);

        return ResponseEntity.ok("password_updated_successfully");
    }
    public static class RegisterReq {
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
    }

    public static class LoginReq {
        @NotBlank @Email
        public String email;

        @NotBlank
        public String password;
    }

    public static class LoginResp {
        public String token;
        public Long userId;
        public String name;
        public String email;
        public List<String> roles;

        public LoginResp(String token, Long userId, String name, String email, List<String> roles) {
            this.token = token;
            this.userId = userId;
            this.name = name; // --- FIX: Use name ---
            this.email = email;
            this.roles = roles;
        }
    }

    public static class UserInfo {
        public Long id;
        public String name;
        public String email;
        public String address;
        public List<String> roles;

        public UserInfo(Long id, String name, String email, String address, List<String> roles) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.address = address;
            this.roles = roles;
        }
    }

    public static class ChangePasswordReq {
        @NotBlank
        public String oldPassword;

        @NotBlank
        @Size(min = 8, max = 16, message = "Password must be 8-16 characters")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$&*]).*$", message = "Password must have one uppercase letter and one special character")
        public String newPassword;
    }
}