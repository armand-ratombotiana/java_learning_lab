# Model Interpretation Guide

## 1. SHAP (SHapley Additive exPlanations)

Based on Shapley values from cooperative game theory. For a prediction f(x), SHAP decomposes:
f(x) = φ₀ + Σ φⱼ where φ₀ = E[f(X)] (baseline).

### Kernel SHAP (Model-agnostic)

```java
public class KernelSHAP {
    private final DoubleUnaryOperator model;
    private final double[] baseline;
    private final int nFeatures;
    private final int nSamples;
    
    public KernelSHAP(DoubleUnaryOperator model, double[] baseline, int nFeatures, int nSamples) {
        this.model = model;
        this.baseline = baseline;
        this.nFeatures = nFeatures;
        this.nSamples = nSamples;
    }
    
    public double[] explain(double[] instance) {
        // Sample coalitions (binary vectors: 1 = feature present, 0 = absent)
        double[][] coalitions = new double[nSamples][nFeatures];
        double[] predictions = new double[nSamples];
        double[] weights = new double[nSamples];
        
        for (int s = 0; s < nSamples; s++) {
            int coalitionSize = 0;
            for (int j = 0; j < nFeatures; j++) {
                coalitions[s][j] = rng.nextDouble() < 0.5 ? 0.0 : 1.0;
                if (coalitions[s][j] > 0) coalitionSize++;
            }
            // Construct input: instance features where coalition=1, baseline where coalition=0
            double[] input = makeInput(instance, coalitions[s]);
            predictions[s] = model.applyAsDouble(input);
            // SHAP kernel weight: (m-1)! / (|S|! * (m-|S|-1)! * C(m, |S|))
            weights[s] = shapleyKernelWeight(coalitionSize, nFeatures);
        }
        
        // Weighted linear regression: coalition -> prediction
        return weightedLinearRegression(coalitions, predictions, weights);
    }
}
```

### TreeSHAP (for tree-based models)

```java
public class TreeSHAP {
    public double[] explain(double[] instance, DecisionTree tree) {
        int nFeatures = instance.length;
        double[] phi = new double[nFeatures];
        recursiveShap(tree.root(), instance, new boolean[nFeatures], 1.0, phi);
        return phi;
    }
    
    private void recursiveShap(TreeNode node, double[] instance, boolean[] mask, double weight, double[] phi) {
        if (node.isLeaf()) {
            for (int j = 0; j < phi.length; j++) {
                if (!mask[j]) phi[j] += weight * node.getPrediction();
            }
            return;
        }
        int splitFeature = node.getSplitFeature();
        // Case 1: feature is used in this path
        mask[splitFeature] = true;
        if (instance[splitFeature] <= node.getThreshold()) {
            recursiveShap(node.left(), instance, mask, weight, phi);
        } else {
            recursiveShap(node.right(), instance, mask, weight, phi);
        }
        mask[splitFeature] = false;
        // Case 2: feature is not used (average of both children)
        recursiveShap(node.left(), instance, mask, weight * node.leftFraction(), phi);
        recursiveShap(node.right(), instance, mask, weight * node.rightFraction(), phi);
    }
}
```

## 2. LIME (Local Interpretable Model-agnostic Explanations)

```java
public class LIME {
    private final DoubleUnaryOperator model;
    private final int nFeatures;
    private final int nSamples;
    private final double kernelWidth;
    
    public LIMEExplanation explain(double[] instance) {
        // Generate perturbed samples around instance
        double[][] samples = new double[nSamples][nFeatures];
        double[] predictions = new double[nSamples];
        double[] distances = new double[nSamples];
        
        for (int s = 0; s < nSamples; s++) {
            for (int j = 0; j < nFeatures; j++) {
                samples[s][j] = instance[j] + kernelWidth * rng.nextGaussian();
            }
            predictions[s] = model.applyAsDouble(samples[s]);
            distances[s] = euclideanDistance(instance, samples[s]);
        }
        
        // Exponential kernel weights
        double[] weights = new double[nSamples];
        double maxDist = Arrays.stream(distances).max().orElseThrow();
        for (int s = 0; s < nSamples; s++) {
            weights[s] = Math.exp(-distances[s] * distances[s] / (2.0 * kernelWidth * kernelWidth));
        }
        
        // Fit interpretable model (approximator)
        double[] coefficients = ridgeRegression(samples, predictions, weights);
        return new LIMEExplanation(coefficients);
    }
}
```

## 3. Permutation Importance

```java
public class PermutationImportance {
    public record FeatureImportance(int featureIndex, double importance, double std) {}
    
    public List<FeatureImportance> compute(double[][] X, double[] y, 
                                            DoubleFunction<double[]> model, int nRepeats) {
        int p = X[0].length;
        double baseline = evaluateModel(model, X, y);
        double[] importances = new double[p];
        double[] stds = new double[p];
        
        for (int j = 0; j < p; j++) {
            double[] scores = new double[nRepeats];
            for (int r = 0; r < nRepeats; r++) {
                double[][] XPermuted = permuteColumn(X, j);
                scores[r] = baseline - evaluateModel(model, XPermuted, y);
            }
            importances[j] = Arrays.stream(scores).average().orElseThrow();
            stds[j] = Math.sqrt(Arrays.stream(scores).map(s -> Math.pow(s - importances[j], 2)).sum() / (nRepeats - 1));
        }
        return IntStream.range(0, p)
            .mapToObj(i -> new FeatureImportance(i, importances[i], stds[i]))
            .sorted(Comparator.comparingDouble(FeatureImportance::importance).reversed())
            .toList();
    }
}
```

## 4. Partial Dependence Plots

```java
public class PartialDependence {
    public record PDPPoint(double featureValue, double averagePrediction, double[] iceCurves) {}
    
    public List<PDPPoint> compute(double[][] X, int featureIndex, double[] grid, 
                                   DoubleFunction<double[]> model) {
        List<PDPPoint> points = new ArrayList<>();
        for (double g : grid) {
            double[] predictions = new double[X.length];
            double[][] ice = new double[X.length][];
            for (int i = 0; i < X.length; i++) {
                double[] modified = X[i].clone();
                modified[featureIndex] = g;
                double pred = model.apply(modified);
                predictions[i] = pred;
            }
            points.add(new PDPPoint(g, Arrays.stream(predictions).average().orElseThrow(), ice));
        }
        return points;
    }
}
```

## 5. Friedman's H-Statistic for Interactions

```java
public record HStatistic(double h, int featureI, int featureJ) {
    // H²_jk = Σ_i [PD_jk(x_iⱼ, x_ᵢₖ) - PD_j(x_iⱼ) - PD_k(x_ᵢₖ)]² / Σ_i PD_jk(x_iⱼ, x_ᵢₖ)²
    
    public static HStatistic compute(double[][] X, int j, int k, DoubleFunction<double[]> model) {
        int n = X.length;
        double num = 0, den = 0;
        for (int i = 0; i < n; i++) {
            double pdJk = computePartialDependence(X, j, k, X[i][j], X[i][k], model);
            double pdJ = computePartialDependence(X, j, X[i][j], model);
            double pdK = computePartialDependence(X, k, X[i][k], model);
            double diff = pdJk - pdJ - pdK;
            num += diff * diff;
            den += pdJk * pdJk;
        }
        return new HStatistic(Math.sqrt(num / den), j, k);
    }
}
```
