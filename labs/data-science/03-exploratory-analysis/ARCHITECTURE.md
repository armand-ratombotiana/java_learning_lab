# Architecture: Automated EDA Report System

## System Design

```
┌─────────────┐    ┌────────────────┐    ┌──────────────────┐
│  Data       │───>│  Profiler      │───>│  Report          │
│  Adapter    │    │  Engine        │    │  Generator       │
└─────────────┘    └────────────────┘    └──────────────────┘
       │                   │                       │
       │            ┌──────┴──────┐               ├──> HTML
       │            │ Statistical │               ├──> PDF
       └────────────│    +        │               └──> JSON
                    │ Graphical   │
                    └─────────────┘
```

## Component Responsibilities

### Data Adapter
- Normalizes data from various sources (CSV, Parquet, JDBC, Avro)
- Detects and reports schema issues before profiling
- Handles incremental data loading for large datasets

### Profiler Engine
- Computes descriptive statistics per column type
- Detects anomalies, missing patterns, distribution shape
- Delegates to specialized profiler implementations:

```java
public interface ColumnProfiler {
    ProfileResult profile(Column<?> column);
}

public class NumericProfiler implements ColumnProfiler {
    public ProfileResult profile(Column<?> column) {
        DoubleColumn c = (DoubleColumn) column;
        return new NumericProfile(
            c.mean(), c.stdDev(), c.min(), c.max(),
            c.percentile(25), c.median(), c.percentile(75),
            c.skewness(), c.kurtosis(), c.countMissing()
        );
    }
}
```

### Report Generator
- Merges all profile results into a structured report
- Generates HTML, PDF, or JSON output
- Includes visualizations (auto-generated histograms, box plots, correlation heatmaps)

## Output Schema (JSON)

```json
{
  "dataset": "customer_data",
  "rows": 50000,
  "columns": 22,
  "profiles": {
    "age": {
      "type": "NUMERIC",
      "missing": 150,
      "mean": 42.3,
      "std": 15.1,
      "min": 18,
      "max": 95,
      "skewness": 0.3,
      "outliers": 230
    },
    "region": {
      "type": "CATEGORICAL",
      "missing": 10,
      "unique": 4,
      "top": "Northeast",
      "top_freq": 0.35,
      "entropy": 1.82
    }
  }
}
```
