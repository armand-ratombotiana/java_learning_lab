# Problem Walkthrough: Time Series Forecasting

## Problem
Build a time series forecasting pipeline that decomposes a series, fits a SARIMA model, generates forecasts with prediction intervals, and evaluates accuracy.

## Step 1: Stationarity Testing (ADF)

```java
public class ADFTest {
    public record ADFResult(double adfStat, double pValue, boolean stationary, int usedLag) {}
    
    public ADFResult test(double[] series, int maxLag) {
        // ΔY_t = α + βt + γY_{t-1} + Σδ_i ΔY_{t-i} + ε_t
        // H0: γ = 0 (unit root, non-stationary)
        int n = series.length;
        maxLag = Math.min(maxLag, n - 2);
        double[] y = Arrays.copyOfRange(series, 1, n);
        double[] yLag = Arrays.copyOf(series, n - 1);
        double[][] X = new double[n - 1][2 + maxLag];
        for (int t = 0; t < n - 1; t++) {
            X[t][0] = 1.0; // constant
            X[t][1] = t;   // trend
            X[t][2] = yLag[t]; // y_{t-1}
            for (int l = 1; l <= maxLag && t - l >= 0; l++) {
                X[t][2 + l] = series[t - l + 1] - series[t - l];
            }
        }
        OLSResult ols = ols(y, X);
        // γ is coefficient at index 2
        double gamma = ols.coefficients[2];
        double se = Math.sqrt(ols.varcov[2][2]);
        double adfStat = gamma / se;
        // MacKinnon critical values (simplified)
        double pValue = adfPValue(adfStat, n);
        return new ADFResult(adfStat, pValue, pValue < 0.05, maxLag);
    }
}
```

## Step 2: ARIMA Fit via MLE

```java
public class ARIMAFitter {
    public ARIMAResult fit(double[] series, int p, int d, int q) {
        double[] diff = series;
        for (int i = 0; i < d; i++) {
            double[] tmp = new double[diff.length - 1];
            for (int t = 1; t < diff.length; t++) tmp[t-1] = diff[t] - diff[t-1];
            diff = tmp;
        }
        // Conditional Sum of Squares (CSS) estimation
        int n = diff.length;
        double mean = Arrays.stream(diff).average().orElseThrow();
        double[] centered = new double[n];
        for (int i = 0; i < n; i++) centered[i] = diff[i] - mean;
        
        // Build AR design matrix
        int m = Math.max(p, q);
        double[][] X = new double[n - m][p];
        double[] y = new double[n - m];
        for (int t = m; t < n; t++) {
            y[t - m] = centered[t];
            for (int j = 0; j < p; j++) {
                X[t - m][j] = centered[t - 1 - j];
            }
        }
        OLSResult ols = ols(y, X);
        
        // Compute residuals
        double[] residuals = new double[n];
        for (int t = 0; t < n; t++) {
            double pred = mean;
            for (int j = 0; j < p && t - 1 - j >= 0; j++) {
                pred += ols.coefficients[j] * (diff[t - 1 - j] - mean);
            }
            residuals[t] = diff[t] - pred;
        }
        
        double sigma2 = Arrays.stream(residuals).map(r -> r * r).sum() / (n - p);
        int k = p + q;
        double aic = n * Math.log(sigma2) + 2 * k;
        double bic = n * Math.log(sigma2) + k * Math.log(n);
        
        return new ARIMAResult(ols.coefficients, p, d, q, sigma2, aic, bic);
    }
}
```

## Step 3: Forecast with Prediction Intervals

```java
public record ForecastPoint(double value, double ciLower, double ciUpper) {}

public List<ForecastPoint> forecastARIMA(double[] series, double[] ar, int d, double sigma2, int steps, double alpha) {
    List<ForecastPoint> result = new ArrayList<>();
    double[] values = Arrays.copyOf(series, series.length + steps);
    int n = series.length;
    double z = normalQuantile(1 - alpha/2);
    
    for (int h = 1; h <= steps; h++) {
        double pred = 0;
        for (int j = 0; j < ar.length && n + h - 2 - j >= 0; j++) {
            pred += ar[j] * values[n + h - 2 - j];
        }
        values[n + h - 1] = pred;
        // Undo differencing
        double fc = pred;
        for (int i = 0; i < d; i++) {
            fc += series[series.length - 1 - i];
        }
        // Prediction interval variance: sigma² * (1 + ψ₁² + ... + ψₕ₋₁²)
        double psiVariance = sigma2 * h; // simplified: accumulates MA innovation variance
        double me = z * Math.sqrt(psiVariance);
        result.add(new ForecastPoint(fc, fc - me, fc + me));
    }
    return result;
}
```

## Step 4: Verification

```java
@Test
public void testHoltWinters() {
    double[] series = {10, 12, 13, 10, 13, 14, 11, 14, 15};
    HoltWinters hw = HoltWinters.fit(series, 3, 0.5, 0.1, 0.3);
    double fc = hw.forecast(1, 3);
    assertEquals(14.5, fc, 0.5);
}

@Test
public void testDecomposition() {
    double[] series = generateSeasonal(365, 7);
    Decomposition dec = Decomposition.additiveMA(series, 7);
    double residualVar = variance(dec.residual());
    double seriesVar = variance(series);
    assertTrue(residualVar / seriesVar < 0.5); // Residual variance should be smaller
}
```
