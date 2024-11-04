package com.koi_express.jwt;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import com.koi_express.entity.customer.Customers;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    // Define constants to avoid hardcoding literals
    private static final String CLAIM_KEY_CUSTOMER_ID = "customerId";
    private static final String CLAIM_KEY_ACCOUNT_ID = "accountId";
    private static final String CLAIM_KEY_STAFF_ID = "staffId";
    private static final String CLAIM_KEY_ROLE = "role";
    private static final String CLAIM_KEY_APPLICATION = "Application";
    private static final String CLAIM_KEY_FULL_NAME = "fullName";
    private static final String CLAIM_KEY_EMAIL = "email";
    private static final long TOKEN_VALIDITY = 1000L * 60 * 60 * 10; // Token validity of 10 hours

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(
            String phoneNumber, String projectName, String role, String userId, String fullName, String email) {
        if (!isValidRole(role)) throw new AppException(ErrorCode.INVALID_ROLE, "Invalid role specified");

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put(CLAIM_KEY_APPLICATION, projectName);
        claims.put(CLAIM_KEY_FULL_NAME, fullName);
        claims.put(CLAIM_KEY_EMAIL, email);
        claims.put(CLAIM_KEY_ROLE, role);
        claims.put(getRoleIdKey(role), userId);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(phoneNumber)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(getSigningKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateTokenOAuth2(Customers customer) {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put(CLAIM_KEY_APPLICATION, "Koi Express");
        claims.put(CLAIM_KEY_FULL_NAME, customer.getFullName());
        claims.put(CLAIM_KEY_EMAIL, customer.getEmail());
        claims.put(CLAIM_KEY_CUSTOMER_ID, customer.getCustomerId());
        claims.put(CLAIM_KEY_ROLE, customer.getRole().name());
        claims.put("authProvider", customer.getAuthProvider().name());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(customer.getProviderId() != null ? customer.getProviderId() : customer.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(getSigningKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractPhoneNumber(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public String extractUserId(String token, String role) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get(getRoleIdKey(role));
    }

    public String extractCustomerId(String token) {
        try {
            String cleanedToken = cleanToken(token);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(cleanedToken)
                    .getBody();
            return claims.get(CLAIM_KEY_CUSTOMER_ID, String.class);
        } catch (JwtException e) {
            logger.error("Error parsing JWT: ", e);
            throw new AppException(ErrorCode.JWT_PARSING_FAILED, "Failed to parse JWT token");
        }
    }

    public String sanitizeToken(String token) {
        return token.trim().replaceAll("\\s", "");

    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // Ensure the token is sanitized before parsing
        String sanitizedToken = sanitizeToken(token);

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(sanitizedToken)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = extractExpiration(token);
        return expiration != null && expiration.before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String phoneNumber = extractPhoneNumber(token);
        return (phoneNumber.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get(CLAIM_KEY_ROLE);
    }

    public String cleanToken(String token) {
        return token != null ? token.replace("Bearer", "").trim() : null;
    }

    private boolean isValidRole(String role) {
        return role.equals("CUSTOMER") || role.equals("SALES_STAFF") || role.equals("MANAGER") || role.equals("DELIVERING_STAFF");
    }

    private String getRoleIdKey(String role) {
        return switch (role) {
            case "CUSTOMER" -> CLAIM_KEY_CUSTOMER_ID;
            case "SALES_STAFF", "MANAGER" -> CLAIM_KEY_ACCOUNT_ID;
            case "DELIVERING_STAFF" -> CLAIM_KEY_STAFF_ID;
            default -> throw new AppException(ErrorCode.INVALID_ROLE, "Invalid role specified");
        };
    }

}
