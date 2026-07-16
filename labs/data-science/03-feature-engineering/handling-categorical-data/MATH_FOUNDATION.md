# Mathematical Foundation of Categorical Encoding

## 📐 The Dummy Variable Trap
When using One-Hot Encoding, if you have $n$ categories, you might be tempted to create $n$ binary columns. However, this introduces **Perfect Multicollinearity**.
- If you have two categories (Male/Female), and you know `Is_Male = 0`, then `Is_Female` *must* be 1. The two columns are perfectly correlated.
- This breaks many linear models because the covariance matrix becomes non-invertible.

**The Fix**: Always drop one category. Use $n-1$ columns to represent $n$ categories.

## 🧮 Target Encoding (Mean Encoding)
For high-cardinality features (e.g., "Zip Code" with 40,000 values), One-Hot Encoding is impossible. Target encoding replaces each category with the average value of the target variable for that category.

Let $y$ be the target. For category $c$:
$$ \hat{x}_c = \frac{\sum y_i \text{ where } x_i = c}{count(x_i = c)} $$

### Smoothing to Prevent Overfitting
If a category only appears once, the encoding will just be the target value for that row, leading to massive leakage and overfitting. We apply **Smoothing**:
$$ \hat{x}_{smoothed} = \lambda \cdot \hat{x}_c + (1 - \lambda) \cdot \mu_{global} $$
Where $\lambda$ is a weight based on the sample size of the category.