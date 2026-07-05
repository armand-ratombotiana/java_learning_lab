package com.learning.backend06;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration using the new (Spring Boot 3.x) lambda DSL.
 *
 * @EnableWebSecurity activates the web security configuration.
 * SecurityFilterChain replaces the old WebSecurityConfigurerAdapter approach.
 * BCryptPasswordEncoder is a strong, adaptive hash function for passwords.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * Defines the security filter chain.
     *
     * Security rules:
     * - /api/public/** is accessible without authentication
     * - /api/admin/** requires ADMIN role
     * - Everything else requires authentication
     * - Stateless session management (no HTTP session, each request is independently authenticated)
     * - HTTP Basic authentication (simplest for demo; use JWT/OAuth2 in production)
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter chain");

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .httpBasic(httpBasic -> {})
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    /**
     * BCryptPasswordEncoder uses the BCrypt strong hashing function.
     * The "strength" parameter (default 10) controls the work factor.
     * Higher values are slower but more resistant to brute-force.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("Creating BCryptPasswordEncoder with strength=10");
        return new BCryptPasswordEncoder(10);
    }
}
