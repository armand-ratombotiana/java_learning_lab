package com.sd.apidesign;

import java.util.*;
import java.util.concurrent.*;

public class Idempotency {

    public static class IdempotencyStore {
        private final Map<String, CachedResponse> store = new ConcurrentHashMap<>();

        public static class CachedResponse {
            public final int statusCode;
            public final String body;
            public final long timestamp;

            public CachedResponse(int statusCode, String body) {
                this.statusCode = statusCode;
                this.body = body;
                this.timestamp = System.currentTimeMillis();
            }
        }

        public Optional<CachedResponse> get(String idempotencyKey) {
            CachedResponse resp = store.get(idempotencyKey);
            if (resp != null) {
                long age = System.currentTimeMillis() - resp.timestamp;
                if (age < 86_400_000) {
                    System.out.println("Idempotency HIT: " + idempotencyKey);
                    return Optional.of(resp);
                }
                store.remove(idempotencyKey);
            }
            return Optional.empty();
        }

        public void put(String idempotencyKey, int statusCode, String body) {
            store.put(idempotencyKey, new CachedResponse(statusCode, body));
            System.out.println("Idempotency stored: " + idempotencyKey);
        }
    }

    public static class PaymentService {
        private final IdempotencyStore idempotencyStore = new IdempotencyStore();
        private final Set<String> processedPayments = ConcurrentHashMap.newKeySet();

        public Map<String, Object> processPayment(String idempotencyKey, String amount, String currency) {
            Optional<IdempotencyStore.CachedResponse> cached = idempotencyStore.get(idempotencyKey);
            if (cached.isPresent()) {
                return Map.of("status", "duplicate", "result", cached.get().body);
            }

            if (processedPayments.contains(idempotencyKey)) {
                return Map.of("status", "duplicate");
            }

            String paymentId = "pay-" + UUID.randomUUID().toString().substring(0, 8);
            processedPayments.add(idempotencyKey);

            String result = "Payment " + paymentId + " of " + amount + " " + currency + " completed";
            idempotencyStore.put(idempotencyKey, 200, result);

            return Map.of("status", "success", "paymentId", paymentId, "result", result);
        }
    }

    public static void main(String[] args) {
        PaymentService service = new PaymentService();

        System.out.println("=== Idempotency ===");
        Map<String, Object> r1 = service.processPayment("idem-001", "100", "USD");
        System.out.println("First attempt: " + r1);

        Map<String, Object> r2 = service.processPayment("idem-001", "100", "USD");
        System.out.println("Duplicate attempt: " + r2);

        Map<String, Object> r3 = service.processPayment("idem-002", "200", "EUR");
        System.out.println("New payment: " + r3);
    }
}
