# Ensemble Methods — Code Deep Dive

## 1. Package Structure

The com.datasci.12 package contains: CoreAlgorithm.java (main algorithm), DataPreprocessor.java (data preparation), Metrics.java (evaluation), Utils.java (helper functions).

### 1.1 Directory Layout

src/main/java/com/datasci/12/
  CoreAlgorithm.java    -- Primary algorithm implementation
  DataPreprocessor.java -- Data cleaning and transformation
  Metrics.java         -- Evaluation metrics
  Utils.java           -- Mathematical utilities

src/test/java/com/datasci/12/
  AllTests.java        -- JUnit 5 test suite

## 2. CoreAlgorithm Implementation

The CoreAlgorithm class implements the primary Ensemble Methods algorithm. It uses a builder pattern for configuration.

### 2.1 Class Design

The algorithm follows the Template Method pattern: fit() defines the training skeleton, while specific methods (forward, computeLoss, computeGradients, updateParameters) can be overridden for different variants.

### 2.2 Training Loop

The fit() method:
1. Validates inputs
2. Initializes weights with small random values
3. Optionally splits data into training and validation sets
4. Iterates through epochs:
   a. Forward pass: computes predictions
   b. Loss computation: calculates training and validation loss
   c. Convergence check: stops if loss change is below tolerance
   d. Backward pass: computes gradients
   e. Parameter update: adjusts weights and bias

### 2.3 Prediction

The predict() method performs a forward pass on new data using the trained parameters. It throws IllegalStateException if called before training.

## 3. AlgorithmConfig

The AlgorithmConfig inner class uses the Builder pattern for flexible configuration:

- learningRate: Step size for gradient descent (default: 0.01)
- maxIterations: Maximum training epochs (default: 1000)
- tolerance: Convergence threshold (default: 1e-6)
- regularization: L2 regularization strength (default: 0.0)
- validationRatio: Fraction of data for validation (default: 0.2)
- randomSeed: Seed for reproducibility (default: 42)

The Builder validates all parameters in build(), throwing IllegalArgumentException for invalid values.

## 4. DataPreprocessor

The DataPreprocessor handles data preparation:

- fit(): Computes statistics (mean, std, min, max, median) from training data
- transform(): Applies fitted transformations
- imputeMean(): Fills NaN values with column means
- standardize(): Z-score normalization
- minMaxScale(): Scales to [0, 1] range
- oneHotEncode(): Converts categorical features to binary columns
- trainTestSplit(): Splits data into training and test sets

## 5. Metrics

The Metrics class provides evaluation functions:

- Classification: accuracy, precision, recall, f1Score, confusionMatrix
- Regression: meanSquaredError, rootMeanSquaredError, meanAbsoluteError, rSquared
- Validation: crossValidate (k-fold cross-validation)
- Visualization: rocCurve, auc (ROC curve and area under curve)

## 6. Utils

Utils provides mathematical utilities:

- Matrix operations: transpose, multiply, matrixVectorMultiply
- Distance metrics: euclideanDistance, manhattanDistance, cosineSimilarity
- Statistics: mean, variance, stdDev, covariance, correlation
- Normalization: zScoreNormalize, minMaxNormalize, standardize
- Random utilities: shuffleRows, argmax

## 7. Exception Handling

Custom exceptions:
- DataValidationException: Invalid input data (null, mismatched sizes, empty)
- ConvergenceException: Training fails to converge

All exceptions include detailed error messages for debugging.

## 8. Thread Safety

Classes are designed for single-threaded use. For parallel training, create separate instances with different random seeds.

## 9. Performance Considerations

- Primitive double arrays avoid boxing overhead
- System.arraycopy for efficient array copying
- Stream-based operations for readability (performance-critical paths use loops)

## 10. Extension Points

To create a custom variant:
1. Extend CoreAlgorithm
2. Override forward(), computeLoss(), computeGradients(), or updateParameters()
3. Use AlgorithmConfig.Builder to set custom parameters

## 11. Detailed Method Analysis

### 11.1 fit() Method Walkthrough
The fit() method orchestrates the entire training process:
- Input validation ensures data consistency before any computation
- Weight initialization uses small random values for symmetry breaking
- Data splitting creates validation set when validation_ratio > 0
- The main loop iterates for max_iterations or until convergence
  - Forward pass computes predictions with current parameters
  - Loss computation evaluates prediction quality
  - Convergence check stops early if loss stabilizes
  - Backward pass computes gradients via chain rule
  - Parameter update adjusts weights in direction of steepest descent

### 11.2 predict() Method
Once trained, predict() applies the learned linear function to new data. It performs a simple matrix-vector multiplication followed by bias addition. The method uses defensive copying where appropriate.

### 11.3 Loss Computation Details
The MSE loss computes the average squared difference between predictions and targets. The regularization term adds a penalty proportional to the square of weight magnitudes. The total loss balances data fit against model complexity.

### 11.4 Gradient Computation
Gradients measure how each parameter affects the loss. The error signal (prediction - target) is propagated back through the model. Feature values amplify or attenuate the error signal for each weight. Regularization adds a constant pull toward zero.

## 12. Configuration Deep Dive

The AlgorithmConfig.Builder provides fluent API for configuration: learningRate controls step size, maxIterations limits training time, tolerance determines convergence strictness, regularization controls overfitting prevention, validationRatio reserves data for monitoring, randomSeed ensures reproducibility.

## 13. Advanced Features

### 13.1 Early Stopping
Training stops when validation loss stops improving for a specified number of epochs (patience). This prevents overfitting by finding the optimal stopping point.

### 13.2 Learning Rate Scheduling
Learning rate can be decayed over time: step decay reduces by a factor every N epochs, exponential decay applies continuous reduction, and cosine annealing cyclically varies the rate.

### 13.3 Gradient Clipping
When gradients explode (become very large), gradient clipping rescales them to a maximum norm. This stabilizes training and prevents numerical overflow.

## 14. Best Practices

- Always standardize/normalize features before training
- Use cross-validation for hyperparameter tuning
- Monitor both training and validation loss
- Start with simple models before adding complexity
- Document all experiments for reproducibility
- Use version control for both code and data
- Write unit tests for each algorithm component
- Profile performance to identify bottlenecks