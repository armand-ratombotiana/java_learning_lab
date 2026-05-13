package com.bank.fraud;

import com.bank.fraud.service.FraudDetectionService;
import com.bank.common.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionConsumer {

    private final FraudDetectionService fraudDetectionService;

    @KafkaListener(topics = "transaction.initiated", groupId = "fraud-service")
    public void consumeTransaction(TransactionEvent event) {
        log.info("Received transaction event: {}", event.transactionId());
        try {
            fraudDetectionService.analyzeTransaction(event);
        } catch (Exception e) {
            log.error("Error processing transaction: {}", e.getMessage());
        }
    }
}