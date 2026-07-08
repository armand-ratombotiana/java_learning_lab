package com.arch.stranglerfig;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LegacySystemTest {
    @Test
    void shouldReturnExistingUser() {
        LegacySystem system = new LegacySystem();
        String user = system.getUser("user_1");
        assertTrue(user.contains("Alice"));
    }

    @Test
    void shouldReturnEmptyForMissingUser() {
        LegacySystem system = new LegacySystem();
        assertEquals("{}", system.getUser("nonexistent"));
    }

    @Test
    void shouldCreateNewUser() {
        LegacySystem system = new LegacySystem();
        system.createUser("new_user", "{\"name\":\"New\"}");
        assertEquals(3, system.getUserCount());
    }

    @Test
    void shouldReportHealthStatus() {
        LegacySystem system = new LegacySystem();
        assertTrue(system.isHealthy());
    }
}
