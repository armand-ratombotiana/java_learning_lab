package com.learning.ml;

import java.util.*;
import java.util.stream.*;

public class MachineLearningLab {

    public static void main(String[] args) {
        System.out.println("=== Machine Learning Lab ===\n");

        System.out.println("1. Linear Regression Demo:");
        double[] x = {1, 2, 3, 4, 5};
        double[] y = {2, 4, 5, 4, 5};
        LinearRegression lr = new LinearRegression();
        lr.train(x, y);
        System.out.println("   Slope: " + String.format("%.2f", lr.getSlope()));
        System.out.println("   Intercept: " + String.format("%.2f", lr.getIntercept()));
        System.out.println("   Prediction for x=6: " + String.format("%.2f", lr.predict(6)));

        System.out.println("\n2. ML Java Libraries:");
        System.out.println("   - Deeplearning4j: Deep learning framework");
        System.out.println("   - Tribuo: Oracle's ML library");
        System.out.println("   - Smile: Statistical ML library");
        System.out.println("   - Weka: Classic ML toolkit");

        System.out.println("\n=== Machine Learning Lab Complete ===");
    }

    static class LinearRegression {
        private double slope, intercept;
        void train(double[] x, double[] y) {
            int n = x.length;
            double sumX = IntStream.range(0, n).mapToDouble(i -> x[i]).sum();
            double sumY = IntStream.range(0, n).mapToDouble(i -> y[i]).sum();
            double sumXY = IntStream.range(0, n).mapToDouble(i -> x[i] * y[i]).sum();
            double sumX2 = IntStream.range(0, n).mapToDouble(i -> x[i] * x[i]).sum();
            slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
            intercept = (sumY - slope * sumX) / n;
        }
        double predict(double x) { return slope * x + intercept; }
        double getSlope() { return slope; }
        double getIntercept() { return intercept; }
    }
}