# Lab 01 Guide: Linear Regression

## Step 1 — Understand the Hypothesis
y = Xβ + ε

## Step 2 — OLS Closed-Form
β = (XᵀX)⁻¹Xᵀy

Implement matrix inversion using Gaussian elimination or Apache Commons Math.

## Step 3 — Gradient Descent
Repeat until convergence:
βⱼ := βⱼ − α * (1/m) * Σ(h(xⁱ) − yⁱ) * xⱼⁱ

## Step 4 — Evaluation Metrics
- **MSE**: (1/n) Σ(yᵢ − ŷᵢ)²
- **MAE**: (1/n) Σ|yᵢ − ŷᵢ|
- **R²**: 1 − (SS_res / SS_tot)

## Step 5 — Validate Assumptions
- Residuals vs fitted plot (linearity)
- Q-Q plot (normality)
- Scale-location plot (homoscedasticity)

## Step 6 — Run Tests
Compile and run Main.java to verify OLS and gradient descent implementations against known datasets.
