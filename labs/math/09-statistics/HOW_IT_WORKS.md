# How Statistics Works

## Descriptive Statistics

### Measures of Central Tendency

- **Mean**: $\bar{x} = \frac{1}{n}\sum_{i=1}^n x_i$
- **Median**: middle value when sorted
- **Mode**: most frequent value

### Measures of Spread

- **Variance**: $s^2 = \frac{1}{n-1}\sum_{i=1}^n (x_i - \bar{x})^2$
- **Standard deviation**: $s = \sqrt{s^2}$
- **IQR**: $Q_3 - Q_1$

## Inferential Statistics

### t-Test (two-sample)

$$
t = \frac{\bar{x}_1 - \bar{x}_2}{\sqrt{\frac{s_1^2}{n_1} + \frac{s_2^2}{n_2}}}
$$

### Linear Regression

$$
y = \beta_0 + \beta_1 x + \varepsilon
$$

$$
\beta_1 = \frac{\sum (x_i - \bar{x})(y_i - \bar{y})}{\sum (x_i - \bar{x})^2}
$$

### In Java: Linear Regression

```java
public static double[] linearRegression(double[] x, double[] y) {
    int n = x.length;
    double mx = mean(x), my = mean(y);
    double num = 0, den = 0;
    for (int i = 0; i < n; i++) {
        num += (x[i] - mx) * (y[i] - my);
        den += (x[i] - mx) * (x[i] - mx);
    }
    double slope = num / den;
    double intercept = my - slope * mx;
    return new double[]{slope, intercept};
}
```
