# Classification - Real World Project

## Production-Ready Credit Risk Classification System

### Project Overview
Build a comprehensive credit risk classification system that predicts loan defaults using multiple ML models, handles class imbalance, provides model explanations, and integrates with a REST API.

---

## Project Structure

```
credit-risk-system/
├── src/main/java/com/ml/credit/
│   ├── CreditRiskApplication.java
│   ├── config/
│   │   ├── ModelConfig.java
│   │   └── SecurityConfig.java
│   ├── model/
│   │   ├── CreditRiskClassifier.java
│   │   ├── ModelFactory.java
│   │   └── ModelRegistry.java
│   ├── pipeline/
│   │   ├── RiskDataPipeline.java
│   │   ├── FeatureEngineer.java
│   │   ├── ImbalancedSampler.java
│   │   └── DataValidator.java
│   ├── evaluation/
│   │   ├── ModelEvaluator.java
│   │   ├── ThresholdOptimizer.java
│   │   └── FairnessAnalyzer.java
│   ├── explainability/
│   │   ├── FeatureImportance.java
│   │   └── SHAPExplanation.java
│   ├── serving/
│   │   ├── RiskPredictionService.java
│   │   ├── BatchProcessor.java
│   │   └── ModelManager.java
│   ├── api/
│   │   ├── CreditRiskController.java
│   │   ├── RiskAssessmentRequest.java
│   │   └── RiskAssessmentResponse.java
│   └── repository/
│       ├── PredictionRepository.java
│       └── MetricsRepository.java
├── src/main/resources/
│   ├── application.yml
│   └── models/
└── build.gradle
```

---

## Core Components

### 1. Data Pipeline with Imbalance Handling

```java
package com.ml.credit.pipeline;

import java.util.*;

public class RiskDataPipeline {

    private final DataValidator validator;
    private final FeatureEngineer engineer;
    private final ImbalancedSampler sampler;
    private final FeatureScaler scaler;

    public RiskDataPipeline(Config config) {
        this.validator = new DataValidator();
        this.engineer = new FeatureEngineer();
        this.sampler = new ImbalancedSampler(config.getSamplingStrategy());
        this.scaler = new FeatureScaler();
    }

    public PipelineResult process(double[][] rawFeatures, int[] labels, boolean isTraining) {
        // Validate data
        validator.validate(rawFeatures, labels);

        // Handle missing values
        double[][] cleaned = handleMissing(rawFeatures);

        // Remove outliers
        cleaned = removeOutliers(cleaned);

        // Feature engineering
        double[][] engineered = engineer.transform(cleaned);

        // Scale features
        double[][] scaled = isTraining ?
            scaler.fitTransform(engineered) :
            scaler.transform(engineered);

        // Handle imbalance (training only)
        int[] balancedLabels = labels;
        double[][] balancedFeatures = scaled;

        if (isTraining && shouldResample()) {
            var resampled = sampler.resample(scaled, labels);
            balancedFeatures = resampled.features();
            balancedLabels = resampled.labels();
        }

        return new PipelineResult(balancedFeatures, balancedLabels);
    }

    private double[][] handleMissing(double[][] data) {
        int rows = data.length;
        int cols = data[0].length;
        double[][] result = new double[rows][cols];

        double[] colMeans = new double[cols];
        for (int j = 0; j < cols; j++) {
            double sum = 0;
            int count = 0;
            for (int i = 0; i < rows; i++) {
                if (!Double.isNaN(data[i][j])) {
                    sum += data[i][j];
                    count++;
                }
            }
            colMeans[j] = count > 0 ? sum / count : 0;
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = Double.isNaN(data[i][j]) ? colMeans[j] : data[i][j];
            }
        }

        return result;
    }

    private double[][] removeOutliers(double[][] data) {
        int cols = data[0].length;
        int rows = data.length;
        double[][] result = data.clone();

        for (int j = 0; j < cols; j++) {
            double[] values = new double[rows];
            for (int i = 0; i < rows; i++) values[i] = data[i][j];
            Arrays.sort(values);

            double q1 = values[rows / 4];
            double q3 = values[3 * rows / 4];
            double iqr = q3 - q1;
            double lower = q1 - 1.5 * iqr;
            double upper = q3 + 1.5 * iqr;

            for (int i = 0; i < rows; i++) {
                if (result[i][j] < lower) result[i][j] = lower;
                if (result[i][j] > upper) result[i][j] = upper;
            }
        }

        return result;
    }

    private boolean shouldResample() {
        return true;
    }

    public record PipelineResult(double[][] features, int[] labels) {}
}
```

### 2. Imbalanced Sampling

```java
package com.ml.credit.pipeline;

import java.util.*;

public class ImbalancedSampler {

    private final SamplingStrategy strategy;

    public enum SamplingStrategy {
        SMOTE,
        UNDERSAMPLING,
        OVERSAMPLING,
        NONE
    }

    public ImbalancedSampler(SamplingStrategy strategy) {
        this.strategy = strategy;
    }

    public record ResampledData(double[][] features, int[] labels) {}

    public ResampledData resample(double[][] X, int[] y) {
        return switch (strategy) {
            case SMOTE -> smote(X, y);
            case UNDERSAMPLING -> undersample(X, y);
            case OVERSAMPLING -> oversample(X, y);
            case NONE -> new ResampledData(X, y);
        };
    }

    private ResampledData smote(double[][] X, int[] y) {
        int[] classCounts = countClasses(y);
        int majorityClass = classCounts[0] > classCounts[1] ? 0 : 1;
        int minorityClass = 1 - majorityClass;

        if (classCounts[minorityClass] == 0) {
            return new ResampledData(X, y);
        }

        // Find minority class samples
        List<double[]> minoritySamples = new ArrayList<>();
        for (int i = 0; i < y.length; i++) {
            if (y[i] == minorityClass) {
                minoritySamples.add(X[i]);
            }
        }

        // Generate synthetic samples
        int numSynthetic = classCounts[majorityClass] - classCounts[minorityClass];
        List<double[]> synthetic = new ArrayList<>();

        Random rand = new Random(42);
        for (int i = 0; i < numSynthetic; i++) {
            double[] sample = minoritySamples.get(rand.nextInt(minoritySamples.size()));
            double[] neighbor = minoritySamples.get(rand.nextInt(minoritySamples.size()));

            double[] syntheticSample = new double[sample.length];
            double alpha = rand.nextDouble();

            for (int j = 0; j < sample.length; j++) {
                syntheticSample[j] = sample[j] + alpha * (neighbor[j] - sample[j]);
            }
            synthetic.add(syntheticSample);
        }

        // Combine original and synthetic
        int totalSize = y.length + numSynthetic;
        double[][] resampledX = new double[totalSize][];
        int[] resampledY = new int[totalSize];

        System.arraycopy(X, 0, resampledX, 0, y.length);
        System.arraycopy(y, 0, resampledY, 0, y.length);

        for (int i = 0; i < numSynthetic; i++) {
            resampledX[y.length + i] = synthetic.get(i);
            resampledY[y.length + i] = minorityClass;
        }

        return new ResampledData(resampledX, resampledY);
    }

    private ResampledData undersample(double[][] X, int[] y) {
        int[] classCounts = countClasses(y);
        int minorityCount = Math.min(classCounts[0], classCounts[1]);

        List<double[]> class0 = new ArrayList<>();
        List<double[]> class1 = new ArrayList<>();

        for (int i = 0; i < y.length; i++) {
            if (y[i] == 0) class0.add(X[i]);
            else class1.add(X[i]);
        }

        Random rand = new Random(42);
        Collections.shuffle(class0, rand);
        Collections.shuffle(class1, rand);

        int n = minorityCount * 2;
        double[][] resampledX = new double[n][];
        int[] resampledY = new int[n];

        for (int i = 0; i < minorityCount; i++) {
            resampledX[i] = class0.get(i);
            resampledY[i] = 0;
            resampledX[minorityCount + i] = class1.get(i);
            resampledY[minorityCount + i] = 1;
        }

        return new ResampledData(resampledX, resampledY);
    }

    private ResampledData oversample(double[][] X, int[] y) {
        int[] classCounts = countClasses(y);
        int majorityCount = Math.max(classCounts[0], classCounts[1]);

        List<double[]> minoritySamples = new ArrayList<>();
        List<double[]> majoritySamples = new ArrayList<>();

        for (int i = 0; i < y.length; i++) {
            if (y[i] == 0) majoritySamples.add(X[i]);
            else minoritySamples.add(X[i]);
        }

        int minorityClass = classCounts[0] < classCounts[1] ? 0 : 1;
        int toAdd = majorityCount - (minorityClass == 0 ? classCounts[0] : classCounts[1]);

        Random rand = new Random(42);
        double[][] resampledX = new double[y.length + toAdd][];
        int[] resampledY = new int[y.length + toAdd];

        System.arraycopy(X, 0, resampledX, 0, y.length);
        System.arraycopy(y, 0, resampledY, 0, y.length);

        for (int i = 0; i < toAdd; i++) {
            double[] sample = minoritySamples.get(rand.nextInt(minoritySamples.size()));
            resampledX[y.length + i] = sample.clone();
            resampledY[y.length + i] = minorityClass;
        }

        return new ResampledData(resampledX, resampledY);
    }

    private int[] countClasses(int[] y) {
        int[] counts = new int[2];
        for (int label : y) counts[label]++;
        return counts;
    }
}
```

### 3. Model Factory

```java
package com.ml.credit.model;

import com.ml.classification.Classifier;
import com.ml.classification.logistic.LogisticRegression;
import com.ml.classification.svm.LinearSVM;
import com.ml.classification.tree.DecisionTreeClassifier;
import com.ml.classification.ensemble.RandomForest;

public class ModelFactory {

    public static Classifier create(String modelType, Map<String, Object> params) {
        return switch (modelType.toLowerCase()) {
            case "logistic-regression" -> createLogisticRegression(params);
            case "svm" -> createSVM(params);
            case "decision-tree" -> createDecisionTree(params);
            case "random-forest" -> createRandomForest(params);
            default -> throw new IllegalArgumentException("Unknown model: " + modelType);
        };
    }

    private static Classifier createLogisticRegression(Map<String, Object> params) {
        double lr = params.containsKey("learningRate") ?
            (double) params.get("learningRate") : 0.1;
        int iter = params.containsKey("maxIterations") ?
            (int) params.get("maxIterations") : 1000;

        return new LogisticRegression(lr, iter);
    }

    private static Classifier createSVM(Map<String, Object> params) {
        double C = params.containsKey("C") ?
            (double) params.get("C") : 1.0;
        return new LinearSVM(C, 0.01, 1000);
    }

    private static Classifier createDecisionTree(Map<String, Object> params) {
        int depth = params.containsKey("maxDepth") ?
            (int) params.get("maxDepth") : 10;
        int minSamples = params.containsKey("minSamplesSplit") ?
            (int) params.get("minSamplesSplit") : 2;

        return new DecisionTreeClassifier(depth, minSamples);
    }

    private static Classifier createRandomForest(Map<String, Object> params) {
        int nTrees = params.containsKey("nTrees") ?
            (int) params.get("nTrees") : 100;
        int maxDepth = params.containsKey("maxDepth") ?
            (int) params.get("maxDepth") : 10;

        return new RandomForest(nTrees, maxDepth);
    }
}
```

### 4. Threshold Optimizer

```java
package com.ml.credit.evaluation;

import java.util.*;

public class ThresholdOptimizer {

    public static class OptimizationResult {
        public final double optimalThreshold;
        public final double bestF1;
        public final Map<String, Double> metricsAtThreshold;

        public OptimizationResult(double threshold, double f1, Map<String, Double> metrics) {
            this.optimalThreshold = threshold;
            this.bestF1 = f1;
            this.metricsAtThreshold = metrics;
        }
    }

    public static OptimizationResult findOptimalThreshold(
            double[] probabilities, int[] trueLabels) {

        double bestF1 = 0;
        double bestThreshold = 0.5;
        Map<String, Double> bestMetrics = new HashMap<>();

        for (double threshold = 0.1; threshold <= 0.9; threshold += 0.05) {
            int[] predictions = new int[probabilities.length];
            for (int i = 0; i < probabilities.length; i++) {
                predictions[i] = probabilities[i] >= threshold ? 1 : 0;
            }

            var metrics = computeMetrics(trueLabels, predictions);

            if (metrics.get("f1") > bestF1) {
                bestF1 = metrics.get("f1");
                bestThreshold = threshold;
                bestMetrics = metrics;
            }
        }

        return new OptimizationResult(bestThreshold, bestF1, bestMetrics);
    }

    private static Map<String, Double> computeMetrics(int[] yTrue, int[] predictions) {
        int tp = 0, fp = 0, tn = 0, fn = 0;

        for (int i = 0; i < yTrue.length; i++) {
            if (yTrue[i] == 1 && predictions[i] == 1) tp++;
            else if (yTrue[i] == 0 && predictions[i] == 1) fp++;
            else if (yTrue[i] == 0 && predictions[i] == 0) tn++;
            else if (yTrue[i] == 1 && predictions[i] == 0) fn++;
        }

        Map<String, Double> metrics = new HashMap<>();

        double accuracy = (tp + tn) / (double)(tp + tn + fp + fn);
        double precision = tp / (double)(tp + fp);
        double recall = tp / (double)(tp + fn);
        double f1 = 2 * precision * recall / (precision + recall);

        metrics.put("accuracy", accuracy);
        metrics.put("precision", precision);
        metrics.put("recall", recall);
        metrics.put("f1", f1);

        return metrics;
    }
}
```

### 5. REST API Controller

```java
package com.ml.credit.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/credit-risk")
public class CreditRiskController {

    private final RiskPredictionService predictionService;

    public CreditRiskController(RiskPredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/assess")
    public RiskAssessmentResponse assessRisk(@RequestBody RiskAssessmentRequest request) {
        RiskPrediction prediction = predictionService.predict(
            request.getFeatures(),
            request.getModelType()
        );

        return new RiskAssessmentResponse(
            prediction.getRiskScore(),
            prediction.getRiskCategory(),
            prediction.getConfidence(),
            prediction.getFeatureImportance()
        );
    }

    @PostMapping("/batch-assess")
    public List<RiskAssessmentResponse> batchAssess(
            @RequestBody List<RiskAssessmentRequest> requests) {

        return requests.stream()
            .map(req -> assessRisk(req))
            .toList();
    }

    @GetMapping("/models")
    public List<String> getAvailableModels() {
        return predictionService.getAvailableModels();
    }

    @GetMapping("/metrics")
    public Map<String, Object> getModelMetrics(@RequestParam String modelType) {
        return predictionService.getModelMetrics(modelType);
    }
}
```

```java
package com.ml.credit.api;

import java.util.List;
import java.util.Map;

public record RiskAssessmentRequest(
    List<Double> features,
    String modelType,
    Map<String, Object> options
) {
    public double[] getFeaturesArray() {
        return features.stream().mapToDouble(Double::doubleValue).toArray();
    }
}
```

```java
package com.ml.credit.api;

import java.util.List;
import java.util.Map;

public record RiskAssessmentResponse(
    double riskScore,
    String riskCategory,
    double confidence,
    List<FeatureImportance> featureImportance
) {}
```

### 6. Feature Importance

```java
package com.ml.credit.explainability;

import java.util.*;

public class FeatureImportance {

    public record FeatureScore(String name, double score) {}

    public static List<FeatureScore> computePermutationImportance(
            Classifier model,
            double[][] X,
            int[] y,
            int nPermutations) {

        int d = X[0].length;
        double baselineScore = model.score(X, y);

        double[] importanceScores = new double[d];

        for (int j = 0; j < d; j++) {
            Random rand = new Random(42);
            double totalDecrease = 0;

            for (int iter = 0; iter < nPermutations; iter++) {
                // Create shuffled version of feature j
                double[][] XPermuted = new double[X.length][];
                for (int i = 0; i < X.length; i++) {
                    XPermuted[i] = X[i].clone();
                }

                double[] featureValues = new double[X.length];
                for (int i = 0; i < X.length; i++) {
                    featureValues[i] = X[i][j];
                }

                List<Double> shuffled = new ArrayList<>();
                for (double v : featureValues) shuffled.add(v);
                Collections.shuffle(shuffled, rand);

                for (int i = 0; i < X.length; i++) {
                    XPermuted[i][j] = shuffled.get(i);
                }

                double permutedScore = model.score(XPermuted, y);
                totalDecrease += baselineScore - permutedScore;
            }

            importanceScores[j] = totalDecrease / nPermutations;
        }

        // Sort by importance
        List<FeatureScore> result = new ArrayList<>();
        for (int i = 0; i < d; i++) {
            result.add(new FeatureScore("feature_" + i, importanceScores[i]));
        }
        result.sort((a, b) -> Double.compare(b.score, a.score));

        return result;
    }
}

import com.ml.classification.Classifier;
```

---

## Configuration

```yaml
server:
  port: 8080

models:
  default: random-forest
  available:
    - logistic-regression
    - svm
    - decision-tree
    - random-forest

sampling:
  strategy: SMOTE
  synthetic-ratio: 1.0

training:
  test-size: 0.2
  validation-folds: 5
  early-stopping: true

optimization:
  threshold-tuning: true
  objective: F1

fairness:
  check-bias: true
  protected-attributes:
    - age
    - gender

api:
  rate-limit: 1000
  timeout-seconds: 30
```

---

## Main Example

```java
package com.ml.credit;

import com.ml.credit.config.*;
import com.ml.credit.model.*;
import com.ml.credit.pipeline.*;
import com.ml.credit.evaluation.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== Credit Risk Classification System ===\n");

        // Load data
        DataLoader loader = new DataLoader();
        var data = loader.loadCSV("data/credit_data.csv");

        double[][] X = data.features();
        int[] y = data.labels();

        // Process data
        Config config = new Config();
        RiskDataPipeline pipeline = new RiskDataPipeline(config);

        var processed = pipeline.process(X, y, true);

        // Split data
        var split = splitData(processed.features(), processed.labels(), 0.2);

        // Train multiple models
        List<String> modelTypes = Arrays.asList(
            "logistic-regression",
            "decision-tree",
            "random-forest"
        );

        Map<String, Classifier> models = new HashMap<>();
        Map<String, double[]> probabilities = new HashMap<>();

        for (String modelType : modelTypes) {
            Map<String, Object> params = new HashMap<>();
            if (modelType.equals("random-forest")) {
                params.put("nTrees", 100);
                params.put("maxDepth", 10);
            }

            Classifier model = ModelFactory.create(modelType, params);
            model.fit(split.XTrain(), split.yTrain());

            models.put(modelType, model);
            probabilities.put(modelType, model.predictProbabilities(split.XTest()));

            System.out.println(modelType + " - Accuracy: " + model.score(split.XTest(), split.yTest()));
        }

        // Optimize threshold for best model
        System.out.println("\n=== Threshold Optimization ===");
        var bestProbs = probabilities.get("random-forest");

        var thresholdResult = ThresholdOptimizer.findOptimalThreshold(
            bestProbs, split.yTest()
        );

        System.out.printf("Optimal threshold: %.2f%n", thresholdResult.optimalThreshold);
        System.out.printf("Best F1 Score: %.4f%n", thresholdResult.bestF1);

        // Compute feature importance
        System.out.println("\n=== Feature Importance (Top 5) ===");
        var importance = FeatureImportance.computePermutationImportance(
            models.get("random-forest"),
            split.XTest(),
            split.yTest(),
            10
        );

        for (int i = 0; i < Math.min(5, importance.size()); i++) {
            System.out.printf("  %s: %.4f%n",
                importance.get(i).name(),
                importance.get(i).score());
        }
    }

    private static TrainTestSplit splitData(double[][] X, int[] y, double testSize) {
        int n = X.length;
        int trainSize = (int)(n * (1 - testSize));

        double[][] XTrain = new double[trainSize][];
        int[] yTrain = new int[trainSize];
        double[][] XTest = new double[n - trainSize][];
        int[] yTest = new int[n - trainSize];

        System.arraycopy(X, 0, XTrain, 0, trainSize);
        System.arraycopy(y, 0, yTrain, 0, trainSize);
        System.arraycopy(X, trainSize, XTest, 0, n - trainSize);
        System.arraycopy(y, trainSize, yTest, 0, n - trainSize);

        return new TrainTestSplit(XTrain, yTrain, XTest, yTest);
    }

    record TrainTestSplit(double[][] XTrain, int[] yTrain,
                        double[][] XTest, int[] yTest) {}
}
```

---

## Key Features

1. **Imbalanced Data Handling**: SMOTE, undersampling, oversampling
2. **Multiple Models**: Logistic Regression, SVM, Decision Trees, Random Forest
3. **Threshold Optimization**: Find optimal classification threshold
4. **Feature Importance**: Permutation-based feature importance
5. **REST API**: Real-time credit risk assessment
6. **Batch Processing**: Process multiple applications
7. **Model Registry**: Version and manage multiple models

This production-ready system provides comprehensive credit risk prediction with proper handling of imbalanced data, model interpretability, and deployment capabilities.