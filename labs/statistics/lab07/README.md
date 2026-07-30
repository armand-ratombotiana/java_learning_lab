# Lab 07: Time Series Analysis

## Overview
Time series analysis examines data points collected over time to identify trends, seasonal patterns, and autocorrelation. Key techniques include moving averages and exponential smoothing.

## Learning Objectives
- Compute simple moving averages (SMA)
- Compute exponential moving averages (EMA)
- Detect trend and seasonality components
- Calculate autocorrelation
- Perform additive trend decomposition

## Key Formulas

| Measure | Formula |
|---------|---------|
| SMA(k) | SMAₜ = (1/k) Σᵢ₌₀ᵏ⁻¹ yₜ₋ᵢ |
| EMA(α) | EMAₜ = α·yₜ + (1-α)·EMAₜ₋₁ |
| Autocorrelation lag k | rₖ = Σ(yₜ-ȳ)(yₜ₋ₖ-ȳ) / Σ(yₜ-ȳ)² |
| Trend Decomposition | yₜ = Trendₜ + Seasonalₜ + Residualₜ |

## Running the Code

```bash
javac -d out src/TimeSeriesAnalysis.java
java -cp out com.statistics.lab07.TimeSeriesAnalysis
```
