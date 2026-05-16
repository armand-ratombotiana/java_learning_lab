# Regression - Flashcards

## Quick Review Cards

### Card 1: Normal Equation
**Q:** What is the closed-form solution for ordinary least squares regression?
**A:** β̂ = (X^T X)^(-1) X^T y - This solves the normal equation by inverting X^T X.

---

### Card 2: Lasso vs Ridge
**Q:** What is the key difference in how Lasso and Ridge handle coefficients?
**A:** Lasso can drive coefficients to exactly zero (sparse), Ridge shrinks but rarely to zero.

---

### Card 3: Bias-Variance Tradeoff
**Q:** What happens to bias and variance as model complexity increases?
**A:** Bias decreases, variance increases - there's an optimal complexity that minimizes total error.

---

### Card 4: R-squared
**Q:** What does R² = 0.85 mean?
**A:** The model explains 85% of the variance in the target variable - higher is better.

---

### Card 5: MSE vs MAE
**Q:** Why is MSE more sensitive to outliers than MAE?
**A:** MSE squares errors, so large errors have exponentially larger impact. MAE uses absolute values.

---

### Card 6: Overfitting
**Q:** What are the main signs of overfitting in regression?
**A:** High training accuracy but low test accuracy, large difference between training and validation error.

---

### Card 7: Regularization Purpose
**Q:** What is the main purpose of adding regularization to regression?
**A:** To prevent overfitting by penalizing large coefficients, improving generalization.

---

### Card 8: Cross-Validation
**Q:** What is k-fold cross-validation?
**A:** Split data into k folds, train k times using k-1 folds for training and 1 for validation, average results.

---

### Card 9: Adjusted R²
**Q:** Why use Adjusted R² instead of regular R²?
**A:** Adjusted R² accounts for the number of predictors, preventing inflation when adding many features.

---

### Card 10: Polynomial Regression
**Q:** What problem does increasing polynomial degree address?
**A:** It captures non-linear relationships but increases risk of overfitting.

---

### Card 11: Elastic Net
**Q:** What does Elastic Net combine?
**A:** Both L1 (Lasso) and L2 (Ridge) regularization - useful when features are correlated.

---

### Card 12: Residual Analysis
**Q:** What should residuals look like in a good regression model?
**A:** Randomly distributed around zero, constant variance (homoscedastic), normally distributed.