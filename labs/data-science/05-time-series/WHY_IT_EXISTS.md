# Why Time Series Analysis Exists

Time series analysis exists because data with a temporal dimension violates the independence assumption of standard statistics. Observations close in time are correlated — yesterday's value predicts today's. Time series methods explicitly model this temporal structure.

## The Gap It Bridges

- **Standard ML** assumes i.i.d. data — shuffling rows is fine. In time series, shuffling destroys the sequential relationship
- **Simple regression** on time (y ~ t) misses seasonality, autocorrelation, and regime changes
- **Business decisions** need forecasts — inventory planning, staffing, budgeting all rely on predicting future values

```java
// Time series violates i.i.d. — you cannot train/test by random split
// Correct: temporal train/test split
int splitPoint = (int) (data.size() * 0.8);
double[] train = Arrays.copyOfRange(values, 0, splitPoint);
double[] test = Arrays.copyOfRange(values, splitPoint, values.length);
```
