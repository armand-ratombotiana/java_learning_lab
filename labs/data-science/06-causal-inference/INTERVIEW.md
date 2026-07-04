# Interview Questions: Causal Inference

## Conceptual

1. **Define confounding. How does it differ from a collider?**
   - Confounding: a variable causes both treatment and outcome → creates spurious correlation
   - Collider: a variable caused by both treatment and outcome → conditioning on it creates bias

2. **What is the fundamental problem of causal inference?**
   - We can only observe one potential outcome per unit. The counterfactual is always missing.

3. **Explain the difference between ATE, ATT, and CATE. When would you use each?**
   - ATE (Average Treatment Effect): effect on the whole population
   - ATT (Average Treatment Effect on the Treated): effect on those who received treatment
   - CATE (Conditional ATE): effect conditional on covariates — personalization

## Coding

4. **Implement a function in Java that estimates the Average Treatment Effect using difference-in-means on matched samples.**

```java
public class SimplePSM {
    public static double estimateATE(Table data, String treatment, String outcome, 
                                      List<String> covariates, double caliper) {
        // Step 1: fit logistic regression
        LogisticRegression lr = new LogisticRegression();
        double[][] X = extractFeatures(data, covariates);
        double[] T = data.booleanColumn(treatment).asDoubleArray();
        lr.fit(X, T);
        
        // Step 2: compute propensity scores
        double[] ps = lr.predictProbabilities(X);
        
        // Step 3: match within caliper
        List<Double> treatedY = new ArrayList<>();
        List<Double> controlY = new ArrayList<>();
        boolean[] used = new boolean[data.rowCount()];
        
        for (int i = 0; i < data.rowCount(); i++) {
            if (T[i] == 1) {
                double bestDist = caliper;
                int bestJ = -1;
                for (int j = 0; j < data.rowCount(); j++) {
                    if (used[j] || T[j] != 0) continue;
                    double dist = Math.abs(ps[i] - ps[j]);
                    if (dist < bestDist) { bestDist = dist; bestJ = j; }
                }
                if (bestJ >= 0) {
                    treatedY.add(data.doubleColumn(outcome).getDouble(i));
                    controlY.add(data.doubleColumn(outcome).getDouble(bestJ));
                    used[bestJ] = true;
                }
            }
        }
        return mean(treatedY) - mean(controlY);
    }
}
```

5. **You find that a new website design correlates with +15% conversions. How do you determine if this is causal?**
   - Check if it was a randomized experiment (A/B test). If so, the correlation is causal.
   - If observational, draw the DAG. Identify confounders (e.g., newer users may be shown the new design AND convert less).
   - Control for confounders via regression, stratification, or matching.
   - Consider natural experiments: did the design rollout happen at different times for different segments?
