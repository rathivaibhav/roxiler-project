package tech.gurugram.rating_app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tech.gurugram.rating_app.model.Role;
import tech.gurugram.rating_app.model.User;
import tech.gurugram.rating_app.repository.RoleRepository;
import tech.gurugram.rating_app.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;
@Component
public class SeedData implements CommandLineRunner {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public SeedData(RoleRepository roleRepo, UserRepository userRepo, BCryptPasswordEncoder passwordEncoder) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        createRoleIfMissing("SYSTEM_ADMIN");
        createRoleIfMissing("STORE_OWNER");
        createRoleIfMissing("USER");

        String adminEmail = "admin@app.com";
        // Find by email, not username
        if (userRepo.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            // Name must be >= 20 chars
            admin.setName("Default System Administrator");
            admin.setEmail(adminEmail);
            admin.setAddress("123 Admin Street");
            // Password must match validation rules
            admin.setPassword(passwordEncoder.encode("AdminPass123!"));

            var adminRole = roleRepo.findByName("SYSTEM_ADMIN").orElseThrow();
            var roles = new HashSet<Role>();
            roles.add(adminRole);
            admin.setRoles(roles);
            userRepo.save(admin);
            System.out.println("SeedData: created admin user (email=" + adminEmail + ")");
        } else {
            System.out.println("SeedData: admin user already exists");
        }
    }

    private void createRoleIfMissing(String name) {
        if (roleRepo.findByName(name).isEmpty()) {
            Role r = new Role(name);
            roleRepo.save(r);
            System.out.println("SeedData: created role " + name);
        }
    }
}