# Regression - Code Deep Dive

```python
import numpy as np
from sklearn.linear_model import LinearRegression, Ridge, Lasso, ElasticNet
from sklearn.preprocessing import PolynomialFeatures, StandardScaler
from sklearn.model_selection import cross_val_score
from sklearn.metrics import mean_squared_error, r2_score

# === LINEAR REGRESSION ===

# Simple linear regression
model = LinearRegression()
model.fit(X_train.reshape(-1, 1), y_train)
y_pred = model.predict(X_test.reshape(-1, 1))

# Coefficients
print(f"Coefficient: {model.coef_}")
print(f"Intercept: {model.intercept_}")

# Multiple linear regression
model = LinearRegression()
model.fit(X_train, y_train)

# Closed-form solution
X_with_intercept = np.column_stack([np.ones(len(X_train)), X_train])
beta = np.linalg.lstsq(X_with_intercept, y_train, rcond=None)[0]

# === POLYNOMIAL REGRESSION ===

poly = PolynomialFeatures(degree=2, include_bias=False)
X_poly = poly.fit_transform(X_train)
X_poly_test = poly.transform(X_test)

model = LinearRegression()
model.fit(X_poly, y_train)

# === RIDGE REGRESSION ===

scaler = StandardScaler()
X_scaled = scaler.fit_transform(X_train)

ridge = Ridge(alpha=1.0)
ridge.fit(X_scaled, y_train)

# Cross-validation for alpha selection
from sklearn.linear_model import RidgeCV
ridge_cv = RidgeCV(alphas=[0.1, 1.0, 10.0], cv=5)
ridge_cv.fit(X_scaled, y_train)
print(f"Best alpha: {ridge_cv.alpha_}")

# === LASSO REGRESSION ===

lasso = Lasso(alpha=0.1, max_iter=10000)
lasso.fit(X_scaled, y_train)

# Sparsity: some coefficients become exactly zero
print(f"Non-zero coefficients: {np.sum(lasso.coef_ != 0)}")

# === ELASTIC NET ===

elastic = ElasticNet(alpha=0.1, l1_ratio=0.5, max_iter=10000)
elastic.fit(X_scaled, y_train)

# === REGULARIZATION PATH ===

alphas = np.logspace(-4, 2, 100)
coefs = []

for alpha in alphas:
    ridge = Ridge(alpha=alpha)
    ridge.fit(X_scaled, y_train)
    coefs.append(ridge.coef_)

# === EVALUATION ===

from sklearn.metrics import mean_absolute_error, mean_squared_error

y_pred = model.predict(X_test)

mse = mean_squared_error(y_test, y_pred)
rmse = np.sqrt(mse)
mae = mean_absolute_error(y_test, y_pred)
r2 = r2_score(y_test, y_pred)

# Adjusted R-squared
n = len(y_test)
p = X_test.shape[1]
r2_adj = 1 - (1 - r2) * (n - 1) / (n - p - 1)

# === CROSS-VALIDATION ===

cv_scores = cross_val_score(LinearRegression(), X_train, y_train, cv=5, scoring='neg_mean_squared_error')
print(f"CV MSE: {-cv_scores.mean():.4f} (+/- {cv_scores.std():.4f})")

# === RESIDUAL ANALYSIS ===

residuals = y_test - y_pred

# Check for patterns in residuals
import matplotlib.pyplot as plt

plt.figure(figsize=(12, 4))

plt.subplot(1, 3, 1)
plt.scatter(y_pred, residuals)
plt.axhline(y=0, color='r')
plt.xlabel('Predicted')
plt.ylabel('Residuals')

plt.subplot(1, 3, 2)
plt.hist(residuals, bins=30)
plt.xlabel('Residuals')
plt.ylabel('Frequency')

plt.subplot(1, 3, 3)
from scipy import stats
stats.probplot(residuals, dist="norm", plot=plt)
plt.title('Q-Q Plot')

plt.tight_layout()
plt.show()
```