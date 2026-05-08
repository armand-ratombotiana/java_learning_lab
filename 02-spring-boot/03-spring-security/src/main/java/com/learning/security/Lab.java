package com.learning.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@SpringBootApplication
public class Lab {
    public static void main(String[] args) {
        SpringApplication.run(Lab.class, args);
    }
}

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(c -> {})
            .csrf(c -> c.disable());
        return http.build();
    }

    @Bean
    public UserDetailsService users() {
        var admin = User.withDefaultPasswordEncoder()
            .username("admin").password("admin").roles("ADMIN").build();
        var user = User.withDefaultPasswordEncoder()
            .username("user").password("user").roles("USER").build();
        return new InMemoryUserDetailsManager(admin, user);
    }
}

@RestController
@RequestMapping("/api")
class SecureController {

    @GetMapping("/public/hello")
    public String publicHello() {
        return "This is public";
    }

    @GetMapping("/private/profile")
    public Map<String, String> profile(@RequestParam(defaultValue = "user") String username) {
        return Map.of("username", username, "role", "AUTHENTICATED");
    }

    @GetMapping("/admin/dashboard")
    public Map<String, Object> adminDashboard() {
        return Map.of(
            "message", "Admin access granted",
            "users", new String[]{"alice", "bob"},
            "stats", Map.of("total", 42, "active", 17)
        );
    }
}
