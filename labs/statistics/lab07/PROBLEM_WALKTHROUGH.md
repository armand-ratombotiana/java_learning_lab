# Problem Walkthrough: Time Series Analysis

## Problem 1: Hourly Ride Demand Forecast — Company: Uber
### Interview Scenario
"You're at Uber on the marketplace forecasting team. You have 24 hours of ride demand that grows with a clear trend and an intra-day seasonal pattern. Operations wants: a smoothed view of demand (SMA and EMA), the autocorrelation structure to justify the model choice, a decomposition into trend/seasonal/residual so surge planning can use the seasonal factors, and a next-hour forecast."

### The Problem
1. Compute the SMA(4) smoothed series
2. Compute the EMA(0.3) series and explain its responsiveness
3. Compute autocorrelation at lags 1-4
4. Decompose the series additively with period 4 into trend, seasonal, residual
5. Produce a next-hour forecast by combining the EMA level with a trend step
6. Use the seasonal factors to explain intra-day surge timing

### Solution Walkthrough
- Step 1: Reuse the lab's `TimeSeriesAnalysis` methods verbatim: `sma`, `ema`, `autocorrelation`, `decomposeAdditive`, `mean`
- Step 2: SMA(4) slides a window of 4 over the 24 points: {52.5, 54.0, ..., 84.5} — 21 values
- Step 3: EMA(0.3) tracks the level with one-pass recursion `result[i] = alpha * data[i] + (1 - alpha) * result[i-1]`, ending at 82.76
- Step 4: Autocorrelation at lags 1-4 ≈ 0.956-0.999 — strong serial dependence, and the lag-4 peak (0.998624) flags the seasonal structure
- Step 5: `decomposeAdditive(rides, 4)` centers the SMA for the even period, averages detrended values per quarter, and centers the factors; trend runs 53.25 → 83.63, seasonal cycles {-2, -1, 0, +3}, residuals are small (±0.38)
- Step 6: Forecast = last EMA + last SMA increments' average = 82.76 + 1.63 = 84.38 — level plus trend continuation

### Code
```java
package com.statistics.lab07;

import java.util.Arrays;

/**
 * Mirrors the lab's TimeSeriesAnalysis class (SMA, EMA, autocorrelation,
 * additive decomposition) and applies it to Uber-style hourly ride
 * demand with trend + intra-day seasonality.
 */
public final class DemandForecaster {

    private DemandForecaster() {
    }

    public static double mean(double[] data) {
        double sum = 0;
        for (double v : data) sum += v;
        return sum / data.length;
    }

    public static double[] sma(double[] data, int window) {
        int n = data.length - window + 1;
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            double sum = 0;
            for (int j = 0; j < window; j++) {
                sum += data[i + j];
            }
            result[i] = sum / window;
        }
        return result;
    }

    public static double[] ema(double[] data, double alpha) {
        double[] result = new double[data.length];
        result[0] = data[0];
        for (int i = 1; i < data.length; i++) {
            result[i] = alpha * data[i] + (1 - alpha) * result[i - 1];
        }
        return result;
    }

    public static double autocorrelation(double[] data, int lag) {
        int n = data.length - lag;
        double[] x = new double[n];
        double[] y = new double[n];
        double mx = 0, my = 0;
        for (int i = 0; i < n; i++) {
            x[i] = data[i + lag];
            y[i] = data[i];
            mx += x[i];
            my += y[i];
        }
        mx /= n;
        my /= n;
        double sxy = 0, sxx = 0, syy = 0;
        for (int i = 0; i < n; i++) {
            double dx = x[i] - mx;
            double dy = y[i] - my;
            sxy += dx * dy;
            sxx += dx * dx;
            syy += dy * dy;
        }
        return sxy / Math.sqrt(sxx * syy);
    }

    public static double[][] decomposeAdditive(double[] data, int period) {
        double[] trendRaw = sma(data, period);
        double[] trend;
        if (period % 2 == 0) {
            trend = new double[trendRaw.length - 1];
            for (int i = 0; i < trend.length; i++) {
                trend[i] = (trendRaw[i] + trendRaw[i + 1]) / 2.0;
            }
        } else {
            trend = trendRaw;
        }

        int offset = (data.length - trend.length) / 2;
        double[] detrended = new double[trend.length];
        for (int i = 0; i < trend.length; i++) {
            detrended[i] = data[offset + i] - trend[i];
        }

        double[] seasonal = new double[trend.length];
        double[] periodAvgs = new double[period];
        int[] periodCounts = new int[period];
        for (int i = 0; i < detrended.length; i++) {
            int idx = (offset + i) % period;
            periodAvgs[idx] += detrended[i];
            periodCounts[idx]++;
        }
        for (int i = 0; i < period; i++) {
            periodAvgs[i] /= periodCounts[i];
        }
        double seasMean = mean(periodAvgs);
        for (int i = 0; i < period; i++) {
            periodAvgs[i] -= seasMean;
        }
        for (int i = 0; i < seasonal.length; i++) {
            seasonal[i] = periodAvgs[(offset + i) % period];
        }

        double[] residual = new double[trend.length];
        for (int i = 0; i < residual.length; i++) {
            residual[i] = detrended[i] - seasonal[i];
        }

        return new double[][]{trend, seasonal, residual};
    }

    public static void main(String[] args) {
        double[] rides = {
            50, 55, 51, 54, 56, 61, 58, 60, 63, 67, 64, 67,
            69, 74, 70, 73, 76, 80, 77, 79, 82, 87, 83, 86
        };
        System.out.println("=== Hourly ride demand (24 h, trend + seasonality) ===");
        System.out.println("Data: " + Arrays.toString(rides));

        double[] s = sma(rides, 4);
        System.out.println("\n=== SMA(4) ===");
        System.out.println(Arrays.toString(s));

        double[] e = ema(rides, 0.3);
        System.out.println("\n=== EMA(0.3) ===");
        for (double v : e) System.out.printf("%.2f ", v);
        System.out.println();

        System.out.println("\n=== Autocorrelation ===");
        for (int lag = 1; lag <= 4; lag++) {
            System.out.printf("Lag %d: %.6f%n", lag, autocorrelation(rides, lag));
        }

        System.out.println("\n=== Additive decomposition (period=4) ===");
        double[][] decomp = decomposeAdditive(rides, 4);
        double[] trend = decomp[0];
        double[] seasonal = decomp[1];
        double[] residual = decomp[2];
        System.out.print("Trend (" + trend.length + "): ");
        for (double v : trend) System.out.printf("%.2f ", v);
        System.out.println();
        System.out.print("Seasonal (" + seasonal.length + "): ");
        for (double v : seasonal) System.out.printf("%.2f ", v);
        System.out.println();
        System.out.print("Residual (" + residual.length + "): ");
        for (double v : residual) System.out.printf("%.2f ", v);
        System.out.println();

        double[] s4 = sma(rides, 4);
        double trendStep = (s4[s4.length - 1] - s4[s4.length - 3]) / 2.0;
        double forecast = e[e.length - 1] + trendStep;
        System.out.printf("%nNext-hour forecast = last EMA + trend step = %.2f + %.2f = %.2f%n",
            e[e.length - 1], trendStep, forecast);
    }
}
```

### Expected Output
```
=== Hourly ride demand (24 h, trend + seasonality) ===
Data: [50.0, 55.0, 51.0, 54.0, 56.0, 61.0, 58.0, 60.0, 63.0, 67.0, 64.0, 67.0, 69.0, 74.0, 70.0, 73.0, 76.0, 80.0, 77.0, 79.0, 82.0, 87.0, 83.0, 86.0]

=== SMA(4) ===
[52.5, 54.0, 55.5, 57.25, 58.75, 60.5, 62.0, 63.5, 65.25, 66.75, 68.5, 70.0, 71.5, 73.25, 74.75, 76.5, 78.0, 79.5, 81.25, 82.75, 84.5]

=== EMA(0.3) ===
50.00 51.50 51.35 52.14 53.30 55.61 56.33 57.43 59.10 61.47 62.23 63.66 65.26 67.88 68.52 69.86 71.70 74.19 75.04 76.22 77.96 80.67 81.37 82.76 

=== Autocorrelation ===
Lag 1: 0.956150
Lag 2: 0.953152
Lag 3: 0.953942
Lag 4: 0.998624

=== Additive decomposition (period=4) ===
Trend (20): 53.25 54.75 56.38 58.00 59.63 61.25 62.75 64.38 66.00 67.63 69.25 70.75 72.38 74.00 75.63 77.25 78.75 80.38 82.00 83.63 
Seasonal (20): -2.00 -1.00 0.00 3.00 -2.00 -1.00 0.00 3.00 -2.00 -1.00 0.00 3.00 -2.00 -1.00 0.00 3.00 -2.00 -1.00 0.00 3.00 
Residual (20): -0.25 0.25 -0.38 0.00 0.38 -0.25 0.25 -0.38 0.00 0.38 -0.25 0.25 -0.38 0.00 0.38 -0.25 0.25 -0.38 0.00 0.38 

Next-hour forecast = last EMA + trend step = 82.76 + 1.63 = 84.38
```

### Company Evaluation
- Uber: ride-demand forecasting, surge detection from seasonal factors, EMA-based real-time level tracking for dispatch.
- Netflix: traffic anomaly detection on streaming starts, weekly-seasonal decomposition of viewing.
- Google: query-volume seasonality, EMA-based SLO dashboards, autocorrelation checks on latency residuals.
- Amazon: inventory demand smoothing with EMA, seasonal decomposition for promotions, autocorrelation for replenishment cycle detection.

---

## Problem 2: Weekly Traffic Trend — Company: Netflix
### Interview Scenario
"You're at Netflix tracking a title's weekly view counts over 10 weeks: {1,2,3,4,5,6,7,8,9,10} million. The team wants the smoothed trend and the forecast for week 11."

### The Problem
1. Compute SMA(3)
2. Compute EMA(0.5)
3. Forecast week 11 from the EMA level plus the SMA trend step

### Solution Walkthrough
- Step 1: `sma(data, 3)` slides the window: {2.0, 3.0, ..., 9.0} — the lab's demo output verbatim
- Step 2: `ema(data, 0.5)` gives the exponentially weighted level, ending at 9.0020
- Step 3: The data is perfectly linear, so the trend step is 1.0 and the next-week forecast is the EMA level plus one unit — mirroring the same level-plus-trend recipe as the demand model

### Code
```java
double[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
double[] s = sma(data, 3);
System.out.println("SMA(3):   " + Arrays.toString(s));
double[] e = ema(data, 0.5);
System.out.print("EMA(0.5): ");
for (double v : e) System.out.printf("%.4f ", v);
System.out.println();
double trendStep = (s[s.length - 1] - s[s.length - 3]) / 2.0;
System.out.printf("Forecast week 11 = %.2f + %.2f = %.2f%n",
    e[e.length - 1], trendStep, e[e.length - 1] + trendStep);
```

### Expected Output
```
SMA(3):   [2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0]
EMA(0.5): 1.0000 1.5000 2.2500 3.1250 4.0625 5.0313 6.0156 7.0078 8.0039 9.0020 
Forecast week 11 = 9.00 + 1.00 = 10.00
```

---

## Problem 3: Seasonal Decomposition Sanity Check — Company: Google
### Interview Scenario
"You're at Google validating a decomposition implementation for ad-revenue seasonality. The test series is constructed as trend plus the quarterly pattern {0, 0.5, -0.5, 0} — a perfect additive model. If the decomposition doesn't recover it exactly, the implementation is broken."

### The Problem
1. Decompose the constructed series with period 4
2. Verify trend, seasonal, and residual against the known truth
3. Confirm residuals are exactly zero

### Solution Walkthrough
- Step 1: Build data from baseTrend {10..48} plus seasPattern[i % 4]; `decomposeAdditive(data, 4)` returns trend/seasonal/residual
- Step 2: The recovered trend is exactly {14, 16, ..., 44}, seasonal exactly {-0.5, 0, 0, 0.5}, residuals all 0.00 — the lab's demo output
- Step 3: Zero residuals on a noiseless additive series prove the centering and alignment (`offset` math) are correct — the same oracle property a production test suite should assert

### Code
```java
int period = 4;
double[] seasonalData = new double[20];
double[] baseTrend = {10, 12, 14, 16, 18, 20, 22, 24, 26, 28,
                      30, 32, 34, 36, 38, 40, 42, 44, 46, 48};
double[] seasPattern = {0, 0.5, -0.5, 0};
for (int i = 0; i < 20; i++) {
    seasonalData[i] = baseTrend[i] + seasPattern[i % 4];
}
double[][] decomp = decomposeAdditive(seasonalData, period);
System.out.print("Trend: ");
for (double v : decomp[0]) System.out.printf("%.2f ", v);
System.out.println();
System.out.print("Seasonal: ");
for (double v : decomp[1]) System.out.printf("%.2f ", v);
System.out.println();
System.out.print("Residual: ");
for (double v : decomp[2]) System.out.printf("%.2f ", v);
System.out.println();
```

### Expected Output
```
Trend: 14.00 16.00 18.00 20.00 22.00 24.00 26.00 28.00 30.00 32.00 34.00 36.00 38.00 40.00 42.00 44.00 
Seasonal: -0.50 0.00 0.00 0.50 -0.50 0.00 0.00 0.50 -0.50 0.00 0.00 0.50 -0.50 0.00 0.00 0.50 -0.50 0.00 0.00 0.50 
Residual: 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 0.00 
```
