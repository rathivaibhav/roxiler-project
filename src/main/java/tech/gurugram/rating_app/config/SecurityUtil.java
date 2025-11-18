package tech.gurugram.rating_app.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

public class SecurityUtil {
    public static Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        try {
            String name = auth.getName();
            return Long.parseLong(name);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean isAdmin() {
        return hasRole("SYSTEM_ADMIN");
    }
    public static boolean isStoreOwner() {
        return hasRole("STORE_OWNER");
    }
    public static boolean isUser() {
        return hasRole("USER");
    }
    public static boolean hasRole(String roleName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals(roleName) ||
                        a.getAuthority().equals("ROLE_" + roleName));
    }
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}