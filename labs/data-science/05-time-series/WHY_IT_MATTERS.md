# Why Time Series Analysis Matters

Time series is the most common data type in enterprise: stock prices, website traffic, sensor readings, sales, server metrics. Almost every organization depends on forecasts for planning.

## Impact Areas

| Domain | Time Series Question | Cost of Error |
|---|---|---|
| Retail | How much inventory to order next month? | Overstock / stockout |
| Finance | Will this stock's volatility increase? | Portfolio loss |
| Cloud ops | Will server load exceed capacity? | Outage |
| Energy | What will peak demand be tomorrow? | Blackout / wasted capacity |
| Healthcare | Will case count exceed hospital capacity? | Resource shortage |

## Java in Time Series

Java powers real-time time series systems (Kafka Streams, Spark Streaming) where models must score millions of events per second. Smile and Tribuo provide Java-native ARIMA and exponential smoothing.

```java
// Smile ARIMA
Arima model = Arima.fit(train, 1, 1, 1);  // ARIMA(p=1, d=1, q=1)
double[] forecast = model.forecast(12);    // forecast 12 steps
```
