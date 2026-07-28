# Problem Walkthrough: Automated ML Pipeline

## Problem
Build an end-to-end automated pipeline that performs feature selection, model selection, hyperparameter tuning, and ensembling — all with proper cross-validation.

## Step 1: Feature Selection Pipeline

```java
public class SequentialFeatureSelector {
    public enum Direction { FORWARD, BACKWARD }
    
    public int[] select(Predictor model, double[][] X, double[] y, int nFeatures, Direction dir) {
        int p = X[0].length;
        boolean[] selected = new boolean[p];
        int currentCount = dir == Direction.BACKWARD ? p : 0;
        
        while (Math.abs(currentCount - nFeatures) > 0) {
            double bestScore = dir == Direction.FORWARD ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            int bestFeature = -1;
            
            for (int j = 0; j < p; j++) {
                if (selected[j] == (dir == Direction.FORWARD)) continue;
                selected[j] = (dir == Direction.FORWARD);
                double[][] subX = selectColumns(X, selected);
                double[] scores = crossValidate(model, subX, y, 5);
                double meanScore = Arrays.stream(scores).average().orElseThrow();
                
                if ((dir == Direction.FORWARD && meanScore > bestScore) ||
                    (dir == Direction.BACKWARD && meanScore < bestScore)) {
                    bestScore = meanScore;
                    bestFeature = j;
                }
                selected[j] = !(dir == Direction.FORWARD);
            }
            
            if (bestFeature >= 0) {
                selected[bestFeature] = dir == Direction.FORWARD;
                currentCount += dir == Direction.FORWARD ? 1 : -1;
            }
        }
        
        List<Integer> result = new ArrayList<>();
        for (int j = 0; j < p; j++) if (selected[j]) result.add(j);
        return result.stream().mapToInt(Integer::intValue).toArray();
    }
}
```

## Step 2: Hyperparameter Optimization

```java
public class HyperparameterOptimizer {
    public record Trial(Map<String, Double> params, double score) {}
    
    public Trial randomSearch(ModelSpec model, double[][] X, double[] y, int nTrials) {
        Trial best = null;
        Random rng = new Random(42L);
        for (int t = 0; t < nTrials; t++) {
            Map<String, Double> params = model.sampleParams(rng);
            Predictor m = model.build(params);
            m.fit(X, y);
            double score = crossValScore(m, X, y, 5);
            if (best == null || score > best.score()) best = new Trial(params, score);
        }
        return best;
    }
}
```

## Step 3: Ensemble Construction

```java
public class EnsembleSelector {
    public Predictor selectEnsemble(List<Predictor> models, double[][] Xval, double[] yval) {
        int n = models.size();
        double[][] preds = new double[Xval.length][n];
        for (int i = 0; i < n; i++) preds[i] = models.get(i).predictBatch(Xval);
        
        // Find optimal weights via constrained optimization (simplex)
        double[] weights = optimizeWeights(preds, yval);
        
        return (features) -> {
            double sum = 0;
            for (int i = 0; i < n; i++) sum += weights[i] * models.get(i).predict(features);
            return sum;
        };
    }
    
    private double[] optimizeWeights(double[][] preds, double[] y) {
        int n = preds[0].length;
        double[] weights = new double[n];
        Arrays.fill(weights, 1.0 / n);
        
        // Simple iterative weight optimization (coordinate ascent)
        for (int iter = 0; iter < 100; iter++) {
            for (int j = 0; j < n; j++) {
                double bestW = weights[j];
                double bestScore = score(weights, preds, y);
                for (double w = 0.01; w <= 0.99; w += 0.05) {
                    weights[j] = w;
                    // Renormalize
                    double sum = Arrays.stream(weights).sum();
                    for (int k = 0; k < n; k++) weights[k] /= sum;
                    double s = score(weights, preds, y);
                    if (s > bestScore) { bestScore = s; bestW = weights[j]; }
                }
                weights[j] = bestW;
            }
        }
        return weights;
    }
}
```

## Step 4: Nested Cross-Validation

```java
public class NestedCV {
    public record CVResult(double outerScore, Map<String, Double> bestParams) {}
    
    public CVResult evaluate(AutoMLPipeline pipeline, double[][] X, double[] y, int outerFolds, int innerFolds) {
        int n = X.length;
        double[] outerScores = new double[outerFolds];
        Map<String, Double> bestParams = null;
        
        CrossValidationSplitter outerCV = new CrossValidationSplitter(outerFolds);
        for (int fold = 0; fold < outerFolds; fold++) {
            double[][] trainX = outerCV.getTrain(X, fold);
            double[] trainY = outerCV.getTrainLabels(y, fold);
            double[][] testX = outerCV.getTest(X, fold);
            double[] testY = outerCV.getTestLabels(y, fold);
            
            // Inner CV for model selection
            Predictor model = pipeline.fitWithInnerCV(trainX, trainY, innerFolds);
            double[] preds = model.predictBatch(testX);
            outerScores[fold] = rmse(preds, testY);
            
            if (pipeline.getBestParams() != null) bestParams = pipeline.getBestParams();
        }
        
        return new CVResult(Arrays.stream(outerScores).average().orElseThrow(), bestParams);
    }
}
```

## Step 5: Verification

```java
@Test
public void testPipelineEndToEnd() {
    double[][] X = generateData(1000, 20);
    double[] y = generateTarget(X);
    
    AutoMLPipeline pipeline = new AutoMLPipeline(
        new MutualInfoFilter(10),  // select top 10 features
        new BayesianOptimization(5, 20),  // 5 initial + 20 iterations
        new StackingEnsemble(),
        5
    );
    
    Predictor model = pipeline.fit(X, y);
    double score = rmse(model.predictBatch(Xtest), ytest);
    assertTrue(score < 0.5);
    
    // Feature selection should identify informative features
    assertEquals(10, pipeline.getSelectedFeatures().length);
}
```
