package com.learning.backend16.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class GatewayRouteConfigTest {

    @Test
    void routesAreConfigured() {
        var config = new GatewayRouteConfig();
        try (var builder = new RouteLocatorBuilder()) {
            RouteLocator locator = config.customRoutes(builder);
            assertNotNull(locator);
        }
    }
}
