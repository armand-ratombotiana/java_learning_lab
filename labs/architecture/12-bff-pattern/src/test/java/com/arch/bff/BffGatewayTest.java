package com.arch.bff;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

class BffGatewayTest {
    @Test
    void mobileBffShouldReturnCompactDashboard() {
        MobileBff bff = new MobileBff("user_1");
        Map<String, Object> dashboard = bff.getDashboard();
        assertTrue(dashboard.containsKey("summary"));
        assertTrue(dashboard.containsKey("quick_actions"));
    }

    @Test
    void webBffShouldReturnFullDashboard() {
        WebBff bff = new WebBff("user_2");
        Map<String, Object> dashboard = bff.getDashboard();
        assertTrue(dashboard.containsKey("full_summary"));
        assertTrue(dashboard.containsKey("analytics"));
    }

    @Test
    void mobileBffProfileShouldBeCompact() {
        MobileBff bff = new MobileBff("user_1");
        Map<String, Object> profile = bff.getUserProfile("test_user");
        assertFalse(profile.containsKey("email"));
        assertFalse(profile.containsKey("address"));
    }

    @Test
    void webBffProfileShouldBeComplete() {
        WebBff bff = new WebBff("user_2");
        Map<String, Object> profile = bff.getUserProfile("test_user");
        assertTrue(profile.containsKey("email"));
        assertTrue(profile.containsKey("address"));
        assertTrue(profile.containsKey("payment_methods"));
    }

    @Test
    void mobileBffShouldRejectLargeOrders() {
        MobileBff bff = new MobileBff("user_1");
        assertThrows(IllegalArgumentException.class, () -> bff.submitOrder("prod_1", 15));
    }

    @Test
    void webBffShouldAcceptLargeOrders() {
        WebBff bff = new WebBff("user_2");
        assertTrue(bff.submitOrder("prod_1", 50));
    }

    @Test
    void shouldRejectInvalidProductId() {
        MobileBff bff = new MobileBff("user_1");
        assertThrows(IllegalArgumentException.class, () -> bff.submitOrder("", 1));
    }

    @Test
    void mobileClientTypeShouldBeCorrect() {
        MobileBff bff = new MobileBff("user_1");
        assertEquals("mobile", bff.getClientType());
    }

    @Test
    void webClientTypeShouldBeCorrect() {
        WebBff bff = new WebBff("user_2");
        assertEquals("web", bff.getClientType());
    }
}
