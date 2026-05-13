package com.bank.fraud.repository;

import com.bank.fraud.entity.FraudAlert;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudAlertRepository extends MongoRepository<FraudAlert, String> {
    List<FraudAlert> findByStatus(String status);
    List<FraudAlert> findByAccountId(java.util.UUID accountId);
}