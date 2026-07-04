# Reflection: Time Series Analysis

## Key Takeaways

- Time series violates the i.i.d. assumption — temporal dependencies are the signal, not the noise
- Stationarity is the key gate: non-stationary series must be transformed before modeling
- Decomposition (trend + seasonality + residual) is the most powerful mental model
- Simple models (exponential smoothing, naive ARIMA) often beat complex ones in production

## Questions to Internalize

1. Is my forecast improving over a naive baseline, or am I just modeling noise?
2. How far ahead can I realistically forecast given the amount of training data?
3. What external factors (holidays, promotions, weather) does my model miss?
4. If my forecast says +20%, what decision does the business make differently?

## Growth Areas

| Skill | Beginner | Proficient | Master |
|---|---|---|---|
| Stationarity | Visual check | ADF test, differencing | KPSS test, seasonal adjustment |
| Model selection | ARIMA(1,1,1) | Auto-ARIMA (AIC) | Ensemble + meta-learning |
| Seasonality | Visual | SARIMA | STL decomposition, Fourier terms |
| Evaluation | RMSE | MAE + MAPE | MASE + prediction interval coverage |
