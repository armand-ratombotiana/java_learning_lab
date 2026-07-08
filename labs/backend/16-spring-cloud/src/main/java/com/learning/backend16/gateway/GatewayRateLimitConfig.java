package com.learning.backend16.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

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
            return Mono.just(exchange.getRequest()
                .getRemoteAddress()
                .map(addr -> addr.getAddress().getHostAddress())
                .orElse("anonymous"));
        };
    }
}
