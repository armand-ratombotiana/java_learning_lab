# Math Foundation for Hexagonal Architecture

## Coupling Metrics
```
Coupling = Number of outbound dependencies from domain
Domain coupling should approach 0 (pure domain logic)

Ideal: Domain depends only on Java standard library
```

## Cohesion Metrics
```
Cohesion = (Internal references) / (External references)
High cohesion means domain classes reference each other more than external classes
```

## Testability Index
```
Testability = TestableComponents / TotalComponents × 100

In hexagonal: All domain components are testable without infrastructure
Example: 15 testable / 20 total = 75% testability
```

## Adapter Ratio
```
AdapterCount = DrivingAdapters + DrivenAdapters
AdapterRatio = AdapterCount / DomainComponentCount

Example: 4 adapters / 10 domain components = 0.4
Higher ratio = more delivery mechanisms supported
```

## Complexity Metrics
```
Cyclomatic complexity per adapter should be low (< 10)
Adapters are "thin" - they translate, not contain business logic
Domain services can have higher complexity (business rules)
```

```java
public class ArchitectureMetrics {
    public double calculateTestability(List<Class<?>> domainClasses,
                                        List<Class<?>> allClasses) {
        return (double) domainClasses.size() / allClasses.size() * 100;
    }

    public boolean isAdapterThin(Class<?> adapterClass) {
        // Adapters should have low cyclomatic complexity
        int complexity = calculateCyclomaticComplexity(adapterClass);
        return complexity < 10;
    }
}
```
