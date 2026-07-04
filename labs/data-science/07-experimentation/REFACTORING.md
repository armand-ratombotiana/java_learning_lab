# Refactoring Experimentation Code

## Smell: Ad-Hoc Analysis Scripts

Each A/B test has its own script with hardcoded calculations.

**Refactor**: Build a reusable analysis class.

```java
public class ABTestAnalyzer {
    public static ABTestResult analyze(Table data, String treatmentCol, String metricCol) {
        boolean[] treatment = data.booleanColumn(treatmentCol).asBooleanArray();
        double[] metric = data.doubleColumn(metricCol).asDoubleArray();
        
        double[] treated = filter(metric, treatment);
        double[] control = filter(metric, !treatment);
        
        double meanTreated = mean(treated);
        double meanControl = mean(control);
        double effect = meanTreated - meanControl;
        double pValue = twoSampleTTest(treated, control);
        double[] ci = bootstrapCI(treated, control, 10000, 0.95);
        
        return new ABTestResult(effect, pValue, ci, treated.length, control.length);
    }
}
```

## Smell: Mixing Randomization and Business Logic

The same class assigns users to experiments and computes their features.

**Refactor**: Separate assignment service from analysis service.

```java
// Assignment service
public class AssignmentService {
    public ExperimentGroup assign(User user, Experiment exp) {
        String hash = Hashing.murmur3_128().hashString(user.getId() + exp.getId());
        return hash % 100 < 50 ? ExperimentGroup.TREATMENT : ExperimentGroup.CONTROL;
    }
}

// Analysis service (separate)
public class AnalysisService {
    public ABTestResult analyze(Experiment exp) { /* ... */ }
}
```

## Smell: Hardcoded Significance Thresholds

```java
// Before: magic number
if (pValue < 0.05) System.out.println("Significant!");

// After: configurable
public class SignificanceThreshold {
    private final double alpha;
    private final boolean oneSided;
    
    public boolean isSignificant(double pValue) {
        return pValue < (oneSided ? alpha : alpha / 2);
    }
}
```

## Smell: No Logging of Decisions

Decisions to ship or kill are made but not recorded.

**Refactor**: Log every experiment decision.

```java
public class ExperimentLogger {
    public void logDecision(Experiment exp, String decision, 
                             ABTestResult result, String reason) {
        // Write to experiment tracking database
        // Fields: experiment_id, timestamp, decision, effect, p-value, CI, reason, analyst
    }
}
```
