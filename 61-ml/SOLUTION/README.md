# Machine Learning Solution

## Overview
This module covers ML algorithms and Weka basics.

## Key Features

### Data Loading
- Loading datasets
- Setting class index

### Data Preprocessing
- Normalization
- Standardization

### Classification
- Decision Tree (J48)
- Random Forest
- Cross-validation

### Regression
- Linear Regression

### Clustering
- K-Means clustering

### Data Transformation
- Train/test split
- Feature preprocessing

## Usage

```java
MLSolution solution = new MLSolution();

// Load data
Instances data = solution.loadDataset("data.arff");
solution.setClassIndex(data, data.numAttributes() - 1);

// Normalize
Instances normalized = solution.normalizeData(data);

// Build model
MLSolution.DecisionTree tree = solution.buildDecisionTree(data);

// Cross-validate
double accuracy = solution.crossValidate(new J48(), data, 10);

// Split data
Instances train = solution.splitData(data, 0.8);
```

## Dependencies
- Weka
- JUnit 5
- Mockito