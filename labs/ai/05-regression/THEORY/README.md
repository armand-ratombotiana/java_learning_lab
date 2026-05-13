# Regression - Theory

## 1. Linear Regression

### Simple Linear Regression
```
y = β₀ + β₁x + ε
```
Single feature, linear relationship.

### Multiple Linear Regression
```
y = Xβ + ε
```
Multiple features, matrix form.

### Least Squares Solution
```
β̂ = (XᵀX)⁻¹Xᵀy
```
Minimizes sum of squared residuals.

## 2. Polynomial Regression

### Model
```
y = β₀ + β₁x + β₂x² + ... + βₖxᵖ + ε
```
Captures non-linear relationships.

### Trade-off
- Higher degree → better fit
- Risk of overfitting
- Use cross-validation

## 3. Regularized Regression

### Ridge (L2)
```
β̂ = argmin ||y - Xβ||² + λ||β||₂²
```
- Shrinks coefficients toward zero
- Handles multicollinearity
- Solution: (XᵀX + λI)⁻¹Xᵀy

### Lasso (L1)
```
β̂ = argmin ||y - Xβ||² + λ||β||₁
```
- Sparse solutions
- Feature selection
- No closed-form solution

### Elastic Net
```
β̂ = argmin ||y - Xβ||² + λ(ρ||β||₁ + (1-ρ)||β||₂²/2)
```
- Combines L1 and L2
- Handles correlated features

## 4. Bias-Variance Trade-off

### Decomposition
```
E[(y - ŷ)²] = σ² + Bias² + Variance
```
- **Bias**: Error from oversimplified model
- **Variance**: Error from model complexity

### Model Selection
- Underfitting: High bias, low variance
- Overfitting: Low bias, high variance
- Optimal: Balance between both

## 5. Loss Functions

### Mean Squared Error (MSE)
```
MSE = (1/n) Σ(yᵢ - ŷᵢ)²
```
- Most common
- Sensitive to outliers

### Mean Absolute Error (MAE)
```
MAE = (1/n) Σ|yᵢ - ŷᵢ|
```
- Robust to outliers

### Huber Loss
```
L(a) = a²/2 if |a| ≤ δ
L(a) = δ(|a| - δ/2) otherwise
```
- Combines MSE and MAE

### Quantile Loss
```
L(a) = a * τ if a > 0
L(a) = a * (τ-1) if a ≤ 0
```
- For quantile regression

## 6. Evaluation Metrics

### R² (Coefficient of Determination)
```
R² = 1 - SS_res / SS_tot
```
- 0 to 1
- Higher is better

### Adjusted R²
```
R²_adj = 1 - (1-R²)(n-1)/(n-p-1)
```
- Accounts for number of predictors

### RMSE
```
RMSE = √MSE
```
- Same scale as target variable

### MAE, MAPE
- Mean Absolute Percentage Error

## 7. Assumptions

### Linear Regression Assumptions
1. **Linearity**: y = Xβ + ε
2. **Independence**: εᵢ independent
3. **Homoscedasticity**: Var(ε) = σ²
4. **Normality**: ε ~ N(0, σ²)
5. **No multicollinearity** (for multiple)