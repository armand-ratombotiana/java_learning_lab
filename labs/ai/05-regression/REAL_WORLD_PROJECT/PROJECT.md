# Regression - Real World Project

## Production-Ready Machine Learning Pipeline for Price Prediction

### Project Overview
Build a complete, production-ready regression system for predicting house prices using multiple regression techniques, feature engineering, cross-validation, and model deployment infrastructure.

### Target Users
- Real estate companies predicting property prices
- Financial institutions for risk assessment
- Data scientists building regression models

---

## Project Architecture

```
price-prediction/
├── src/main/java/com/ml/price/
│   ├── PricePredictionApplication.java
│   ├── config/
│   │   ├── ApplicationConfig.java
│   │   └── ModelConfig.java
│   ├── model/
│   │   ├── RegressionModelFactory.java
│   │   ├── models/
│   │   │   ├── LinearRegressionModel.java
│   │   │   ├── RidgeRegressionModel.java
│   │   │   ├── LassoRegressionModel.java
│   │   │   ├── ElasticNetModel.java
│   │   │   └── GradientBoostingModel.java
│   │   └── ModelMetadata.java
│   ├── pipeline/
│   │   ├── DataPipeline.java
│   │   ├── DataCleaner.java
│   │   ├── FeatureEngineer.java
│   │   ├── FeatureScaler.java
│   │   └── OutlierHandler.java
│   ├── features/
│   │   ├── FeatureTransformer.java
│   │   ├── PolynomialFeatures.java
│   │   ├── CategoricalEncoder.java
│   │   └── FeatureSelector.java
│   ├── evaluation/
│   │   ├── ModelEvaluator.java
│   │   ├── CrossValidator.java
│   │   ├── MetricsPublisher.java
│   │   └── HyperparameterTuner.java
│   ├── training/
│   │   ├── ModelTrainer.java
│   │   ├── TrainingJob.java
│   │   └── CheckpointManager.java
│   ├── serving/
│   │   ├── PredictionService.java
│   │   ├── ModelLoader.java
│   │   └── BatchPredictor.java
│   ├── api/
│   │   ├── PricePredictionController.java
│   │   └── PredictionRequest.java
│   └── util/
│       ├── DataLoader.java
│       └── MetricsLogger.java
├── src/main/resources/
│   ├── application.yml
│   └── models/
├── src/test/java/com/ml/price/
│   ├── IntegrationTests.java
│   └── ModelTests.java
├── build.gradle
└── README.md
```

---

## Configuration and Application Setup

### application.yml

```yaml
server:
  port: 8080

app:
  models:
    default: elastic-net
    available:
      - linear-regression
      - ridge
      - lasso
      - elastic-net
      - gradient-boosting

  features:
    categorical-encoding: one-hot
    scaling: standard
    polynomial-degree: 2

  training:
    cv-folds: 5
    test-size: 0.2
    random-seed: 42
    early-stopping: true
    patience: 10

  hyperparameters:
    ridge:
      alpha:
        min: 0.001
        max: 100
        scale: log
    lasso:
      alpha:
        min: 0.001
        max: 10
        scale: log
    elastic-net:
      alpha:
        min: 0.001
        max: 10
        scale: log
      l1-ratio:
        min: 0.1
        max: 0.9

logging:
  level: INFO
  metrics-enabled: true
```

### Application Main

```java
package com.ml.price;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class PricePredictionApplication {

    public static void main(String[] args) {
        SpringApplication.run(PricePredictionApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

---

## Feature Engineering Pipeline

```java
package com.ml.price.pipeline;

import java.util.*;
import java.util.stream.*;

public class DataPipeline {

    private final DataCleaner cleaner;
    private final FeatureEngineer engineer;
    private final FeatureScaler scaler;
    private final OutlierHandler outlierHandler;

    public DataPipeline(Config config) {
        this.cleaner = new DataCleaner();
        this.engineer = new FeatureEngineer();
        this.scaler = new FeatureScaler(config.getScalerType());
        this.outlierHandler = new OutlierHandler(config.getOutlierMethod());
    }

    public PipelineResult process(double[][] rawFeatures, double[] target) {
        // Step 1: Handle missing values
        double[][] cleaned = cleaner.handleMissing(rawFeatures);

        // Step 2: Handle outliers
        cleaned = outlierHandler.handle(cleaned);

        // Step 3: Feature engineering
        double[][] engineered = engineer.transform(cleaned);

        // Step 4: Scale features
        double[][] scaled = scaler.fitTransform(engineered);

        // Step 5: Handle infinite values
        scaled = cleanInfinity(scaled);

        return new PipelineResult(scaled, target);
    }

    private double[][] cleanInfinity(double[][] data) {
        int rows = data.length;
        int cols = data[0].length;
        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double val = data[i][j];
                if (Double.isInfinite(val) || Double.isNaN(val)) {
                    result[i][j] = 0.0;
                } else {
                    result[i][j] = val;
                }
            }
        }
        return result;
    }

    public record PipelineResult(
        double[][] features,
        double[] target
    ) {}
}
```

```java
package com.ml.price.pipeline;

import java.util.*;

public class DataCleaner {

    public double[][] handleMissing(double[][] data) {
        int rows = data.length;
        int cols = data[0].length;
        double[][] result = new double[rows][cols];

        double[] colMeans = new double[cols];
        int[] missingCount = new int[cols];

        // Calculate column means (ignoring missing)
        for (int j = 0; j < cols; j++) {
            double sum = 0;
            int count = 0;
            for (int i = 0; i < rows; i++) {
                if (!isMissing(data[i][j])) {
                    sum += data[i][j];
                    count++;
                }
            }
            colMeans[j] = count > 0 ? sum / count : 0;
            missingCount[j] = rows - count;
        }

        // Replace missing values with column means
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (isMissing(data[i][j])) {
                    result[i][j] = colMeans[j];
                } else {
                    result[i][j] = data[i][j];
                }
            }
        }

        return result;
    }

    private boolean isMissing(double value) {
        return Double.isNaN(value) || value == -999 || value == -1;
    }

    public int[][] detectConstantColumns(double[][] data) {
        Set<Integer> constantCols = new HashSet<>();
        int rows = data.length;
        int cols = data[0].length;

        for (int j = 0; j < cols; j++) {
            double firstVal = data[0][j];
            boolean isConstant = true;
            for (int i = 1; i < rows; i++) {
                if (data[i][j] != firstVal) {
                    isConstant = false;
                    break;
                }
            }
            if (isConstant) {
                constantCols.add(j);
            }
        }

        return new int[][]{constantCols.stream().mapToInt(Integer::intValue).toArray()};
    }
}
```

```java
package com.ml.price.pipeline;

import java.util.*;

public class FeatureEngineer {

    private int polynomialDegree = 1;
    private boolean includeInteractions = true;
    private List<String> featureNames;

    public FeatureEngineer() {}

    public FeatureEngineer(int polynomialDegree, boolean includeInteractions) {
        this.polynomialDegree = polynomialDegree;
        this.includeInteractions = includeInteractions;
    }

    public double[][] transform(double[][] data) {
        if (polynomialDegree == 1 && !includeInteractions) {
            return data;
        }

        List<double[]> transformed = new ArrayList<>();

        for (double[] row : data) {
            List<Double> features = new ArrayList<>();

            // Original features
            for (double val : row) {
                features.add(val);
            }

            // Polynomial features
            if (polynomialDegree > 1) {
                for (int i = 0; i < row.length; i++) {
                    for (int d = 2; d <= polynomialDegree; d++) {
                        features.add(Math.pow(row[i], d));
                    }
                }
            }

            // Interaction features
            if (includeInteractions) {
                for (int i = 0; i < row.length; i++) {
                    for (int j = i + 1; j < row.length; j++) {
                        features.add(row[i] * row[j]);
                    }
                }
            }

            transformed.add(features.stream().mapToDouble(Double::doubleValue).toArray());
        }

        return transformed.toArray(new double[0][]);
    }

    public void setFeatureNames(List<String> names) {
        this.featureNames = names;
    }

    public List<String> getGeneratedFeatureNames() {
        List<String> names = new ArrayList<>();

        if (featureNames != null) {
            names.addAll(featureNames);
        }

        // Add polynomial feature names (simplified)
        // Add interaction feature names (simplified)

        return names;
    }
}
```

```java
package com.ml.price.pipeline;

import java.util.*;

public class FeatureScaler {

    private final ScalerType type;
    private double[] mean;
    private double[] std;
    private double[] min;
    private double[] max;
    private boolean fitted = false;

    public enum ScalerType {
        STANDARD,
        MINMAX,
        ROBUST
    }

    public FeatureScaler(ScalerType type) {
        this.type = type;
    }

    public double[][] fitTransform(double[][] data) {
        int rows = data.length;
        int cols = data[0].length;

        mean = new double[cols];
        std = new double[cols];
        min = new double[cols];
        max = new double[cols];

        // Calculate statistics
        for (int j = 0; j < cols; j++) {
            double sum = 0;
            min[j] = Double.MAX_VALUE;
            max[j] = Double.MIN_VALUE;

            for (int i = 0; i < rows; i++) {
                sum += data[i][j];
                min[j] = Math.min(min[j], data[i][j]);
                max[j] = Math.max(max[j], data[i][j]);
            }
            mean[j] = sum / rows;

            double sqSum = 0;
            for (int i = 0; i < rows; i++) {
                sqSum += (data[i][j] - mean[j]) * (data[i][j] - mean[j]);
            }
            std[j] = Math.sqrt(sqSum / rows);
            if (std[j] < 1e-10) {
                std[j] = 1.0;
            }
        }

        fitted = true;
        return transform(data);
    }

    public double[][] transform(double[][] data) {
        if (!fitted) {
            throw new IllegalStateException("Scaler must be fitted before transform");
        }

        int rows = data.length;
        int cols = data[0].length;
        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = switch (type) {
                    case STANDARD -> (data[i][j] - mean[j]) / std[j];
                    case MINMAX -> (data[i][j] - min[j]) / (max[j] - min[j]);
                    case ROBUST -> {
                        double median = calculateMedian(data, j);
                        double iqr = calculateIQR(data, j);
                        yield (data[i][j] - median) / iqr;
                    }
                };
            }
        }

        return result;
    }

    private double calculateMedian(double[][] data, int col) {
        double[] values = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            values[i] = data[i][col];
        }
        Arrays.sort(values);
        int mid = values.length / 2;
        return values.length % 2 == 0
            ? (values[mid - 1] + values[mid]) / 2
            : values[mid];
    }

    private double calculateIQR(double[][] data, int col) {
        double[] values = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            values[i] = data[i][col];
        }
        Arrays.sort(values);
        int q1 = values.length / 4;
        int q3 = values.length * 3 / 4;
        return values[q3] - values[q1];
    }
}
```

---

## Model Factory and Implementation

```java
package com.ml.price.model;

import com.ml.regression.*;

public class RegressionModelFactory {

    public static RegressionModel create(String modelType, double... hyperparameters) {
        return switch (modelType.toLowerCase()) {
            case "linear-regression" -> new LinearRegression();
            case "ridge" -> new RidgeRegression(hyperparameters.length > 0 ? hyperparameters[0] : 1.0);
            case "lasso" -> new LassoRegression(hyperparameters.length > 0 ? hyperparameters[0] : 1.0);
            case "elastic-net" -> new ElasticNetRegression(
                hyperparameters.length > 0 ? hyperparameters[0] : 1.0,
                hyperparameters.length > 1 ? hyperparameters[1] : 0.5
            );
            default -> throw new IllegalArgumentException("Unknown model type: " + modelType);
        };
    }

    public static RegressionModel createWithConfig(String modelType, Map<String, Object> config) {
        return switch (modelType.toLowerCase()) {
            case "ridge" -> {
                double alpha = config.containsKey("alpha") ? (double) config.get("alpha") : 1.0;
                yield new RidgeRegression(alpha);
            }
            case "lasso" -> {
                double alpha = config.containsKey("alpha") ? (double) config.get("alpha") : 1.0;
                yield new LassoRegression(alpha);
            }
            case "elastic-net" -> {
                double alpha = config.containsKey("alpha") ? (double) config.get("alpha") : 1.0;
                double l1Ratio = config.containsKey("l1Ratio") ? (double) config.get("l1Ratio") : 0.5;
                yield new ElasticNetRegression(alpha, l1Ratio);
            }
            default -> create(modelType);
        };
    }
}
```

---

## Model Evaluation and Cross-Validation

```java
package com.ml.price.evaluation;

import com.ml.regression.RegressionModel;
import com.ml.regression.evaluation.RegressionMetrics;

import java.util.*;

public class ModelEvaluator {

    public static class EvaluationResult {
        public final String modelName;
        public final double trainR2;
        public final double testR2;
        public final double trainRMSE;
        public final double testRMSE;
        public final double trainMAE;
        public final double testMAE;
        public final Map<String, Double> additionalMetrics;
        public final long trainingTimeMs;

        public EvaluationResult(String modelName, double trainR2, double testR2,
                               double trainRMSE, double testRMSE,
                               double trainMAE, double testMAE,
                               Map<String, Double> additionalMetrics,
                               long trainingTimeMs) {
            this.modelName = modelName;
            this.trainR2 = trainR2;
            this.testR2 = testR2;
            this.trainRMSE = trainRMSE;
            this.testRMSE = testRMSE;
            this.trainMAE = trainMAE;
            this.testMAE = testMAE;
            this.additionalMetrics = additionalMetrics;
            this.trainingTimeMs = trainingTimeMs;
        }
    }

    public EvaluationResult evaluate(RegressionModel model,
                                     double[][] XTrain, double[] yTrain,
                                     double[][] XTest, double[] yTest,
                                     String modelName) {

        long startTime = System.currentTimeMillis();

        model.fit(XTrain, yTrain);

        long trainingTime = System.currentTimeMillis() - startTime;

        double[] trainPred = model.predict(XTrain);
        double[] testPred = model.predict(XTest);

        double trainR2 = RegressionMetrics.r2Score(yTrain, trainPred);
        double testR2 = RegressionMetrics.r2Score(yTest, testPred);
        double trainRMSE = RegressionMetrics.rmse(yTrain, trainPred);
        double testRMSE = RegressionMetrics.rmse(yTest, testPred);
        double trainMAE = RegressionMetrics.mae(yTrain, trainPred);
        double testMAE = RegressionMetrics.mae(yTest, testPred);

        Map<String, Double> additional = new HashMap<>();
        double[] residuals = RegressionMetrics.residuals(yTest, testPred);
        additional.put("maxResidual", Arrays.stream(residuals).max().orElse(0));
        additional.put("minResidual", Arrays.stream(residuals).min().orElse(0));
        additional.put("stdResidual", calculateStd(residuals));

        return new EvaluationResult(modelName, trainR2, testR2,
            trainRMSE, testRMSE, trainMAE, testMAE, additional, trainingTime);
    }

    private static double calculateStd(double[] values) {
        double mean = Arrays.stream(values).average().orElse(0);
        double sqSum = 0;
        for (double v : values) {
            sqSum += (v - mean) * (v - mean);
        }
        return Math.sqrt(sqSum / values.length);
    }

    public static void printResults(List<EvaluationResult> results) {
        System.out.println("╔════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                        MODEL EVALUATION RESULTS                        ║");
        System.out.println("╠═══════════╦══════════╦══════════╦══════════╦══════════╦══════════╦════════╣");
        System.out.println("║   Model   ║Train R²  ║ Test R²  ║Train RMSE║Test RMSE ║Test MAE  ║ Time   ║");
        System.out.println("╠═══════════╬══════════╬══════════╬══════════╬══════════╬══════════╬════════╣");

        for (var r : results) {
            System.out.printf("║ %-9s ║  %.4f   ║  %.4f   ║  %.4f   ║  %.4f   ║  %.4f   ║ %4dms ║%n",
                r.modelName, r.trainR2, r.testR2, r.trainRMSE, r.testRMSE, r.testMAE, r.trainingTimeMs);
        }
        System.out.println("╚═══════════╩══════════╩══════════╩══════════╩══════════╩══════════╩════════╝");
    }
}
```

```java
package com.ml.price.evaluation;

import com.ml.regression.RegressionModel;

import java.util.*;

public class CrossValidator {

    public static class CVResults {
        public final double[] trainR2Scores;
        public final double[] testR2Scores;
        public final double[] trainRMSE;
        public final double[] testRMSE;
        public final double meanTrainR2;
        public final double meanTestR2;
        public final double stdTestR2;
        public final double meanTestRMSE;
        public final double stdTestRMSE;

        public CVResults(double[] trainR2, double[] testR2, double[] trainRMSE, double[] testRMSE) {
            this.trainR2Scores = trainR2;
            this.testR2Scores = testR2;
            this.trainRMSE = trainRMSE;
            this.testRMSE = testRMSE;
            this.meanTrainR2 = mean(trainR2);
            this.meanTestR2 = mean(testR2);
            this.stdTestR2 = std(testR2);
            this.meanTestRMSE = mean(testRMSE);
            this.stdTestRMSE = std(testRMSE);
        }

        private double mean(double[] arr) {
            return Arrays.stream(arr).average().orElse(0);
        }

        private double std(double[] arr) {
            double m = mean(arr);
            double sqSum = 0;
            for (double v : arr) {
                sqSum += (v - m) * (v - m);
            }
            return Math.sqrt(sqSum / arr.length);
        }
    }

    public static CVResults crossValidate(RegressionModel modelTemplate,
                                         double[][] X, double[] y,
                                         int folds, String modelType) {

        int n = X.length;
        int foldSize = n / folds;

        double[] trainR2 = new double[folds];
        double[] testR2 = new double[folds];
        double[] trainRMSE = new double[folds];
        double[] testRMSE = new double[folds];

        for (int fold = 0; fold < folds; fold++) {
            // Create train/test split
            int testStart = fold * foldSize;
            int testEnd = (fold == folds - 1) ? n : (fold + 1) * foldSize;
            int testSize = testEnd - testStart;
            int trainSize = n - testSize;

            double[][] XTrain = new double[trainSize][];
            double[] yTrain = new double[trainSize];
            double[][] XTest = new double[testSize][];
            double[] yTest = new double[testSize];

            int trainIdx = 0;
            for (int i = 0; i < n; i++) {
                if (i >= testStart && i < testEnd) {
                    XTest[i - testStart] = X[i];
                    yTest[i - testStart] = y[i];
                } else {
                    XTrain[trainIdx] = X[i];
                    yTrain[trainIdx] = y[i];
                    trainIdx++;
                }
            }

            // Clone and train model
            RegressionModel model = RegressionModelFactory.create(modelType);
            model.fit(XTrain, yTrain);

            // Evaluate
            trainR2[fold] = model.getScore(XTrain, yTrain);
            testR2[fold] = model.getScore(XTest, yTest);

            double[] trainPred = model.predict(XTrain);
            double[] testPred = model.predict(XTest);

            trainRMSE[fold] = com.ml.regression.evaluation.RegressionMetrics.rmse(yTrain, trainPred);
            testRMSE[fold] = com.ml.regression.evaluation.RegressionMetrics.rmse(yTest, testPred);
        }

        return new CVResults(trainR2, testR2, trainRMSE, testRMSE);
    }
}
```

---

## Hyperparameter Tuning

```java
package com.ml.price.evaluation;

import com.ml.regression.RegressionModel;
import com.ml.regression.evaluation.RegressionMetrics;

import java.util.*;

public class HyperparameterTuner {

    public static class TuningResult {
        public final Map<String, Object> bestParams;
        public final double bestScore;
        public final List<ParamScore> allResults;

        public TuningResult(Map<String, Object> bestParams, double bestScore,
                           List<ParamScore> allResults) {
            this.bestParams = bestParams;
            this.bestScore = bestScore;
            this.allResults = allResults;
        }
    }

    public static class ParamScore {
        public final Map<String, Object> params;
        public final double score;

        public ParamScore(Map<String, Object> params, double score) {
            this.params = params;
            this.score = score;
        }
    }

    public TuningResult tuneRidge(double[][] X, double[] y,
                                  List<Double> alphas,
                                  int folds) {

        List<ParamScore> results = new ArrayList<>();

        for (double alpha : alphas) {
            RegressionModel model = new com.ml.regression.RidgeRegression(alpha);
            var cv = com.ml.price.evaluation.CrossValidator.crossValidate(
                model, X, y, folds, "ridge");

            Map<String, Object> params = new HashMap<>();
            params.put("alpha", alpha);

            results.add(new ParamScore(params, cv.meanTestR2));
        }

        // Find best
        ParamScore best = Collections.max(results, Comparator.comparing(r -> r.score));

        return new TuningResult(best.params, best.score, results);
    }

    public TuningResult tuneElasticNet(double[][] X, double[] y,
                                       List<Double> alphas,
                                       List<Double> l1Ratios,
                                       int folds) {

        List<ParamScore> results = new ArrayList<>();

        for (double alpha : alphas) {
            for (double l1Ratio : l1Ratios) {
                RegressionModel model = new com.ml.regression.ElasticNetRegression(alpha, l1Ratio);
                var cv = com.ml.price.evaluation.CrossValidator.crossValidate(
                    model, X, y, folds, "elastic-net");

                Map<String, Object> params = new HashMap<>();
                params.put("alpha", alpha);
                params.put("l1Ratio", l1Ratio);

                results.add(new ParamScore(params, cv.meanTestR2));
            }
        }

        ParamScore best = Collections.max(results, Comparator.comparing(r -> r.score));

        return new TuningResult(best.params, best.score, results);
    }
}
```

---

## API and Serving

```java
package com.ml.price.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/predictions")
public class PricePredictionController {

    private final PredictionService predictionService;

    public PricePredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/predict")
    public PredictionResponse predict(@RequestBody PredictionRequest request) {
        double[] features = request.getFeatures();
        double predictedPrice = predictionService.predict(features);

        return new PredictionResponse(
            predictedPrice,
            predictionService.getConfidence(),
            predictionService.getModelVersion()
        );
    }

    @PostMapping("/predict-batch")
    public List<PredictionResponse> predictBatch(@RequestBody List<PredictionRequest> requests) {
        return requests.stream()
            .map(req -> predict(req))
            .toList();
    }
}
```

```java
package com.ml.price.api;

import java.util.List;

public record PredictionRequest(
    List<Double> features,
    String modelType,
    Map<String, Object> options
) {
    public double[] getFeatures() {
        return features.stream().mapToDouble(Double::doubleValue).toArray();
    }
}
```

```java
package com.ml.price.api;

public record PredictionResponse(
    double predictedPrice,
    double confidence,
    String modelVersion
) {}
```

```java
package com.ml.price.serving;

import com.ml.regression.RegressionModel;
import com.ml.price.model.ModelLoader;
import org.springframework.stereotype.Service;

@Service
public class PredictionService {

    private RegressionModel model;
    private String modelVersion;
    private double confidence;
    private final ModelLoader modelLoader;

    public PredictionService(ModelLoader modelLoader) {
        this.modelLoader = modelLoader;
        loadModel("elastic-net");
    }

    public void loadModel(String modelType) {
        this.model = modelLoader.loadModel(modelType);
        this.modelVersion = modelLoader.getModelMetadata(modelType).version();
    }

    public double predict(double[] features) {
        double[][] X = new double[][]{features};
        double[] predictions = model.predict(X);
        return predictions[0];
    }

    public double getConfidence() {
        // Simplified confidence estimation
        return 0.85;
    }

    public String getModelVersion() {
        return modelVersion;
    }
}
```

---

## Model Serialization

```java
package com.ml.price.serving;

import com.ml.regression.RegressionModel;
import com.ml.price.model.ModelMetadata;

import java.io.*;

public class ModelLoader {

    private static final String MODEL_DIR = "src/main/resources/models/";

    public RegressionModel loadModel(String modelType) {
        String path = MODEL_DIR + modelType + ".ser";

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (RegressionModel) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load model: " + e.getMessage());
        }
    }

    public void saveModel(RegressionModel model, String modelType, ModelMetadata metadata) {
        String path = MODEL_DIR + modelType + ".ser";

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(model);

            // Save metadata separately
            String metaPath = MODEL_DIR + modelType + ".meta";
            try (ObjectOutputStream metaOos = new ObjectOutputStream(new FileOutputStream(metaPath))) {
                metaOos.writeObject(metadata);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save model: " + e.getMessage());
        }
    }

    public ModelMetadata getModelMetadata(String modelType) {
        String path = MODEL_DIR + modelType + ".meta";

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (ModelMetadata) ois.readObject();
        } catch (Exception e) {
            return new ModelMetadata(modelType, "unknown", System.currentTimeMillis());
        }
    }
}
```

---

## Usage Example

```java
package com.ml.price;

import com.ml.price.config.*;
import com.ml.price.model.*;
import com.ml.price.pipeline.*;
import com.ml.price.evaluation.*;
import com.ml.regression.RegressionModel;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== House Price Prediction Pipeline ===\n");

        // Load data
        DataLoader loader = new DataLoader();
        var data = loader.loadCSV("data/housing.csv");

        double[][] X = data.features();
        double[] y = data.target();

        // Create pipeline
        Config config = new Config();
        DataPipeline pipeline = new DataPipeline(config);

        var processed = pipeline.process(X, y);

        // Split data
        var split = trainTestSplit(processed.features(), processed.target(), 0.2);

        // Evaluate multiple models
        List<String> modelTypes = Arrays.asList(
            "linear-regression", "ridge", "lasso", "elastic-net"
        );

        List<ModelEvaluator.EvaluationResult> results = new ArrayList<>();

        for (String modelType : modelTypes) {
            RegressionModel model = RegressionModelFactory.create(modelType);

            var result = ModelEvaluator.evaluate(
                model,
                split.XTrain(), split.yTrain(),
                split.XTest(), split.yTest(),
                modelType
            );

            results.add(result);
        }

        ModelEvaluator.printResults(results);

        // Tune best model
        System.out.println("\n=== Hyperparameter Tuning (Elastic Net) ===");
        HyperparameterTuner tuner = new HyperparameterTuner();

        List<Double> alphas = Arrays.asList(0.001, 0.01, 0.1, 1.0, 10.0);
        List<Double> l1Ratios = Arrays.asList(0.1, 0.3, 0.5, 0.7, 0.9);

        var tuningResult = tuner.tuneElasticNet(
            processed.features(), processed.target(),
            alphas, l1Ratios, 5
        );

        System.out.printf("Best params: %s%n", tuningResult.bestParams);
        System.out.printf("Best CV R²: %.4f%n", tuningResult.bestScore);

        // Save best model
        RegressionModel bestModel = RegressionModelFactory.createWithConfig(
            "elastic-net", tuningResult.bestParams
        );
        bestModel.fit(processed.features(), processed.target());

        ModelMetadata metadata = new ModelMetadata(
            "elastic-net-tuned",
            "v1.0",
            System.currentTimeMillis()
        );

        ModelLoader modelLoader = new ModelLoader();
        modelLoader.saveModel(bestModel, "elastic-net-tuned", metadata);

        System.out.println("\n✓ Model saved successfully!");
    }

    private static TrainTestSplit trainTestSplit(double[][] X, double[] y, double testSize) {
        int n = X.length;
        int trainSize = (int)(n * (1 - testSize));

        double[][] XTrain = new double[trainSize][];
        double[] yTrain = new double[trainSize];
        double[][] XTest = new double[n - trainSize][];
        double[] yTest = new double[n - trainSize];

        System.arraycopy(X, 0, XTrain, 0, trainSize);
        System.arraycopy(y, 0, yTrain, 0, trainSize);
        System.arraycopy(X, trainSize, XTest, 0, n - trainSize);
        System.arraycopy(y, trainSize, yTest, 0, n - trainSize);

        return new TrainTestSplit(XTrain, yTrain, XTest, yTest);
    }

    record TrainTestSplit(double[][] XTrain, double[] yTrain,
                        double[][] XTest, double[] yTest) {}
}
```

---

## Key Features

1. **Complete Pipeline**: Data cleaning, feature engineering, scaling
2. **Multiple Models**: Linear, Ridge, Lasso, Elastic Net support
3. **Hyperparameter Tuning**: Grid search with cross-validation
4. **Model Serialization**: Save/load trained models
5. **REST API**: Real-time predictions via HTTP
6. **Comprehensive Metrics**: R², RMSE, MAE with cross-validation
7. **Production Ready**: Configuration, logging, error handling

---

## Unit Tests

```java
package com.ml.price;

import com.ml.regression.*;
import com.ml.price.evaluation.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ModelTests {

    @Test
    public void testLinearRegression() {
        double[][] X = {{1, 1}, {1, 2}, {1, 3}};
        double[] y = {2, 3, 4};

        LinearRegression model = new LinearRegression();
        model.fit(X, y);

        double[] pred = model.predict(X);
        assertTrue(Math.abs(pred[0] - 2) < 0.1);
    }

    @Test
    public void testRidgeCoefficientShrinkage() {
        double[][] X = generateCorrelatedFeatures(100, 20);
        double[] y = generateTarget(X);

        LinearRegression lr = new LinearRegression();
        lr.fit(X, y);

        RidgeRegression ridge = new RidgeRegression(10.0);
        ridge.fit(X, y);

        double lrNorm = norm(lr.getCoefficients());
        double ridgeNorm = norm(ridge.getCoefficients());

        assertTrue(ridgeNorm < lrNorm, "Ridge should shrink coefficients");
    }

    @Test
    public void testLassoSparsity() {
        double[][] X = generateCorrelatedFeatures(100, 10);
        double[] y = generateTarget(X);

        LassoRegression lasso = new LassoRegression(0.1);
        lasso.fit(X, y);

        long nonZero = Arrays.stream(lasso.getCoefficients())
            .filter(c -> Math.abs(c) > 1e-6)
            .count();

        assertTrue(nonZero < 10, "Lasso should create sparse solution");
    }

    private double[][] generateCorrelatedFeatures(int n, int d) {
        java.util.Random rand = new java.util.Random(42);
        double[][] X = new double[n][d];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < d; j++) {
                X[i][j] = rand.nextDouble() * 10;
            }
        }
        return X;
    }

    private double[] generateTarget(double[][] X) {
        double[] y = new double[X.length];
        for (int i = 0; i < X.length; i++) {
            y[i] = X[i][0] * 2 + X[i][1] * 3 + Math.random() * 0.1;
        }
        return y;
    }

    private double norm(double[] arr) {
        double sum = 0;
        for (double v : arr) sum += v * v;
        return Math.sqrt(sum);
    }
}
```

---

## Dependencies

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web:3.1.0'
    implementation 'org.apache.commons:commons-math3:3.6.1'
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.15.2'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.9.3'
}
```

This production-ready system provides comprehensive price prediction capabilities with proper ML engineering practices including feature engineering, model selection, hyperparameter tuning, and deployment infrastructure.