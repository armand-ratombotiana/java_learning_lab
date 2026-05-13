package com.fraud.controller;

import com.fraud.model.FraudAlert;
import com.fraud.model.Transaction;
import com.fraud.service.FraudDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fraud")
@RequiredArgsConstructor
public class FraudController {

    private final FraudDetectionService fraudDetectionService;

    @PostMapping("/transactions")
    public ResponseEntity<Transaction> submitTransaction(@RequestBody Map<String, Object> request) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(UUID.fromString((String) request.get("accountId")));
        transaction.setAmount(new BigDecimal(request.get("amount").toString()));
        transaction.setCurrency((String) request.getOrDefault("currency", "USD"));
        transaction.setMerchantCategory((String) request.getOrDefault("merchantCategory", "RETAIL"));
        transaction.setDeviceFingerprint((String) request.getOrDefault("deviceFingerprint", ""));
        transaction.setIpAddress((String) request.getOrDefault("ipAddress", ""));
        transaction.setLatitude(request.get("latitude") != null ? Double.parseDouble(request.get("latitude").toString()) : null);
        transaction.setLongitude(request.get("longitude") != null ? Double.parseDouble(request.get("longitude").toString()) : null);
        transaction.setTransactionType((String) request.getOrDefault("transactionType", "PURCHASE"));

        fraudDetectionService.processTransaction(transaction);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<FraudAlert>> getAlerts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String riskLevel) {
        
        List<FraudAlert> alerts;
        if (status != null) {
            alerts = fraudDetectionService.getOpenAlerts();
        } else {
            alerts = fraudDetectionService.getOpenAlerts();
        }
        return ResponseEntity.ok(alerts);
    }

    @PostMapping("/alerts/{id}/resolve")
    public ResponseEntity<FraudAlert> resolveAlert(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        
        String resolvedBy = request.get("resolvedBy");
        String resolution = request.get("resolution");
        
        FraudAlert alert = fraudDetectionService.resolveAlert(id, resolvedBy, resolution);
        return ResponseEntity.ok(alert);
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable UUID id) {
        return ResponseEntity.ok(new Transaction());
    }
}