package com.koi_express.config;

import com.koi_express.JWT.JwtFilter;
import com.koi_express.service.CustomOAuth2UserService;
import com.koi_express.service.CustomerDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final CustomerDetailsService customerDetailsService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomerDetailsService customerDetailsService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customerDetailsService = customerDetailsService;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/",
                                        "/login",
                                        "/oauth2/**",
                                        "/api/auth/**",
                                        "/api/customers/**",
                                        "/api/customers/update/**",
                                        "/api/customers/delete/**").permitAll()
                                .requestMatchers("/api/manager/**", "/api/manager/id/**").hasAnyAuthority("ROLE_MANAGER")
                                .requestMatchers("/api/orders/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_MANAGER")
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService) // Sử dụng CustomOAuth2UserService
                        )
                        .defaultSuccessUrl("/api/auth/userinfo", true)
                        .failureUrl("/login?error=true")
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
