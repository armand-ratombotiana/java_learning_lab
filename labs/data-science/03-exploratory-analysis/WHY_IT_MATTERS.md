# Why Exploratory Data Analysis Matters

Skipping EDA is the most common and most costly mistake in applied data science. Models trained on unexamined data can produce excellent cross-validation scores while being completely wrong for the business problem.

## Real-World Consequences

| Skipped EDA Issue | Manifestation | Cost |
|---|---|---|
| Unseen bimodality | Single mean fits neither mode | Wrong pricing strategy |
| Undetected garbage values | Age = 999 pulled all regressions | Bad policy targeting |
| Confounded variables | X causes Y, but Y also causes X — model learns feedback | Unstable predictions |
| Data leak in train/test | Future information in training features | Perfect CV, production failure |

## Java EDA Workflow

```java
// Minimal EDA before any modeling
public void explore(Table data) {
    // 1. Shape and types
    System.out.println("Dimensions: " + data.shape());
    System.out.println("Columns: " + data.columnNames());
    
    // 2. Missing value patterns
    for (String col : data.columnNames()) {
        int missing = data.column(col).countMissing();
        if (missing > 0) {
            System.out.println(col + ": " + missing + " missing (" +
                String.format("%.1f%%", 100.0 * missing / data.rowCount()) + ")");
        }
    }
    
    // 3. Numeric distributions
    for (String col : data.numericColumnNames()) {
        DoubleColumn c = data.doubleColumn(col);
        System.out.printf("%s: mean=%.2f, std=%.2f, min=%.2f, max=%.2f, skew=%.2f%n",
            col, c.mean(), c.stdDev(), c.min(), c.max(), c.skewness());
    }
    
    // 4. Correlation (Pearson)
    for (int i = 0; i < data.numericColumnNames().size(); i++) {
        for (int j = i+1; j < data.numericColumnNames().size(); j++) {
            double r = Smile.correlation(
                data.doubleColumn(data.numericColumnNames().get(i)).asDoubleArray(),
                data.doubleColumn(data.numericColumnNames().get(j)).asDoubleArray()
            );
            if (Math.abs(r) > 0.7) {
                System.out.println("Strong correlation: " +
                    data.numericColumnNames().get(i) + " vs " +
                    data.numericColumnNames().get(j) + " r=" + String.format("%.3f", r));
            }
        }
    }
}
```
