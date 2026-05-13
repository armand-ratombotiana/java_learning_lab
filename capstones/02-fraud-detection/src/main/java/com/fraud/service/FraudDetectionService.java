package com.fraud.service;

import com.fraud.ml.FraudScoringEngine;
import com.fraud.ml.FraudScoringEngine.FraudScoreResult;
import com.fraud.ml.FraudScoringEngine.TransactionFeatures;
import com.fraud.model.FraudAlert;
import com.fraud.model.Transaction;
import com.fraud.repository.AlertRepository;
import com.fraud.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService {

    private final TransactionRepository transactionRepository;
    private final AlertRepository alertRepository;
    private final FraudScoringEngine scoringEngine;

    private static final double HIGH_RISK_THRESHOLD = 0.6;
    private static final double CRITICAL_RISK_THRESHOLD = 0.8;

    public void processTransaction(Transaction transaction) {
        log.info("Processing transaction: {}", transaction.getId());
        long startTime = System.currentTimeMillis();

        try {
            TransactionFeatures features = extractFeatures(transaction);
            FraudScoreResult result = scoringEngine.calculateScore(features);

            transaction.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            transaction.setFraudScore(result.score());
            transaction.setRiskLevel(result.riskLevel());

            if (result.score() >= CRITICAL_RISK_THRESHOLD) {
                handleCriticalRisk(transaction, result);
            } else if (result.score() >= HIGH_RISK_THRESHOLD) {
                handleHighRisk(transaction, result);
            } else {
                transaction.markAsClean();
            }

            transactionRepository.save(transaction);

            log.info("Transaction {} processed in {}ms, score: {}, level: {}",
                transaction.getId(), transaction.getProcessingTimeMs(), result.score(), result.riskLevel());

        } catch (Exception e) {
            log.error("Error processing transaction {}: {}", transaction.getId(), e.getMessage());
            transaction.setStatus("ERROR");
            transactionRepository.save(transaction);
        }
    }

    private TransactionFeatures extractFeatures(Transaction transaction) {
        Instant now = Instant.now();
        List<Transaction> recentTxns = transactionRepository
            .findRecentByAccount(transaction.getAccountId(), now.minus(Duration.ofHours(1)))
            .orElse(List.of());

        double avgAmount = recentTxns.stream()
            .mapToDouble(t -> t.getAmount().doubleValue())
            .average()
            .orElse(100.0);

        int txnCountLastHour = recentTxns.size();
        double avgTxnPerHour = txnCountLastHour > 0 ? txnCountLastHour : 1.0;

        return new TransactionFeatures(
            transaction.getAmount().doubleValue(),
            transaction.getTimestamp().atZone(java.time.ZoneId.systemDefault()).getHour(),
            transaction.getTimestamp().atZone(java.time.ZoneId.systemDefault()).getDayOfWeek().getValue(),
            transaction.getMerchantCategory() != null ? transaction.getMerchantCategory() : "RETAIL",
            calculateDistanceFromUsual(transaction),
            0.0,
            txnCountLastHour,
            avgTxnPerHour,
            avgAmount,
            30,
            false
        );
    }

    private double calculateDistanceFromUsual(Transaction transaction) {
        if (transaction.getLatitude() == null || transaction.getLongitude() == null) {
            return 0.0;
        }
        return Math.sqrt(
            Math.pow(transaction.getLatitude() - 40.7128, 2) +
            Math.pow(transaction.getLongitude() - -74.0060, 2)
        ) * 111.0;
    }

    private void handleCriticalRisk(Transaction transaction, FraudScoreResult result) {
        log.warn("CRITICAL risk detected for transaction: {} score: {}", 
            transaction.getId(), result.score());
        
        transaction.markAsFraud(result.score(), result.riskLevel());

        FraudAlert alert = new FraudAlert(
            transaction.getId(),
            transaction.getAccountId(),
            result.score(),
            result.riskLevel(),
            result.riskFactors(),
            "Critical risk threshold exceeded"
        );
        alertRepository.save(alert);

        notifySecurityTeam(alert);
    }

    private void handleHighRisk(Transaction transaction, FraudScoreResult result) {
        log.warn("HIGH risk detected for transaction: {} score: {}",
            transaction.getId(), result.score());

        transaction.markAsFraud(result.score(), result.riskLevel());

        FraudAlert alert = new FraudAlert(
            transaction.getId(),
            transaction.getAccountId(),
            result.score(),
            result.riskLevel(),
            result.riskFactors(),
            "High risk threshold exceeded"
        );
        alertRepository.save(alert);
    }

    private void notifySecurityTeam(FraudAlert alert) {
        log.info("NOTIFICATION: Critical fraud alert for transaction {} account {}",
            alert.getTransactionId(), alert.getAccountId());
    }

    public List<FraudAlert> getOpenAlerts() {
        return alertRepository.findByStatus("OPEN");
    }

    @Transactional
    public FraudAlert resolveAlert(String alertId, String resolvedBy, String resolution) {
        FraudAlert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));
        alert.resolve(resolvedBy, resolution);
        return alertRepository.save(alert);
    }
}