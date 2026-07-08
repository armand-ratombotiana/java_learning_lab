# Model Evaluation — Exercises

## Exercise 1: Basic Implementation

Implement a basic version of the core Model Evaluation algorithm. Start with a simple synthetic dataset and verify your implementation produces correct outputs.

Hint: Follow the step-by-step guide. Use the forward() and computeGradients() methods as starting points.

## Exercise 2: Parameter Tuning

Experiment with different hyperparameter values. Create a systematic grid search and evaluate how each parameter affects results.

Parameters to tune: learning rate, regularization strength, max iterations, tolerance.

Hint: Use Metrics.crossValidate() for robust evaluation.

## Exercise 3: Data Preprocessing

Apply different preprocessing techniques and compare their impact on algorithm performance. Test with missing values, outliers, and unscaled features.

Hint: Use DataPreprocessor's imputeMean(), standardize(), and minMaxScale() methods.

## Exercise 4: Performance Optimization

Profile your implementation and identify bottlenecks. Optimize the hot paths using appropriate data structures and algorithms.

Hint: Focus on the inner loop. Consider using Java's Vector API (jdk.incubator.vector) for SIMD operations.

## Exercise 5: Algorithm Extension

Extend the algorithm with a new feature. For example: add L1 regularization, implement a different loss function, or add support for early stopping based on validation loss.

Hint: Override computeGradients() to implement L1 regularization with proximal gradient.

## Exercise 6: Comparison Study

Compare your implementation against a naive baseline and a more sophisticated variant. Create visualizations of the comparison.

Hint: Generate synthetic datasets with known properties (linear, non-linear, noisy).

## Exercise 7: Robustness Testing

Test your implementation on edge cases: empty datasets, constant features, highly correlated variables, extreme outliers.

Hint: Write parameterized JUnit 5 tests for each edge case.

## Exercise 8: Real Dataset Application

Apply the algorithm to a real-world dataset from sources like UCI ML Repository or Kaggle. Document the full workflow from data loading to result interpretation.

Hint: Start with a small, well-understood dataset like Iris or Boston Housing.

## Exercise 9: Ensemble Variant

Create an ensemble version that combines multiple instances of the algorithm with different configurations. Compare ensemble performance against individual models.

Hint: Implement a VotingEnsemble that averages predictions from multiple CoreAlgorithm instances.

## Exercise 10: Production Readiness

Refactor your code to meet production standards: add logging, input validation, error handling, configuration via properties files, and comprehensive documentation.

Hint: Use SLF4J for logging, implement a config file reader, and add Javadoc to all public methods.

## Exercise 1: Basic Implementation (Detailed)

Implement a basic version of the core algorithm from scratch. Follow these steps:
1. Create a CoreAlgorithm class with fit() and predict() methods
2. Implement forward pass: weighted sum of inputs plus bias
3. Implement MSE loss computation with L2 regularization
4. Implement backward pass: gradient of loss w.r.t. parameters
5. Implement parameter update using gradient descent
6. Add convergence check based on loss change tolerance
7. Generate a synthetic dataset: y = 2*x1 + 3*x2 + 1 + noise
8. Train the model and verify it recovers the true coefficients

Expected outcome: The learned weights should approximate [2.0, 3.0] and bias should approximate 1.0. The final MSE should be close to the noise variance.

Hints: Use the Utils class for matrix operations. Start with a small learning rate (0.001) and increase gradually. Monitor loss to ensure it decreases monotonically.

## Exercise 2: Data Preprocessing Pipeline

Build a complete preprocessing pipeline:
1. Load a CSV file with mixed data types (numeric and categorical)
2. Handle missing values using mean/median imputation
3. Encode categorical features using one-hot encoding
4. Scale numeric features using standardization (z-score)
5. Split data into training (70%), validation (15%), and test (15%) sets
6. Verify that preprocessing is reversible (inverse transform)

Expected outcome: A reusable preprocessing pipeline that can be saved and reloaded. The pipeline should properly handle new data using statistics computed from training data only.

## Exercise 3: Hyperparameter Tuning (Grid Search)

Implement a systematic grid search for hyperparameter optimization:
1. Define parameter grid: learning_rate in [0.001, 0.01, 0.1, 1.0],
   regularization in [0, 0.001, 0.01, 0.1], max_iterations in [100, 500, 1000]
2. For each combination, perform 5-fold cross-validation
3. Record mean validation score and standard deviation
4. Identify the best parameter combination
5. Visualize the results as a heatmap or parallel coordinates plot

Expected outcome: A table of results showing which parameters have the most impact on performance. The optimal parameters should generalize well to unseen data.

## Exercise 4: Regularization Comparison

Compare different regularization strategies:
1. Implement L1, L2, and Elastic Net regularization variants
2. Train each variant on a dataset with irrelevant features (noise dimensions)
3. Compare weight sparsity, prediction accuracy, and generalization
4. Plot regularization path: how weights change with regularization strength

Expected outcome: L1 regularization should drive irrelevant feature weights to exactly zero. L2 regularization should shrink weights but keep them non-zero. Elastic Net should provide a compromise between the two.

## Exercise 5: Learning Curves Analysis

Generate and interpret learning curves:
1. Train models on increasing subsets of training data (10%, 25%, 50%, 75%, 100%)
2. Plot training and validation error vs training set size
3. Identify whether the model suffers from high bias or high variance
4. Suggest strategies to address the identified issue

Expected outcome: High bias is indicated by both curves converging to high error. High variance is indicated by a large gap between the curves.

## Exercise 6: Feature Engineering Challenge

Apply feature engineering to improve model performance:
1. Start with raw features and a baseline model
2. Create polynomial features (x^2, x^3, interactions)
3. Apply log and Box-Cox transformations to skewed features
4. Create aggregate features (means, max, min per category)
5. Compare model performance with each feature set

Expected outcome: A systematic improvement in model performance as more sophisticated feature engineering is applied. Document which features provide the most benefit.

## Exercise 7: Outlier Detection and Robustness

Test model robustness to outliers:
1. Add outliers to training data (extreme values in 5% of samples)
2. Train standard model and observe performance degradation
3. Implement winsorization (clipping extreme values)
4. Implement Huber loss (less sensitive to outliers)
5. Compare robustness of different approaches

Expected outcome: Standard MSE loss is highly sensitive to outliers. Huber loss and data winsorization provide more robust estimates.

## Exercise 8: Ensemble Methods

Implement ensemble techniques:
1. Train multiple models with different random seeds
2. Average predictions (bagging-style ensemble)
3. Implement weighted voting based on validation performance
4. Compare ensemble performance vs individual models
5. Analyze diversity among ensemble members

Expected outcome: The ensemble should outperform individual models, especially in terms of variance reduction. Model diversity is key to ensemble success.

## Exercise 9: Model Serialization and Deployment

Implement model persistence:
1. Save trained model parameters to a JSON or binary file
2. Load model from file and verify predictions match
3. Create a simple REST API wrapper using Java HTTP server
4. Implement model versioning for rollback capability

Expected outcome: A complete model serving pipeline that can save, load, and serve predictions via HTTP. Include version metadata for reproducibility.

## Exercise 10: Comprehensive Evaluation Report

Generate a complete evaluation report including:
1. Dataset description and exploratory analysis
2. Preprocessing steps applied
3. Model configuration and training results
4. Performance metrics on test set
5. Confusion matrix and ROC curve (for classification)
6. Learning curves and hyperparameter sensitivity analysis
7. Limitations and potential improvements

Expected outcome: A professional evaluation report that demonstrates thorough understanding of the model development process and its limitations.