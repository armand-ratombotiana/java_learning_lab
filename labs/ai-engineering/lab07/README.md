# Lab 07: AI Testing & Evaluation

## Learning Objectives
- Write unit tests for AI components (tokenizers, classifiers)
- Build regression test suites to detect model degradation
- Compute standard evaluation metrics (accuracy, precision, recall, F1)
- Automate benchmarks for performance tracking

## Concepts Covered
- **Unit Testing**: Testing individual AI components in isolation
- **Regression Testing**: Detecting accuracy degradation over time
- **Evaluation Metrics**: Accuracy, Precision, Recall, F1, AUC-ROC
- **Benchmark Automation**: Measuring latency and throughput
- **Test Suites**: Organizing tests by component and severity

## Setup
```bash
cd lab07
javac src/com/aiengineering/lab07/AiTestingAndEvaluationDemo.java
java com.aiengineering.lab07.AiTestingAndEvaluationDemo
```

## Key Takeaways
- AI testing requires both functional and performance tests
- Metrics must be tracked over time to detect regression
- Automated benchmarks prevent silent performance degradation
