package com.apex.apt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class ApexSecurityManagerTest {
    private ApexSecurityManager mgr;
    @BeforeEach void setUp() { mgr = ApexSecurityManager.createSample(); }

    @Test void shouldAuthenticate() {
        var result = mgr.authenticate("admin", "pwd");
        assertTrue(result.success());
        assertEquals("admin", result.userId());
    }

    @Test void shouldFailUnknownUser() {
        var result = mgr.authenticate("unknown", "pwd");
        assertFalse(result.success());
    }

    @Test void shouldAuthorizeAdmin() { assertTrue(mgr.authorize("admin", "ANYTHING")); }
    @Test void shouldAuthorizeEditor() { assertTrue(mgr.authorize("editor", "DATA_EDIT")); }
    @Test void shouldDenyViewer() { assertFalse(mgr.authorize("viewer", "DATA_EDIT")); }
    @Test void shouldGenerateCsrf() { assertNotNull(mgr.generateCsrfToken()); }

    @Test void shouldValidateCsrf() {
        var token = mgr.generateCsrfToken();
        assertTrue(mgr.validateCsrfToken(token, token));
        assertFalse(mgr.validateCsrfToken(token, "wrong"));
    }

    @Test void shouldSanitizeHtml() {
        var clean = mgr.sanitizeHtml("<script>alert('xss')</script>");
        assertFalse(clean.contains("<script>"));
        assertTrue(clean.contains("&lt;"));
    }
}