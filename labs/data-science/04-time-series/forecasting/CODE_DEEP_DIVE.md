# Time Series Code Deep Dive

This lab provides a pure Java implementation of Simple Exponential Smoothing (SES), a fundamental algorithm for time series forecasting.

## 💻 Pure Java Implementation

```java file="labs/data-science/04-time-series/forecasting/SOLUTION/ExponentialSmoothing.java"
package datascience.timeseries;

import java.util.Arrays;

/**
 * A fundamental implementation of Simple Exponential Smoothing (SES).
 * Used for forecasting time series data that has no clear trend or seasonality.
 */
public class ExponentialSmoothing {

    private final double alpha;

    /**
     * @param alpha The smoothing factor. Must be between 0 and 1.
     *              High alpha = fast reaction to recent changes.
     *              Low alpha = smooths out noise, relies on historical average.
     */
    public ExponentialSmoothing(double alpha) {
        if (alpha <= 0 || alpha > 1.0) {
            throw new IllegalArgumentException("Alpha must be between 0 and 1");
        }
        this.alpha = alpha;
    }

    /**
     * Fits the model to historical data and generates a forecast for the next time step.
     * 
     * @param historicalData An array of past observations, ordered from oldest to newest.
     * @return The predicted value for time step T+1.
     */
    public double forecastNext(double[] historicalData) {
        if (historicalData == null || historicalData.length == 0) {
            throw new IllegalArgumentException("Historical data cannot be empty");
        }

        // Initialize the first smoothed value (often just set to the first observation)
        double smoothedValue = historicalData[0];

        // Iteratively apply the exponential smoothing formula
        for (int t = 1; t < historicalData.length; t++) {
            // Formula: S_t = alpha * Y_t + (1 - alpha) * S_{t-1}
            smoothedValue = (alpha * historicalData[t]) + ((1.0 - alpha) * smoothedValue);
        }

        // The forecast for T+1 is simply the last smoothed value
        return smoothedValue;
    }

    /**
     * Generates a smoothed version of the entire historical dataset.
     */
    public double[] smoothData(double[] historicalData) {
        double[] smoothedData = new double[historicalData.length];
        smoothedData[0] = historicalData[0];

        for (int t = 1; t < historicalData.length; t++) {
            smoothedData[t] = (alpha * historicalData[t]) + ((1.0 - alpha) * smoothedData[t - 1]);
        }
        return smoothedData;
    }

    public static void main(String[] args) {
        // Historical daily server CPU load percentages (noisy data)
        double[] cpuLoad = {45.0, 48.0, 42.0, 85.0, 46.0, 44.0, 49.0};

        System.out.println("Historical Data: " + Arrays.toString(cpuLoad));

        // Model 1: High Alpha (0.8) - Highly reactive to recent changes
        ExponentialSmoothing reactiveModel = new ExponentialSmoothing(0.8);
        double reactiveForecast = reactiveModel.forecastNext(cpuLoad);
        System.out.printf("Forecast (Alpha 0.8): %.2f%n", reactiveForecast);
        // Notice how the spike of 85.0 heavily influenced the final forecast, 
        // even though the load dropped back down to ~45 afterwards.

        // Model 2: Low Alpha (0.2) - Smooths out noise
        ExponentialSmoothing smoothModel = new ExponentialSmoothing(0.2);
        double smoothForecast = smoothModel.forecastNext(cpuLoad);
        System.out.printf("Forecast (Alpha 0.2): %.2f%n", smoothForecast);
        // Notice how this forecast remains much closer to the historical average of ~45,
        // largely ignoring the temporary spike to 85.0.
    }
}
```

## 🔍 Key Takeaways
1. **The Recursive Nature**: Look at the loop in `forecastNext`. `smoothedValue` depends on the previous `smoothedValue`, which depends on the one before that, all the way back to the beginning of time. This is why it's called *exponential* smoothing—the mathematical weight of an observation from 10 days ago decays exponentially compared to yesterday's observation.
2. **The Alpha Trade-off**: If you run the code, you'll see how the outlier `85.0` affects the two models differently. Selecting the correct $\alpha$ is a hyperparameter tuning problem. In production, you would run a loop to test $\alpha$ values from 0.01 to 0.99 and pick the one that minimizes the Mean Squared Error (MSE) on a validation dataset.