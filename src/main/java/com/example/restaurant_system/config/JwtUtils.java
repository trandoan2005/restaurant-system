package com.example.restaurant_system.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    // ✅ Secret cố định (đủ dài, an toàn)
    private final String JWT_SECRET = "coffee-shop-jwt-secret-key-2024-lab06-spring-boot-strong-secret-key-512-bits-here";

    @Value("${app.jwt.expiration:86400000}") // 1 ngày
    private long JWT_EXPIRATION_MS;

    // ✅ Tạo khóa ký
    private SecretKey getSigningKey() {
        String secret = JWT_SECRET;
        if (secret.length() < 64) {
            secret = secret + "0".repeat(64 - secret.length());
        }
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ✅ Sinh token JWT
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // ✅ Lấy username từ token
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ✅ Xác thực token hợp lệ
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            System.err.println("JWT validation error: empty token");
            return false;
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("JWT validation error: " + e.getMessage());
            return false;
        }
    }
}
