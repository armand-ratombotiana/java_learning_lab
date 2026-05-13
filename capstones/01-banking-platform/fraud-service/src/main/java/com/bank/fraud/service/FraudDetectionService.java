package com.bank.fraud.service;

import com.bank.common.event.FraudAlertEvent;
import com.bank.common.event.TransactionEvent;
import com.bank.fraud.entity.FraudAlert;
import com.bank.fraud.repository.FraudAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService {

    private final FraudAlertRepository fraudAlertRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final BigDecimal HIGH_AMOUNT_THRESHOLD = new BigDecimal("10000");
    private static final BigDecimal VERY_HIGH_AMOUNT_THRESHOLD = new BigDecimal("50000");

    public void analyzeTransaction(TransactionEvent event) {
        log.info("Analyzing transaction: {}", event.transactionId());
        
        String riskLevel = calculateRiskLevel(event.amount());
        String reason = determineRiskReason(event.amount());

        if (!"LOW".equals(riskLevel)) {
            FraudAlert alert = new FraudAlert(
                event.transactionId(),
                event.fromAccountId(),
                riskLevel,
                reason
            );
            fraudAlertRepository.save(alert);

            FraudAlertEvent alertEvent = FraudAlertEvent.create(
                event.transactionId(),
                event.fromAccountId(),
                riskLevel,
                reason
            );
            kafkaTemplate.send("fraud.alerts", event.transactionId().toString(), alertEvent);
            log.warn("Fraud alert created for transaction: {} risk: {}", event.transactionId(), riskLevel);
        }
    }

    private String calculateRiskLevel(BigDecimal amount) {
        if (amount.compareTo(VERY_HIGH_AMOUNT_THRESHOLD) > 0) {
            return "CRITICAL";
        } else if (amount.compareTo(HIGH_AMOUNT_THRESHOLD) > 0) {
            return "HIGH";
        } else if (amount.compareTo(new BigDecimal("5000")) > 0) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private String determineRiskReason(BigDecimal amount) {
        if (amount.compareTo(VERY_HIGH_AMOUNT_THRESHOLD) > 0) {
            return "Very high transaction amount exceeds safety threshold";
        } else if (amount.compareTo(HIGH_AMOUNT_THRESHOLD) > 0) {
            return "High transaction amount detected";
        }
        return "Unusual transaction pattern";
    }

    public FraudAlert resolveAlert(String alertId, String resolvedBy) {
        FraudAlert alert = fraudAlertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));
        alert.resolve(resolvedBy);
        return fraudAlertRepository.save(alert);
    }
}