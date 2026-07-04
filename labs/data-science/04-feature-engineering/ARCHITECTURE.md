# Architecture: Feature Engineering Pipeline

## System Design

```
┌──────────┐    ┌─────────────┐    ┌──────────────┐    ┌────────────┐
│  Raw     │───>│  Feature    │───>│  Feature     │───>│  Model     │
│  Data    │    │  Pipeline   │    │  Store       │    │  Training  │
│  Store   │    │             │    │  (Feast)     │    │            │
└──────────┘    └─────────────┘    └──────────────┘    └────────────┘
                      │                    │
                      │                    ├──> Online serving (low-latency)
                      │                    └──> Offline training (batch)
                      │
                      ▼
               ┌──────────────┐
               │  Feature     │
               │  Registry    │
               │  (Metadata)  │
               └──────────────┘
```

## Component Responsibilities

### Feature Pipeline
- Dag-based execution of transformers
- Handles dependencies: transform A must run before transform B
- Supports incremental computation (only recompute when source changes)

### Feature Store
- Stores computed feature values for both training and serving
- Provides point-in-time correct feature retrieval (crucial for time series)
- Handles feature versioning and backfill

### Feature Registry (Metadata)
- Feature definitions, owners, types, dependencies
- Data lineage tracking
- Monitoring: feature freshness, distribution shifts, null rates

## Pipeline Execution Model

```java
public class FeaturePipeline {
    private final DAG<FeatureTransformer> dag;
    private final Map<String, Column<?>> cache = new HashMap<>();
    
    public Table execute(Table input) {
        Table result = input.copy();
        for (FeatureTransformer t : dag.topologicalSort()) {
            Column<?> column = t.transform(result);
            result.addColumn(column);
            cache.put(t.getFeatureName(), column);
        }
        return result;
    }
}
```
