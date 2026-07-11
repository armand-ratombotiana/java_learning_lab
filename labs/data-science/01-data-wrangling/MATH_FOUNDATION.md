# Mathematical Foundation of Data Wrangling

## 📏 Feature Scaling Mathematics

### 1. Min-Max Normalization
This technique rescales the data so that all values fall within a specific range, typically $[0, 1]$.

Let $x$ be an original value, $x_{min}$ be the minimum value in the feature column, and $x_{max}$ be the maximum value.
The normalized value $x'$ is:
$$ x' = \frac{x - x_{min}}{x_{max} - x_{min}} $$

*Pros*: Preserves all relationships in the data exactly. Does not change the distribution.
*Cons*: Highly sensitive to outliers. If one outlier is extremely large, it will compress all the normal data into a tiny range near 0.

### 2. Z-Score Standardization
This technique centers the data around a mean of 0 and scales it based on the standard deviation.

Let $\mu$ be the mean of the feature column and $\sigma$ be the standard deviation.
The standardized value $z$ is:
$$ z = \frac{x - \mu}{\sigma} $$

Where:
$$ \mu = \frac{1}{n} \sum_{i=1}^{n} x_i $$
$$ \sigma = \sqrt{\frac{1}{n} \sum_{i=1}^{n} (x_i - \mu)^2} $$

*Pros*: Less sensitive to extreme outliers than Min-Max. Required for many algorithms (like SVMs and Neural Networks) that assume data is normally distributed around zero.

## 🎯 Outlier Detection: Interquartile Range (IQR)
The IQR is a robust statistical measure to identify outliers without being skewed by the outliers themselves (unlike mean and standard deviation).

1. Order the data from lowest to highest.
2. Find the median (Q2).
3. Find the median of the lower half (Q1, 25th percentile).
4. Find the median of the upper half (Q3, 75th percentile).
5. Calculate the IQR: 
   $$ IQR = Q3 - Q1 $$
6. Define the bounds. Any data point outside these bounds is considered an outlier:
   - Lower Bound = $Q1 - 1.5 \times IQR$
   - Upper Bound = $Q3 + 1.5 \times IQR$