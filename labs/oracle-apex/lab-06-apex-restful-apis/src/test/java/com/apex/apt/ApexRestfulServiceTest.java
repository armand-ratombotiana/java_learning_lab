package com.apex.apt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class ApexRestfulServiceTest {
    private ApexRestfulService svc;
    @BeforeEach void setUp() { svc = ApexRestfulService.createSample(); }

    @Test void shouldHandleGet() {
        var resp = svc.handleRequest("employees", Map.of("id", "100"), Map.of(), null);
        assertEquals(200, resp.status());
        assertTrue(resp.body().contains("employees"));
    }

    @Test void shouldHandlePost() {
        var resp = svc.handleRequest("employees_create", Map.of(), Map.of(), "{\"name\":\"Test\"}");
        assertEquals(201, resp.status());
    }

    @Test void shouldReturn404() {
        var resp = svc.handleRequest("unknown", Map.of(), Map.of(), null);
        assertEquals(404, resp.status());
    }

    @Test void shouldHaveEtag() {
        var resp = svc.handleRequest("employees", Map.of("id", "1"), Map.of(), null);
        assertTrue(resp.headers().containsKey("ETag"));
    }

    @Test void shouldLogRequests() {
        svc.handleRequest("employees", Map.of("id", "1"), Map.of(), null);
        assertEquals(1, svc.getRequestLog().size());
    }
}