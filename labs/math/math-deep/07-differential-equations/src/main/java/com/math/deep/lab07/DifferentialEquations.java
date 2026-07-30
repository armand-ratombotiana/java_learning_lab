package com.math.deep.lab07;

import java.util.function.BiFunction;
import java.util.ArrayList;
import java.util.List;

public class DifferentialEquations {

    public static List<double[]> euler(BiFunction<Double, double[], double[]> f,
                                        double t0, double[] y0, double h, int steps) {
        List<double[]> result = new ArrayList<>();
        double t = t0;
        double[] y = y0.clone();
        result.add(new double[]{t, y[0]});
        for (int i = 0; i < steps; i++) {
            double[] k = f.apply(t, y);
            for (int j = 0; j < y.length; j++) y[j] += h * k[j];
            t += h;
            result.add(new double[]{t, y[0]});
        }
        return result;
    }

    public static List<double[]> rk4(BiFunction<Double, double[], double[]> f,
                                      double t0, double[] y0, double h, int steps) {
        List<double[]> result = new ArrayList<>();
        double t = t0;
        double[] y = y0.clone();
        result.add(new double[]{t, y[0]});
        int n = y.length;
        for (int i = 0; i < steps; i++) {
            double[] k1 = f.apply(t, y);
            double[] y2 = new double[n];
            for (int j = 0; j < n; j++) y2[j] = y[j] + h * k1[j] / 2.0;
            double[] k2 = f.apply(t + h / 2.0, y2);
            double[] y3 = new double[n];
            for (int j = 0; j < n; j++) y3[j] = y[j] + h * k2[j] / 2.0;
            double[] k3 = f.apply(t + h / 2.0, y3);
            double[] y4 = new double[n];
            for (int j = 0; j < n; j++) y4[j] = y[j] + h * k3[j];
            double[] k4 = f.apply(t + h, y4);
            for (int j = 0; j < n; j++) {
                y[j] += h / 6.0 * (k1[j] + 2.0 * k2[j] + 2.0 * k3[j] + k4[j]);
            }
            t += h;
            result.add(new double[]{t, y[0]});
        }
        return result;
    }

    public static double[] heatEquationExplicit(double[] u0, double alpha, double dx, double dt, int steps) {
        double[] u = u0.clone();
        int n = u.length;
        double r = alpha * dt / (dx * dx);
        for (int s = 0; s < steps; s++) {
            double[] un = u.clone();
            for (int i = 1; i < n - 1; i++) {
                u[i] = un[i] + r * (un[i + 1] - 2.0 * un[i] + un[i - 1]);
            }
        }
        return u;
    }

    public static List<double[]> lorenzSystem(double sigma, double rho, double beta,
                                                double t0, double[] y0, double h, int steps) {
        BiFunction<Double, double[], double[]> f = (t, y) -> new double[]{
            sigma * (y[1] - y[0]),
            y[0] * (rho - y[2]) - y[1],
            y[0] * y[1] - beta * y[2]
        };
        return rk4(f, t0, y0, h, steps);
    }

    public static List<double[]> adaptiveRK45(BiFunction<Double, double[], double[]> f,
                                                double t0, double[] y0, double h0,
                                                double tEnd, double tol) {
        List<double[]> result = new ArrayList<>();
        double t = t0;
        double[] y = y0.clone();
        double h = h0;
        result.add(new double[]{t, y[0]});
        int n = y.length;
        while (t < tEnd) {
            if (h > tEnd - t) h = tEnd - t;
            double[] k1 = f.apply(t, y);
            double[] y2 = new double[n];
            for (int j = 0; j < n; j++) y2[j] = y[j] + h * k1[j] / 4.0;
            double[] k2 = f.apply(t + h / 4.0, y2);
            for (int j = 0; j < n; j++) y2[j] = y[j] + 3.0 * h * k1[j] / 32.0 + 9.0 * h * k2[j] / 32.0;
            double[] k3 = f.apply(t + 3.0 * h / 8.0, y2);
            for (int j = 0; j < n; j++) y2[j] = y[j] + 1932.0 * h * k1[j] / 2197.0 - 7200.0 * h * k2[j] / 2197.0 + 7296.0 * h * k3[j] / 2197.0;
            double[] k4 = f.apply(t + 12.0 * h / 13.0, y2);
            for (int j = 0; j < n; j++) y2[j] = y[j] + 439.0 * h * k1[j] / 216.0 - 8.0 * h * k2[j] + 3680.0 * h * k3[j] / 513.0 - 845.0 * h * k4[j] / 4104.0;
            double[] k5 = f.apply(t + h, y2);
            for (int j = 0; j < n; j++) y2[j] = y[j] - 8.0 * h * k1[j] / 27.0 + 2.0 * h * k2[j] - 3544.0 * h * k3[j] / 2565.0 + 1859.0 * h * k4[j] / 4104.0 - 11.0 * h * k5[j] / 40.0;
            double[] k6 = f.apply(t + h / 2.0, y2);
            double error = 0;
            for (int j = 0; j < n; j++) {
                double e = Math.abs(h * (k1[j] / 360.0 - 128.0 * k3[j] / 4275.0 - 2197.0 * k4[j] / 75240.0 + k5[j] / 50.0 + 2.0 * k6[j] / 55.0));
                error = Math.max(error, e);
            }
            if (error < tol) {
                t += h;
                for (int j = 0; j < n; j++) {
                    y[j] += h * (16.0 * k1[j] / 135.0 + 6656.0 * k3[j] / 12825.0 + 28561.0 * k4[j] / 56430.0 - 9.0 * k5[j] / 50.0 + 2.0 * k6[j] / 55.0);
                }
                result.add(new double[]{t, y[0]});
            }
            double S = 0.84 * Math.pow(tol * h / (2.0 * error), 0.25);
            h = h * Math.max(0.1, Math.min(4.0, S));
        }
        return result;
    }
}
