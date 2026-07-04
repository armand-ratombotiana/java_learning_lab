# Math Foundation for Layered Architecture

## Layer Violation Metrics
```
ViolationCount = DirectDependenciesAcrossLayers
Target: 0 violations (only adjacent layer dependencies)

Example:
Controller -> Repository (direct) = 1 violation
Should be: Controller -> Service -> Repository
```

## N-Tier Distribution
```
Frontend Tier: N servers
Business Tier: M servers
Data Tier: K database nodes

Total capacity = min(N resources, M resources, K resources)
Bottleneck: the tier with least capacity
```

## Layer Cohesion
```
LayerCohesion = InternalReferences / (InternalReferences + ExternalReferences)
Higher = better (components in a layer reference each other more than outside)

Target: Service layer > 0.7
         Repository layer > 0.6
```

## Request Latency by Layer
```
TotalLatency = ControllerTime + ServiceTime + RepositoryTime + DatabaseTime
Average: 5ms + 20ms + 10ms + 50ms = 85ms per request
```

```java
public class LayeredMetrics {
    public boolean validateNoLayerSkipping(Class<?> controller, Class<?> repo) {
        // Controllers should not directly inject repositories
        return !controller.getDeclaredFields()
            .anyMatch(f -> repo.isAssignableFrom(f.getType()));
    }

    public double layerCohesion(List<Class<?>> layerClasses,
                                 Class<?> layerPackage) {
        // Calculate internal vs external references
    }
}
```
