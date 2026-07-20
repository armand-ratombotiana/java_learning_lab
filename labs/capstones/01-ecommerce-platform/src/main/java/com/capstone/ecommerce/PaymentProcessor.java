package com.capstone.ecommerce;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentProcessor {
    private final Map<String, Payment> payments = new ConcurrentHashMap<>();
    private final Map<String, String> idempotencyKeys = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(0);

    public enum PaymentStatus { PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED }

    public enum PaymentMethod { CREDIT_CARD, DEBIT_CARD, PAYPAL, STRIPE, BANK_TRANSFER }

    public record Payment(String paymentId, String orderId, BigDecimal amount, PaymentMethod method,
                          PaymentStatus status, String idempotencyKey, Instant createdAt, Instant updatedAt) {
        public Payment {
            if (paymentId == null || paymentId.isBlank()) throw new IllegalArgumentException("Payment ID required");
        }
    }

    private static final Set<PaymentMethod> SUPPORTED_METHODS = Set.of(PaymentMethod.values());

    public Payment createPayment(String orderId, BigDecimal amount, PaymentMethod method, String idempotencyKey) {
        if (idempotencyKey != null && idempotencyKeys.containsKey(idempotencyKey)) {
            String existingId = idempotencyKeys.get(idempotencyKey);
            return payments.get(existingId);
        }
        if (!SUPPORTED_METHODS.contains(method)) {
            throw new IllegalArgumentException("Unsupported payment method: " + method);
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        String paymentId = "PAY-" + idGen.incrementAndGet();
        Instant now = Instant.now();
        Payment payment = new Payment(paymentId, orderId, amount, method, PaymentStatus.PENDING, idempotencyKey, now, now);
        payments.put(paymentId, payment);
        if (idempotencyKey != null) idempotencyKeys.put(idempotencyKey, paymentId);
        return payment;
    }

    public Payment processPayment(String paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null) throw new IllegalArgumentException("Payment not found: " + paymentId);
        if (payment.status() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment already " + payment.status());
        }
        Payment processing = new Payment(paymentId, payment.orderId(), payment.amount(), payment.method(),
            PaymentStatus.PROCESSING, payment.idempotencyKey(), payment.createdAt(), Instant.now());
        payments.put(paymentId, processing);
        boolean success = simulateGatewayCall(payment);
        Payment updated = new Payment(paymentId, payment.orderId(), payment.amount(), payment.method(),
            success ? PaymentStatus.COMPLETED : PaymentStatus.FAILED,
            payment.idempotencyKey(), payment.createdAt(), Instant.now());
        payments.put(paymentId, updated);
        return updated;
    }

    public Payment refundPayment(String paymentId) {
        Payment payment = payments.get(paymentId);
        if (payment == null) throw new IllegalArgumentException("Payment not found: " + paymentId);
        if (payment.status() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot refund payment with status: " + payment.status());
        }
        Payment refunded = new Payment(paymentId, payment.orderId(), payment.amount(), payment.method(),
            PaymentStatus.REFUNDED, payment.idempotencyKey(), payment.createdAt(), Instant.now());
        payments.put(paymentId, refunded);
        return refunded;
    }

    public Optional<Payment> getPayment(String paymentId) {
        return Optional.ofNullable(payments.get(paymentId));
    }

    public boolean hasIdempotencyKey(String key) {
        return idempotencyKeys.containsKey(key);
    }

    public int size() { return payments.size(); }

    public void reset() {
        payments.clear();
        idempotencyKeys.clear();
        idGen.set(0);
    }

    private boolean simulateGatewayCall(Payment payment) {
        return Math.random() > 0.05;
    }
}
