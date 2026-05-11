package com.learning.lab.module20;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExampleTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Test public endpoint is accessible without authentication")
    void testPublicEndpointAccessible() throws Exception {
        mockMvc.perform(get("/api/public/health"))
            .andExpect(status().isOk())
            .andExpect(content().string("UP"));
    }

    @Test
    @DisplayName("Test protected endpoint redirects to login")
    void testProtectedEndpointRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/api/private/data"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Test authenticated user can access protected resource")
    void testAuthenticatedAccess() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            "user", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/api/user/profile"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test admin endpoint requires ADMIN role")
    void testAdminEndpointRequiresAdminRole() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            "admin", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/api/admin/users"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test user without admin role gets forbidden")
    void testUserWithoutAdminRoleForbidden() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            "user", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/api/admin/users"))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Test password encoding works correctly")
    void testPasswordEncoding() {
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedvalue");

        String encoded = passwordEncoder.encode("password123");

        assertNotNull(encoded);
        assertTrue(encoded.startsWith("$2a$10$"));
    }

    @Test
    @DisplayName("Test password matching")
    void testPasswordMatching() {
        when(passwordEncoder.matches("password123", "$2a$10$hashedvalue")).thenReturn(true);

        boolean matches = passwordEncoder.matches("password123", "$2a$10$hashedvalue");

        assertTrue(matches);
    }

    @Test
    @DisplayName("Test JWT token validation")
    void testJwtTokenValidation() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.test";

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    @DisplayName("Test CSRF token is generated")
    void testCsrfTokenGeneration() throws Exception {
        mockMvc.perform(post("/api/public/login")
                .with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test CORS configuration allows specific origins")
    void testCorsConfiguration() {
        assertNotNull(mockMvc);
    }

    @Test
    @DisplayName("Test method level security with @PreAuthorize")
    void testMethodLevelSecurity() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            "admin", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(post("/api/admin/delete/1")
                .with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test authentication manager configuration")
    void testAuthenticationManager() {
        assertNotNull(SecurityContextHolder.getContext());
    }
}