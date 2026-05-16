# Recommendation Systems - Real World Project

## Production-Ready E-commerce Product Recommendation System

### Project Overview
Build a comprehensive product recommendation system for e-commerce that provides personalized recommendations, handles cold start, and integrates with a recommendation API.

---

## Project Architecture

```
product-recommender/
├── src/main/java/com/ml/recsys/
│   ├── ProductRecommendationApplication.java
│   ├── recommendation/
│   │   ├── RecommendationEngine.java
│   │   ├── MatrixFactorizationRecommender.java
│   │   ├── CollaborativeFilteringRecommender.java
│   │   └── HybridRecommender.java
│   ├── model/
│   │   ├── RatingData.java
│   │   └── ProductData.java
│   ├── evaluation/
│   │   ├── RecommenderEvaluator.java
│   │   └── A_B_Tester.java
│   ├── serving/
│   │   ├── RecommendationService.java
│   │   └── UserHistoryTracker.java
│   └── api/
│       ├── RecommendationController.java
│       └── ProductRecommendationRequest.java
└── build.gradle
```

---

## Core Components

### 1. Hybrid Recommendation Engine

```java
package com.ml.recsys.recommendation;

import java.util.*;

public class HybridRecommender {
    private final MatrixFactorizationRecommender mf;
    private final CollaborativeFilteringRecommender cf;
    private final ContentBasedRecommender content;
    private final double[] weights;

    public HybridRecommender(double wMf, double wCf, double wContent) {
        this.mf = new MatrixFactorizationRecommender(20, 0.01, 0.02);
        this.cf = new CollaborativeFilteringRecommender();
        this.content = new ContentBasedRecommender();
        this.weights = new double[]{wMf, wCf, wContent};
    }

    public void fit(RatingData ratings, ProductData products) {
        mf.fit(ratings);
        cf.fit(ratings.getUserItemMatrix());
        content.fit(products);
    }

    public List<ProductScore> recommend(int userId, int n,
                                        Set<Integer> excludeItems,
                                        Map<String, Double> userFeatures) {
        // Get scores from each recommender
        List<ProductScore> mfScores = mf.getScores(userId, excludeItems);
        List<ProductScore> cfScores = cf.getScores(userId, excludeItems);
        List<ProductScore> contentScores = content.getScores(userId, userFeatures, excludeItems);

        // Merge and weight
        Map<Integer, Double> combined = new HashMap<>();

        for (var score : mfScores) {
            combined.put(score.productId(), score.score() * weights[0]);
        }
        for (var score : cfScores) {
            combined.merge(score.productId(), score.score() * weights[1], Double::sum);
        }
        for (var score : contentScores) {
            combined.merge(score.productId(), score.score() * weights[2], Double::sum);
        }

        // Sort and return top N
        return combined.entrySet().stream()
            .map(e -> new ProductScore(e.getKey(), e.getValue()))
            .sorted((a, b) -> Double.compare(b.score(), a.score()))
            .limit(n)
            .toList();
    }
}
```

### 2. A/B Testing for Recommendations

```java
package com.ml.recsys.evaluation;

import java.util.*;

public class A_B_Tester {
    private Map<String, List<Double>> metrics = new HashMap<>();

    public void recordExperiment(String experimentId, String variant,
                                double clickRate, double conversionRate, double revenue) {
        metrics.computeIfAbsent(experimentId + "_" + variant,
            k -> new ArrayList<>()).add(clickRate);
    }

    public ExperimentResult analyze(String experimentId) {
        List<Double> control = metrics.get(experimentId + "_control");
        List<Double> treatment = metrics.get(experimentId + "_treatment");

        if (control == null || treatment == null) {
            return new ExperimentResult(0, 0, false);
        }

        double meanControl = control.stream().average().orElse(0);
        double meanTreatment = treatment.stream().average().orElse(0);

        double improvement = (meanTreatment - meanControl) / meanControl;
        boolean significant = improvement > 0.1; // 10% improvement

        return new ExperimentResult(meanControl, meanTreatment, significant);
    }
}
```

### 3. REST API

```java
package com.ml.recsys.api;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {
    private final RecommendationService service;

    @PostMapping("/products")
    public ProductRecommendationResponse recommendProducts(
            @RequestBody ProductRecommendationRequest request) {

        List<ProductScore> recommendations = service.recommend(
            request.getUserId(),
            request.getNumRecommendations(),
            request.getExcludeItems()
        );

        return new ProductRecommendationResponse(
            recommendations,
            System.currentTimeMillis()
        );
    }

    @PostMapping("/similar")
    public SimilarProductsResponse getSimilarProducts(
            @RequestParam int productId,
            @RequestParam(defaultValue = "10") int numSimilar) {

        List<ProductScore> similar = service.getSimilarProducts(
            productId, numSimilar);

        return new SimilarProductsResponse(productId, similar);
    }
}
```

This system provides comprehensive product recommendations with hybrid methods, A/B testing capabilities, and production-ready API.