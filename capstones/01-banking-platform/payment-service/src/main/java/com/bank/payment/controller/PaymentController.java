package com.bank.payment.controller;

import com.bank.payment.entity.Transaction;
import com.bank.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Transaction> initiateTransfer(@RequestBody Map<String, Object> request) {
        UUID from = UUID.fromString((String) request.get("fromAccountId"));
        UUID to = UUID.fromString((String) request.get("toAccountId"));
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String description = (String) request.getOrDefault("description", "");
        
        Transaction transaction = paymentService.initiateTransfer(from, to, amount, description);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransfer(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getTransaction(id));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> getAccountTransfers(@PathVariable UUID accountId) {
        return ResponseEntity.ok(paymentService.getAccountTransactions(accountId));
    }
}