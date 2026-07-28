# Problem Walkthrough: Model Interpretation Dashboard

## Problem
Build an interpretation framework for a black-box model that computes SHAP values, permutation importance, and partial dependence plots for a given dataset and model.

## Step 1: Model Interface

```java
@FunctionalInterface
public interface Predictor {
    double predict(double[] features);
    
    default double[] predictBatch(double[][] X) {
        return Arrays.stream(X).mapToDouble(this::predict).toArray();
    }
}
```

## Step 2: Kernel SHAP Implementation

```java
public class KernelSHAPExplainer {
    private final Predictor model;
    private final double[][] background;
    private final int nSamples;
    
    public KernelSHAPExplainer(Predictor model, double[][] background, int nSamples) {
        this.model = model;
        this.background = background;
        this.nSamples = nSamples;
    }
    
    public double[] explain(double[] instance) {
        int m = instance.length;
        double[][] coalitions = new double[nSamples][m];
        double[] preds = new double[nSamples];
        double[] weights = new double[nSamples];
        
        // Baseline prediction: average over background
        double baseline = model.predictBatch(background).average();
        
        for (int s = 0; s < nSamples; s++) {
            // Sample random coalition
            int size = 0;
            double[] input = new double[m];
            int bgIdx = rng.nextInt(background.length);
            for (int j = 0; j < m; j++) {
                if (rng.nextDouble() < 0.5) {
                    coalitions[s][j] = 1.0;
                    input[j] = instance[j];
                    size++;
                } else {
                    coalitions[s][j] = 0.0;
                    input[j] = background[bgIdx][j];
                }
            }
            preds[s] = model.predict(input) - baseline;
            weights[s] = kernelWeight(size, m);
        }
        
        // Weighted least squares: Z * φ ≈ preds
        // Z[i] = coalition vector for sample i, with intercept
        double[][] Z = new double[nSamples][m + 1];
        for (int s = 0; s < nSamples; s++) {
            Z[s][0] = 1.0; // intercept
            System.arraycopy(coalitions[s], 0, Z[s], 1, m);
        }
        
        double[] phi = weightedOLS(Z, preds, weights);
        // phi[0] = intercept (baseline), phi[1..m] = SHAP values
        return Arrays.copyOfRange(phi, 1, phi.length);
    }
    
    private double kernelWeight(int size, int m) {
        if (size == 0 || size == m) return 1000000; // large weight for empty/full coalitions
        return (double) (m - 1) / (size * (m - size) * binom(m, size));
    }
}
```

## Step 3: Summary Plot (Feature Importance Ranking)

```java
public class SHAPSummary {
    public record SummaryRow(int featureIndex, double meanAbsSHAP, double[] values) {}
    
    public List<SummaryRow> compute(double[][] X, double[][] shapValues) {
        int p = X[0].length;
        List<SummaryRow> rows = new ArrayList<>();
        for (int j = 0; j < p; j++) {
            double[] colSHAP = getColumn(shapValues, j);
            double meanAbs = Arrays.stream(colSHAP).map(Math::abs).average().orElseThrow();
            rows.add(new SummaryRow(j, meanAbs, colSHAP));
        }
        rows.sort(Comparator.comparingDouble(SummaryRow::meanAbsSHAP).reversed());
        return rows;
    }
}
```

## Step 4: Dependence Plot with Interaction

```java
public class SHAPDependence {
    public record DependencePoint(double featureValue, double shapValue, int interactionFeature, double interactionValue) {}
    
    public List<DependencePoint> plot(double[] Xj, double[] shapJ, double[] interactionVals) {
        List<DependencePoint> points = new ArrayList<>();
        for (int i = 0; i < Xj.length; i++) {
            points.add(new DependencePoint(Xj[i], shapJ[i], -1, interactionVals[i]));
        }
        return points;
    }
}
```

## Step 5: Verification

```java
@Test
public void testShapValues() {
    // Simple linear model: f(x) = x0 + 2*x1
    Predictor linear = (x) -> x[0] + 2 * x[1];
    double[][] bg = {{0, 0}, {1, 1}};
    
    KernelSHAPExplainer explainer = new KernelSHAPExplainer(linear, bg, 1000);
    double[] shap = explainer.explain(new double[]{3.0, 4.0});
    
    // SHAP values should approximately equal coefficients times (feature - baseline)
    // phi_0 ≈ 1 * (3 - 0.5) = 2.5
    // phi_1 ≈ 2 * (4 - 0.5) = 7.0
    assertEquals(2.5, shap[0], 0.5);
    assertEquals(7.0, shap[1], 0.5);
}
```
