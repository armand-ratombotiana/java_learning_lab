# Common Mistakes in Feature Engineering

## 1. Target Leakage

Using information that would not be available at prediction time.

```java
// LEAKAGE: using the full dataset mean
double globalMean = data.doubleColumn("target").mean();
data.addColumn("target_diff", data.doubleColumn("target").subtract(globalMean));

// No leakage: only use training portion
double trainMean = trainData.doubleColumn("target").mean();
trainData.addColumn("target_diff", trainData.doubleColumn("target").subtract(trainMean));
```

## 2. Creating Too Many Features (Curse of Dimensionality)

With 10,000 features and 1,000 rows, almost every model will overfit.

**Fix**: Feature selection (LASSO, mutual information, importance-based pruning)

## 3. Applying Transformations Without Fitting on Train Only

```java
// WRONG: fit on all data
data.doubleColumn("age").subtract(data.doubleColumn("age").mean());

// RIGHT: fit on train, transform both train and test
double meanAge = train.doubleColumn("age").mean();
train.doubleColumn("age").subtract(meanAge);
test.doubleColumn("age").subtract(meanAge);
```

## 4. One-Hot Encoding High-Cardinality Columns

One-hot encoding a column with 2000 categories (like ZIP code) creates 2000 sparse columns.

**Fix**: Target encoding, frequency encoding, or embedding.

## 5. Adding Future Information in Time Series Features

In time series, features like "average of next 3 days" use future data.

**Fix**: Use only expanding window or rolling features that look backward.

## 6. Ignoring Feature Interactions

Linear models can't capture interactions — if there's an interaction, it must be explicitly created.

**Fix**: Use tree-based models that handle interactions naturally, or create interaction features for linear models.
