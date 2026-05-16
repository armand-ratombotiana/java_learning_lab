# Anomaly Detection - Real World Project

## Production-Ready Fraud Detection System

### Project Overview
Build a real-time fraud detection system for credit card transactions using multiple anomaly detection algorithms and ensemble methods.

---

## Project Architecture

```
fraud-detection/
├── src/main/java/com/ml/fraud/
│   ├── FraudDetectionApplication.java
│   ├── detection/
│   │   ├── FraudDetector.java
│   │   ├── EnsembleDetector.java
│   │   └── detectors/
│   │       ├── ZScoreDetector.java
│   │       ├── IsolationForestDetector.java
│   │       └── AutoencoderDetector.java
│   ├── features/
│   │   ├── FeatureEngine.java
│   │   └── TransactionFeatures.java
│   ├── evaluation/
│   │   ├── MetricsCalculator.java
│   │   └── AlertEvaluator.java
│   └── api/
│       ├── FraudController.java
│       └── TransactionRequest.java
└── build.gradle
```

---

## Core Components

### 1. Ensemble Fraud Detector

```java
package com.ml.fraud.detection;

import java.util.*;

public class EnsembleDetector {
    private final List<AnomalyDetector> detectors;
    private final double[] weights;

    public EnsembleDetector(List<AnomalyDetector> detectors, double[] weights) {
        this.detectors = detectors;
        this.weights = weights;
    }

    public void fit(double[][] X) {
        for (var detector : detectors) {
            detector.fit(X);
        }
    }

    public AnomalyResult predict(double[][] X) {
        double[][] allScores = new double[detectors.size()][X.length];

        for (int i = 0; i < detectors.size(); i++) {
            allScores[i] = detectors.get(i).getAnomalyScores(X);
        }

        // Weighted average
        double[] ensembleScores = new double[X.length];
        for (int i = 0; i < X.length; i++) {
            double sum = 0;
            for (int d = 0; d < detectors.size(); d++) {
                sum += weights[d] * allScores[d][i];
            }
            ensembleScores[i] = sum;
        }

        int[] predictions = new int[X.length];
        for (int i = 0; i < X.length; i++) {
            predictions[i] = ensembleScores[i] > 0.5 ? 1 : 0;
        }

        return new AnomalyResult(predictions, ensembleScores);
    }
}
```

### 2. Feature Engineering for Transactions

```java
package com.ml.fraud.features;

import java.util.*;

public class TransactionFeatures {
    private double amount;
    private double hourOfDay;
    private double dayOfWeek;
    private double merchantCategory;
    private double distanceFromHome;
    private double timeSinceLastTransaction;
    private double amountVsAvg;
    private double locationDeviation;

    public static TransactionFeatures extract(Map<String, Object> transaction) {
        return new TransactionFeatures();
    }

    public double[] toArray() {
        return new double[]{amount, hourOfDay, dayOfWeek, merchantCategory,
            distanceFromHome, timeSinceLastTransaction, amountVsAvg, locationDeviation};
    }
}
```

### 3. REST API

```java
package com.ml.fraud.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fraud")
public class FraudController {
    private final FraudDetectionService service;

    @PostMapping("/check")
    public FraudCheckResponse checkTransaction(@RequestBody TransactionRequest req) {
        AnomalyResult result = service.detect(req.getFeatures());
        return new FraudCheckResponse(
            result.isAnomaly(),
            result.getRiskScore(),
            result.getRecommendations()
        );
    }
}
```

This system provides comprehensive fraud detection with ensemble methods, feature engineering, and real-time API capabilities.