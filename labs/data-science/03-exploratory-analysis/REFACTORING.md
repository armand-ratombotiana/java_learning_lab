# Refactoring EDA Code

## Smell: One Giant EDA Notebook

EDA code in a single 500-line main method is unmaintainable and unrepeatable.

**Refactor**: Break into reusable profile functions.

```java
public class DataProfiler {
    public static ProfileResult profile(Table data) { /* ... */ }
    public static NumericProfile profileNumeric(DoubleColumn col) { /* ... */ }
    public static CategoricalProfile profileCategorical(StringColumn col) { /* ... */ }
}

// Usage
ProfileResult result = DataProfiler.profile(data);
System.out.println(result.markdownSummary());
```

## Smell: Inconsistent Missing Value Handling

Some EDA scripts treat "" as missing, others treat "null" as missing, others ignore both.

**Refactor**: Centralize missing value detection.

```java
public class MissingDetector {
    private static final Set<String> NULL_STRINGS = Set.of("", "null", "NULL", "N/A", "NaN");
    
    public static boolean isMissing(String val) {
        return val == null || NULL_STRINGS.contains(val.trim());
    }
}
```

## Smell: Duplicate EDA Across Projects

Every new dataset starts with the same boilerplate.

**Refactor**: Create a generic EDA pipeline.

```java
// Single reusable EDA runner
public class ExplorationPipeline {
    public ExplorationReport run(Table data) {
        ExplorationReport report = new ExplorationReport(data.name());
        report.setSummary(data.summary());
        report.setMissing(data.missingCount());
        report.setCorrelations(computeCorrelations(data));
        report.setOutliers(detectOutliers(data));
        // Generate plots
        // ...
        report.save("eda_report.json");
        return report;
    }
}
```

## Smell: Hardcoded File Paths

Every EDA run reads from the same hardcoded path — can't easily rerun on new data.

**Refactor**: Parameterize the data source.

```java
public class EDAConfig {
    private String dataPath;
    private List<String> targetColumns;
    private boolean generatePlots;
    // constructor, getters, setters
}
```
