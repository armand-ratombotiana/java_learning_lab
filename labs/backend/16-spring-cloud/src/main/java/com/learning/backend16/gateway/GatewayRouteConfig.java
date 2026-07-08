package com.learning.backend16.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRouteConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("product-service", r -> r
                .path("/api/products/**")
                .filters(f -> f
                    .circuitBreaker(config -> config
                        .setName("productServiceCB")
                        .setFallbackUri("forward:/fallback/products"))
                    .retry(3)
                    .addRequestHeader("X-Gateway-Source", "api-gateway"))
                .uri("lb://PRODUCT-SERVICE"))
            .route("order-service", r -> r
                .path("/api/orders/**")
                .filters(f -> f
                    .circuitBreaker(config -> config
                        .setName("orderServiceCB")
                        .setFallbackUri("forward:/fallback/orders")))
                .uri("lb://ORDER-SERVICE"))
            .route("discovery-service", r -> r
                .path("/discovery/**")
                .filters(f -> f
                    .rewritePath("/discovery/(?<segment>.*)", "/${segment}")
                    .setPath("/eureka/${segment}"))
                .uri("lb://EURKEA-SERVER"))
            .build();
    }
}
