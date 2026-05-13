package com.bank.payment.service;

import com.bank.common.event.TransactionEvent;
import com.bank.payment.entity.Transaction;
import com.bank.payment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Transaction initiateTransfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount, String description) {
        log.info("Initiating transfer from {} to {} amount {}", fromAccountId, toAccountId, amount);
        
        Transaction transaction = new Transaction(fromAccountId, toAccountId, amount, description);
        transaction = transactionRepository.save(transaction);

        TransactionEvent event = TransactionEvent.create(
            transaction.getId(), fromAccountId, toAccountId, amount, "USD"
        );
        kafkaTemplate.send("transaction.initiated", transaction.getId().toString(), event);
        
        kafkaTemplate.send("account.withdraw", fromAccountId.toString(), 
            new WithdrawRequest(fromAccountId, amount));
        
        return transaction;
    }

    public void processTransaction(TransactionEvent event) {
        log.info("Processing transaction: {}", event.transactionId());
        Transaction transaction = transactionRepository.findById(event.transactionId())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (!"PENDING".equals(transaction.getStatus())) {
            return;
        }

        try {
            kafkaTemplate.send("account.deposit", event.toAccountId().toString(),
                new DepositRequest(event.toAccountId(), event.amount()));
            transaction.complete();
            transactionRepository.save(transaction);
            
            TransactionEvent completedEvent = new TransactionEvent(
                UUID.randomUUID(), event.transactionId(), event.fromAccountId(),
                event.toAccountId(), event.amount(), event.currency(), java.time.Instant.now()
            );
            kafkaTemplate.send("transaction.completed", event.transactionId().toString(), completedEvent);
        } catch (Exception e) {
            transaction.fail(e.getMessage());
            transactionRepository.save(transaction);
            log.error("Transaction failed: {}", e.getMessage());
        }
    }

    public Transaction getTransaction(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));
    }

    public List<Transaction> getAccountTransactions(UUID accountId) {
        return transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId);
    }

    public record WithdrawRequest(UUID accountId, BigDecimal amount) {}
    public record DepositRequest(UUID accountId, BigDecimal amount) {}
}