# Math Foundation for DDD

## Aggregate Consistency
### Invariant Enforcement
```
∀ order ∈ Orders: order.items ≠ ∅ ⇒ order.customerId ≠ null
∀ order ∈ Orders: order.totalAmount = ∑(item.price × item.quantity)
```

## Bounded Context Mapping Complexity
### Context Dependency Graph
```
C = (V, E)
Where:
V = set of bounded contexts
E = set of context relationships
Cyclomatic complexity = |E| - |V| + 2|P|

Example: 5 contexts, 7 relationships = 7 - 5 + 2 = 4
```

## Event Storming Probability
### Event Likelihood
```
P(event occurs) = BusinessRuleTriggered × TimeWindow
Multiple events often correlate in domain processes
```

## Domain Model Metrics
```java
public class DomainModelMetrics {
    // Measure domain model health
    public double calculateRichness(Class<?> domainClass) {
        Method[] methods = domainClass.getMethods();
        long behaviorMethods = Arrays.stream(methods)
            .filter(m -> !isGetterOrSetter(m))
            .count();
        return (double) behaviorMethods / methods.length;
    }
}
```

## Aggregate Size Heuristic
```
Optimal aggregate count ≈ √(domain operations)
Too small: excessive coordination
Too large: concurrency bottlenecks
Rule of thumb: 3-7 entities per aggregate ideal
```
