# Weka Machine Learning - Solution

## Overview

This module provides comprehensive examples for machine learning using Weka. It covers classification, regression, clustering, data preprocessing, and model evaluation.

## Dependencies

```xml
<dependency>
    <groupId>nz.ac.waikato.cms.weka</groupId>
    <artifactId>weka</artifactId>
    <version>3.9.6</version>
</dependency>
```

## Key Concepts

### 1. Classification

The `ClassificationExample` class demonstrates:
- Decision Tree (J48)
- Random Forest
- Naive Bayes
- K-Nearest Neighbors (IBk)
- Logistic Regression
- SVM (SMO)
- AdaBoost
- Rule-based classification (JRip)

### 2. Regression

The `RegressionExample` class covers:
- Linear Regression
- Model evaluation (RMSE, MAE, R-squared)

### 3. Clustering

The `ClusteringExample` class provides:
- K-Means clustering
- EM (Expectation Maximization)
- Cluster assignment
- Cluster membership calculation

### 4. Data Preprocessing

The `DataPreprocessingExample` class implements:
- ARFF and CSV data loading
- Data normalization
- Data standardization
- Discretization
- Attribute removal
- Train/test splitting

### 5. Model Persistence

The `ModelPersistenceExample` class covers:
- Saving trained classifiers
- Loading saved models
- Saving and loading clusterers

### 6. Visualization

The `VisualizationExample` class provides:
- Dataset attribute summary
- Class distribution analysis

## Classes Overview

| Class | Description |
|-------|-------------|
| `ClassificationExample` | Various classification algorithms |
| `RegressionExample` | Regression models and metrics |
| `ClusteringExample` | K-Means, EM clustering |
| `DataPreprocessingExample` | Data loading and transformation |
| `ModelPersistenceExample` | Model save/load utilities |
| `VisualizationExample` | Dataset statistics and visualization |

## Running Tests

```bash
cd 75-weka
mvn test -Dtest=Test
```

## Examples

### Classification

```java
J48 decisionTree = new J48();
decisionTree.buildClassifier(trainingData);

double[] predictions = new ClassificationExample().predict(decisionTree, testData);
```

### Clustering

```java
SimpleKMeans kMeans = new SimpleKMeans();
kMeans.setNumClusters(3);
kMeans.buildClusterer(data);

int[] assignments = kMeans.clusterInstances(data);
```

### Model Evaluation

```java
Evaluation evaluation = new Evaluation(trainingData);
evaluation.crossValidateModel(classifier, trainingData, 10, new Random(1));

double accuracy = evaluation.pctCorrect();
double precision = evaluation.precision(0);
```

## Best Practices

1. **Data Preprocessing**: Always preprocess data before training
2. **Cross-Validation**: Use cross-validation for robust evaluation
3. **Model Selection**: Try multiple algorithms and compare performance
4. **Parameter Tuning**: Tune hyperparameters for optimal performance
5. **Evaluation Metrics**: Use multiple metrics (accuracy, precision, recall, F1)

## Further Reading

- [Weka Documentation](https://www.cs.waikato.ac.nz/ml/weka/documentation.html)
- [Weka Tutorials](https://www.cs.waikato.ac.nz/ml/weka/tutorial.html)
- [Weka GitHub](https://github.com/Waikato/weka-3)