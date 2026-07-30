# AI Testing & Evaluation — Deep Dive Guide

## Testing AI Components

AI systems require two categories of tests:

1. **Functional tests**: Do components behave correctly? (unit tests)
2. **Non-functional tests**: Are they fast enough? (benchmarks)

## Code Walkthrough: TestSuite

The `TestSuite` class demonstrates a lightweight test runner:

- `addTest(name, assertion)`: Registers a test with a name and executable assertion
- Each test is wrapped in a `Supplier<TestResult>` for lazy execution
- `run()` executes all tests and collects pass/fail/error results
- Tests can throw `AssertionError` for failures or any `Exception` for errors

### Unit Tests Implemented
- `classify_positive`: "This is good" → "positive"
- `classify_negative`: "This is bad" → "negative"
- `classify_neutral`: "This is a table" → "neutral"
- `classify_empty`: Empty string → "neutral"

## Evaluation Metrics

### Accuracy
```
Accuracy = Correct Predictions / Total Predictions
```
Best for balanced datasets. Misleading when classes are imbalanced.

### Precision
```
Precision = True Positives / (True Positives + False Positives)
```
Measures: How many positive predictions were actually correct?

### Recall
```
Recall = True Positives / (True Positives + False Negatives)
```
Measures: How many actual positives did we catch?

### F1 Score
```
F1 = 2 * Precision * Recall / (Precision + Recall)
```
Harmonic mean of precision and recall. Best single-number metric for imbalanced data.

## Regression Testing

The `Sentiment Regression` suite runs the same test inputs across deployments:

- Fixed input/output pairs (golden test set)
- Accuracy threshold checks (must be >= 1.0)
- Metric computation (precision, recall, F1) with minimum thresholds

## Benchmark Automation

The `BenchmarkRunner` measures:
- Average latency per invocation (ms)
- Operations per second (throughput)
- Uses `System.nanoTime()` for high-precision timing

## Production Considerations

- Run regression suites on every model deployment
- Track metrics over time in a dashboard (Grafana)
- Set alert thresholds for metric degradation
- Use canary deployments with automated rollback if tests fail
- Include adversarial/edge-case tests specifically