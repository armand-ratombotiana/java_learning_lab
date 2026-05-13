package timeseries;

import java.util.*;

public class SalesForecasting {
    public static void main(String[] args) {
        System.out.println("=== Sales Forecasting Project ===\n");
        
        double[] sales = generateSalesData();
        
        System.out.println("=== Data Overview ===");
        System.out.println("Period: 60 months");
        System.out.printf("Mean: $%.2fK%n", Arrays.stream(sales).sum() / sales.length);
        System.out.printf("Min: $%.2fK%n", Arrays.stream(sales).min().orElse(0));
        System.out.printf("Max: $%.2fK%n", Arrays.stream(sales).max().orElse(0));
        
        System.out.println("\n=== Moving Average Forecasts ===");
        
        double[] ma3 = TimeSeriesAnalysis.MovingAverage.simple(sales, 3);
        double[] ma6 = TimeSeriesAnalysis.MovingAverage.simple(sales, 6);
        double[] ma12 = TimeSeriesAnalysis.MovingAverage.simple(sales, 12);
        
        System.out.println("3-month MA (last 6):");
        for (int i = 54; i < 60; i++) {
            System.out.printf("  Month %d: Actual=%.1f, MA=%.1f%n", i + 1, sales[i], ma3[i]);
        }
        
        System.out.println("\n=== Exponential Smoothing ===");
        
        TimeSeriesAnalysis.ExponentialSmoothing ses = new TimeSeriesAnalysis.ExponentialSmoothing();
        double[] emaForecast = ses.simple(sales, 0.3);
        
        System.out.println("EMA forecast (alpha=0.3):");
        for (int i = 58; i < 63; i++) {
            if (i < 60) {
                System.out.printf("  Month %d: Actual=%.1f, Forecast=%.1f%n", i + 1, sales[i], emaForecast[i]);
            } else {
                System.out.printf("  Month %d: Forecast=%.1f%n", i + 1, emaForecast[i]);
            }
        }
        
        System.out.println("\n=== Holt's Linear Trend ===");
        
        double[] holtForecast = ses.holt(sales, 0.3, 0.1);
        System.out.println("Holt forecast (next 12 months):");
        for (int i = 60; i < 72; i++) {
            System.out.printf("  Month %d: %.1f%n", i + 1, holtForecast[i]);
        }
        
        System.out.println("\n=== Holt-Winters (Seasonal) ===");
        
        double[] hwForecast = ses.holtWinters(sales, 0.3, 0.1, 0.1, 12, true);
        System.out.println("Holt-Winters forecast (seasonal period=12):");
        for (int i = 60; i < 72; i++) {
            System.out.printf("  Month %d: %.1f%n", i + 1, hwForecast[i]);
        }
        
        System.out.println("\n=== Decomposition ===");
        
        TimeSeriesAnalysis.Decomposition.DecomposedSeries decomp = 
            TimeSeriesAnalysis.Decomposition.classical(sales, 12);
        
        System.out.println("Seasonal factors (monthly):");
        for (int i = 0; i < 12; i++) {
            System.out.printf("  Month %d: %.2f%n", i + 1, decomp.seasonal[i]);
        }
        
        System.out.println("\n=== Stationarity Test ===");
        
        double adfStat = TimeSeriesAnalysis.Stationarity.adfTest(sales);
        System.out.printf("ADF statistic: %.4f%n", adfStat);
        System.out.println("At 5% significance level, critical value ≈ -2.86");
        System.out.println("Series is " + (adfStat < -2.86 ? "STATIONARY" : "NON-STATIONARY"));
        
        System.out.println("\n=== Model Evaluation (Last 12 months) ===");
        
        double[] actual = Arrays.copyOfRange(sales, 48, 60);
        double[] predictedMA = new double[12];
        double[] predictedEMA = new double[12];
        double[] predictedHW = new double[12];
        
        for (int i = 0; i < 12; i++) {
            predictedMA[i] = ma3[47 + i];
            predictedEMA[i] = emaForecast[47 + i];
            predictedHW[i] = hwForecast[47 + i];
        }
        
        System.out.println("MA(3) Metrics:");
        System.out.printf("  MAE: %.2f%n", TimeSeriesAnalysis.ForecastMetrics.mae(actual, predictedMA));
        System.out.printf("  RMSE: %.2f%n", TimeSeriesAnalysis.ForecastMetrics.rmse(actual, predictedMA));
        
        System.out.println("\nExponential Smoothing Metrics:");
        System.out.printf("  MAE: %.2f%n", TimeSeriesAnalysis.ForecastMetrics.mae(actual, predictedEMA));
        System.out.printf("  RMSE: %.2f%n", TimeSeriesAnalysis.ForecastMetrics.rmse(actual, predictedEMA));
        
        System.out.println("\nHolt-Winters Metrics:");
        System.out.printf("  MAE: %.2f%n", TimeSeriesAnalysis.ForecastMetrics.mae(actual, predictedHW));
        System.out.printf("  RMSE: %.2f%n", TimeSeriesAnalysis.ForecastMetrics.rmse(actual, predictedHW));
        
        System.out.println("\n=== Summary ===");
        System.out.println("Best model: Holt-Winters (captures seasonality)");
        System.out.println("Forecast for next 12 months generated");
    }
    
    private static double[] generateSalesData() {
        Random rand = new Random(42);
        double[] sales = new double[60];
        
        for (int i = 0; i < 60; i++) {
            double trend = 100 + i * 2;
            double seasonal = 15 * Math.sin(2 * Math.PI * i / 12);
            double noise = rand.nextGaussian() * 5;
            sales[i] = trend + seasonal + noise;
        }
        
        return sales;
    }
}