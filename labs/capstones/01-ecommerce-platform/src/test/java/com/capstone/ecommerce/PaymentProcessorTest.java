package com.capstone.ecommerce;

import org.junit.jupiter.api.*;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentProcessorTest {
    private PaymentProcessor processor;

    @BeforeEach
    void setUp() { processor = new PaymentProcessor(); }

    @Test void testCreatePayment() {
        var payment = processor.createPayment("ord-1", new BigDecimal("100.00"),
            PaymentProcessor.PaymentMethod.CREDIT_CARD, "idem-1");
        assertEquals(PaymentProcessor.PaymentStatus.PENDING, payment.status());
        assertEquals("ord-1", payment.orderId());
    }

    @Test void testProcessPayment() {
        var payment = processor.createPayment("ord-1", new BigDecimal("100.00"),
            PaymentProcessor.PaymentMethod.CREDIT_CARD, "idem-1");
        var processed = processor.processPayment(payment.paymentId());
        assertTrue(processed.status() == PaymentProcessor.PaymentStatus.COMPLETED ||
                   processed.status() == PaymentProcessor.PaymentStatus.FAILED);
    }

    @Test void testIdempotency() {
        var p1 = processor.createPayment("ord-1", new BigDecimal("100.00"),
            PaymentProcessor.PaymentMethod.CREDIT_CARD, "idem-1");
        var p2 = processor.createPayment("ord-1", new BigDecimal("100.00"),
            PaymentProcessor.PaymentMethod.CREDIT_CARD, "idem-1");
        assertEquals(p1.paymentId(), p2.paymentId());
    }

    @Test void testRefund() {
        var payment = processor.createPayment("ord-1", new BigDecimal("100.00"),
            PaymentProcessor.PaymentMethod.PAYPAL, "idem-1");
        var processed = processor.processPayment(payment.paymentId());
        if (processed.status() == PaymentProcessor.PaymentStatus.COMPLETED) {
            var refunded = processor.refundPayment(processed.paymentId());
            assertEquals(PaymentProcessor.PaymentStatus.REFUNDED, refunded.status());
        }
    }

    @Test void testInvalidAmount() {
        assertThrows(IllegalArgumentException.class,
            () -> processor.createPayment("ord-1", BigDecimal.ZERO,
                PaymentProcessor.PaymentMethod.CREDIT_CARD, null));
    }

    @Test void testInvalidMethod() {
        assertThrows(IllegalArgumentException.class,
            () -> processor.createPayment("ord-1", BigDecimal.TEN, null, null));
    }
}
