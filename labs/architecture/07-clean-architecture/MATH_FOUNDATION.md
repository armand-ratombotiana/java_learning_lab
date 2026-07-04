# Math Foundation for Clean Architecture

## Layer Dependency Metrics
```
ViolationCount = Number of inward dependency violations
Target: ViolationCount = 0

Rule: class in layer X must not import from layer X + N (N > 0)
```

## Stability Metrics
```
Instability = Efferent / (Efferent + Afferent)
Efferent = outgoing dependencies
Afferent = incoming dependencies

Outer layers should be more unstable (change frequently)
Inner layers should be stable (rarely change)
```

## Abstractness Metrics
```
Abstractness = abstract classes / total classes
Ports/Interfaces should be abstract
Implementations should be concrete

Ideal: Inner layers more abstract, outer layers more concrete
```

## Coupling Analysis
```
CyclicDependencyCount = Number of circular dependencies
Target: 0 cyclic dependencies

Fan-out of entity layer should approach 0
Fan-out of framework layer is unrestricted
```

```java
public class ArchitectureMetrics {
    public double calculateStability(JavaClass clazz) {
        int efferent = clazz.getDirectDependenciesFromThisClass().size();
        int afferent = clazz.getDirectDependenciesToThisClass().size();
        return (double) efferent / (efferent + afferent);
    }

    public boolean hasCyclicDependency(JavaClasses classes) {
        // Use ArchUnit to detect cycles
        return slices().matching("..(entity|usecase|adapter)..")
            .should().beFreeOfCycles()
            .check(classes);
    }
}
```
