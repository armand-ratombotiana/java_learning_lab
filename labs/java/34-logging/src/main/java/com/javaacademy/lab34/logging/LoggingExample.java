package com.javaacademy.lab34.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import java.util.UUID;

public class LoggingExample {

    private static final Logger log = LoggerFactory.getLogger(LoggingExample.class);

    public void processOrder(String orderId, String userId) {
        MDC.put("orderId", orderId);
        MDC.put("userId", userId);
        log.info("Processing order for user");
        try {
            validateOrder(orderId);
            log.debug("Order validated successfully");
            chargeUser(userId, orderId);
            log.info("Order {} completed successfully", orderId);
        } catch (Exception e) {
            log.error("Order processing failed for orderId={}", orderId, e);
        } finally {
            MDC.clear();
        }
    }

    private void validateOrder(String orderId) {
        log.trace("Validating order {}", orderId);
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Invalid order ID");
        }
    }

    private void chargeUser(String userId, String orderId) {
        log.debug("Charging user {} for order {}", userId, orderId);
        if (userId.startsWith("fail")) {
            throw new RuntimeException("Payment gateway timeout");
        }
    }

    public double calculateTotal(double price, int quantity) {
        log.debug("Calculating total: price={}, quantity={}", price, quantity);
        double total = price * quantity;
        double tax = total * 0.08;
        double finalTotal = total + tax;
        log.info("Total calculated: $" + String.format("%.2f", finalTotal) + " (tax: $" + String.format("%.2f", tax) + ")");
        return finalTotal;
    }

    public void simulateWorkflow() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        log.info("Starting workflow with traceId={}", traceId);
        log.warn("This is a warning message - rate limit approaching");
        log.error("This is a simulated error for demonstration");
        MDC.clear();
    }
}
