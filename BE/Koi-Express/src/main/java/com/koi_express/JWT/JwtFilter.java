package com.koi_express.JWT;

import java.io.IOException;
import java.util.List;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String phoneNumber = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
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
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(phoneNumber);

        if (jwtUtil.validateToken(jwt, userDetails)) {

            String role = jwtUtil.extractRole(jwt);

            List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_" + role);
            logger.info("Extracted role: " + role);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            logger.info("Authentication in SecurityContext: "
                    + SecurityContextHolder.getContext().getAuthentication());
        }
    }

    private void handleException(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
