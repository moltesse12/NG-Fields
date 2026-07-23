package tg.ngstars.auth.service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;

@Service
public class EmailVerificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationService.class);
    private static final String PURPOSE = "email_verification";
    private static final long EXPIRATION_MINUTES = 15;

    @Value("${app.jwt.secret:}")
    private String jwtSecret;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public String generateVerificationToken(UUID userId, String email) {
        Key key = getSigningKey();
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_MINUTES * 60 * 1000);

        return Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("purpose", PURPOSE)
            .issuedAt(now)
            .expiration(expiration)
            .signWith(key)
            .compact();
    }

    public String generateVerificationLink(UUID userId, String email) {
        String token = generateVerificationToken(userId, email);
        return frontendUrl + "/verify-email?token=" + token;
    }

    public VerificationResult verifyToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

            String purpose = claims.get("purpose", String.class);
            if (!PURPOSE.equals(purpose)) {
                return VerificationResult.invalid("Token invalide");
            }

            UUID userId = UUID.fromString(claims.getSubject());
            String email = claims.get("email", String.class);

            return VerificationResult.valid(userId, email);

        } catch (ExpiredJwtException e) {
            log.warn("Email verification token expired");
            return VerificationResult.expired("Ce lien de verification a expire");
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            log.warn("Invalid email verification token: {}", e.getMessage());
            return VerificationResult.invalid("Token invalide");
        }
    }

    private Key getSigningKey() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            jwtSecret = "ng-stars-default-secret-key-for-email-verification-2024";
        }
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(jwtSecret);
        } catch (IllegalArgumentException e) {
            keyBytes = jwtSecret.getBytes();
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public record VerificationResult(boolean valid, boolean expired, UUID userId, String email, String errorMessage) {
        public static VerificationResult valid(UUID userId, String email) {
            return new VerificationResult(true, false, userId, email, null);
        }

        public static VerificationResult expired(String message) {
            return new VerificationResult(false, true, null, null, message);
        }

        public static VerificationResult invalid(String message) {
            return new VerificationResult(false, false, null, null, message);
        }
    }
}
