# Step-by-Step: Forecasting Monthly Sales

**Goal**: Build an ARIMA model to forecast monthly retail sales 6 months ahead.

## Step 1: Load and Plot

```java
// Assuming monthly sales data loaded as double array
double[] sales = loadSales("monthly_sales.csv");
plotSeries(sales);  // visual check: upward trend + yearly seasonality
```

## Step 2: Check Stationarity

```java
ADFTest adf = new ADFTest();
double pValue = adf.test(sales);
System.out.println("ADF p-value: " + pValue);
// p=0.32 > 0.05 → non-stationary → need differencing
```

## Step 3: Difference (d=1)

```java
double[] diff1 = difference(sales, 1);
pValue = adf.test(diff1);
System.out.println("After d=1, ADF p-value: " + pValue);
// p=0.001 < 0.05 → stationary
```

## Step 4: Identify p and q from ACF/PACF

```java
double[] acf = autocorrelation(diff1, 20);
double[] pacf = partialAutocorrelation(diff1, 20);
// ACF: significant at lag 1, then decays → suggests MA(1)
// PACF: significant at lag 1, cuts off → suggests AR(1)
// Try ARIMA(1,1,1)
```

## Step 5: Fit ARIMA(1,1,1)

```java
Arima model = Arima.fit(sales, 1, 1, 1);
System.out.println(model);  // AIC, coefficients
// AIC=2842.3
```

## Step 6: Diagnostic Check

```java
double[] residuals = model.residuals();
double lbPValue = ljungBox(residuals, 20);
System.out.println("Ljung-Box p-value: " + lbPValue);
// p=0.43 > 0.05 → residuals are white noise → model OK
```

## Step 7: Forecast

```java
double[] forecast = model.forecast(6);
System.out.println("Forecast for next 6 months:");
for (int i = 0; i < forecast.length; i++) {
    System.out.printf("Month %d: %.2f%n", i + 1, forecast[i]);
}
```

## Step 8: Evaluate

```java
double[] actual = loadActual("next_6_months.csv");
System.out.println("RMSE: " + ForecastMetrics.rmse(actual, forecast));
System.out.println("MAPE: " + ForecastMetrics.mape(actual, forecast));
// MAPE = 4.2% — within acceptable range for this business
```
