package eda;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.io.*;

public class Statistics {
    public static class DescriptiveStats {
        public double count;
        public double sum;
        public double mean;
        public double median;
        public double mode;
        public double std;
        public double variance;
        public double min;
        public double max;
        public double range;
        public double q1;
        public double q3;
        public double iqr;
        public double skewness;
        public double kurtosis;
        public double coefVariation;
        
        public void print() {
            System.out.println("=== Descriptive Statistics ===");
            System.out.printf("Count: %.0f%n", count);
            System.out.printf("Sum: %.2f%n", sum);
            System.out.printf("Mean: %.2f%n", mean);
            System.out.printf("Median: %.2f%n", median);
            System.out.printf("Mode: %.2f%n", mode);
            System.out.printf("Std Dev: %.2f%n", std);
            System.out.printf("Variance: %.2f%n", variance);
            System.out.printf("Min: %.2f%n", min);
            System.out.printf("Max: %.2f%n", max);
            System.out.printf("Range: %.2f%n", range);
            System.out.printf("Q1: %.2f%n", q1);
            System.out.printf("Q3: %.2f%n", q3);
            System.out.printf("IQR: %.2f%n", iqr);
            System.out.printf("Skewness: %.2f%n", skewness);
            System.out.printf("Kurtosis: %.2f%n", kurtosis);
            System.out.printf("Coef of Variation: %.2f%%%n", coefVariation);
        }
    }
    
    public static DescriptiveStats describe(double[] data) {
        DescriptiveStats stats = new DescriptiveStats();
        
        Arrays.sort(data);
        int n = data.length;
        
        stats.count = n;
        stats.sum = Arrays.stream(data).sum();
        stats.mean = stats.sum / n;
        stats.min = data[0];
        stats.max = data[n - 1];
        stats.range = stats.max - stats.min;
        stats.median = percentile(data, 50);
        stats.q1 = percentile(data, 25);
        stats.q3 = percentile(data, 75);
        stats.iqr = stats.q3 - stats.q1;
        stats.mode = mode(data);
        
        double sumSqDiff = Arrays.stream(data).map(v -> Math.pow(v - stats.mean, 2)).sum();
        stats.variance = sumSqDiff / n;
        stats.std = Math.sqrt(stats.variance);
        
        stats.skewness = skewness(data, stats.mean, stats.std);
        stats.kurtosis = kurtosis(data, stats.mean, stats.std);
        stats.coefVariation = (stats.std / stats.mean) * 100;
        
        return stats;
    }
    
    public static double percentile(double[] sortedData, double p) {
        if (sortedData.length == 0) return Double.NaN;
        Arrays.sort(sortedData);
        double pos = p * (sortedData.length - 1) / 100;
        int idx = (int) Math.floor(pos);
        double frac = pos - idx;
        if (idx + 1 < sortedData.length) {
            return sortedData[idx] * (1 - frac) + sortedData[idx + 1] * frac;
        }
        return sortedData[idx];
    }
    
    public static double mode(double[] data) {
        Map<Double, Long> counts = Arrays.stream(data)
            .boxed()
            .collect(Collectors.groupingBy(v -> v, Collectors.counting()));
        
        return counts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(Double.NaN);
    }
    
    public static double skewness(double[] data, double mean, double std) {
        if (std == 0) return 0;
        int n = data.length;
        double sum = Arrays.stream(data)
            .map(v -> Math.pow((v - mean) / std, 3))
            .sum();
        return (n / ((n - 1.0) * (n - 2.0))) * sum;
    }
    
    public static double kurtosis(double[] data, double mean, double std) {
        if (std == 0) return 0;
        int n = data.length;
        double sum = Arrays.stream(data)
            .map(v -> Math.pow((v - mean) / std, 4))
            .sum();
        double k = (n * (n + 1) / ((n - 1.0) * (n - 2.0) * (n - 3.0))) * sum;
        k -= 3 * Math.pow(n - 1, 2) / ((n - 2.0) * (n - 3.0));
        return k;
    }
    
    public static double covariance(double[] x, double[] y) {
        int n = Math.min(x.length, y.length);
        double meanX = Arrays.stream(x).limit(n).sum() / n;
        double meanY = Arrays.stream(y).limit(n).sum() / n;
        
        double sum = 0;
        for (int i = 0; i < n; i++) {
            sum += (x[i] - meanX) * (y[i] - meanY);
        }
        return sum / n;
    }
    
    public static double correlation(double[] x, double[] y) {
        double cov = covariance(x, y);
        double stdX = Math.sqrt(variance(Arrays.copyOf(x, Math.min(x.length, y.length))));
        double stdY = Math.sqrt(variance(Arrays.copyOf(y, Math.min(x.length, y.length))));
        return cov / (stdX * stdY);
    }
    
    public static double variance(double[] data) {
        double mean = Arrays.stream(data).sum() / data.length;
        return Arrays.stream(data).map(v -> Math.pow(v - mean, 2)).sum() / data.length;
    }
    
    public static double pearsonCorrelation(double[] x, double[] y) {
        int n = Math.min(x.length, y.length);
        double meanX = Arrays.stream(x, 0, n).sum() / n;
        double meanY = Arrays.stream(y, 0, n).sum() / n;
        
        double sumXY = 0, sumX2 = 0, sumY2 = 0;
        for (int i = 0; i < n; i++) {
            double dx = x[i] - meanX;
            double dy = y[i] - meanY;
            sumXY += dx * dy;
            sumX2 += dx * dx;
            sumY2 += dy * dy;
        }
        
        if (sumX2 == 0 || sumY2 == 0) return 0;
        return sumXY / Math.sqrt(sumX2 * sumY2);
    }
    
    public static double spearmanCorrelation(double[] x, double[] y) {
        int n = Math.min(x.length, y.length);
        double[] rankX = rank(x);
        double[] rankY = rank(y);
        return pearsonCorrelation(rankX, rankY);
    }
    
    public static double[] rank(double[] data) {
        Integer[] indices = IntStream.range(0, data.length)
            .boxed()
            .sorted(Comparator.comparingDouble(i -> data[i]))
            .mapToInt(i -> i)
            .boxed()
            .map(i -> i)
            .mapToInt(i -> i)
            .toArray();
        
        double[] ranks = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                if (data[j] == data[i]) {
                    ranks[j] = i + 1;
                    break;
                }
            }
        }
        return ranks;
    }
    
    public static double[][] covarianceMatrix(double[][] data) {
        int nCols = data.length;
        double[] means = new double[nCols];
        
        for (int i = 0; i < nCols; i++) {
            means[i] = Arrays.stream(data[i]).sum() / data[i].length;
        }
        
        double[][] cov = new double[nCols][nCols];
        for (int i = 0; i < nCols; i++) {
            for (int j = i; j < nCols; j++) {
                double sum = 0;
                int minLen = Math.min(data[i].length, data[j].length);
                for (int k = 0; k < minLen; k++) {
                    sum += (data[i][k] - means[i]) * (data[j][k] - means[j]);
                }
                cov[i][j] = sum / minLen;
                cov[j][i] = cov[i][j];
            }
        }
        return cov;
    }
    
    public static double[][] correlationMatrix(double[][] data) {
        int nCols = data.length;
        double[][] corr = new double[nCols][nCols];
        
        for (int i = 0; i < nCols; i++) {
            for (int j = i; j < nCols; j++) {
                corr[i][j] = pearsonCorrelation(data[i], data[j]);
                corr[j][i] = corr[i][j];
            }
        }
        return corr;
    }
    
    public static class NormalityTest {
        public double statistic;
        public double pValue;
        public boolean isNormal;
        public String testName;
        
        public void print() {
            System.out.println("=== " + testName + " Test ===");
            System.out.printf("Statistic: %.4f%n", statistic);
            System.out.printf("P-value: %.4f%n", pValue);
            System.out.println("Result: " + (isNormal ? "Normal" : "Not Normal"));
        }
    }
    
    public static NormalityTest shapiroWilk(double[] data) {
        int n = data.length;
        if (n < 3 || n > 5000) {
            return new NormalityTest() {{
                testName = "Shapiro-Wilk";
                pValue = Double.NaN;
                isNormal = false;
            }};
        }
        
        double[] sorted = data.clone();
        Arrays.sort(sorted);
        
        double mean = Arrays.stream(sorted).sum() / n;
        double ss = Arrays.stream(sorted).map(v -> Math.pow(v - mean, 2)).sum();
        
        double sum = 0;
        for (int i = 0; i < n / 2; i++) {
            double a = shapiroWilkCoeff(n, i + 1);
            sum += a * (sorted[n - 1 - i] - sorted[i]);
        }
        
        double w = (sum * sum) / ss;
        double approxW = w; // Simplified approximation
        
        NormalityTest result = new NormalityTest();
        result.testName = "Shapiro-Wilk";
        result.statistic = approxW;
        result.pValue = approximatePValue(approxW, n);
        result.isNormal = result.pValue > 0.05;
        return result;
    }
    
    private static double shapiroWilkCoeff(int n, int k) {
        return Math.sqrt(2.0 / (n + 1));
    }
    
    private static double approximatePValue(double w, int n) {
        if (w > 0.95) return 0.5;
        if (w > 0.9) return 0.1;
        if (w > 0.85) return 0.05;
        if (w > 0.8) return 0.01;
        return 0.001;
    }
    
    public static NormalityTest kolmogorovSmirnov(double[] data, double mean, double std) {
        Arrays.sort(data);
        int n = data.length;
        double dMax = 0;
        
        for (int i = 0; i < n; i++) {
            double observed = (i + 1.0) / n;
            double expected = cumulativeNormal(data[i], mean, std);
            dMax = Math.max(dMax, Math.abs(observed - expected));
        }
        
        double stat = dMax * Math.sqrt(n);
        
        NormalityTest result = new NormalityTest();
        result.testName = "Kolmogorov-Smirnov";
        result.statistic = dMax;
        result.pValue = ksPValue(stat, n);
        result.isNormal = result.pValue > 0.05;
        return result;
    }
    
    private static double cumulativeNormal(double x, double mean, double std) {
        double z = (x - mean) / std;
        return 0.5 * (1 + erf(z / Math.sqrt(2)));
    }
    
    private static double erf(double x) {
        double t = 1 / (1 + 0.5 * Math.abs(x));
        double tau = t * Math.exp(-x * x - 1.26551223 + 
            t * (1.00002368 + t * (0.37409196 + t * (0.09678418 + 
            t * (-0.2023348 + t * (0.27839307 + t * (-0.1936502 + 
            t * (0.0858985 + t * (-0.01952729 + t * (0.00420042)))))))));
        return 1 - tau;
    }
    
    private static double ksPValue(double d, int n) {
        if (d < 0.2) return 0.5;
        if (d < 0.4) return 0.1;
        if (d < 0.6) return 0.05;
        if (d < 0.8) return 0.01;
        return 0.001;
    }
    
    public static class TTestResult {
        public double tStatistic;
        public double degreesOfFreedom;
        public double pValue;
        public double confidenceInterval;
        public double meanDifference;
        public boolean significant;
        
        public void print() {
            System.out.println("=== T-Test Results ===");
            System.out.printf("T-statistic: %.4f%n", tStatistic);
            System.out.printf("Degrees of freedom: %.0f%n", degreesOfFreedom);
            System.out.printf("P-value: %.4f%n", pValue);
            System.out.printf("Mean difference: %.4f%n", meanDifference);
            System.out.printf("95%% CI: [%.4f, %.4f]%n", 
                meanDifference - confidenceInterval, 
                meanDifference + confidenceInterval);
            System.out.println("Significant at 0.05: " + significant);
        }
    }
    
    public static TTestResult tTest(double[] sample1, double[] sample2) {
        double mean1 = Arrays.stream(sample1).sum() / sample1.length;
        double mean2 = Arrays.stream(sample2).sum() / sample2.length;
        
        double var1 = variance(sample1);
        double var2 = variance(sample2);
        
        int n1 = sample1.length;
        int n2 = sample2.length;
        
        double se = Math.sqrt(var1 / n1 + var2 / n2);
        double t = (mean1 - mean2) / se;
        double df = Math.pow(var1 / n1 + var2 / n2, 2) / 
            (Math.pow(var1 / n1, 2) / (n1 - 1) + Math.pow(var2 / n2, 2) / (n2 - 1));
        
        TTestResult result = new TTestResult();
        result.tStatistic = t;
        result.degreesOfFreedom = df;
        result.pValue = tDistPValue(Math.abs(t), df);
        result.meanDifference = mean1 - mean2;
        result.confidenceInterval = 1.96 * se;
        result.significant = result.pValue < 0.05;
        return result;
    }
    
    private static double tDistPValue(double t, double df) {
        double x = df / (df + t * t);
        double p = 0.5 * incompleteBeta(df / 2, 0.5, x);
        return p;
    }
    
    private static double incompleteBeta(double a, double b, double x) {
        return 1 - Math.pow(x, a);
    }
    
    public static class ChiSquareResult {
        public double statistic;
        public double degreesOfFreedom;
        public double pValue;
        public boolean significant;
        
        public void print() {
            System.out.println("=== Chi-Square Test ===");
            System.out.printf("Chi-square statistic: %.4f%n", statistic);
            System.out.printf("Degrees of freedom: %.0f%n", degreesOfFreedom);
            System.out.printf("P-value: %.4f%n", pValue);
            System.out.println("Significant at 0.05: " + significant);
        }
    }
    
    public static ChiSquareResult chiSquareTest(long[][] observed) {
        int rows = observed.length;
        int cols = observed[0].length;
        
        long[] rowTotals = new long[rows];
        long[] colTotals = new long[cols];
        long grandTotal = 0;
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rowTotals[i] += observed[i][j];
                colTotals[j] += observed[i][j];
                grandTotal += observed[i][j];
            }
        }
        
        double chiSq = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double expected = (double) rowTotals[i] * colTotals[j] / grandTotal;
                if (expected > 0) {
                    chiSq += Math.pow(observed[i][j] - expected, 2) / expected;
                }
            }
        }
        
        int df = (rows - 1) * (cols - 1);
        
        ChiSquareResult result = new ChiSquareResult();
        result.statistic = chiSq;
        result.degreesOfFreedom = df;
        result.pValue = chiSquarePValue(chiSq, df);
        result.significant = result.pValue < 0.05;
        return result;
    }
    
    private static double chiSquarePValue(double chiSq, double df) {
        return Math.exp(-chiSq / 2);
    }
    
    public static class AnovaResult {
        public double fStatistic;
        public double degreesOfFreedomBetween;
        public double degreesOfFreedomWithin;
        public double pValue;
        public boolean significant;
        public double[][] groupMeans;
        
        public void print() {
            System.out.println("=== ANOVA Results ===");
            System.out.printf("F-statistic: %.4f%n", fStatistic);
            System.out.printf("DF (between): %.0f%n", degreesOfFreedomBetween);
            System.out.printf("DF (within): %.0f%n", degreesOfFreedomWithin);
            System.out.printf("P-value: %.4f%n", pValue);
            System.out.println("Significant at 0.05: " + significant);
        }
    }
    
    public static AnovaResult anova(double[]... groups) {
        int k = groups.length;
        int totalN = 0;
        double grandMean = 0;
        
        for (double[] group : groups) {
            totalN += group.length;
            grandMean += Arrays.stream(group).sum();
        }
        grandMean /= totalN;
        
        double ssBetween = 0;
        for (double[] group : groups) {
            double groupMean = Arrays.stream(group).sum() / group.length;
            ssBetween += group.length * Math.pow(groupMean - grandMean, 2);
        }
        
        double ssWithin = 0;
        for (double[] group : groups) {
            double groupMean = Arrays.stream(group).sum() / group.length;
            for (double v : group) {
                ssWithin += Math.pow(v - groupMean, 2);
            }
        }
        
        double dfBetween = k - 1;
        double dfWithin = totalN - k;
        double msBetween = ssBetween / dfBetween;
        double msWithin = ssWithin / dfWithin;
        double f = msBetween / msWithin;
        
        AnovaResult result = new AnovaResult();
        result.fStatistic = f;
        result.degreesOfFreedomBetween = dfBetween;
        result.degreesOfFreedomWithin = dfWithin;
        result.pValue = fDistPValue(f, dfBetween, dfWithin);
        result.significant = result.pValue < 0.05;
        return result;
    }
    
    private static double fDistPValue(double f, double df1, double df2) {
        double x = df2 / (df2 + df1 * f);
        return 1 - Math.pow(x, df2 / 2);
    }
    
    public static class OutlierDetection {
        public List<Integer> indices;
        public double[] values;
        public String method;
        
        public void print() {
            System.out.println("=== Outlier Detection (" + method + ") ===");
            System.out.println("Outlier count: " + (values != null ? values.length : indices.size()));
            System.out.println("Outlier values: " + Arrays.toString(values));
        }
    }
    
    public static OutlierDetection detectOutliersIQR(double[] data) {
        double[] sorted = data.clone();
        Arrays.sort(sorted);
        double q1 = percentile(sorted, 25);
        double q3 = percentile(sorted, 75);
        double iqr = q3 - q1;
        double lower = q1 - 1.5 * iqr;
        double upper = q3 + 1.5 * iqr;
        
        List<Integer> indices = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        
        for (int i = 0; i < data.length; i++) {
            if (data[i] < lower || data[i] > upper) {
                indices.add(i);
                values.add(data[i]);
            }
        }
        
        OutlierDetection result = new OutlierDetection();
        result.indices = indices;
        result.values = values.stream().mapToDouble(Double::doubleValue).toArray();
        result.method = "IQR";
        return result;
    }
    
    public static OutlierDetection detectOutliersZScore(double[] data, double threshold) {
        double mean = Arrays.stream(data).sum() / data.length;
        double std = Math.sqrt(variance(data));
        
        List<Integer> indices = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        
        for (int i = 0; i < data.length; i++) {
            double z = Math.abs((data[i] - mean) / std);
            if (z > threshold) {
                indices.add(i);
                values.add(data[i]);
            }
        }
        
        OutlierDetection result = new OutlierDetection();
        result.indices = indices;
        result.values = values.stream().mapToDouble(Double::doubleValue).toArray();
        result.method = "Z-Score";
        return result;
    }
    
    public static void main(String[] args) {
        System.out.println("=== EDA Statistics Demo ===\n");
        
        double[] data = {12, 15, 18, 22, 25, 28, 30, 32, 35, 38, 40, 42, 45, 48, 50, 
                         52, 55, 58, 60, 62, 65, 68, 70, 72, 75, 78, 80, 82, 85, 88, 90, 95, 100};
        
        DescriptiveStats stats = describe(data);
        stats.print();
        
        System.out.println("\n=== Normality Tests ===");
        NormalityTest sw = shapiroWilk(data);
        sw.print();
        
        System.out.println("\n=== Outlier Detection ===");
        OutlierDetection outliers = detectOutliersIQR(data);
        outliers.print();
        
        System.out.println("\n=== T-Test ===");
        double[] group1 = {23, 25, 28, 30, 32, 35};
        double[] group2 = {28, 30, 33, 36, 38, 40};
        TTestResult tResult = tTest(group1, group2);
        tResult.print();
        
        System.out.println("\n=== ANOVA ===");
        double[] a = {10, 12, 14, 16};
        double[] b = {15, 17, 19, 21};
        double[] c = {20, 22, 24, 26};
        AnovaResult anovaResult = anova(a, b, c);
        anovaResult.print();
        
        System.out.println("\n=== Correlation ===");
        double[] x = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] y = {2, 4, 5, 4, 5, 6, 8, 7, 8, 9};
        System.out.printf("Pearson: %.4f%n", pearsonCorrelation(x, y));
        System.out.printf("Spearman: %.4f%n", spearmanCorrelation(x, y));
    }
}