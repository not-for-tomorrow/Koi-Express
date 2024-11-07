package com.koi_express.config;

import com.koi_express.jwt.JwtFilter;
import com.koi_express.service.customer.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(
            CustomOAuth2UserService customOAuth2UserService, JwtFilter jwtFilter) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(
                                "/",
                                "/login",
                                "/oauth2/**",
                                "/api/auth/**",
                                "/api/customers/**",
                                "/api/customers/update/**",
                                "/api/customers/delete/**",
                                "api/v1/payment/**",
                                "api/orders/{orderId}",
                                "/ws/**")
                        .permitAll()
                        .requestMatchers("/api/manager/**", "/api/manager/id/**")
                        .hasAnyAuthority("ROLE_MANAGER")
                        .requestMatchers("/api/orders/**")
                        .hasAnyAuthority("ROLE_CUSTOMER", "ROLE_MANAGER")
                        .anyRequest()
                        .authenticated())
                .oauth2Login(oauth2 -> oauth2.userInfoEndpoint(
                                userInfo ->
                                        userInfo.userService(customOAuth2UserService) // Sử dụng CustomOAuth2UserService
                                )
                        .defaultSuccessUrl("http://localhost:5173/appkoiexpress", true)
                        .failureUrl("/login?error=true"))
                .logout(logout -> logout.logoutSuccessUrl("/login"))
                .exceptionHandling(
                        exception -> exception.authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        }))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:5173");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
