package com.fraud.repository;

import com.fraud.model.FraudAlert;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends MongoRepository<FraudAlert, String> {
    List<FraudAlert> findByStatus(String status);
    List<FraudAlert> findByAccountId(java.util.UUID accountId);
    List<FraudAlert> findByRiskLevel(String riskLevel);
    List<FraudAlert> findByTransactionId(java.util.UUID transactionId);
}