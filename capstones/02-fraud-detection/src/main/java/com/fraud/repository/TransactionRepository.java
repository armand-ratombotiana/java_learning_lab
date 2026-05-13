package com.fraud.repository;

import com.fraud.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId AND t.timestamp >= :since ORDER BY t.timestamp DESC")
    Optional<List<Transaction>> findRecentByAccount(@Param("accountId") UUID accountId, @Param("since") Instant since);

    List<Transaction> findByStatus(String status);

    List<Transaction> findByRiskLevel(String riskLevel);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.accountId = :accountId AND t.timestamp >= :since")
    long countRecentTransactions(@Param("accountId") UUID accountId, @Param("since") Instant since);
}