package tech.gurugram.rating_app.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) return bearer.substring(7);
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);
        if (token != null) {
            try {
                Jws<Claims> parsed = jwtUtil.validate(token);
                Claims claims = parsed.getBody();

                String subject = claims.getSubject();
                if (subject != null) {
                    Object rolesObj = claims.get("roles");
                    List<String> roles;
                    if (rolesObj instanceof List) {
                        roles = ((List<?>) rolesObj).stream().map(Object::toString).collect(Collectors.toList());
                    } else {
                        roles = List.of();
                    }

                    var authorities = roles.stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                            .collect(Collectors.toList());

                    var authToken = new UsernamePasswordAuthenticationToken(subject, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception ex) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
