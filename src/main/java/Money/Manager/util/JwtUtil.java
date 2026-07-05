package Money.Manager.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Generate Token
    public String generateToken(String email) {

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    // Extract Username
    public String extractUsername(String token) {

        return extractClaims(token).getSubject();
    }

    // Extract Expiration Date
    public Date extractExpiration(String token) {

        return extractClaims(token).getExpiration();
    }

    // Extract Claims
    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Check Expiration
    public boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }

    // Validate Token
    public boolean validateToken(String token, String email) {

        String username = extractUsername(token);

        return username.equals(email)
                && !isTokenExpired(token);
    }
}