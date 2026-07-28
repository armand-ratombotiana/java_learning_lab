# Time Series Analysis Guide

## 1. Classical Decomposition

\[
Y_t = Trend_t + Seasonal_t + Residual_t \quad \text{(additive)}
\]
\[
Y_t = Trend_t \times Seasonal_t \times Residual_t \quad \text{(multiplicative)}
\]

```java
public record Decomposition(double[] trend, double[] seasonal, double[] residual) {
    public static Decomposition additiveMA(double[] series, int period) {
        int n = series.length;
        double[] trend = new double[n];
        for (int t = period; t < n - period; t++) {
            double sum = 0;
            for (int i = t - period; i <= t + period; i++) sum += series[i];
            trend[t] = sum / (2 * period + 1);
        }
        // Handle edges via reflection
        for (int t = 0; t < period; t++) { trend[t] = trend[period]; }
        for (int t = n - period; t < n; t++) { trend[t] = trend[n - period - 1]; }
        
        // Detrended: Y - T
        double[] detrended = new double[n];
        for (int t = 0; t < n; t++) detrended[t] = series[t] - trend[t];
        
        // Seasonal: average detrended by season position
        double[] seasonal = new double[n];
        double[] seasonAvg = new double[period];
        int[] seasonCount = new int[period];
        for (int t = 0; t < n; t++) {
            int s = t % period;
            seasonAvg[s] += detrended[t];
            seasonCount[s]++;
        }
        for (int s = 0; s < period; s++) seasonAvg[s] /= seasonCount[s];
        double meanAdj = Arrays.stream(seasonAvg).average().orElseThrow();
        for (int s = 0; s < period; s++) seasonAvg[s] -= meanAdj;
        for (int t = 0; t < n; t++) seasonal[t] = seasonAvg[t % period];
        
        double[] residual = new double[n];
        for (int t = 0; t < n; t++) residual[t] = series[t] - trend[t] - seasonal[t];
        
        return new Decomposition(trend, seasonal, residual);
    }
}
```

## 2. ARIMA(p, d, q) Model

- **p**: autoregressive order (AR)
- **d**: differencing order (I)
- **q**: moving average order (MA)

```java
public class ARIMA {
    private final int p, d, q;
    private final double[] arCoeffs, maCoeffs;
    private double mean = 0;

    public ARIMA(int p, int d, int q) {
        this.p = p; this.d = d; this.q = q;
        this.arCoeffs = new double[p];
        this.maCoeffs = new double[q];
    }

    public double[] difference(double[] series) {
        double[] diff = Arrays.copyOf(series, series.length);
        for (int i = 0; i < d; i++) {
            double[] tmp = new double[diff.length - 1];
            for (int t = 1; t < diff.length; t++) tmp[t - 1] = diff[t] - diff[t - 1];
            diff = tmp;
        }
        return diff;
    }

    public void fit(double[] series) {
        double[] diffSeries = difference(series);
        mean = Arrays.stream(diffSeries).average().orElseThrow();
        double[] centered = Arrays.stream(diffSeries).map(v -> v - mean).toArray();
        
        // Estimate AR coefficients via Yule-Walker equations
        if (p > 0) {
            double[] acvf = new double[p + 1];
            for (int k = 0; k <= p; k++) {
                acvf[k] = autocovariance(centered, k);
            }
            double[][] toeplitz = new double[p][p];
            for (int i = 0; i < p; i++) {
                for (int j = 0; j < p; j++) toeplitz[i][j] = acvf[Math.abs(i - j)];
            }
            double[] rhs = Arrays.copyOfRange(acvf, 1, p + 1);
            arCoeffs[0] = rhs[0] / acvf[0]; // simplified - use solveToeplitz for p > 1
            if (p > 1) {
                arCoeffs = solveToeplitz(toeplitz, rhs);
            }
        }
        // Estimate MA coefficients via innovation algorithm (simplified)
        if (q > 0) {
            Arrays.fill(maCoeffs, 0.01); // placeholder for full implementation
        }
    }

    public double[] forecast(double[] series, int steps) {
        double[] diff = Arrays.copyOf(series, series.length);
        for (int i = 0; i < d; i++) {
            diff = difference(series);
        }
        double[] fc = new double[steps];
        double[] errors = new double[Math.max(p, q)];
        for (int h = 0; h < steps; h++) {
            double pred = mean;
            for (int i = 0; i < p && i < series.length + h; i++) {
                pred += arCoeffs[i] * (series[series.length - 1 - i + (h > 0 ? 0 : 0)] - mean);
            }
            fc[h] = pred;
        }
        return fc;
    }
}
```

## 3. Exponential Smoothing (Holt-Winters)

```java
public record HoltWinters(double level, double trend, double[] seasonal) {
    public static HoltWinters fit(double[] series, int period, double alpha, double beta, double gamma) {
        int n = series.length;
        // Initialize
        double level = series[0];
        double trend = (series[period] - series[0]) / period;
        double[] seasonals = new double[period];
        for (int s = 0; s < period; s++) seasonals[s] = series[s] - level;
        
        for (int t = 1; t < n; t++) {
            double lastLevel = level;
            double lastSeasonal = seasonals[t % period];
            level = alpha * (series[t] - lastSeasonal) + (1 - alpha) * (level + trend);
            trend = beta * (level - lastLevel) + (1 - beta) * trend;
            seasonals[t % period] = gamma * (series[t] - level) + (1 - gamma) * lastSeasonal;
        }
        return new HoltWinters(level, trend, seasonals);
    }
    
    public double forecast(int steps, int period) {
        return level + steps * trend + seasonal[(seasonal.length - period + steps % period) % seasonal.length];
    }
}
```

## 4. Model Evaluation

```java
public record ForecastMetrics(double mae, double rmse, double mase, double mape) {
    public static ForecastMetrics compute(double[] actual, double[] forecast, double[] naive) {
        int n = actual.length;
        double mae = 0, rmse = 0, mape = 0;
        for (int i = 0; i < n; i++) {
            mae += Math.abs(actual[i] - forecast[i]);
            rmse += Math.pow(actual[i] - forecast[i], 2);
            mape += Math.abs((actual[i] - forecast[i]) / actual[i]);
        }
        mae /= n; rmse = Math.sqrt(rmse / n); mape /= n;
        double naiveMae = 0;
        for (int i = 1; i < naive.length; i++) naiveMae += Math.abs(naive[i] - naive[i - 1]);
        naiveMae /= (naive.length - 1);
        double mase = naiveMae > 0 ? mae / naiveMae : Double.NaN;
        return new ForecastMetrics(mae, rmse, mase, mape);
    }
}
```
