# Debugging Time Series Models

## Model Fails to Fit

**Symptom**: `Arima.fit()` throws convergence error.

**Diagnosis**: Series is non-stationary, or too much missing data.

```java
// Check: has the series been differenced?
double[] diff = difference(series, 1);
ADFTest adf = new ADFTest();
System.out.println("Stationary after diff: " + (adf.test(diff) < 0.05));

// Check: are there NaN/Inf values?
for (int i = 0; i < series.length; i++) {
    if (Double.isNaN(series[i]) || Double.isInfinite(series[i])) {
        System.out.println("Bad value at index " + i);
    }
}
```

## Forecast Is Flat Line

**Symptom**: Forecast converges to a constant value after a few steps.

**Diagnosis**: The model is essentially predicting the mean (no trend/seasonality captured).

```java
// Check: are the AR coefficients near zero?
// A well-fit AR(1) should have |phi| > 0.1
// Check: is seasonality present in the training data?
double[] acf = autocorrelation(series, 24);
// If no significant ACF peaks → series may be white noise → flat forecast is correct
```

## Forecast Is Too Volatile

**Symptom**: Forecast shows unrealistic oscillations.

**Diagnosis**: Over-parameterized model (too large p or q).

```java
// Fix: reduce model order
// Try ARIMA(0,1,1) instead of ARIMA(3,1,2)
Arima model = Arima.fit(series, 0, 1, 1);
```

## Prediction Intervals Are Too Wide

**Symptom**: 95% CI covers ±∞ after a few steps.

**Diagnosis**: Multi-step forecast uncertainty compounds.

```java
// This is expected for long horizons — variance grows with horizon
// Solution: report shorter horizons or use ensemble methods
```

## AIC/BIC Not Decreasing After Adding Parameters

**Symptom**: ARIMA(2,1,2) has higher AIC than ARIMA(1,1,1).

**Diagnosis**: Additional parameters are not improving fit — the model is being penalized for complexity.

```java
// Accept the simpler model
System.out.println("ARIMA(1,1,1) AIC: " + arima111.AIC());
System.out.println("ARIMA(2,1,2) AIC: " + arima212.AIC());
// Lower AIC = better. Simpler model is preferred.
```
