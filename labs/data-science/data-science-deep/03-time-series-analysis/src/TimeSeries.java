package com.datascience.deep.lab03;

import java.util.*;

public final class TimeSeries {

    public record Decomposition(double[] trend, double[] seasonal, double[] residual) {}

    public static Decomposition additiveDecomposition(double[] series, int period) {
        int n = series.length;
        double[] trend = new double[n];
        int half = period / 2;
        for (int t = 0; t < n; t++) {
            int start = Math.max(0, t - half);
            int end = Math.min(n - 1, t + half);
            double sum = 0;
            for (int i = start; i <= end; i++) sum += series[i];
            trend[t] = sum / (end - start + 1);
        }
        double[] detrended = new double[n];
        for (int t = 0; t < n; t++) detrended[t] = series[t] - trend[t];
        double[] seasonAvg = new double[period];
        int[] seasonCount = new int[period];
        for (int t = 0; t < n; t++) {
            int s = t % period;
            seasonAvg[s] += detrended[t];
            seasonCount[s]++;
        }
        for (int s = 0; s < period; s++) seasonAvg[s] /= seasonCount[s];
        double meanAdj = Arrays.stream(seasonAvg).average().orElseThrow();
        double[] seasonal = new double[n];
        for (int t = 0; t < n; t++) seasonal[t] = seasonAvg[t % period] - meanAdj;
        double[] residual = new double[n];
        for (int t = 0; t < n; t++) residual[t] = series[t] - trend[t] - seasonal[t];
        return new Decomposition(trend, seasonal, residual);
    }

    public record HoltWintersResult(double level, double trend, double[] seasonals, double sse) {}

    public static HoltWintersResult holtWinters(double[] series, int period, double alpha, double beta, double gamma) {
        int n = series.length;
        double level = series[0];
        double trend = (series[Math.min(period, n - 1)] - series[0]) / Math.min(period, n - 1);
        double[] seasonals = new double[period];
        for (int s = 0; s < period && s < n; s++) seasonals[s] = series[s] - level;
        double sse = 0;
        for (int t = 1; t < n; t++) {
            double lastLevel = level;
            double lastSeasonal = seasonals[t % period];
            level = alpha * (series[t] - lastSeasonal) + (1 - alpha) * (level + trend);
            trend = beta * (level - lastLevel) + (1 - beta) * trend;
            seasonals[t % period] = gamma * (series[t] - level) + (1 - gamma) * lastSeasonal;
            sse += Math.pow(series[t] - (lastLevel + trend + lastSeasonal), 2);
        }
        return new HoltWintersResult(level, trend, seasonals, sse);
    }

    public double holtWintersForecast(HoltWintersResult hw, int steps, int period) {
        return hw.level() + steps * hw.trend() + hw.seasonals()[(period - steps % period) % period];
    }

    public record ARIMAResult(double[] ar, double[] ma, double sigma2, double aic, double bic) {}

    public static ARIMAResult fitARIMA(double[] series, int p, int d, int q) {
        double[] diff = series;
        for (int i = 0; i < d; i++) {
            double[] tmp = new double[diff.length - 1];
            for (int t = 1; t < diff.length; t++) tmp[t - 1] = diff[t] - diff[t - 1];
            diff = tmp;
        }
        int n = diff.length;
        double mean = Arrays.stream(diff).average().orElseThrow();
        double[] centered = new double[n];
        for (int i = 0; i < n; i++) centered[i] = diff[i] - mean;
        int m = Math.max(p, q);
        if (n <= m || p == 0) {
            double sigma2 = Arrays.stream(centered).map(v -> v * v).average().orElseThrow();
            return new ARIMAResult(new double[0], new double[0], sigma2,
                n * Math.log(sigma2) + 2 * (p + q), n * Math.log(sigma2) + (p + q) * Math.log(n));
        }
        double[][] X = new double[n - m][p];
        double[] y = new double[n - m];
        for (int t = m; t < n; t++) {
            y[t - m] = centered[t];
            for (int j = 0; j < p; j++) X[t - m][j] = centered[t - 1 - j];
        }
        OLSResult ols = solveOLS(y, X);
        double[] residuals = new double[n];
        for (int t = Math.max(p, q); t < n; t++) {
            double pred = mean;
            for (int j = 0; j < p && t - 1 - j >= 0; j++) pred += ols.coefficients[j] * (diff[t - 1 - j] - mean);
            residuals[t] = diff[t] - pred;
        }
        double sigma2 = Arrays.stream(residuals).map(r -> r * r).sum() / (n - p);
        int k = p + q;
        return new ARIMAResult(ols.coefficients, new double[q], sigma2,
            n * Math.log(sigma2) + 2 * k, n * Math.log(sigma2) + k * Math.log(n));
    }

    public static double[] acf(double[] series, int maxLag) {
        int n = series.length;
        double mean = Arrays.stream(series).average().orElseThrow();
        double var = Arrays.stream(series).map(v -> Math.pow(v - mean, 2)).sum();
        double[] r = new double[maxLag + 1];
        for (int k = 0; k <= maxLag; k++) {
            double cov = 0;
            for (int t = k; t < n; t++) cov += (series[t] - mean) * (series[t - k] - mean);
            r[k] = cov / var;
        }
        return r;
    }

    public static double[] pacf(double[] series, int maxLag) {
        double[] pac = new double[maxLag + 1];
        pac[0] = 1.0;
        for (int k = 1; k <= maxLag; k++) {
            double[] acf = acf(series, k);
            double[][] R = new double[k][k];
            double[] r = new double[k];
            for (int i = 0; i < k; i++) {
                r[i] = acf[i + 1];
                for (int j = 0; j < k; j++) R[i][j] = acf[Math.abs(i - j)];
            }
            OLSResult ols = solveOLS(r, R);
            pac[k] = ols.coefficients[k - 1];
        }
        return pac;
    }

    public record ForecastMetrics(double mae, double rmse, double mase, double mape) {
        public static ForecastMetrics compute(double[] actual, double[] forecast) {
            int n = actual.length;
            double mae = 0, rmse = 0, mape = 0;
            for (int i = 0; i < n; i++) {
                mae += Math.abs(actual[i] - forecast[i]);
                rmse += Math.pow(actual[i] - forecast[i], 2);
                if (actual[i] != 0) mape += Math.abs((actual[i] - forecast[i]) / actual[i]);
            }
            mae /= n; rmse = Math.sqrt(rmse / n); mape /= n;
            double naiveMae = 0;
            for (int i = 1; i < actual.length; i++) naiveMae += Math.abs(actual[i] - actual[i - 1]);
            naiveMae /= (actual.length - 1);
            double mase = naiveMae > 0 ? mae / naiveMae : Double.NaN;
            return new ForecastMetrics(mae, rmse, mase, mape);
        }
    }

    // -- Internal OLS helpers --

    private record OLSResult(double[] coefficients, double[][] varcov) {}

    private static OLSResult solveOLS(double[] y, double[][] X) {
        int n = y.length, p = X[0].length;
        double[][] XtX = new double[p][p];
        double[] Xty = new double[p];
        for (int i = 0; i < p; i++) {
            for (int j = 0; j < p; j++) {
                for (int k = 0; k < n; k++) XtX[i][j] += X[k][i] * X[k][j];
            }
            for (int k = 0; k < n; k++) Xty[i] += X[k][i] * y[k];
        }
        double[][] inv = invert(XtX);
        double[] beta = new double[p];
        for (int i = 0; i < p; i++) {
            for (int j = 0; j < p; j++) beta[i] += inv[i][j] * Xty[j];
        }
        return new OLSResult(beta, inv);
    }

    private static double[][] invert(double[][] A) {
        int n = A.length;
        double[][] aug = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, aug[i], 0, n);
            aug[i][n + i] = 1.0;
        }
        for (int col = 0; col < n; col++) {
            int pivot = col;
            for (int row = col + 1; row < n; row++) {
                if (Math.abs(aug[row][col]) > Math.abs(aug[pivot][col])) pivot = row;
            }
            double[] tmp = aug[col]; aug[col] = aug[pivot]; aug[pivot] = tmp;
            double div = aug[col][col];
            for (int j = 0; j < 2 * n; j++) aug[col][j] /= div;
            for (int row = 0; row < n; row++) {
                if (row == col) continue;
                double mult = aug[row][col];
                for (int j = 0; j < 2 * n; j++) aug[row][j] -= mult * aug[col][j];
            }
        }
        double[][] inv = new double[n][n];
        for (int i = 0; i < n; i++) System.arraycopy(aug[i], n, inv[i], 0, n);
        return inv;
    }
}
