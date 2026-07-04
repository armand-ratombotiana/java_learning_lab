# Architecture: Time Series Forecasting System

## System Design

```
┌──────────────┐    ┌──────────────┐    ┌───────────────┐    ┌────────────┐
│  Event Bus   │───>│  Feature     │───>│  Forecast     │───>│  Decision  │
│  (Kafka)     │    │  Store       │    │  Engine       │    │  Service   │
└──────────────┘    └──────────────┘    └───────────────┘    └────────────┘
                                                    │
                                                    ├──> Batch forecast (nightly)
                                                    ├──> Real-time forecast (streaming)
                                                    └──> Anomaly alerts
```

## Component Responsibilities

### Feature Store (Time Series)
- Stores historical time series data
- Supports point-in-time queries (what did this value look like 30 days ago?)
- Handles backfill for new features

### Forecast Engine
- Supports multiple model types (ARIMA, Prophet, Theta, ensemble)
- Auto-model selection via cross-validation on the training window
- Versioned model registry (each forecast is tagged with model + data version)

### Decision Service
- Consumes forecasts and applies business rules
- Generates alerts if forecast exceeds thresholds
- Logs forecast accuracy for monitoring

## Forecast Engine Job Design

```java
public class ForecastJob {
    private final ModelRegistry models;
    private final FeatureStore features;
    
    public ForecastResult run(String seriesId, ForecastConfig config) {
        double[] history = features.getSeries(seriesId, config.getLookbackPeriod());
        
        // Auto-select best model
        Model best = ModelSelector.select(history, config.getCandidateModels());
        
        // Train and forecast
        best.fit(history);
        double[] forecast = best.forecast(config.getHorizon());
        
        // Compute prediction intervals
        double[] intervals = best.predictionIntervals(0.95);
        
        // Log and publish
        models.register(seriesId, best);
        return new ForecastResult(seriesId, forecast, intervals);
    }
}
```
