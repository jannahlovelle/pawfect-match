package cit.edu.pawfect.match.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.DecodingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME;

    public String generateToken(String email) { // Changed parameter from username to email
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty when generating JWT token");
        }
        try {
            byte[] keyBytes;
            try {
                keyBytes = Decoders.BASE64.decode(SECRET_KEY);
            } catch (DecodingException e) {
                throw new IllegalArgumentException("Invalid Base64-encoded secret key in configuration", e);
            }
            return Jwts.builder()
                    .subject(email) // Use email as the subject
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(Keys.hmacShaKeyFor(keyBytes))
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JWT token: " + e.getMessage(), e);
        }
    }

    public String extractEmail(String token) { // Changed method name from extractUsername to extractEmail
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        } catch (DecodingException e) {
            throw new IllegalArgumentException("Invalid Base64-encoded secret key in configuration", e);
        }
        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(keyBytes))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            throw new RuntimeException("Failed to extract email from JWT token: " + e.getMessage(), e);
        }
    }

    public boolean validateToken(String token, String email) { // Changed parameter from username to email
        try {
            final String extractedEmail = extractEmail(token);
            return (extractedEmail.equals(email) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        } catch (DecodingException e) {
            throw new IllegalArgumentException("Invalid Base64-encoded secret key in configuration", e);
        }
        try {
            Date expiration = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(keyBytes))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            throw new RuntimeException("Failed to check token expiration: " + e.getMessage(), e);
        }
    }
}