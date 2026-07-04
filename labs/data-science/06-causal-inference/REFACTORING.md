# Refactoring Causal Inference Code

## Smell: Ad-Hoc Adjustment Sets

Each analysis chooses adjustment variables differently with no consistency.

**Refactor**: Codify the DAG and derive adjustment sets automatically.

```java
public class CausalGraph {
    private Set<String> nodes;
    private Map<String, List<String>> edges;  // parent → children
    
    public Set<String> backdoorAdjustmentSet(String treatment, String outcome) {
        // Implement Pearl's backdoor criterion
        Set<String> adjust = new HashSet<>();
        for (String node : nodes) {
            if (isAncestorOf(node, treatment) || node.equals(treatment)) continue;
            // check if node blocks all backdoor paths
            if (blocksAllBackdoorPaths(node, treatment, outcome)) {
                adjust.add(node);
            }
        }
        return adjust;
    }
}
```

## Smell: Propensity Score Estimation Scattered

PS estimation, matching, and effect estimation are in different scripts may disagree.

**Refactor**: Single causal estimator class.

```java
public class PropensityScoreMatcher {
    private final double caliper = 0.05;
    private LogisticRegression propensityModel;
    private int[] matches;
    
    public PropensityScoreMatcher fit(Table data, String treatment, List<String> covariates) {
        propensityModel = fitLogistic(data, treatment, covariates);
        double[] scores = propensityModel.predictProbabilities(extract(data, covariates));
        matches = nearestNeighborMatch(scores, data.booleanColumn(treatment));
        return this;
    }
    
    public double estimateATE(Table data, String outcome) {
        double[] y = data.doubleColumn(outcome).asDoubleArray();
        boolean[] t = data.booleanColumn(treatment).asBooleanArray();
        return mean(select(y, t == true)) - mean(select(y, matches));
    }
}
```

## Smell: DiD Without Standard Errors

Difference-in-differences without standard error calculation.

**Refactor**: Include boostrapped standard errors.

```java
public class DiDResult {
    private double estimate;
    private double standardError;
    private double[] bootstrapDistribution;
    
    public DiDResult withBootstrapSE(Table data, int nBoots) {
        double[] bootEstimates = new double[nBoots];
        for (int b = 0; b < nBoots; b++) {
            Table sample = data.sampleN(data.rowCount(), true);  // with replacement
            bootEstimates[b] = estimateDiD(sample);
        }
        this.standardError = std(bootEstimates);
        return this;
    }
}
```
