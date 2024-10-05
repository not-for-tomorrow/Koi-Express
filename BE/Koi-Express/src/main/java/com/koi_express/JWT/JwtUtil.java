package com.koi_express.JWT;

import com.koi_express.entity.customer.Customers;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    public String generateToken(String phoneNumber, String projectName, String role, String customerId, String fullName, String email) {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("Application", projectName);
        claims.put("fullName", fullName);
        claims.put("email", email);
        claims.put("customerId", customerId);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(phoneNumber)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // Token có thời hạn 10 giờ
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)  // Sử dụng thuật toán HS256
                .compact();
    }

    public String generateTokenOAuth2(Customers customer) {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("application", "Koi Express");
        claims.put("fullName", customer.getFullName()); // Correct full name here
        claims.put("email", customer.getEmail()); // Correct email here
        claims.put("customerId", customer.getCustomerId()); // Use customer's unique ID
        claims.put("role", customer.getRole().name()); // Role, e.g., CUSTOMER
        claims.put("authProvider", customer.getAuthProvider().name()); // Auth provider, e.g., GOOGLE

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(customer.getProviderId() != null ? customer.getProviderId() : customer.getEmail()) // Set subject based on unique identifier
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Token valid for 10 hours
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractPhoneNumber(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String extractCustomerId(String token)  {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return (String) claims.get("customerId");
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
