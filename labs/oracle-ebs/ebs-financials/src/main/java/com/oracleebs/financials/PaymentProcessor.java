package com.oracleebs.financials;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentProcessor {
    public enum PaymentMethod { CHECK, WIRE, ACH, ELECTRONIC }
    public enum PaymentStatus { PENDING, VALIDATED, PROCESSED, FAILED }

    public static class Payment {
        private final String id;
        private final String supplierId;
        private final String invoiceRef;
        private final BigDecimal amount;
        private final String currency;
        private final PaymentMethod method;
        private final LocalDate paymentDate;
        private PaymentStatus status;
        private String errorDetail;

        public Payment(String id, String supplier, String invoice, BigDecimal amount, String currency, PaymentMethod method, LocalDate date) {
            this.id = id;
            this.supplierId = supplier;
            this.invoiceRef = invoice;
            this.amount = amount;
            this.currency = currency;
            this.method = method;
            this.paymentDate = date;
            this.status = PaymentStatus.PENDING;
        }

        public String getId() { return id; }
        public String getSupplierId() { return supplierId; }
        public String getInvoiceRef() { return invoiceRef; }
        public BigDecimal getAmount() { return amount; }
        public String getCurrency() { return currency; }
        public PaymentMethod getMethod() { return method; }
        public LocalDate getPaymentDate() { return paymentDate; }
        public PaymentStatus getStatus() { return status; }
        public String getErrorDetail() { return errorDetail; }
        public void setStatus(PaymentStatus s) { this.status = s; }
        public void setErrorDetail(String d) { this.errorDetail = d; }
    }

    public static class PaymentBatch {
        private final String batchId;
        private final LocalDate batchDate;
        private final List<Payment> payments;
        private final Set<String> processedIds;

        public PaymentBatch(String batchId, LocalDate date) {
            this.batchId = batchId;
            this.batchDate = date;
            this.payments = new ArrayList<>();
            this.processedIds = new HashSet<>();
        }

        public void addPayment(Payment p) { payments.add(p); }
        public String getBatchId() { return batchId; }
        public LocalDate getBatchDate() { return batchDate; }
        public List<Payment> getPayments() { return Collections.unmodifiableList(payments); }
    }

    private final Map<String, Payment> payments;
    private final Map<String, PaymentBatch> batches;
    private final Map<String, BigDecimal> supplierLimits;

    public PaymentProcessor() {
        this.payments = new ConcurrentHashMap<>();
        this.batches = new ConcurrentHashMap<>();
        this.supplierLimits = new ConcurrentHashMap<>();
    }

    public void setSupplierLimit(String supplierId, BigDecimal limit) {
        supplierLimits.put(supplierId, limit);
    }

    public PaymentBatch createBatch(String batchId, LocalDate date) {
        PaymentBatch batch = new PaymentBatch(batchId, date);
        batches.put(batchId, batch);
        return batch;
    }

    public void addPaymentToBatch(String batchId, Payment payment) {
        PaymentBatch batch = batches.get(batchId);
        if (batch == null) throw new IllegalArgumentException("Batch not found: " + batchId);
        batch.addPayment(payment);
        payments.put(payment.getId(), payment);
    }

    public ValidationResult processBatch(String batchId) {
        PaymentBatch batch = batches.get(batchId);
        if (batch == null) return new ValidationResult(false, "Batch not found");

        for (Payment p : batch.getPayments()) {
            BigDecimal limit = supplierLimits.get(p.getSupplierId());
            if (limit != null && p.getAmount().compareTo(limit) > 0) {
                p.setStatus(PaymentStatus.FAILED);
                p.setErrorDetail("Exceeds supplier limit");
                continue;
            }
            if (p.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                p.setStatus(PaymentStatus.FAILED);
                p.setErrorDetail("Invalid amount");
                continue;
            }
            p.setStatus(PaymentStatus.VALIDATED);
        }
        return new ValidationResult(true, "Batch processed");
    }

    public void executePayments(String batchId) {
        PaymentBatch batch = batches.get(batchId);
        if (batch == null) return;
        for (Payment p : batch.getPayments()) {
            if (p.getStatus() == PaymentStatus.VALIDATED) {
                p.setStatus(PaymentStatus.PROCESSED);
            }
        }
    }

    public record ValidationResult(boolean success, String message) {}
}
