package com.arch.apicomposition;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class ApiComposerTest {
    @Test
    void shouldComposeMultipleServices() {
        ApiComposer composer = new ApiComposer();
        composer.registerService("users", userId -> new ServiceResponse("users", "{\"name\":\"Alice\"}", null));
        composer.registerService("orders", userId -> new ServiceResponse("orders", "{\"total\":100}", null));
        ComposedResponse response = composer.compose("user1", List.of("users", "orders"));
        assertEquals(2, response.getResponses().size());
    }

    @Test
    void shouldHandleServiceErrors() {
        ApiComposer composer = new ApiComposer();
        composer.registerService("failing", userId -> { throw new RuntimeException("error"); });
        ComposedResponse response = composer.compose("user1", List.of("failing"));
        assertTrue(response.hasErrors());
    }
}

class CompositeServiceTest {
    @Test
    void shouldFetchMultipleDataSources() {
        CompositeService cs = new CompositeService();
        cs.registerFetcher("profile", id -> Map.of("name", "Alice"));
        cs.registerFetcher("settings", id -> Map.of("theme", "dark"));
        CompositeResponse response = cs.fetchAll("user1", "profile", "settings");
        assertNotNull(response.getData("profile"));
        assertNotNull(response.getData("settings"));
    }
}
