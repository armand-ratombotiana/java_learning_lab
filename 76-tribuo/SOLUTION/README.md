# Microsoft Tribuo - Solution

## Overview

This module provides comprehensive examples for machine learning using Microsoft Tribuo. It covers classification, regression, clustering, data processing, and model evaluation with ONNX integration.

## Dependencies

```xml
<dependency>
    <groupId>com.oracle.labs.olran</groupId>
    <artifactId>tribuo-all</artifactId>
    <version>4.3.0</version>
</dependency>
```

## Key Concepts

### 1. Classification

The `ClassificationExample` class demonstrates:
- Logistic Regression
- Decision Trees
- Random Forest
- SGD Classifier
- Naive Bayes

### 2. Regression

The `RegressionExample` class covers:
- Linear Regression
- Ridge Regression
- SGD Regressor

### 3. Clustering

The `ClusteringExample` class provides:
- K-Means clustering
- Cluster prediction
- Centroid extraction

### 4. Data Processing

The `DataProcessingExample` class implements:
- CSV data loading
- Dataset creation
- Train/test splitting
- Data normalization
- Data scaling

### 5. Model Management

The `ModelExample` class covers:
- Model serialization
- Model deserialization
- Feature importance extraction

### 6. ONNX Integration

The `ONNXExample` class provides:
- ONNX model loading
- Model export to ONNX format

## Classes Overview

| Class | Description |
|-------|-------------|
| `ClassificationExample` | Various classification algorithms |
| `RegressionExample` | Regression models |
| `ClusteringExample` | K-Means clustering |
| `DataProcessingExample` | Data loading and transformation |
| `ModelExample` | Model persistence and inspection |
| `ONNXExample` | ONNX integration |
| `EvaluationExample` | Model evaluation |

## Running Tests

```bash
cd 76-tribuo
mvn test -Dtest=Test
```

## Examples

### Classification

```java
DecisionTreeTrainer trainer = new DecisionTreeTrainer(5);
Classifier classifier = trainer.train(trainingData);

Map<String, Double> predictions = classifier.predict(example);
```

### Model Saving

```java
Model.serialize(model, Paths.get("model.bin"));
Model loadedModel = Model.deserialize(Paths.get("model.bin"));
```

### Evaluation

```java
LabelEvaluator evaluator = new LabelEvaluator();
Evaluation<Label> evaluation = evaluator.evaluate(classifier, testData);

double accuracy = evaluation.accuracy();
```

## Best Practices

1. **Data Preprocessing**: Normalize/scale data before training
2. **Train/Test Split**: Use appropriate split ratios
3. **Model Selection**: Compare multiple algorithms
4. **Evaluation**: Use multiple metrics (accuracy, precision, recall)
5. **Model Export**: Export to ONNX for cross-platform deployment

## Further Reading

- [Tribuo Documentation](https://tribuo.org/)
- [Tribuo GitHub](https://github.com/oracle/tribuo)
- [ONNX Runtime Java](https://onnxruntime.ai/docs/runners/)