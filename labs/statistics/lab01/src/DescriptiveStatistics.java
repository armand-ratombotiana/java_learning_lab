package com.statistics.lab01;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Computes descriptive statistics: mean, median, mode, variance,
 * standard deviation, quartiles, and interquartile range (IQR).
 * <p>
 * All methods operate on {@code double[]} arrays and use double precision.
 */
public final class DescriptiveStatistics {

    private DescriptiveStatistics() {
    }

    /**
     * Returns the arithmetic mean of the data.
     *
     * @param data sample values
     * @return arithmetic mean
     */
    public static double mean(double[] data) {
        double sum = 0;
        for (double v : data) {
            sum += v;
        }
        return sum / data.length;
    }

    /**
     * Returns the median (50th percentile) of the data.
     *
     * @param data sample values
     * @return median
     */
    public static double median(double[] data) {
        double[] sorted = data.clone();
        Arrays.sort(sorted);
        int n = sorted.length;
        if (n % 2 == 0) {
            return (sorted[n / 2 - 1] + sorted[n / 2]) / 2.0;
        } else {
            return sorted[n / 2];
        }
    }

    /**
     * Returns all modes (values that appear most frequently).
     *
     * @param data sample values
     * @return list of mode(s)
     */
    public static List<Double> mode(double[] data) {
        Map<Double, Integer> freq = new HashMap<>();
        for (double v : data) {
            freq.merge(v, 1, Integer::sum);
        }
        int maxCount = 0;
        for (int count : freq.values()) {
            if (count > maxCount) {
                maxCount = count;
            }
        }
        List<Double> modes = new ArrayList<>();
        for (Map.Entry<Double, Integer> e : freq.entrySet()) {
            if (e.getValue() == maxCount) {
                modes.add(e.getKey());
            }
        }
        return modes;
    }

    /**
     * Returns the population variance.
     *
     * @param data sample values
     * @return population variance (divides by N)
     */
    public static double populationVariance(double[] data) {
        double m = mean(data);
        double sumSq = 0;
        for (double v : data) {
            sumSq += (v - m) * (v - m);
        }
        return sumSq / data.length;
    }

    /**
     * Returns the sample variance (unbiased estimator).
     *
     * @param data sample values
     * @return sample variance (divides by n-1)
     */
    public static double sampleVariance(double[] data) {
        double m = mean(data);
        double sumSq = 0;
        for (double v : data) {
            sumSq += (v - m) * (v - m);
        }
        return sumSq / (data.length - 1);
    }

    /**
     * Returns the population standard deviation.
     *
     * @param data sample values
     * @return population standard deviation
     */
    public static double populationStdDev(double[] data) {
        return Math.sqrt(populationVariance(data));
    }

    /**
     * Returns the sample standard deviation.
     *
     * @param data sample values
     * @return sample standard deviation
     */
    public static double sampleStdDev(double[] data) {
        return Math.sqrt(sampleVariance(data));
    }

    /**
     * Returns the first quartile (Q1, 25th percentile).
     *
     * @param data sample values
     * @return Q1
     */
    public static double quartile1(double[] data) {
        double[] sorted = data.clone();
        Arrays.sort(sorted);
        int n = sorted.length;
        int half = n / 2;
        double[] lower = Arrays.copyOfRange(sorted, 0, half);
        return median(lower);
    }

    /**
     * Returns the third quartile (Q3, 75th percentile).
     *
     * @param data sample values
     * @return Q3
     */
    public static double quartile3(double[] data) {
        double[] sorted = data.clone();
        Arrays.sort(sorted);
        int n = sorted.length;
        int half = n / 2;
        int start = (n % 2 == 0) ? half : half + 1;
        double[] upper = Arrays.copyOfRange(sorted, start, n);
        return median(upper);
    }

    /**
     * Returns the interquartile range (IQR = Q3 - Q1).
     *
     * @param data sample values
     * @return IQR
     */
    public static double iqr(double[] data) {
        return quartile3(data) - quartile1(data);
    }

    /**
     * Runs test cases for all descriptive statistics methods.
     */
    public static void main(String[] args) {
        double[] data1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        System.out.println("=== Dataset 1: {1..10} ===");
        System.out.printf("Mean:          %.4f%n", mean(data1));
        System.out.printf("Median:        %.4f%n", median(data1));
        System.out.printf("Mode:          %s%n", mode(data1));
        System.out.printf("Pop Variance:  %.4f%n", populationVariance(data1));
        System.out.printf("Pop StdDev:    %.4f%n", populationStdDev(data1));
        System.out.printf("Sample Var:    %.4f%n", sampleVariance(data1));
        System.out.printf("Sample StdDev: %.4f%n", sampleStdDev(data1));
        System.out.printf("Q1:            %.4f%n", quartile1(data1));
        System.out.printf("Q3:            %.4f%n", quartile3(data1));
        System.out.printf("IQR:           %.4f%n", iqr(data1));

        double[] data2 = {1, 1, 2, 3, 4, 4, 4, 5};
        System.out.println("\n=== Dataset 2: {1,1,2,3,4,4,4,5} ===");
        System.out.printf("Mean:          %.4f%n", mean(data2));
        System.out.printf("Median:        %.4f%n", median(data2));
        System.out.printf("Mode:          %s%n", mode(data2));
        System.out.printf("Q1:            %.4f%n", quartile1(data2));
        System.out.printf("Q3:            %.4f%n", quartile3(data2));
        System.out.printf("IQR:           %.4f%n", iqr(data2));

        double[] data3 = {5, 5, 5, 5, 5};
        System.out.println("\n=== Dataset 3: constant {5,5,5,5,5} ===");
        System.out.printf("Mean:          %.4f%n", mean(data3));
        System.out.printf("Variance:      %.4f%n", populationVariance(data3));
        System.out.printf("StdDev:        %.4f%n", populationStdDev(data3));
    }
}
