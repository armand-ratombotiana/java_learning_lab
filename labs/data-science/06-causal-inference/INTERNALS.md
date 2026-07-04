# Internals: Propensity Score Matching

## How PSM Works Step by Step

### Step 1: Estimate Propensity Scores

Fit a logistic regression: $P(T=1|X) = \frac{1}{1 + e^{-X\beta}}$

```java
public class PropensityEstimator {
    private LogisticRegression model;
    
    public double[] estimate(Table data, String treatmentCol, List<String> covariates) {
        double[][] X = extractCovariates(data, covariates);
        double[] T = data.booleanColumn(treatmentCol).asDoubleArray();
        
        model = new LogisticRegression();
        model.fit(X, T);
        return model.predictProbabilities(X);  // propensity scores
    }
}
```

### Step 2: Match Treated to Control

Nearest-neighbor matching on propensity scores:

```java
public class NearestNeighborMatcher {
    public int[] match(double[] treatedScores, double[] controlScores) {
        int[] matches = new int[treatedScores.length];
        boolean[] used = new boolean[controlScores.length];
        
        for (int i = 0; i < treatedScores.length; i++) {
            double bestDist = Double.MAX_VALUE;
            int bestIdx = -1;
            for (int j = 0; j < controlScores.length; j++) {
                if (used[j]) continue;
                double dist = Math.abs(treatedScores[i] - controlScores[j]);
                if (dist < bestDist) {
                    bestDist = dist;
                    bestIdx = j;
                }
            }
            matches[i] = bestIdx;
            used[bestIdx] = true;  // match without replacement
        }
        return matches;
    }
}
```

### Step 3: Estimate Treatment Effect

```java
double[] yTreated = /* outcomes for treated units */;
double[] yMatchedControl = /* outcomes for matched control units */;
double ate = mean(yTreated) - mean(yMatchedControl);
```

### Step 4: Check Balance

After matching, compare covariate means between treated and matched control. Standardized mean differences should be < 0.1.

```java
public boolean checkBalance(double[] treated, double[] control) {
    double smd = Math.abs(mean(treated) - mean(control)) / 
                 Math.sqrt((variance(treated) + variance(control)) / 2);
    return smd < 0.1;
}
```
