# Math Foundation: EDA Statistics

## 1. Descriptive Statistics

### Central Tendency
- **Mean**: $\mu = \frac{1}{n}\sum_{i=1}^{n} x_i$ — sensitive to outliers
- **Median**: $P_{50}$ — robust to outliers
- **Mode**: most frequent value — useful for categorical data

### Dispersion
- **Variance**: $\sigma^2 = \frac{1}{n-1}\sum_{i=1}^{n}(x_i - \bar{x})^2$
- **Standard deviation**: $\sigma = \sqrt{\sigma^2}$
- **IQR**: $Q_3 - Q_1$
- **CV** (Coefficient of Variation): $\sigma / \mu$ — scale-invariant dispersion

### Shape
- **Skewness**: $\gamma_1 = \frac{\frac{1}{n}\sum(x_i - \bar{x})^3}{\sigma^3}$
  - > 0: right-skewed (long tail on right)
  - < 0: left-skewed (long tail on left)
- **Kurtosis**: $\gamma_2 = \frac{\frac{1}{n}\sum(x_i - \bar{x})^4}{\sigma^4} - 3$
  - > 0: heavy tails, more outliers
  - < 0: light tails, fewer outliers

## 2. Correlation

### Pearson (linear)
$$ r = \frac{\sum(x_i - \bar{x})(y_i - \bar{y})}{\sqrt{\sum(x_i - \bar{x})^2 \sum(y_i - \bar{y})^2}} $$
Range: [-1, 1]. Assumes linearity and normality.

### Spearman (monotonic)
Pearson on rank-transformed values. Detects any monotonic relationship.

$$ \rho = \frac{\sum(rg(x_i) - \overline{rg_x})(rg(y_i) - \overline{rg_y})}{\sqrt{\sum(rg(x_i) - \overline{rg_x})^2 \sum(rg(y_i) - \overline{rg_y})^2}} $$

## 3. Information Theory Metrics

### Entropy (for categorical variables)
$$ H(X) = -\sum_{i} P(x_i) \log_2 P(x_i) $$
Higher entropy = more uncertainty = more uniform distribution.

### Mutual Information
$$ I(X;Y) = \sum_{x,y} P(x,y) \log_2 \frac{P(x,y)}{P(x)P(y)} $$
Measures any dependency (linear or non-linear) between variables.
