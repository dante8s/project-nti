package com.nti.nti_backend.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Мінімум 32 символи — інакше помилка
    private final String SECRET = "nti-super-secret-key-minimum-32-chars!!";

    // Токен живе 24 години
    private final long EXPIRATION = 86400000L;

    // Згенерувати токен по email
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis()
                                + EXPIRATION)
                )
                .signWith(getKey())
                .compact();
    }

    // Витягти email з токена
    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Перевірити чи токен валідний
    public boolean isTokenValid(
            String token, UserDetails userDetails) {
        try {
            String email = extractEmail(token);
            return email.equals(userDetails.getUsername());
        } catch (JwtException e) {
            return false;
        }
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }
}
