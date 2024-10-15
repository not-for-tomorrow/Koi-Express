package com.koi_express.JWT;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import com.koi_express.entity.customer.Customers;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    public String generateToken(
            String phoneNumber, String projectName, String role, String userId, String fullName, String email) {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("Application", projectName);
        claims.put("fullName", fullName);
        claims.put("email", email);
        switch (role) {
            case "CUSTOMER":
                claims.put("customerId", userId);
                break;
            case "SALES_STAFF":
            case "MANAGER":
                claims.put("accountId", userId);
                break;
            case "DELIVERING_STAFF":
                claims.put("staffId", userId);
                break;
            default:
                throw new IllegalArgumentException("Invalid role specified");
        }

        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(phoneNumber)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String generateTokenOAuth2(Customers customer) {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("application", "Koi Express");
        claims.put("fullName", customer.getFullName());
        claims.put("email", customer.getEmail());
        claims.put("customerId", customer.getCustomerId());
        claims.put("role", customer.getRole().name());
        claims.put("authProvider", customer.getAuthProvider().name());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(customer.getProviderId() != null ? customer.getProviderId() : customer.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractPhoneNumber(String token) {
        Claims claims =
                Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public String extractUserId(String token, String role) {
        Claims claims = extractAllClaims(token);
        switch (role) {
            case "CUSTOMER":
                return (String) claims.get("customerId");
            case "SALES_STAFF":
            case "MANAGER":
                return (String) claims.get("accountId");
            case "DELIVERING_STAFF":
                return (String) claims.get("staffId");
            default:
                throw new IllegalArgumentException("Invalid role specified");
        }
    }

    public String extractCustomerId(String token) {
        try {
            // Sanitize the token by trimming whitespace
            String sanitizedToken = sanitizeToken(token);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(sanitizedToken)
                    .getBody();
            return claims.get("customerId", String.class);
        } catch (JwtException e) {
            logger.error("Error parsing JWT: ", e);
            throw new AppException(ErrorCode.JWT_PARSING_FAILED, "Failed to parse JWT token");
        }
    }

    private String sanitizeToken(String token) {
        return token.trim().replaceAll("[\\r\\n\\s]", "");
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String phoneNumber = extractPhoneNumber(token);
        return (phoneNumber.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get("role");
    }
}
