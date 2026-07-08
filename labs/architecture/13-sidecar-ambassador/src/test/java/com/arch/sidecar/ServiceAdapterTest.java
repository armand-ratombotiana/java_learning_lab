package com.arch.sidecar;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ServiceAdapterUnitTest {
    @Test
    void shouldExecuteWithLegacyByDefault() {
        ServiceAdapter<String, String> adapter = new ServiceAdapter<>(s -> "legacy", s -> "new");
        assertEquals("legacy", adapter.execute("test"));
    }

    @Test
    void shouldSwitchToNewAndBack() {
        ServiceAdapter<String, String> adapter = new ServiceAdapter<>(s -> "legacy", s -> "new");
        adapter.switchToNew();
        assertTrue(adapter.isUsingNew());
        adapter.switchToLegacy();
        assertFalse(adapter.isUsingNew());
    }
}
