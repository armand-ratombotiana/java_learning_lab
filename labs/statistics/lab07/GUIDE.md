# Guide: Time Series Analysis in Java

## Step 1: Simple Moving Average (SMA)
Slide a window of size k over the data. At each position t, average values from t-k+1 to t.

## Step 2: Exponential Moving Average (EMA)
Weight recent observations more heavily. EMAₜ = α·yₜ + (1-α)·EMAₜ₋₁. Typical α = 2/(k+1).

## Step 3: Autocorrelation
Compute Pearson correlation between the series and itself lagged by k periods.

## Step 4: Trend Decomposition (Additive)
- Trend: SMA of appropriate window
- Seasonal: average of detrended values per period
- Residual: original - trend - seasonal

## Step 5: Java Implementation
```java
public static double[] sma(double[] data, int window) {
    double[] result = new double[data.length - window + 1];
    for (int i = 0; i < result.length; i++) {
        double sum = 0;
        for (int j = 0; j < window; j++) sum += data[i + j];
        result[i] = sum / window;
    }
    return result;
}
```

## Test Cases
- Data: {1,2,3,4,5,6,7,8,9,10}, SMA(3) → {2,3,4,5,6,7,8,9}
- EMA(0.5) on same data → exponentially weighted values
- Autocorrelation lag 1 on linear data ≈ 0.97
