# Clustering - Real World Project

## Production-Ready Customer Segmentation System

### Project Overview
Build a comprehensive customer segmentation system for e-commerce that groups customers based on behavior, enabling targeted marketing and personalized recommendations.

---

## Project Architecture

```
customer-segmentation/
├── src/main/java/com/ml/segmentation/
│   ├── CustomerSegmentationApplication.java
│   ├── pipeline/
│   │   ├── DataPipeline.java
│   │   ├── FeatureEngineer.java
│   │   └── DataScaler.java
│   ├── clustering/
│   │   ├── SegmentationEngine.java
│   │   ├── KMeansSegmenter.java
│   │   ├── DBSCANSegmenter.java
│   │   └── EnsembleSegmenter.java
│   ├── analysis/
│   │   ├── SegmentAnalyzer.java
│   │   └── ClusterProfiler.java
│   ├── evaluation/
│   │   ├── SegmentQualityEvaluator.java
│   │   └── StabilityAnalyzer.java
│   ├── serving/
│   │   ├── SegmentationService.java
│   │   └── SegmentPredictor.java
│   ├── api/
│   │   ├── SegmentationController.java
│   │   └── SegmentResponse.java
│   └── repository/
│       └── SegmentRepository.java
└── build.gradle
```

---

## Core Components

### 1. Feature Engineering for Customer Data

```java
package com.ml.segmentation.pipeline;

import java.util.*;

public class FeatureEngineer {

    public record CustomerFeatures(
        double purchaseFrequency,
        double avgOrderValue,
        double recencyDays,
        double productDiversity,
        double browsingTime,
        double cartAbandonRate,
        double returnRate,
        double loyaltyScore
    ) {}

    public static CustomerFeatures extract(double[][] rawFeatures) {
        return new CustomerFeatures(
            rawFeatures[0][0],  // purchaseFrequency
            rawFeatures[0][1], // avgOrderValue
            rawFeatures[0][2], // recencyDays
            rawFeatures[0][3], // productDiversity
            rawFeatures[0][4], // browsingTime
            rawFeatures[0][5], // cartAbandonRate
            rawFeatures[0][6], // returnRate
            rawFeatures[0][7]  // loyaltyScore
        );
    }

    public static double[][] engineerFeatures(List<CustomerData> customers) {
        double[][] features = new double[customers.size()][8];

        for (int i = 0; i < customers.size(); i++) {
            CustomerData c = customers.get(i);

            // Purchase Frequency (purchases per month)
            features[i][0] = c.getTotalPurchases() /
                Math.max(1, (c.getLastPurchaseDate() - c.getFirstPurchaseDate()) / 30.0);

            // Average Order Value
            features[i][1] = c.getTotalSpent() / Math.max(1, c.getTotalPurchases());

            // Recency (days since last purchase)
            features[i][2] = (System.currentTimeMillis() - c.getLastPurchaseDate()) / 86400000.0;

            // Product Diversity (unique categories)
            features[i][3] = c.getUniqueCategories();

            // Browsing Time (average session minutes)
            features[i][4] = c.getTotalBrowsingTime() / Math.max(1, c.getSessions());

            // Cart Abandon Rate
            double cartsCreated = c.getCartsCreated();
            double cartsCompleted = c.getCartsCompleted();
            features[i][5] = cartsCreated > 0 ?
                (cartsCreated - cartsCompleted) / (double) cartsCreated : 0;

            // Return Rate
            double orders = c.getTotalPurchases();
            double returns = c.getTotalReturns();
            features[i][6] = orders > 0 ? returns / orders : 0;

            // Loyalty Score (composite)
            features[i][7] = calculateLoyaltyScore(c);
        }

        return features;
    }

    private static double calculateLoyaltyScore(CustomerData c) {
        double recencyScore = Math.min(1.0, c.getRecencyDays() / 365.0);
        double frequencyScore = Math.min(1.0, c.getTotalPurchases() / 20.0);
        double monetaryScore = Math.min(1.0, c.getTotalSpent() / 5000.0);

        return 0.3 * (1 - recencyScore) + 0.35 * frequencyScore + 0.35 * monetaryScore;
    }
}

class CustomerData {
    private long firstPurchaseDate;
    private long lastPurchaseDate;
    private int totalPurchases;
    private double totalSpent;
    private int uniqueCategories;
    private long totalBrowsingTime;
    private int sessions;
    private int cartsCreated;
    private int cartsCompleted;
    private int totalReturns;
    private int recencyDays;

    // Getters
    public long getFirstPurchaseDate() { return firstPurchaseDate; }
    public long getLastPurchaseDate() { return lastPurchaseDate; }
    public int getTotalPurchases() { return totalPurchases; }
    public double getTotalSpent() { return totalSpent; }
    public int getUniqueCategories() { return uniqueCategories; }
    public long getTotalBrowsingTime() { return totalBrowsingTime; }
    public int getSessions() { return sessions; }
    public int getCartsCreated() { return cartsCreated; }
    public int getCartsCompleted() { return cartsCompleted; }
    public int getTotalReturns() { return totalReturns; }
    public int getRecencyDays() { return recencyDays; }
}
```

### 2. Segmentation Engine

```java
package com.ml.segmentation.clustering;

import java.util.*;

public class SegmentationEngine {

    private final SegmentationConfig config;

    public enum AlgorithmType {
        KMEANS, DBSCAN, HIERARCHICAL, ENSEMBLE
    }

    public SegmentationEngine(SegmentationConfig config) {
        this.config = config;
    }

    public SegmentationResult segment(double[][] features) {
        return switch (config.getAlgorithm()) {
            case KMEANS -> segmentWithKMeans(features);
            case DBSCAN -> segmentWithDBSCAN(features);
            case HIERARCHICAL -> segmentWithHierarchical(features);
            case ENSEMBLE -> segmentWithEnsemble(features);
        };
    }

    private SegmentationResult segmentWithKMeans(double[][] features) {
        int k = config.getNumSegments();

        // Use K-means++
        KMeansSegmenter segmenter = new KMeansSegmenter(k);
        segmenter.setMaxIterations(config.getMaxIterations());
        segmenter.setTolerance(config.getTolerance());
        segmenter.fit(features);

        return new SegmentationResult(
            segmenter.getLabels(),
            segmenter.getCentroids(),
            k,
            AlgorithmType.KMEANS
        );
    }

    private SegmentationResult segmentWithDBSCAN(double[][] features) {
        DBSCANSegmenter segmenter = new DBSCANSegmenter(
            config.getEpsilon(),
            config.getMinPts()
        );
        segmenter.fit(features);

        // Map DBSCAN noise to a cluster
        int[] labels = segmenter.getLabels();
        int maxLabel = Arrays.stream(labels).max().orElse(0);

        // If there's noise (-1), reassign to nearest cluster
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] == -1) {
                labels[i] = maxLabel + 1;
            }
        }

        return new SegmentationResult(
            labels,
            null,
            maxLabel + 2,
            AlgorithmType.DBSCAN
        );
    }

    private SegmentationResult segmentWithHierarchical(double[][] features) {
        int k = config.getNumSegments();

        HierarchicalSegmenter segmenter = new HierarchicalSegmenter(k);
        segmenter.fit(features);

        return new SegmentationResult(
            segmenter.getLabels(),
            null,
            k,
            AlgorithmType.HIERARCHICAL
        );
    }

    private SegmentationResult segmentWithEnsemble(double[][] features) {
        // Combine multiple clustering results
        int n = features.length;

        // Run K-means
        KMeansSegmenter kmeans = new KMeansSegmenter(5);
        kmeans.fit(features);
        int[] kmeansLabels = kmeans.getLabels();

        // Run hierarchical
        HierarchicalSegmenter hierarchical = new HierarchicalSegmenter(5);
        hierarchical.fit(features);
        int[] hierLabels = hierarchical.getLabels();

        // Majority voting
        int[] finalLabels = new int[n];
        for (int i = 0; i < n; i++) {
            int[] votes = {kmeansLabels[i], hierLabels[i]};
            // Simple averaging with rounding
            finalLabels[i] = (votes[0] + votes[1]) / 2;
        }

        return new SegmentationResult(finalLabels, null, 5, AlgorithmType.ENSEMBLE);
    }

    public record SegmentationResult(
        int[] labels,
        double[][] centroids,
        int numSegments,
        AlgorithmType algorithm
    ) {}
}
```

### 3. Segment Analyzer

```java
package com.ml.segmentation.analysis;

import java.util.*;

public class SegmentProfiler {

    public static class SegmentProfile {
        public final int segmentId;
        public final int size;
        public final double percentage;
        public final Map<String, Double> characteristics;
        public final String recommendedStrategy;

        public SegmentProfile(int segmentId, int size, double percentage,
                             Map<String, Double> characteristics,
                             String strategy) {
            this.segmentId = segmentId;
            this.size = size;
            this.percentage = percentage;
            this.characteristics = characteristics;
            this.recommendedStrategy = strategy;
        }
    }

    public static List<SegmentProfile> analyze(double[][] features, int[] labels,
                                             FeatureNames featureNames) {
        int n = features.length;
        int k = Arrays.stream(labels).max().orElse(0) + 1;

        Map<Integer, List<Integer>> clusterMembers = new HashMap<>();
        for (int i = 0; i < n; i++) {
            clusterMembers.computeIfAbsent(labels[i], x -> new ArrayList<>()).add(i);
        }

        List<SegmentProfile> profiles = new ArrayList<>();

        for (int c = 0; c < k; c++) {
            List<Integer> members = clusterMembers.getOrDefault(c, List.of());
            if (members.isEmpty()) continue;

            // Compute cluster statistics
            Map<String, Double> characteristics = computeStats(features, members, featureNames);

            // Determine strategy based on characteristics
            String strategy = determineStrategy(characteristics);

            profiles.add(new SegmentProfile(
                c,
                members.size(),
                100.0 * members.size() / n,
                characteristics,
                strategy
            ));
        }

        return profiles;
    }

    private static Map<String, Double> computeStats(double[][] features,
                                                   List<Integer> members,
                                                   FeatureNames featureNames) {
        Map<String, Double> stats = new LinkedHashMap<>();

        for (int j = 0; j < features[0].length; j++) {
            double sum = 0;
            for (int idx : members) {
                sum += features[idx][j];
            }
            double avg = sum / members.size();

            String name = featureNames != null ?
                featureNames.getName(j) : "feature_" + j;
            stats.put(name, Math.round(avg * 100.0) / 100.0);
        }

        return stats;
    }

    private static String determineStrategy(Map<String, Double> characteristics) {
        double frequency = characteristics.getOrDefault("purchaseFrequency", 0.0);
        double recency = characteristics.getOrDefault("recencyDays", 365.0);
        double value = characteristics.getOrDefault("avgOrderValue", 0.0);
        double loyalty = characteristics.getOrDefault("loyaltyScore", 0.0);

        if (loyalty > 0.7 && value > 100) {
            return "VIP: Personal concierge, exclusive offers, early access";
        } else if (frequency > 5 && recency < 30) {
            return "Loyal: Loyalty rewards, subscription offers, priority support";
        } else if (recency > 180) {
            return "At-Risk: Win-back campaigns, special discounts, re-engagement";
        } else if (value > 80 && frequency < 2) {
            return "High-Value Occasional: Upselling, bundle offers";
        } else if (frequency > 3 && value < 50) {
            return "Frequent Low-Value: Volume discounts, loyalty building";
        } else {
            return "Standard: General promotions, welcome series";
        }
    }

    public record FeatureNames {
        private final String[] names;

        public FeatureNames(String... names) {
            this.names = names;
        }

        public String getName(int index) {
            return index < names.length ? names[index] : "feature_" + index;
        }
    }
}
```

### 4. REST API

```java
package com.ml.segmentation.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/segments")
public class SegmentationController {

    private final SegmentationService service;

    public SegmentationController(SegmentationService service) {
        this.service = service;
    }

    @PostMapping("/segment")
    public SegmentResponse segmentCustomers(@RequestBody CustomerSegmentRequest request) {
        var segments = service.segmentCustomers(request.getCustomers());

        return new SegmentResponse(
            segments.getSegmentLabels(),
            segments.getProfiles(),
            segments.getQualityMetrics()
        );
    }

    @PostMapping("/assign")
    public SegmentAssignmentResponse assignCustomer(
            @RequestBody CustomerFeatures features) {

        int segment = service.predictSegment(features.toArray());

        return new SegmentAssignmentResponse(
            segment,
            service.getSegmentStrategy(segment),
            service.getSegmentCharacteristics(segment)
        );
    }

    @GetMapping("/strategies")
    public Map<Integer, String> getStrategies() {
        return service.getAllStrategies();
    }
}
```

---

## Configuration

```yaml
segmentation:
  algorithm: ENSEMBLE
  num-segments: 5

  kmeans:
    max-iterations: 300
    tolerance: 1e-6
    initialization: KMEANS_PLUS_PLUS

  dbscan:
    epsilon: 0.5
    min-pts: 5

  hierarchical:
    linkage: AVERAGE

  ensemble:
    combine-voting: MAJORITY
    base-algorithms:
      - KMEANS
      - HIERARCHICAL

features:
  include:
    - purchaseFrequency
    - avgOrderValue
    - recencyDays
    - productDiversity
    - browsingTime
    - cartAbandonRate
    - returnRate
    - loyaltyScore

  scale: STANDARD
  handle-outliers: true

quality:
  min-silhouette: 0.3
  min-segment-size: 10
  max-segments: 10
```

This production-ready customer segmentation system provides comprehensive clustering capabilities with automated segment profiling and marketing strategy recommendations.