package com.bank.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("account-service", r -> r.path("/api/v1/accounts/**")
                .filters(f -> f.stripPrefix(0).addRequestHeader("X-Gateway", "Banking"))
                .uri("http://localhost:8081"))
            .route("payment-service", r -> r.path("/api/v1/transfers/**")
                .filters(f -> f.stripPrefix(0))
                .uri("http://localhost:8082"))
            .route("user-service", r -> r.path("/api/v1/users/**")
                .filters(f -> f.stripPrefix(0))
                .uri("http://localhost:8083"))
            .build();
    }
}