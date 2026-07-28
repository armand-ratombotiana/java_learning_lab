# Causal Inference Guide

## Potential Outcomes Framework

For each unit i, define:
- Y_i(1): outcome if treated
- Y_i(0): outcome if control
- T_i: treatment indicator (1 = treated, 0 = control)

**Individual Treatment Effect (ITE)**: τ_i = Y_i(1) - Y_i(0) — unobservable (fundamental problem of causal inference)

**Average Treatment Effect (ATE)**: E[Y(1) - Y(0)]

**Average Treatment Effect on the Treated (ATT)**: E[Y(1) - Y(0) | T = 1]

## Identification Assumptions

1. **Unconfoundedness** (ignorability): Y(1), Y(0) ⟂ T | X
2. **Overlap** (positivity): 0 < P(T=1|X) < 1
3. **Consistency**: Y = Y(T) (observed outcome matches potential outcome for assigned treatment)
4. **SUTVA**: No interference between units

## Methods

### 1. Propensity Score Matching

```java
public record PropensityScore(double[] scores, double[] weights) {
    public static PropensityScore estimate(double[][] features, boolean[] treated) {
        // Logistic regression via gradient descent
        double[] beta = new double[features[0].length];
        for (int iter = 0; iter < 100; iter++) {
            double[] gradient = new double[beta.length];
            for (int i = 0; i < features.length; i++) {
                double linear = dot(beta, features[i]);
                double pred = sigmoid(linear);
                double error = (treated[i] ? 1.0 : 0.0) - pred;
                for (int j = 0; j < beta.length; j++) {
                    gradient[j] += error * features[i][j];
                }
            }
            for (int j = 0; j < beta.length; j++) {
                beta[j] += gradient[j] / features.length;
            }
        }
        double[] scores = new double[features.length];
        for (int i = 0; i < features.length; i++) {
            scores[i] = sigmoid(dot(beta, features[i]));
        }
        // Inverse probability of treatment weights
        double[] weights = new double[features.length];
        for (int i = 0; i < features.length; i++) {
            weights[i] = treated[i] ? 1.0 / scores[i] : 1.0 / (1.0 - scores[i]);
        }
        return new PropensityScore(scores, weights);
    }
}
```

### 2. Difference-in-Differences (DiD)

\[
ATT_{DiD} = (E[Y_{t=1} | T=1] - E[Y_{t=0} | T=1]) - (E[Y_{t=1} | T=0] - E[Y_{t=0} | T=0])
\]

```java
public record DiffInDiff(double att) {
    public static DiffInDiff estimate(
            double[] preTreatment, double[] postTreatment,
            double[] preControl, double[] postControl) {
        double dT = mean(postTreatment) - mean(preTreatment);
        double dC = mean(postControl) - mean(preControl);
        return new DiffInDiff(dT - dC);
    }

    public double standardError(double[] preT, double[] postT, double[] preC, double[] postC) {
        double varT = variance(postT, mean(postT)) / postT.length
                    + variance(preT, mean(preT)) / preT.length;
        double varC = variance(postC, mean(postC)) / postC.length
                    + variance(preC, mean(preC)) / preC.length;
        return Math.sqrt(varT + varC);
    }
}
```

### 3. Instrumental Variables (2SLS)

When treatment assignment is confounded, use an instrument Z that:
1. **Relevance**: Z correlates with T
2. **Exclusion**: Z affects Y only through T
3. **Exogeneity**: Z is as-if randomly assigned

```java
public record TwoStageLeastSquares(double betaIV) {
    public static TwoStageLeastSquares estimate(
            double[] instrument, double[] treatment, double[] outcome) {
        // Stage 1: T ~ Z
        double covZT = covariance(instrument, treatment);
        double varZ = variance(instrument, mean(instrument));
        double pi = covZT / varZ;
        double[] treatmentHat = Arrays.stream(instrument).map(z -> pi * z).toArray();
        
        // Stage 2: Y ~ T_hat
        double covTY = covariance(treatmentHat, outcome);
        double varTHat = variance(treatmentHat, mean(treatmentHat));
        double beta = covTY / varTHat;
        
        return new TwoStageLeastSquares(beta);
    }
}
```

### 4. Sensitivity Analysis (Rosenbaum Bounds)

```java
public record RosenbaumBounds(double gamma, double pValueUpper, double pValueLower) {
    // Gamma = odds of differential assignment due to unobserved confounder
    // Γ = 1 means no hidden bias; Γ > 1 tests sensitivity
    
    public static RosenbaumBounds test(
            double[] matchedOutcomes, boolean[] treated, double gamma) {
        int n = matchedOutcomes.length;
        double[] signedScores = new double[n / 2];
        for (int pair = 0; pair < n / 2; pair++) {
            int i = treated[pair * 2] ? pair * 2 : pair * 2 + 1;
            int j = treated[pair * 2] ? pair * 2 + 1 : pair * 2;
            double diff = matchedOutcomes[i] - matchedOutcomes[j];
            signedScores[pair] = diff > 0 ? 1.0 / (1.0 + 1.0 / gamma) : 1.0 / (1.0 + gamma);
        }
        double sumScores = Arrays.stream(signedScores).sum();
        double se = Math.sqrt(Arrays.stream(signedScores)
            .map(s -> s * (1 - s)).sum());
        double z = (sumScores - n / 4.0) / se;
        double pUpper = 1.0 - normalCDF(z);
        double pLower = normalCDF(z);
        return new RosenbaumBounds(gamma, pUpper, pLower);
    }
}
```

## Diagnostics

### Balance Check (Standardized Mean Difference)

```java
public double smd(double[] treated, double[] control) {
    double diff = mean(treated) - mean(control);
    double pooledSD = Math.sqrt((variance(treated, mean(treated)) + variance(control, mean(control))) / 2.0);
    return diff / pooledSD;
}
```

### Overlap Assessment
Check propensity score distributions: use histograms and Kolmogorov-Smirnov test.
