package tech.gurugram.rating_app.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private final Key signingKey;
    private final long expirationMs;

    public JwtUtil(@Value("${jwt.secret:}") String base64Secret,
                   @Value("${jwt.expiration-ms:86400000}") long expirationMs) {

        if (base64Secret == null || base64Secret.isBlank()) {
            byte[] keyBytes = "ThisIsADevSecretChangeMeAndMakeItLongEnough12345".getBytes();
            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        } else {
            byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        }
        this.expirationMs = expirationMs;
    }

    public String generateToken(String subject, List<String> roles) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .claim("roles", roles)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> validate(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
    }

    public String getSubjectOrNull(String token) {
        try {
            return validate(token).getBody().getSubject();
        } catch (JwtException e) {
            return null;
        }
    }
}