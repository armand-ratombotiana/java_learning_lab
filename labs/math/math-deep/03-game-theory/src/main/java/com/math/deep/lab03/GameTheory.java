package com.math.deep.lab03;

import java.util.ArrayList;
import java.util.List;

public class GameTheory {

    public static boolean isNashEquilibrium(double[][] payoff, int[] strategy) {
        int n = payoff.length;
        int m = payoff[0].length;
        int pi = strategy[0], pj = strategy[1];
        double val = payoff[pi][pj];
        for (int i = 0; i < n; i++) {
            if (payoff[i][pj] > val) return false;
        }
        return true;
    }

    public static double[] mixedNash2x2(double[][] payoff) {
        double a = payoff[0][0], b = payoff[0][1];
        double c = payoff[1][0], d = payoff[1][1];
        double p = (d - c) / (a - b - c + d);
        double q = (d - b) / (a - b - c + d);
        if (p < 0 || p > 1 || q < 0 || q > 1) return null;
        return new double[]{p, 1 - p, q, 1 - q};
    }

    public static double minimax(double[][] payoff) {
        int n = payoff.length;
        int m = payoff[0].length;
        double value = Double.POSITIVE_INFINITY;
        for (int j = 0; j < m; j++) {
            double maxVal = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < n; i++) {
                maxVal = Math.max(maxVal, payoff[i][j]);
            }
            value = Math.min(value, maxVal);
        }
        return value;
    }

    public static double maximin(double[][] payoff) {
        int n = payoff.length;
        int m = payoff[0].length;
        double value = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < n; i++) {
            double minVal = Double.POSITIVE_INFINITY;
            for (int j = 0; j < m; j++) {
                minVal = Math.min(minVal, payoff[i][j]);
            }
            value = Math.max(value, minVal);
        }
        return value;
    }

    public static String prisonerDilemmaOutcome(boolean cooperateA, boolean cooperateB) {
        if (cooperateA && cooperateB) return "Both serve 1 year";
        if (!cooperateA && !cooperateB) return "Both serve 2 years";
        if (cooperateA) return "A serves 3 years, B goes free";
        return "A goes free, B serves 3 years";
    }

    public static double[] fictitiousPlay(double[][] payoff, int iterations) {
        int n = payoff.length;
        int m = payoff[0].length;
        double[] pCount = new double[n];
        double[] qCount = new double[m];
        for (int t = 0; t < iterations; t++) {
            int bestI = 0;
            double bestVal = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < n; i++) {
                double val = 0;
                for (int j = 0; j < m; j++) val += payoff[i][j] * (t == 0 ? 1.0 / m : qCount[j] / t);
                if (val > bestVal) { bestVal = val; bestI = i; }
            }
            pCount[bestI]++;
            int bestJ = 0;
            bestVal = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < m; j++) {
                double val = 0;
                for (int i = 0; i < n; i++) val += payoff[i][j] * (t == 0 ? 1.0 / n : pCount[i] / t);
                if (val > bestVal) { bestVal = val; bestJ = j; }
            }
            qCount[bestJ]++;
        }
        double[] result = new double[n + m];
        for (int i = 0; i < n; i++) result[i] = pCount[i] / iterations;
        for (int j = 0; j < m; j++) result[n + j] = qCount[j] / iterations;
        return result;
    }
}
