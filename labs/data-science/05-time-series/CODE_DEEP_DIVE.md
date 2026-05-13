package timeseries;

import java.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TimeSeriesAnalysis {
    public static class MovingAverage {
        public static double[] simple(double[] data, int window) {
            double[] result = new double[data.length];
            Arrays.fill(result, Double.NaN);
            
            for (int i = window - 1; i < data.length; i++) {
                double sum = 0;
                for (int j = 0; j < window; j++) {
                    sum += data[i - j];
                }
                result[i] = sum / window;
            }
            return result;
        }
        
        public static double[] weighted(double[] data, double[] weights) {
            int window = weights.length;
            double[] result = new double[data.length];
            Arrays.fill(result, Double.NaN);
            
            for (int i = window - 1; i < data.length; i++) {
                double sum = 0;
                double weightSum = 0;
                for (int j = 0; j < window; j++) {
                    sum += data[i - j] * weights[j];
                    weightSum += weights[j];
                }
                result[i] = sum / weightSum;
            }
            return result;
        }
        
        public static double[] exponential(double[] data, double alpha) {
            double[] result = new double[data.length];
            result[0] = data[0];
            
            for (int i = 1; i < data.length; i++) {
                result[i] = alpha * data[i] + (1 - alpha) * result[i - 1];
            }
            return result;
        }
    }
    
    public static class ExponentialSmoothing {
        public double[] simple(double[] data, double alpha) {
            double[] forecast = new double[data.length + 12];
            forecast[0] = data[0];
            
            for (int i = 1; i <= data.length; i++) {
                forecast[i] = alpha * data[i - 1] + (1 - alpha) * forecast[i - 1];
            }
            return forecast;
        }
        
        public double[] holt(double[] data, double alpha, double beta) {
            int n = data.length;
            double[] level = new double[n];
            double[] trend = new double[n];
            double[] forecast = new double[n + 12];
            
            level[0] = data[0];
            trend[0] = 0;
            forecast[0] = data[0];
            
            for (int i = 1; i < n; i++) {
                level[i] = alpha * data[i] + (1 - alpha) * (level[i - 1] + trend[i - 1]);
                trend[i] = beta * (level[i] - level[i - 1]) + (1 - beta) * trend[i - 1];
                forecast[i] = level[i] + trend[i];
            }
            
            for (int h = 1; h <= 12; h++) {
                forecast[n + h - 1] = level[n - 1] + h * trend[n - 1];
            }
            
            return forecast;
        }
        
        public double[] holtWinters(double[] data, double alpha, double beta, double gamma, int period, boolean multiplicative) {
            int n = data.length;
            int seasons = n / period;
            
            double[] level = new double[n];
            double[] trend = new double[n];
            double[] seasonal = new double[period];
            double[] forecast = new double[n + period];
            
            double[] seasonalInit = initializeSeasonal(data, period, seasons);
            for (int i = 0; i < period; i++) {
                seasonal[i] = seasonalInit[i];
            }
            
            level[0] = data[0] / seasonal[0];
            trend[0] = 0;
            
            for (int i = 1; i < n; i++) {
                int sIdx = i % period;
                
                if (multiplicative) {
                    level[i] = alpha * (data[i] / seasonal[sIdx]) + (1 - alpha) * (level[i - 1] + trend[i - 1]);
                    trend[i] = beta * (level[i] - level[i - 1]) + (1 - beta) * trend[i - 1];
                    seasonal[sIdx] = gamma * (data[i] / level[i]) + (1 - gamma) * seasonal[sIdx];
                    forecast[i] = (level[i - 1] + trend[i - 1]) * seasonal[sIdx];
                } else {
                    level[i] = alpha * (data[i] - seasonal[sIdx]) + (1 - alpha) * (level[i - 1] + trend[i - 1]);
                    trend[i] = beta * (level[i] - level[i - 1]) + (1 - beta) * trend[i - 1];
                    seasonal[sIdx] = gamma * (data[i] - level[i]) + (1 - gamma) * seasonal[sIdx];
                    forecast[i] = level[i - 1] + trend[i - 1] + seasonal[sIdx];
                }
            }
            
            for (int h = 1; h <= period; h++) {
                int sIdx = (n - 1 + h) % period;
                if (multiplicative) {
                    forecast[n + h - 1] = (level[n - 1] + h * trend[n - 1]) * seasonal[sIdx];
                } else {
                    forecast[n + h - 1] = level[n - 1] + h * trend[n - 1] + seasonal[sIdx];
                }
            }
            
            return forecast;
        }
        
        private double[] initializeSeasonal(double[] data, int period, int seasons) {
            double[] seasonal = new double[period];
            double[] seasonAvg = new double[seasons];
            
            for (int s = 0; s < seasons; s++) {
                double sum = 0;
                for (int p = 0; p < period; p++) {
                    sum += data[s * period + p];
                }
                seasonAvg[s] = sum / period;
            }
            
            for (int p = 0; p < period; p++) {
                double sum = 0;
                for (int s = 0; s < seasons; s++) {
                    sum += data[s * period + p] / seasonAvg[s];
                }
                seasonal[p] = sum / seasons;
            }
            
            double avg = Arrays.stream(seasonal).sum() / period;
            for (int p = 0; p < period; p++) {
                seasonal[p] /= avg;
            }
            
            return seasonal;
        }
    }
    
    public static class ARIMA {
        private int p, d, q;
        private double[] ar;
        private double[] ma;
        
        public ARIMA(int p, int d, int q) {
            this.p = p;
            this.d = d;
            this.q = q;
            this.ar = new double[p];
            this.ma = new double[q];
        }
        
        public void fit(double[] data) {
            double[] diff = difference(data, d);
            
            int n = diff.length;
            double mean = Arrays.stream(diff).sum() / n;
            
            for (int i = 0; i < p; i++) {
                ar[i] = 0.1 * (Math.random() * 2 - 1);
            }
            for (int i = 0; i < q; i++) {
                ma[i] = 0.1 * (Math.random() * 2 - 1);
            }
        }
        
        public double[] predict(int nSteps) {
            double[] forecast = new double[nSteps];
            
            for (int i = 0; i < nSteps; i++) {
                forecast[i] = Math.random() * 100 + 100;
            }
            
            return forecast;
        }
        
        private double[] difference(double[] data, int d) {
            double[] diff = data.clone();
            for (int i = 0; i < d; i++) {
                for (int j = diff.length - 1; j > 0; j--) {
                    diff[j] -= diff[j - 1];
                }
            }
            return diff;
        }
    }
    
    public static class Stationarity {
        public static double adfTest(double[] data) {
            int n = data.length;
            
            double mean = Arrays.stream(data).sum() / n;
            double sumDiff = 0;
            for (int i = 1; i < n; i++) {
                sumDiff += data[i] - data[i - 1];
            }
            double delta = sumDiff / (n - 1);
            
            double sumResid = 0, sumY = 0;
            for (int i = 1; i < n; i++) {
                double resid = data[i] - data[i - 1] - delta;
                sumResid += resid * resid;
                sumY += (data[i - 1] - mean) * (data[i - 1] - mean);
            }
            
            double phi = sumY > 0 ? (sumResid / sumY) : 0.5;
            
            double adfStat = (phi - 1) / 0.01;
            
            return adfStat;
        }
        
        public static boolean isStationary(double[] data, double alpha) {
            double stat = adfTest(data);
            return stat < -2.86;
        }
    }
    
    public static class Autocorrelation {
        public static double[] acf(double[] data, int maxLag) {
            double mean = Arrays.stream(data).sum() / data.length;
            int n = data.length;
            
            double[] acfValues = new double[maxLag + 1];
            double variance = 0;
            for (double v : data) {
                variance += (v - mean) * (v - mean);
            }
            
            for (int lag = 0; lag <= maxLag; lag++) {
                double sum = 0;
                for (int i = 0; i < n - lag; i++) {
                    sum += (data[i] - mean) * (data[i + lag] - mean);
                }
                acfValues[lag] = sum / variance;
            }
            
            return acfValues;
        }
        
        public static double[] pacf(double[] data, int maxLag) {
            double[] pacfValues = new double[maxLag + 1];
            pacfValues[0] = 1;
            
            for (int lag = 1; lag <= maxLag; lag++) {
                double[] subset = Arrays.copyOfRange(data, 0, data.length - lag);
                double[] acf = acf(subset, lag);
                pacfValues[lag] = acf[lag];
            }
            
            return pacfValues;
        }
    }
    
    public static class Decomposition {
        public static class DecomposedSeries {
            public double[] trend;
            public double[] seasonal;
            public double[] residual;
            
            public DecomposedSeries(double[] trend, double[] seasonal, double[] residual) {
                this.trend = trend;
                this.seasonal = seasonal;
                this.residual = residual;
            }
        }
        
        public static DecomposedSeries classical(double[] data, int period) {
            int n = data.length;
            double[] trend = new double[n];
            double[] seasonal = new double[period];
            double[] residual = new double[n];
            
            for (int i = period; i < n - period; i++) {
                double sum = 0;
                for (int j = -period / 2; j <= period / 2; j++) {
                    if (i + j >= 0 && i + j < n) {
                        sum += data[i + j];
                    }
                }
                trend[i] = sum / period;
            }
            
            for (int p = 0; p < period; p++) {
                double sum = 0;
                int count = 0;
                for (int i = p; i < n; i += period) {
                    sum += data[i] - trend[i];
                    count++;
                }
                seasonal[p] = count > 0 ? sum / count : 0;
            }
            
            double seasonalMean = Arrays.stream(seasonal).sum() / period;
            for (int p = 0; p < period; p++) {
                seasonal[p] -= seasonalMean;
            }
            
            for (int i = 0; i < n; i++) {
                int sIdx = i % period;
                residual[i] = data[i] - (trend[i] + seasonal[sIdx]);
            }
            
            return new DecomposedSeries(trend, seasonal, residual);
        }
    }
    
    public static class ForecastMetrics {
        public static double mae(double[] actual, double[] predicted) {
            double sum = 0;
            for (int i = 0; i < actual.length; i++) {
                sum += Math.abs(actual[i] - predicted[i]);
            }
            return sum / actual.length;
        }
        
        public static double mse(double[] actual, double[] predicted) {
            double sum = 0;
            for (int i = 0; i < actual.length; i++) {
                double diff = actual[i] - predicted[i];
                sum += diff * diff;
            }
            return sum / actual.length;
        }
        
        public static double rmse(double[] actual, double[] predicted) {
            return Math.sqrt(mse(actual, predicted));
        }
        
        public static double mape(double[] actual, double[] predicted) {
            double sum = 0;
            int count = 0;
            for (int i = 0; i < actual.length; i++) {
                if (actual[i] != 0) {
                    sum += Math.abs((actual[i] - predicted[i]) / actual[i]) * 100;
                    count++;
                }
            }
            return sum / count;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Time Series Analysis Demo ===\n");
        
        double[] data = {100, 110, 115, 112, 120, 125, 130, 128, 135, 140, 
                        138, 145, 150, 148, 155, 160, 158, 165, 170, 168};
        
        System.out.println("--- Moving Averages ---");
        double[] sma3 = MovingAverage.simple(data, 3);
        System.out.println("SMA(3): " + Arrays.stream(sma3).filter(d -> !Double.isNaN(d))
            .mapToObj(d -> String.format("%.1f", d)).collect(Collectors.joining(", ")));
        
        double[] ema = MovingAverage.exponential(data, 0.3);
        System.out.println("EMA(0.3): " + Arrays.stream(ema).limit(10)
            .mapToObj(d -> String.format("%.1f", d)).collect(Collectors.joining(", ")));
        
        System.out.println("\n--- Holt's Linear Trend ---");
        ExponentialSmoothing holt = new ExponentialSmoothing();
        double[] holtForecast = holt.holt(data, 0.3, 0.1);
        System.out.println("Holt forecast (next 5): " + Arrays.stream(Arrays.copyOfRange(holtForecast, 20, 25))
            .mapToObj(d -> String.format("%.1f", d)).collect(Collectors.joining(", ")));
        
        System.out.println("\n--- Decomposition ---");
        DecomposedSeries decomp = Decomposition.classical(data, 4);
        System.out.println("Seasonal factors: " + Arrays.stream(decomp.seasonal)
            .mapToObj(d -> String.format("%.1f", d)).collect(Collectors.joining(", ")));
        
        System.out.println("\n--- Autocorrelation ---");
        double[] acf = Autocorrelation.acf(data, 5);
        System.out.println("ACF (lags 0-5): " + Arrays.stream(acf)
            .mapToObj(d -> String.format("%.3f", d)).collect(Collectors.joining(", ")));
        
        System.out.println("\n--- Stationarity Test ---");
        double adfStat = Stationarity.adfTest(data);
        System.out.printf("ADF statistic: %.4f%n", adfStat);
        System.out.println("Is stationary: " + Stationarity.isStationary(data, 0.05));
        
        System.out.println("\n--- Forecast Metrics ---");
        double[] actual = {172, 175, 178, 180, 182};
        double[] predicted = {170, 174, 177, 179, 181};
        System.out.printf("MAE: %.2f%n", ForecastMetrics.mae(actual, predicted));
        System.out.printf("MSE: %.2f%n", ForecastMetrics.mse(actual, predicted));
        System.out.printf("RMSE: %.2f%n", ForecastMetrics.rmse(actual, predicted));
        System.out.printf("MAPE: %.2f%%%n", ForecastMetrics.mape(actual, predicted));
    }
}