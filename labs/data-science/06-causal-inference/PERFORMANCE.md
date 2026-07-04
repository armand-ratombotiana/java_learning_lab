# Performance in Causal Inference

## 1. Propensity Score Estimation at Scale

Fitting logistic regression on 10M rows with 100 covariates is computationally heavy.

**Optimization**: 
- Use stochastic gradient descent (SGD) instead of Newton-Raphson
- Reduce covariates via feature selection before PS estimation
- Use Apache Spark for distributed logistic regression

## 2. Nearest-Neighbor Matching

Naive pairwise distance computation is O(n_t × n_c) — for 100K treated and 100K control, that's 10B comparisons.

**Optimization**: Use KD-Tree or ball tree for O(n log n) matching:

```java
public class KDTreeMatcher {
    private KDTree<double[]> tree;
    
    // Build tree on control units' propensity scores
    public void buildIndex(double[] controlScores) {
        tree = new KDTree<>(controlScores);
    }
    
    // O(log n) per match
    public int match(double treatedScore) {
        return tree.nearestNeighbor(treatedScore);
    }
}
```

## 3. Bootstrap for Standard Errors

Standard bootstrap (nBoots = 10,000 × n) is expensive.

**Optimization**: Use the delta method or influence function approximation for analytical standard errors instead of bootstrapping.

## 4. Double ML with Many Covariates

Double ML (Chernozhukov et al.) uses cross-fitting with ML models on each fold. Training 10× ML models on 10M rows is expensive.

**Optimization**: Use small ensembles (2–5 folds instead of 10), or use linear models for the nuisance functions when the number of covariates is manageable.

## Memory Considerations

Propensity score matching stores the full dataset plus distance matrices. For large datasets, process in batches:

```java
for (int batch = 0; batch < totalTreated; batch += BATCH_SIZE) {
    processBatch(treated.subList(batch, batch + BATCH_SIZE));
    System.gc();  // hint for GC to reclaim distance matrix
}
```
