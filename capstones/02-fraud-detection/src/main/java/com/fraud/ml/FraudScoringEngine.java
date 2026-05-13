package com.fraud.ml;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudScoringEngine {

    private final IsolationForestModel isolationForest;

    public FraudScoreResult calculateScore(TransactionFeatures features) {
        long startTime = System.currentTimeMillis();
        
        double[] featureVector = features.toVector();
        double anomalyScore = isolationForest.predict(featureVector);
        
        double ruleScore = calculateRuleBasedScore(features);
        double patternScore = detectPatterns(features);
        
        double finalScore = (anomalyScore * 0.4) + (ruleScore * 0.4) + (patternScore * 0.2);
        
        String riskLevel = determineRiskLevel(finalScore);
        List<String> riskFactors = identifyRiskFactors(features, finalScore);
        
        long processingTime = System.currentTimeMillis() - startTime;
        
        return new FraudScoreResult(finalScore, riskLevel, riskFactors, processingTime);
    }

    private double calculateRuleBasedScore(TransactionFeatures features) {
        double score = 0.0;
        
        if (features.amount() > 10000) score += 0.3;
        if (features.amount() > 50000) score += 0.2;
        
        if (isNewDevice(features)) score += 0.2;
        if (isUnusualLocation(features)) score += 0.15;
        
        if (features.transactionCountLastHour() > 5) score += 0.25;
        if (features.transactionCountLastHour() > 10) score += 0.2;
        
        if (isHighRiskMerchant(features)) score += 0.15;
        
        if (isVelocityAnomaly(features)) score += 0.2;
        
        return Math.min(score, 1.0);
    }

    private double detectPatterns(TransactionFeatures features) {
        double score = 0.0;
        
        if (isRoundAmountAnomaly(features)) score += 0.15;
        if (isTimePatternAnomaly(features)) score += 0.1;
        if (isGeographicAnomaly(features)) score += 0.2;
        if (isAmountPatternAnomaly(features)) score += 0.1;
        
        return Math.min(score, 1.0);
    }

    private boolean isNewDevice(TransactionFeatures features) {
        return features.deviceAgeDays() < 7;
    }

    private boolean isUnusualLocation(TransactionFeatures features) {
        return features.distanceFromUsual() > 500;
    }

    private boolean isHighRiskMerchant(TransactionFeatures features) {
        Set<String> highRiskCategories = Set.of("GAMBLING", "CRYPTO", "ADULT", "OFFSHORE");
        return highRiskCategories.contains(features.merchantCategory());
    }

    private boolean isVelocityAnomaly(TransactionFeatures features) {
        return features.transactionCountLastHour() > features.averageTransactionsPerHour() * 3;
    }

    private boolean isRoundAmountAnomaly(TransactionFeatures features) {
        double amount = features.amount();
        return amount % 100 == 0 && amount > 1000;
    }

    private boolean isTimePatternAnomaly(TransactionFeatures features) {
        int hour = features.hourOfDay();
        return hour >= 2 && hour <= 5;
    }

    private boolean isGeographicAnomaly(TransactionFeatures features) {
        return features.distanceFromLastTransaction() > 1000;
    }

    private boolean isAmountPatternAnomaly(TransactionFeatures features) {
        double ratio = features.amount() / features.averageTransactionAmount();
        return ratio > 5 || ratio < 0.2;
    }

    private String determineRiskLevel(double score) {
        if (score >= 0.8) return "CRITICAL";
        if (score >= 0.6) return "HIGH";
        if (score >= 0.4) return "MEDIUM";
        if (score >= 0.2) return "LOW";
        return "MINIMAL";
    }

    private List<String> identifyRiskFactors(TransactionFeatures features, double score) {
        List<String> factors = new ArrayList<>();
        
        if (features.amount() > 10000) factors.add("High transaction amount");
        if (isNewDevice(features)) factors.add("New device detected");
        if (isUnusualLocation(features)) factors.add("Unusual location");
        if (isVelocityAnomaly(features)) factors.add("Velocity anomaly");
        if (isHighRiskMerchant(features)) factors.add("High-risk merchant category");
        if (isRoundAmountAnomaly(features)) factors.add("Suspicious round amount");
        if (isTimePatternAnomaly(features)) factors.add("Unusual transaction time");
        
        return factors;
    }

    public record TransactionFeatures(
        double amount,
        int hourOfDay,
        int dayOfWeek,
        String merchantCategory,
        double distanceFromUsual,
        double distanceFromLastTransaction,
        int transactionCountLastHour,
        double averageTransactionsPerHour,
        double averageTransactionAmount,
        int deviceAgeDays,
        boolean isInternational
    ) {
        public double[] toVector() {
            return new double[] {
                amount / 10000,
                hourOfDay / 24.0,
                dayOfWeek / 7.0,
                distanceFromUsual / 1000,
                distanceFromLastTransaction / 1000,
                transactionCountLastHour / 20.0,
                averageTransactionsPerHour / 10.0,
                averageTransactionAmount / 1000,
                deviceAgeDays / 365.0,
                isInternational ? 1.0 : 0.0
            };
        }
    }

    public record FraudScoreResult(
        double score,
        String riskLevel,
        List<String> riskFactors,
        long processingTimeMs
    ) {}
}