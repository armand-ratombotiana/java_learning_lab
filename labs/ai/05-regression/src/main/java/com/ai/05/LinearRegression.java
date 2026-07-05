package com.ai05;

public class LinearRegression {
    private double slope;
    private double intercept;

    public void fit(double[] x, double[] y) {
        int n = x.length;
        double meanX = 0, meanY = 0;
        for (int i = 0; i < n; i++) {
            meanX += x[i];
            meanY += y[i];
        }
        meanX /= n;
        meanY /= n;
        double num = 0, den = 0;
        for (int i = 0; i < n; i++) {
            num += (x[i] - meanX) * (y[i] - meanY);
            den += (x[i] - meanX) * (x[i] - meanX);
        }
        slope = num / den;
        intercept = meanY - slope * meanX;
    }

    public double predict(double x) {
        return slope * x + intercept;
    }

    public double rSquared(double[] x, double[] y) {
        double meanY = 0;
        for (double v : y) meanY += v;
        meanY /= y.length;
        double ssRes = 0, ssTot = 0;
        for (int i = 0; i < y.length; i++) {
            ssRes += (y[i] - predict(x[i])) * (y[i] - predict(x[i]));
            ssTot += (y[i] - meanY) * (y[i] - meanY);
        }
        return 1 - ssRes / ssTot;
    }

    public double getSlope() { return slope; }
    public double getIntercept() { return intercept; }

    public static void main(String[] args) {
        System.out.println("=== Linear Regression (OLS) Demo ===");
        double[] x = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] y = {2.1, 4.2, 5.8, 8.1, 10.2, 12.3, 13.9, 16.0, 18.1, 20.2};
        LinearRegression lr = new LinearRegression();
        lr.fit(x, y);
        System.out.println("Slope: " + lr.getSlope());
        System.out.println("Intercept: " + lr.getIntercept());
        System.out.println("R^2: " + lr.rSquared(x, y));
        System.out.println("Predict x=11: " + lr.predict(11));
    }
}
