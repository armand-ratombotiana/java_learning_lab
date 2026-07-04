# History of Time Series Analysis

## 1920s–1930s: Foundations

- Yule (1927) introduced autoregressive (AR) models to predict sunspot activity
- Slutsky (1937) introduced moving average (MA) models
- Wold's decomposition (1938): any stationary process = deterministic + moving average

## 1970s: The ARIMA Revolution

- Box and Jenkins (1970) published *Time Series Analysis: Forecasting and Control*
- ARIMA (Autoregressive Integrated Moving Average) became the standard approach
- The Box-Jenkins method formalized identification → estimation → diagnostic checking

## 1980s: ARCH/GARCH

- Engle (1982) introduced ARCH (Autoregressive Conditional Heteroskedasticity) for volatility modeling
- Bollerslev (1986) extended to GARCH — crucial for financial time series
- Nobel Prize in Economics (2003) to Engle and Granger

## 2000s–2020s: ML & Deep Learning for TS

- Prophet (2017, Facebook) made forecasting accessible to non-experts
- LSTMs and Transformers applied to time series (Informer, 2021)
- Probabilistic forecasting (GluonTS, Pyro) gave prediction intervals, not just point estimates

## Java Timeline

| Year | Library | Significance |
|---|---|---|
| 2014 | Smile | ARIMA, GARCH, exponential smoothing |
| 2019 | Tribuo | Time series built into ML framework |
| 2021 | Kafka Streams | Real-time TS anomaly detection |
