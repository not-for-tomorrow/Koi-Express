package com.koi_express.jwt;

import java.io.IOException;
import java.util.List;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        String path = request.getRequestURI();

        if (path.equals("/api/auth/forgot-password") || path.equals("/api/auth/reset-password")) {
            filterChain.doFilter(request, response);
            return;
        }

        String phoneNumber = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            jwt = jwtUtil.sanitizeToken(jwt);
            try {
                phoneNumber = jwtUtil.extractPhoneNumber(jwt);
            } catch (ExpiredJwtException e) {
                handleException(response, "Token expired");
                return;
            } catch (Exception e) {
                handleException(response, "Invalid token");
                return;
            }
        }

        if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            validateAndAuthenticateToken(request, jwt, phoneNumber);
        }

        logger.info("Authorization Header: " + authorizationHeader);
        logger.info("Extracted phone number: " + phoneNumber);
        logger.info("Extracted JWT: " + jwt);
        filterChain.doFilter(request, response);
    }

    private void validateAndAuthenticateToken(HttpServletRequest request, String jwt, String phoneNumber) {
        logger.info("Validating token for phone number: {}");

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(phoneNumber);

        if (jwtUtil.validateToken(jwt, userDetails)) {
            String role = jwtUtil.extractRole(jwt);
            if (role == null || role.isEmpty()) {
                logger.error("Invalid role in token for phone number: {}");
                return;
            }

            List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_" + role);
            logger.info("Extracted role: {}");

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            logger.info("Authentication successful for phone number: {}");
        } else {
            logger.warn("Invalid token for phone number: {}");
            SecurityContextHolder.clearContext();
        }
    }

    private void handleException(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
