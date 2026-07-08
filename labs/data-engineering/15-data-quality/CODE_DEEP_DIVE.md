# Code Deep Dive: Data Quality Engineering

See Java source files in src/main/java/com/dataeng/fifteen/ for:
- QualityCheckEngine.java: Rule execution engine with pass/fail evaluation
- SchemaValidator.java: Schema drift detection and evolution

Key patterns:
```java
// Quality check rule
QualityRule rule = new NotNullRule("customer_id");
RuleResult result = rule.evaluate(dataset);
if (!result.isPassed()) {
    alertService.sendAlert(result);
}

// Schema drift detection
SchemaDiff diff = SchemaValidator.compare(incomingSchema, expectedSchema);
if (diff.hasBreakingChanges()) {
    throw new SchemaDriftException("Breaking schema change detected");
}
```
