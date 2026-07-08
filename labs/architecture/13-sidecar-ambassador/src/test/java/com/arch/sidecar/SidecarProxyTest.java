package com.arch.sidecar;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;

class SidecarProxyTest {
    @Test
    void shouldForwardRequestToService() {
        ServiceRegistry registry = new ServiceRegistry();
        MetricCollector metrics = new MetricCollector();
        SidecarProxy proxy = new SidecarProxy(registry, metrics);
        String response = proxy.forwardRequest("payment", "{\"amount\":100}");
        assertTrue(response.contains("Response from"));
    }

    @Test
    void shouldCheckServiceHealth() {
        ServiceRegistry registry = new ServiceRegistry();
        MetricCollector metrics = new MetricCollector();
        SidecarProxy proxy = new SidecarProxy(registry, metrics);
        assertTrue(proxy.healthCheck("orders"));
    }

    @Test
    void shouldCollectMetrics() {
        ServiceRegistry registry = new ServiceRegistry();
        MetricCollector metrics = new MetricCollector();
        SidecarProxy proxy = new SidecarProxy(registry, metrics);
        proxy.forwardRequest("payment", "test");
        assertFalse(proxy.getMetrics().isEmpty());
    }

    @Test
    void shouldThrowOnUnknownService() {
        ServiceRegistry registry = new ServiceRegistry();
        MetricCollector metrics = new MetricCollector();
        SidecarProxy proxy = new SidecarProxy(registry, metrics);
        assertThrows(ProxyException.class, () -> proxy.forwardRequest("unknown", "test"));
    }
}

class AmbassadorProxyTest {
    @Test
    void shouldCheckHealth() {
        AmbassadorProxy proxy = new AmbassadorProxy("localhost", 9999, Duration.ofSeconds(1));
        String health = proxy.getHealth();
        assertNotNull(health);
    }
}

class ServiceAdapterTest {
    @Test
    void shouldUseLegacyByDefault() {
        ServiceAdapter<String, String> adapter = new ServiceAdapter<>(s -> "legacy:" + s, s -> "new:" + s);
        assertEquals("legacy:test", adapter.execute("test"));
    }

    @Test
    void shouldSwitchToNew() {
        ServiceAdapter<String, String> adapter = new ServiceAdapter<>(s -> "legacy:" + s, s -> "new:" + s);
        adapter.switchToNew();
        assertEquals("new:test", adapter.execute("test"));
    }
}
