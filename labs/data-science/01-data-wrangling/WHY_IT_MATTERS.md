# Why Data Wrangling Matters

Data wrangling is the highest-leverage activity in data science. Industry estimates show that 60–80% of a data scientist's time is spent wrangling data — not modeling. Errors introduced during wrangling cascade invisibly into downstream models, producing confident-but-wrong predictions.

## Cost of Bad Wrangling

| Impact | Example |
|---|---|
| Silent corruption | Nulls filled with 0 in a salary column shifts mean |
| Selection bias | Dropping missing rows that are systematically missing |
| Type mismatches | Ages stored as strings sort lexicographically: "9" > "100" |
| Duplicate inflation | Same customer counted multiple times skews LTV models |

## Java-Specific Stakes

Java's compile-time safety means fewer type surprises at runtime, but the JVM makes it expensive to detect issues after the fact. A `NullPointerException` in a production wrangling pipeline can halt batch jobs for hours. Defensive wrangling — explicit null checks, schema validation, and type assertions — prevents these failures.

```java
// Defensive: fail fast on unexpected nulls
public double computeMean(DoubleColumn col) {
    if (col.containsMissing()) {
        throw new IllegalStateException(
            "Cannot compute mean on column with missing values: " + col.name()
        );
    }
    return col.mean();
}
```
