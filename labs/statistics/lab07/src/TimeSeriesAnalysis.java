package com.statistics.lab07;

import java.util.Arrays;

/**
 * Performs time series analysis: simple moving average (SMA),
 * exponential moving average (EMA), autocorrelation, and
 * additive trend-seasonal decomposition.
 */
public final class TimeSeriesAnalysis {

    private TimeSeriesAnalysis() {
    }

    /**
     * Computes the arithmetic mean.
     */
    public static double mean(double[] data) {
        double sum = 0;
        for (double v : data) sum += v;
        return sum / data.length;
    }

    /**
     * Computes the simple moving average (SMA).
     *
     * @param data   input time series
     * @param window window size (must be >= 1 and <= data.length)
     * @return array of length data.length - window + 1
     */
    public static double[] sma(double[] data, int window) {
        int n = data.length - window + 1;
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            double sum = 0;
            for (int j = 0; j < window; j++) {
                sum += data[i + j];
            }
            result[i] = sum / window;
        }
        return result;
    }

    /**
     * Computes the exponential moving average (EMA).
     *
     * @param data   input time series
     * @param alpha  smoothing factor (0 < alpha <= 1)
     * @return array of same length as input
     */
    public static double[] ema(double[] data, double alpha) {
        double[] result = new double[data.length];
        result[0] = data[0];
        for (int i = 1; i < data.length; i++) {
            result[i] = alpha * data[i] + (1 - alpha) * result[i - 1];
        }
        return result;
    }

    /**
     * Computes autocorrelation at a given lag.
     *
     * @param data input time series
     * @param lag  lag value (must be < data.length)
     * @return autocorrelation coefficient at the specified lag
     */
    public static double autocorrelation(double[] data, int lag) {
        int n = data.length - lag;
        double[] x = new double[n];
        double[] y = new double[n];
        double mx = 0, my = 0;
        for (int i = 0; i < n; i++) {
            x[i] = data[i + lag]; // lagged series
            y[i] = data[i];       // original series (aligned)
            mx += x[i];
            my += y[i];
        }
        mx /= n;
        my /= n;
        double sxy = 0, sxx = 0, syy = 0;
        for (int i = 0; i < n; i++) {
            double dx = x[i] - mx;
            double dy = y[i] - my;
            sxy += dx * dy;
            sxx += dx * dx;
            syy += dy * dy;
        }
        return sxy / Math.sqrt(sxx * syy);
    }

    /**
     * Performs additive trend-seasonal decomposition.
     *
     * @param data       input time series
     * @param period     seasonal period length (e.g., 4 for quarterly, 12 for monthly)
     * @return 2D array where rows are: [trend, seasonal, residual]
     *         trend length = data.length - period + 1 (from centered SMA),
     *         seasonal/residual same length as trend, aligned to center
     */
    public static double[][] decomposeAdditive(double[] data, int period) {
        // Step 1: Estimate trend using centered SMA with window = period
        double[] trendRaw = sma(data, period);
        // Center the trend (if period is even, use centered moving average)
        double[] trend;
        if (period % 2 == 0) {
            // Centered: average of two consecutive SMAs
            trend = new double[trendRaw.length - 1];
            for (int i = 0; i < trend.length; i++) {
                trend[i] = (trendRaw[i] + trendRaw[i + 1]) / 2.0;
            }
        } else {
            trend = trendRaw;
        }

        // Align: detrended = data - trend (center-matched)
        int offset = (data.length - trend.length) / 2;
        double[] detrended = new double[trend.length];
        for (int i = 0; i < trend.length; i++) {
            detrended[i] = data[offset + i] - trend[i];
        }

        // Step 2: Estimate seasonal component
        double[] seasonal = new double[trend.length];
        double[] periodAvgs = new double[period];
        int[] periodCounts = new int[period];
        for (int i = 0; i < detrended.length; i++) {
            int idx = (offset + i) % period;
            periodAvgs[idx] += detrended[i];
            periodCounts[idx]++;
        }
        for (int i = 0; i < period; i++) {
            periodAvgs[i] /= periodCounts[i];
        }
        // Center seasonal factors to sum to zero
        double seasMean = mean(periodAvgs);
        for (int i = 0; i < period; i++) {
            periodAvgs[i] -= seasMean;
        }
        for (int i = 0; i < seasonal.length; i++) {
            seasonal[i] = periodAvgs[(offset + i) % period];
        }

        // Step 3: Residual = detrended - seasonal
        double[] residual = new double[trend.length];
        for (int i = 0; i < residual.length; i++) {
            residual[i] = detrended[i] - seasonal[i];
        }

        return new double[][]{trend, seasonal, residual};
    }

    /**
     * Runs test cases for time series analysis.
     */
    public static void main(String[] args) {
        System.out.println("=== Simple Moving Average ===");
        double[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] s = sma(data, 3);
        System.out.println("Data:     " + Arrays.toString(data));
        System.out.println("SMA(3):   " + Arrays.toString(s));

        System.out.println("\n=== Exponential Moving Average ===");
        double[] e = ema(data, 0.5);
        System.out.print("EMA(0.5): ");
        for (double v : e) System.out.printf("%.4f ", v);
        System.out.println();

        System.out.println("\n=== Autocorrelation ===");
        double[] linear = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        for (int lag = 1; lag <= 3; lag++) {
            System.out.printf("Lag %d: %.6f%n", lag, autocorrelation(linear, lag));
        }

        double[] noisy = {1, 3, 2, 5, 4, 7, 6, 9, 8, 10};
        System.out.printf("Lag 1 (noisy): %.6f%n", autocorrelation(noisy, 1));

        System.out.println("\n=== Trend Decomposition ===");
        // Create data with trend + seasonality
        int period = 4;
        double[] seasonalData = new double[20];
        double[] baseTrend = {10, 12, 14, 16, 18, 20, 22, 24, 26, 28,
                               30, 32, 34, 36, 38, 40, 42, 44, 46, 48};
        double[] seasPattern = {0, 0.5, -0.5, 0};
        for (int i = 0; i < 20; i++) {
            seasonalData[i] = baseTrend[i] + seasPattern[i % 4];
        }

        System.out.println("Seasonal data (trend + quarterly pattern):");
        System.out.println(Arrays.toString(seasonalData));

        double[][] decomp = decomposeAdditive(seasonalData, period);
        double[] trend = decomp[0];
        double[] seasonal_comp = decomp[1];
        double[] residual = decomp[2];

        System.out.print("Trend (" + trend.length + "): ");
        for (double v : trend) System.out.printf("%.2f ", v);
        System.out.println();

        System.out.print("Seasonal (" + seasonal_comp.length + "): ");
        for (double v : seasonal_comp) System.out.printf("%.2f ", v);
        System.out.println();

        System.out.print("Residual (" + residual.length + "): ");
        for (double v : residual) System.out.printf("%.2f ", v);
        System.out.println();
    }
}
