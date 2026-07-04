# Internals: Encoding Algorithms

## One-Hot Encoding — Memory Model

A one-hot encoded column with k categories creates k boolean columns. For a column with 1M rows and 100 categories, that's 100M booleans (~100MB as bits or ~800MB as Java booleans).

```java
// Sparse representation saves memory
public class SparseBooleanColumn extends AbstractColumn {
    private int size;
    private Map<Integer, Boolean> values;  // only non-false values stored
    private BitSet bits;                    // compact bit storage
}
```

## Target Encoding — Risk of Target Leakage

Naive target encoding (mean(target) per category on the full dataset) leaks information:

```java
// WRONG: leaks target information
double meanSalary = data.groupBy("department").mean("salary");

// CORRECT: use cross-validation strategy
// Compute encoding on fold training data only, apply to validation
public class TargetEncoder {
    public double[] encodeWithCV(Column<String> categories, double[] target, int folds) {
        double[] encoded = new double[categories.size()];
        CrossValidation cv = new CrossValidation(categories.size(), folds);
        for (Fold fold : cv) {
            for (int i : fold.train()) {
                // compute mean on training set
            }
            for (int i : fold.test()) {
                // apply to test set
            }
        }
        return encoded;
    }
}
```

## Feature Scaling — Fit Parameters

Standardization (`StandardScaler`) stores mean and std per column:

```java
public class StandardScaler {
    private double[] means;
    private double[] stds;
    
    public void fit(Table data) {
        int n = data.numericColumnNames().size();
        means = new double[n];
        stds = new double[n];
        for (int i = 0; i < n; i++) {
            DoubleColumn c = data.doubleColumn(i);
            means[i] = c.mean();
            stds[i] = c.stdDev();
        }
    }
    
    public Table transform(Table data) {
        Table result = data.copy();
        for (int i = 0; i < data.numericColumnNames().size(); i++) {
            result.doubleColumn(i).subtract(means[i]).divide(stds[i]);
        }
        return result;
    }
}
```
