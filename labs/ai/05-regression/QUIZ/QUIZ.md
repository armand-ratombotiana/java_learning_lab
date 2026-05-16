# Regression - Quiz

## Assessment Questions

### Question 1
What is the closed-form solution for linear regression using least squares?

A) β = (XX^T)^(-1)Xy
B) β = (X^T X)^(-1)X^T y
C) β = X^T y
D) β = Xy^T

**Answer: B** - The normal equation for linear regression is β̂ = (X^T X)^(-1) X^T y, which minimizes the sum of squared residuals.

---

### Question 2
Which regularization method is most likely to produce sparse solutions (some coefficients exactly zero)?

A) Ridge (L2)
B) Lasso (L1)
C) Elastic Net
D) None of the above

**Answer: B** - Lasso (L1 regularization) tends to drive some coefficients exactly to zero, providing automatic feature selection.

---

### Question 3
In the bias-variance decomposition, what happens to bias and variance as model complexity increases?

A) Bias increases, variance decreases
B) Bias decreases, variance increases
C) Both increase
D) Both decrease

**Answer: B** - More complex models have lower bias (fit data better) but higher variance (overfit to noise).

---

### Question 4
Which metric is most sensitive to outliers in regression?

A) Mean Absolute Error (MAE)
B) Mean Squared Error (MSE)
C) R-squared
D) Adjusted R-squared

**Answer: B** - MSE squares the errors, so large outliers have a disproportionately large effect on the metric.

---

### Question 5
What does a negative R-squared value indicate?

A) The model is perfect
B) The model performs worse than simply predicting the mean
C) The model has zero variance
D) The data is linearly correlated

**Answer: B** - Negative R² means the model predictions are worse than simply using the mean of the target variable.

---

### Question 6
In polynomial regression, what happens as you increase the polynomial degree indefinitely?

A) The model always improves
B) The model eventually overfits
C) The model becomes linear
D) The model always underfits

**Answer: B** - Higher degrees increase model flexibility, but beyond a certain point, the model overfits to training data noise.

---

### Question 7
What is the primary purpose of cross-validation in regression?

A) To increase training speed
B) To estimate generalization performance
C) To reduce the number of features
D) To remove outliers

**Answer: B** - Cross-validation provides an estimate of how well the model will generalize to unseen data by repeatedly training and testing on different data splits.

---

### Question 8
Which loss function is more robust to outliers?

A) Mean Squared Error
B) Mean Absolute Error
C) Huber Loss
D) Both B and C

**Answer: D** - MAE and Huber loss are less sensitive to outliers than MSE because they don't square the errors.

---

### Question 9
What is multicollinearity in regression?

A) When predictions are correlated with each other
B) When two or more predictor variables are highly correlated
C) When the target variable has multiple values
D) When there are too few data points

**Answer: B** - Multicollinearity occurs when predictor variables are highly correlated, which can cause instability in coefficient estimates.

---

### Question 10
In Ridge regression, what happens to the coefficient estimates as the regularization parameter (λ) increases?

A) They approach zero but never reach it exactly
B) They become exactly zero
C) They increase without bound
D) They remain unchanged

**Answer: A** - L2 regularization (Ridge) shrinks coefficients toward zero but rarely makes them exactly zero.

---

### Question 11 (Bonus)
What is the advantage of using the Huber loss function?

A) It always uses squared errors
B) It combines benefits of MSE and MAE
C) It is faster to compute
D) It requires no tuning

**Answer: B** - Huber loss behaves like MSE for small errors and like MAE for large errors, combining robustness with smooth optimization.

---

### Question 12 (Bonus)
What does the intercept represent in linear regression?

A) The slope of the line
B) The predicted value when all independent variables are zero
C) The residual
D) The R-squared value

**Answer: B** - The intercept (β₀) is the predicted value of y when all x variables equal zero.