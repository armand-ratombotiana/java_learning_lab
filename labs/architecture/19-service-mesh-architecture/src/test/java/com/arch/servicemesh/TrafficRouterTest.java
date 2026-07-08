package com.arch.servicemesh;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

class TrafficRouterUnitTest {
    @Test
    void shouldSelectTargetByWeight() {
        TrafficRouter router = new TrafficRouter();
        router.addRoute("svc", new RouteTarget("v1", 100.0));
        router.addRoute("svc", new RouteTarget("v2", 0.0));
        RouteTarget target = router.selectTarget("svc", Map.of());
        assertEquals("v1", target.version());
    }

    @Test
    void shouldThrowForUnknownService() {
        TrafficRouter router = new TrafficRouter();
        assertThrows(IllegalArgumentException.class, () -> router.selectTarget("unknown", Map.of()));
    }
}
