package com.oracleebs.financials;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;

class PaymentProcessorTest {
    private PaymentProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new PaymentProcessor();
        processor.setSupplierLimit("SUP001", BigDecimal.valueOf(50000));
    }

    @Test
    void testCreateBatch() {
        var batch = processor.createBatch("BATCH001", LocalDate.of(2026, 2, 1));
        assertNotNull(batch);
        assertEquals("BATCH001", batch.getBatchId());
    }

    @Test
    void testAddPaymentToBatch() {
        var batch = processor.createBatch("BATCH001", LocalDate.of(2026, 2, 1));
        var payment = new PaymentProcessor.Payment("PMT001", "SUP001", "INV001", BigDecimal.valueOf(10000), "USD",
            PaymentProcessor.PaymentMethod.WIRE, LocalDate.of(2026, 2, 5));
        processor.addPaymentToBatch("BATCH001", payment);
        assertEquals(1, batch.getPayments().size());
    }

    @Test
    void testProcessBatchValidatesPayments() {
        processor.createBatch("BATCH001", LocalDate.of(2026, 2, 1));
        var p1 = new PaymentProcessor.Payment("PMT001", "SUP001", "INV001", BigDecimal.valueOf(30000), "USD",
            PaymentProcessor.PaymentMethod.ACH, LocalDate.of(2026, 2, 5));
        processor.addPaymentToBatch("BATCH001", p1);
        var result = processor.processBatch("BATCH001");
        assertTrue(result.success());
    }

    @Test
    void testPaymentExceedsSupplierLimit() {
        processor.createBatch("BATCH001", LocalDate.of(2026, 2, 1));
        var p1 = new PaymentProcessor.Payment("PMT001", "SUP001", "INV001", BigDecimal.valueOf(100000), "USD",
            PaymentProcessor.PaymentMethod.CHECK, LocalDate.of(2026, 2, 5));
        processor.addPaymentToBatch("BATCH001", p1);
        processor.processBatch("BATCH001");
        assertEquals(PaymentProcessor.PaymentStatus.FAILED, p1.getStatus());
    }

    @Test
    void testInvalidAmountRejected() {
        processor.createBatch("BATCH001", LocalDate.of(2026, 2, 1));
        var p1 = new PaymentProcessor.Payment("PMT001", "SUP001", "INV001", BigDecimal.valueOf(-100), "USD",
            PaymentProcessor.PaymentMethod.ELECTRONIC, LocalDate.of(2026, 2, 5));
        processor.addPaymentToBatch("BATCH001", p1);
        processor.processBatch("BATCH001");
        assertEquals(PaymentProcessor.PaymentStatus.FAILED, p1.getStatus());
    }

    @Test
    void testExecutePayments() {
        processor.createBatch("BATCH001", LocalDate.of(2026, 2, 1));
        var p1 = new PaymentProcessor.Payment("PMT001", "SUP001", "INV001", BigDecimal.valueOf(10000), "USD",
            PaymentProcessor.PaymentMethod.ACH, LocalDate.of(2026, 2, 5));
        processor.addPaymentToBatch("BATCH001", p1);
        processor.processBatch("BATCH001");
        processor.executePayments("BATCH001");
        assertEquals(PaymentProcessor.PaymentStatus.PROCESSED, p1.getStatus());
    }
}
