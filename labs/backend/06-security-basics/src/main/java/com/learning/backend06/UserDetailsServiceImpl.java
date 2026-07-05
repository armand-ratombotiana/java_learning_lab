package com.learning.backend06;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Provides user details for authentication.
 *
 * In production, this would load users from a database. Here we use
 * an in-memory store for demonstration purposes.
 *
 * UserDetailsService is the core interface that loads user-specific data.
 * Spring Security uses it during authentication.
 */
@Configuration
public class UserDetailsServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        log.info("Creating in-memory user details service");

        // Password: "password" encoded with BCrypt
        String encodedPassword = encoder.encode("password");

        UserDetails user = User.builder()
            .username("user")
            .password(encodedPassword)
            .roles("USER")
            .build();

        UserDetails admin = User.builder()
            .username("admin")
            .password(encodedPassword)
            .roles("USER", "ADMIN")
            .build();

        log.info("Created users: user (role=USER), admin (roles=USER,ADMIN)");
        return new InMemoryUserDetailsManager(user, admin);
    }
}
