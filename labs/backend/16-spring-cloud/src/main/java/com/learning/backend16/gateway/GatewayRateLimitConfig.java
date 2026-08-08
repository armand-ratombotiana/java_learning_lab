package com.learning.backend16.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Configuration
public class GatewayRateLimitConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String apiKey = exchange.getRequest().getHeaders()
                .getFirst("X-API-Key");
            if (apiKey != null) {
                return Mono.just(apiKey);
            }
            return Mono.just(Optional.ofNullable(exchange.getRequest()
                .getRemoteAddress())
                .map(addr -> addr.getAddress().getHostAddress())
                .orElse("anonymous"));
        };
    }
}
