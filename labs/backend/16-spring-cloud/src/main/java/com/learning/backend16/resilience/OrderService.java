package com.learning.backend16.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public OrderService(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @PostConstruct
    public void monitorCircuitBreakers() {
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(cb -> {
            cb.getEventPublisher()
                .onStateTransition(event ->
                    log.info("CircuitBreaker '{}' state changed: {} -> {}",
                        event.getCircuitBreakerName(),
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()))
                .onCallNotPermitted(event ->
                    log.warn("CircuitBreaker '{}' blocked a call",
                        event.getCircuitBreakerName()))
                .onError(event ->
                    log.error("CircuitBreaker '{}' recorded error: {}",
                        event.getCircuitBreakerName(),
                        event.getThrowable().getMessage()));
        });
    }
}
