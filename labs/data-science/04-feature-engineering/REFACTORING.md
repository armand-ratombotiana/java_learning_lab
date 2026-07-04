# Refactoring Feature Engineering Code

## Smell: Ad-Hoc Feature Definitions

Features are defined inline in training scripts, making them unreusable.

**Refactor**: Implement the Transformer pattern.

```java
public interface FeatureTransformer {
    String getFeatureName();
    double transform(Row row);
}

public class AgeSquaredTransformer implements FeatureTransformer {
    @Override
    public String getFeatureName() { return "age_squared"; }
    
    @Override
    public double transform(Row row) {
        double age = row.getDouble("age");
        return age * age;
    }
}

// Usage
List<FeatureTransformer> transformers = List.of(
    new AgeSquaredTransformer(),
    new LogIncomeTransformer(),
    new WeekendTransformer()
);
```

## Smell: Transform Logic Scattered in Training Script

**Refactor**: Build a pipeline.

```java
public class FeaturePipeline {
    private List<FeatureTransformer> transformers = new ArrayList<>();
    private StandardScaler scaler;
    
    public FeaturePipeline add(FeatureTransformer t) {
        transformers.add(t);
        return this;
    }
    
    public Table fitTransform(Table data) {
        Table features = applyTransforms(data);
        scaler = new StandardScaler();
        scaler.fit(features);
        return scaler.transform(features);
    }
    
    public Table transform(Table data) {
        return scaler.transform(applyTransforms(data));
    }
}
```

## Smell: Hardcoded Category Mappings

```java
// Before: magic map
Map<String, Integer> qualityMap = Map.of("Ex", 5, "Gd", 4, "TA", 3, "Fa", 2, "Po", 1);

// After: config-driven mapping
// In YAML or JSON config file:
// categorical_mappings:
//   quality: { Ex: 5, Gd: 4, TA: 3, Fa: 2, Po: 1 }
```

## Smell: Feature Explosion

500 features after engineering, most unused.

**Refactor**: Add feature selection as a pipeline step.

```java
// Mutual information based selection
int[] selected = Smile.mutualInformation(features, target, 20);
// Keep only top 20 features
```
