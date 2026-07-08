package com.arch.bff;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MobileBffTest {
    @Test
    void shouldCreateMobileBff() {
        MobileBff bff = new MobileBff("user1");
        assertEquals("user1", bff.getUserId());
    }

    @Test
    void mobileDashboardShouldBeCompact() {
        MobileBff bff = new MobileBff("user1");
        var dash = bff.getDashboard();
        assertTrue(dash.containsKey("summary"));
        assertTrue(dash.containsKey("notifications"));
    }

    @Test
    void mobileShouldRejectInvalidOrders() {
        MobileBff bff = new MobileBff("user1");
        assertThrows(IllegalArgumentException.class, () -> bff.submitOrder(null, 1));
    }
}
