# Lab 01 Interview: Linear Regression

## Q1: What are the key assumptions of linear regression?
Linearity, independence of errors, homoscedasticity, normality of residuals, no multicollinearity.

## Q2: Explain the difference between MSE and MAE.
MSE squares errors (penalizes large errors more), MAE uses absolute values (robust to outliers).

## Q3: When would you prefer gradient descent over OLS?
When n (samples) or p (features) is very large; OLS inversion is O(n·p² + p³).

## Q4: What does R² = 0.85 mean?
85% of the variance in the target is explained by the model.

## Q5: How do you detect multicollinearity?
Variance Inflation Factor (VIF) > 10 indicates multicollinearity.
