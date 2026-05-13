package com.bank.notification.service;

import com.bank.common.event.FraudAlertEvent;
import com.bank.common.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @KafkaListener(topics = "transaction.completed", groupId = "notification-service")
    public void notifyTransactionCompleted(TransactionEvent event) {
        log.info("Sending notification for completed transaction: {}", event.transactionId());
        sendEmailNotification(event.toAccountId().toString(), 
            "Transaction Completed", 
            "Your transaction of " + event.amount() + " " + event.currency() + " has been completed.");
    }

    @KafkaListener(topics = "fraud.alerts", groupId = "notification-service")
    public void notifyFraudAlert(FraudAlertEvent event) {
        log.warn("Sending fraud alert notification for account: {}", event.accountId());
        sendSmsNotification(event.accountId().toString(),
            "SECURITY ALERT: " + event.reason());
    }

    private void sendEmailNotification(String userId, String subject, String message) {
        log.info("Email sent to user {}: {} - {}", userId, subject, message);
    }

    private void sendSmsNotification(String phoneNumber, String message) {
        log.info("SMS sent to {}: {}", phoneNumber, message);
    }

    public void sendPushNotification(String userId, String title, String body) {
        log.info("Push notification sent to user {}: {} - {}", userId, title, body);
    }
}