package com.ai05;

public class PolynomialRegression {
    private double[] coefficients;
    private int degree;

    public PolynomialRegression(int degree) {
        this.degree = degree;
    }

    public void fit(double[] x, double[] y) {
        int n = x.length;
        int m = degree + 1;
        double[][] X = new double[n][m];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                X[i][j] = Math.pow(x[i], j);
        MultipleRegression mr = new MultipleRegression();
        mr.fit(X, y);
        coefficients = mr.getCoefficients();
    }

    public double predict(double x) {
        double sum = 0;
        for (int i = 0; i < coefficients.length; i++)
            sum += coefficients[i] * Math.pow(x, i);
        return sum;
    }

    public double[] getCoefficients() { return coefficients; }

    public static void main(String[] args) {
        System.out.println("=== Polynomial Regression Demo ===");
        double[] x = {0, 1, 2, 3, 4, 5};
        double[] y = {1, 1.8, 3.2, 6.0, 9.8, 15.5};
        PolynomialRegression pr = new PolynomialRegression(2);
        pr.fit(x, y);
        System.out.println("Coefficients (b0 + b1*x + b2*x^2): " + java.util.Arrays.toString(pr.getCoefficients()));
        for (double xi = 0; xi <= 5; xi++)
            System.out.println("x=" + xi + " pred=" + pr.predict(xi));
    }
}
