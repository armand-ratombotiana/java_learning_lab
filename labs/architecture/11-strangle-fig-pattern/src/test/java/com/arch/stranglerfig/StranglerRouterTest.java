package com.arch.stranglerfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StranglerRouterTest {
    private LegacySystem legacy;
    private NewSystemModule newSystem;
    private StranglerRouter router;

    @BeforeEach
    void setUp() {
        legacy = new LegacySystem();
        newSystem = new NewSystemModule();
        router = new StranglerRouter(legacy, newSystem, userId -> userId.startsWith("new_"));
    }

    @Test
    void shouldRouteMigratedUsersToNewSystem() {
        String result = router.getUser("user_1");
        assertTrue(result.contains("alice@new.com"));
    }

    @Test
    void shouldRouteNonMigratedUsersToLegacy() {
        String result = router.getUser("user_2");
        assertTrue(result.contains("bob@legacy.com"));
    }

    @Test
    void shouldFallbackToLegacyWhenUserNotFoundInNew() {
        String result = router.getUser("nonexistent");
        assertEquals("{}", result);
    }

    @Test
    void shouldCreateUserInCorrectSystem() {
        router.createUser("new_user_1", "{\"name\":\"Test\"}");
        assertTrue(newSystem.getUser("new_user_1").contains("Test"));
    }

    @Test
    void shouldDeleteUserFromCorrectSystem() {
        router.deleteUser("user_1");
        assertEquals("{}", newSystem.getUser("user_1"));
    }
}
