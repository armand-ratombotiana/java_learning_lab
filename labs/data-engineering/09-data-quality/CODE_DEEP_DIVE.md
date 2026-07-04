# Code Deep Dive: Quality Framework

## Complete Validator
```java
@Component
public class DataQualityFramework {
    private final List<QualityRule> rules;
    private final AlertService alertService;

    public QualityReport validate(Dataset<Row> data, String dataset) {
        QualityReport report = new QualityReport(dataset, Instant.now());
        for (QualityRule rule : rules) {
            RuleResult result = rule.evaluate(data);
            report.addResult(result);
            if (!result.isPassed() && rule.getSeverity() == CRITICAL) {
                alertService.sendCriticalAlert(result);
            }
        }
        return report;
    }
}

@Component
class NullCheckRule implements QualityRule {
    @Override
    public RuleResult evaluate(Dataset<Row> data) {
        Map<String, Double> nullRates = new HashMap<>();
        for (String col : data.columns()) {
            long nullCount = data.filter(col(col).isNull()).count();
            nullRates.put(col, (double) nullCount / data.count());
        }
        return new RuleResult("NullCheck", nullRates.values().stream()
            .allMatch(r -> r < 0.05), nullRates, "", Severity.HIGH);
    }
}
```
