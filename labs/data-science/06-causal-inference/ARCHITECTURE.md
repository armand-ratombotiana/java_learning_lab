# Architecture: Causal Inference Platform

## System Design

```
┌────────────┐    ┌──────────────┐    ┌────────────────┐    ┌──────────┐
│  Data      │───>│  Causal      │───>│  Sensitivity   │───>│  Effect  │
│  Pipeline  │    │  Estimator   │    │  Analyzer      │    │  Store   │
└────────────┘    └──────────────┘    └────────────────┘    └──────────┘
                        │                       │
                        ▼                       ▼
                 ┌──────────────┐       ┌────────────────┐
                 │  DAG         │       │  Assumption    │
                 │  Builder     │       │  Checker       │
                 └──────────────┘       └────────────────┘
```

## Component Responsibilities

### DAG Builder
- Takes variable metadata + domain expert input
- Constructs a directed acyclic graph
- Validates acyclicity and provides adjustment set recommendations
- Output: `CausalGraph` object with adjacency matrix

### Causal Estimator
- Supports multiple estimation strategies: PS matching, DiD, IV, Double ML
- Selects method based on data availability and assumptions
- Each estimator implements a standard interface:

```java
public interface CausalEstimator {
    CausalResult estimate(Table data, String treatment, String outcome);
    String getRequiredAssumptions();
    double[] getConfidenceInterval(double alpha);
}
```

### Sensitivity Analyzer
- Tests robustness to violations of key assumptions
- Reports "tipping point": how strong would an unobserved confounder need to be to nullify the result
- Generates contour plots of effect estimate vs. confounder strength

### Effect Store
- Stores all causal effect estimates with metadata
- Tracks: analysis date, data version, estimator, assumptions, sensitivity results
- Prevents p-hacking: all analyses are logged even if unpublished

## Estimation Workflow

```java
CausalAnalysis analysis = new CausalAnalysis()
    .defineDag(dag)
    .withEstimator(new PropensityScoreMatcher())
    .withData(customerData)
    .configureSensitivity()
    .run("training", "salary");

CausalResult result = analysis.getResult();
System.out.println("ATE: " + result.getATE());
System.out.println("CI: " + result.getConfidenceInterval());
System.out.println("Robustness: " + result.getSensitivityReport());
```
