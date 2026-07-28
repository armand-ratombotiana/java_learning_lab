# ML Pipeline Automation Guide

## 1. Feature Selection Pipeline

### Filter Methods
```java
public sealed interface FilterSelector permits VarianceFilter, CorrelationFilter, MutualInfoFilter {
    int[] select(double[][] X, double[] y, int k);
}

public record MutualInfoFilter(int bins) implements FilterSelector {
    @Override
    public int[] select(double[][] X, double[] y, int k) {
        int p = X[0].length;
        double[] scores = new double[p];
        for (int j = 0; j < p; j++) {
            scores[j] = mutualInformation(getCol(X, j), y, bins);
        }
        return topK(scores, k);
    }
}
```

### Wrapper Methods (Recursive Feature Elimination)
```java
public class RecursiveFeatureElimination {
    private final Predictor model;
    private final int minFeatures;
    
    public int[] select(double[][] X, double[] y) {
        int n = X.length, p = X[0].length;
        boolean[] selected = new boolean[p];
        Arrays.fill(selected, true);
        int currentCount = p;
        
        while (currentCount > minFeatures) {
            double[][] subX = selectColumns(X, selected);
            double[] cvScores = crossValidate(model, subX, y, 5);
            double baselineScore = Arrays.stream(cvScores).average().orElseThrow();
            
            int worstFeature = -1;
            double worstScore = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < p; j++) {
                if (!selected[j]) continue;
                selected[j] = false;
                double[][] subX_j = selectColumns(X, selected);
                double[] scores = crossValidate(model, subX_j, y, 5);
                double meanScore = Arrays.stream(scores).average().orElseThrow();
                if (meanScore > worstScore) {
                    worstScore = meanScore;
                    worstFeature = j;
                }
                selected[j] = true;
            }
            if (worstFeature >= 0) {
                selected[worstFeature] = false;
                currentCount--;
            }
        }
        
        List<Integer> result = new ArrayList<>();
        for (int j = 0; j < p; j++) if (selected[j]) result.add(j);
        return result.stream().mapToInt(Integer::intValue).toArray();
    }
}
```

### Embedded (L1-Regularization)
```java
public record LassoSelector(double lambda) implements EmbeddedSelector {
    @Override
    public int[] select(double[][] X, double[] y) {
        double[] beta = fitLasso(X, y, lambda);
        List<Integer> selected = new ArrayList<>();
        for (int j = 0; j < beta.length; j++) {
            if (Math.abs(beta[j]) > 1e-6) selected.add(j);
        }
        return selected.stream().mapToInt(Integer::intValue).toArray();
    }
}
```

## 2. Hyperparameter Tuning

### Grid Search
```java
public record GridSearch(Map<String, double[]> paramGrid) {
    public record GridResult(Map<String, Double> params, double score) {}
    
    public GridResult search(double[][] X, double[] y, ModelBuilder builder) {
        List<String> paramNames = new ArrayList<>(paramGrid.keySet());
        List<List<Double>> grids = paramGrid.values().stream()
            .map(a -> Arrays.stream(a).boxed().toList()).toList();
        
        GridResult best = null;
        for (List<Double> combo : cartesianProduct(grids)) {
            Map<String, Double> params = new HashMap<>();
            for (int i = 0; i < paramNames.size(); i++) params.put(paramNames.get(i), combo.get(i));
            Predictor model = builder.build(params);
            double[] scores = crossValidate(model, X, y, 5);
            double meanScore = Arrays.stream(scores).average().orElseThrow();
            if (best == null || meanScore > best.score()) {
                best = new GridResult(params, meanScore);
            }
        }
        return best;
    }
}
```

### Bayesian Optimization (via Gaussian Process surrogate)
```java
public record BayesianOptimization(int nInitial, int nIter) {
    public Map<String, Double> optimize(double[][] X, double[] y, 
                                          Map<String, double[]> searchSpace, ModelBuilder builder) {
        // Initial random points
        List<Map<String, Double>> trials = new ArrayList<>();
        List<Double> scores = new ArrayList<>();
        for (int i = 0; i < nInitial; i++) {
            Map<String, Double> params = sampleRandom(searchSpace);
            Predictor model = builder.build(params);
            double score = mean(crossValidate(model, X, y, 5));
            trials.add(params); scores.add(score);
        }
        
        for (int i = 0; i < nIter; i++) {
            // Fit GP surrogate
            GaussianProcess gp = fitGP(trials, scores);
            
            // Find next point via expected improvement
            Map<String, Double> next = argmaxEI(gp, searchSpace, max(scores));
            Predictor model = builder.build(next);
            double score = mean(crossValidate(model, X, y, 5));
            trials.add(next); scores.add(score);
        }
        
        return trials.get(argmax(scores));
    }
}
```

## 3. Ensembling

### Stacking
```java
public class StackingEnsemble {
    private final List<ModelBuilder> baseLearners;
    private final ModelBuilder metaLearner;
    
    public Predictor train(double[][] X, double[] y) {
        int n = X.length;
        int k = baseLearners.size();
        double[][] metaFeatures = new double[n][k];
        
        // K-fold out-of-fold predictions for each base learner
        CrossValidationSplitter cv = new CrossValidationSplitter(5);
        for (int m = 0; m < k; m++) {
            for (int fold = 0; fold < 5; fold++) {
                double[][] trainX = cv.getTrain(X, fold);
                double[] trainY = cv.getTrainLabels(y, fold);
                double[][] validX = cv.getTest(X, fold);
                Predictor foldModel = baseLearners.get(m).build(trainX, trainY);
                double[] preds = foldModel.predictBatch(validX);
                for (int i = 0; i < preds.length; i++) {
                    metaFeatures[cv.getTestIndices(fold)[i]][m] = preds[i];
                }
            }
        }
        // Train meta-learner on out-of-fold predictions
        Predictor meta = metaLearner.build(metaFeatures, y);
        
        // Retrain base learners on full data
        List<Predictor> finalBaseModels = baseLearners.stream()
            .map(bl -> bl.build(X, y)).toList();
        
        return (features) -> {
            double[] basePreds = new double[k];
            for (int m = 0; m < k; m++) basePreds[m] = finalBaseModels.get(m).predict(features);
            return meta.predict(basePreds);
        };
    }
}
```

### Voting
```java
public record VotingEnsemble(List<Predictor> models, double[] weights) implements Predictor {
    @Override
    public double predict(double[] features) {
        double sum = 0, wSum = 0;
        for (int i = 0; i < models.size(); i++) {
            sum += weights[i] * models.get(i).predict(features);
            wSum += weights[i];
        }
        return sum / wSum;
    }
}
```

## 4. Complete Pipeline

```java
public record AutoMLPipeline(FeatureSelector selector, HyperparameterTuner tuner, 
                              EnsembleBuilder ensemble, int cvFolds) {
    public Predictor fit(double[][] X, double[] y) {
        // 1. Feature selection
        int[] selectedFeatures = selector.select(X, y);
        double[][] XSelected = selectColumns(X, selectedFeatures);
        
        // 2. Hyperparameter tuning for each candidate model
        Map<String, Predictor> candidates = new HashMap<>();
        for (String modelType : List.of("linear", "tree", "gbm")) {
            Map<String, Double> bestParams = tuner.tune(XSelected, y, modelType);
            candidates.put(modelType, builder.build(modelType, bestParams, XSelected, y));
        }
        
        // 3. Ensemble the best candidates
        List<Predictor> topModels = candidates.values().stream()
            .sorted(Comparator.comparingDouble(m -> -cvScore(m, XSelected, y, cvFolds)))
            .limit(3).toList();
        
        return ensemble.build(topModels, XSelected, y);
    }
}
```
