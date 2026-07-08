package com.arch.servicemesh;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;

class ControlPlaneTest {
    private ControlPlane controlPlane;

    @BeforeEach
    void setUp() {
        controlPlane = new ControlPlane();
        controlPlane.registerService("payment", "v1", List.of("10.0.0.1:8080", "10.0.0.2:8080"));
    }

    @Test
    void shouldRegisterAndRetrieveService() {
        ServiceConfig config = controlPlane.getServiceConfig("payment");
        assertEquals("payment", config.getName());
        assertEquals(2, config.getEndpoints().size());
    }

    @Test
    void shouldIssueCertificate() {
        String cert = controlPlane.issueCertificate("payment");
        assertNotNull(cert);
        assertTrue(controlPlane.verifyCertificate("payment", cert));
    }

    @Test
    void shouldAddRoutingRules() {
        controlPlane.addRoutingRule("payment", "header(canary)=true -> payment-v2");
        assertEquals(1, controlPlane.getRoutingRules("payment").size());
    }
}

class DataPlaneProxyTest {
    @Test
    void shouldRouteRequest() {
        ControlPlane cp = new ControlPlane();
        cp.registerService("orders", "v1", List.of("10.0.0.1:8081"));
        DataPlaneProxy proxy = new DataPlaneProxy(cp);
        ProxyResponse response = proxy.routeRequest("web", "orders", "test");
        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldTrackErrorRates() {
        ControlPlane cp = new ControlPlane();
        DataPlaneProxy proxy = new DataPlaneProxy(cp);
        proxy.routeRequest("web", "unknown", "test");
        assertTrue(proxy.getErrorRate("unknown") > 0);
    }
}

class TrafficRouterTest {
    @Test
    void shouldRouteBasedOnWeight() {
        TrafficRouter router = new TrafficRouter();
        router.addRoute("svc", new RouteTarget("v1", 100.0));
        RouteTarget target = router.selectTarget("svc", Map.of());
        assertEquals("v1", target.version());
    }
}
