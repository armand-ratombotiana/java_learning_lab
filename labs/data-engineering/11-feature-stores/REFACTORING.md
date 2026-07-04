# Refactoring Feature Engineering

## Before: Ad-hoc Notebook Features
```python
features = df.groupby('user').agg({'amount': 'mean'})
```

## After: Centralized Feature Store
```java
FeatureDefinition def = FeatureDefinition.builder()
    .name("avg_order_value")
    .transformation("SELECT user_id, AVG(amount) FROM transactions GROUP BY user_id")
    .build();
registry.register(def);
```

## Before: Inconsistent Serving Logic
Batch and serving use different code!

## After: Unified Logic
```java
public double compute(List<Transaction> txns) {
    return txns.stream().mapToDouble(Transaction::getAmount).average().orElse(0);
}
// Used in both Spark (offline) and Java service (online)
```
