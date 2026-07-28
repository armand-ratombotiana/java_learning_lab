package com.datascience.deep.lab05;

import java.util.*;

public final class ExperimentalDesign {

    // -- Factorial Designs --

    public static double[][] fullFactorial2K(int k) {
        int runs = 1 << k;
        double[][] design = new double[runs][k];
        for (int i = 0; i < runs; i++) {
            for (int j = 0; j < k; j++) {
                design[i][j] = ((i >> (k - 1 - j)) & 1) == 0 ? -1.0 : 1.0;
            }
        }
        return design;
    }

    public static double[][] fractionalFactorial(int k, int p) {
        int runs = 1 << (k - p);
        double[][] design = new double[runs][k];
        for (int i = 0; i < runs; i++) {
            for (int j = 0; j < k - p; j++) {
                design[i][j] = ((i >> (k - p - 1 - j)) & 1) == 0 ? -1.0 : 1.0;
            }
        }
        // Use generators: last p factors = product of selected first (k-p) factors
        for (int j = k - p; j < k; j++) {
            for (int i = 0; i < runs; i++) {
                double val = 1.0;
                for (int g = 0; g < k - p; g++) {
                    if (((j - (k - p)) & (1 << g)) != 0) val *= design[i][g];
                }
                design[i][j] = val;
            }
        }
        return design;
    }

    // -- Block Randomization --

    public static int[] blockRandomize(int blocks, int treatments, Random rng) {
        int[] assignments = new int[blocks * treatments];
        for (int b = 0; b < blocks; b++) {
            int offset = b * treatments;
            for (int t = 0; t < treatments; t++) assignments[offset + t] = t;
            for (int t = treatments - 1; t > 0; t--) {
                int k = rng.nextInt(t + 1);
                int tmp = assignments[offset + t];
                assignments[offset + t] = assignments[offset + k];
                assignments[offset + k] = tmp;
            }
        }
        return assignments;
    }

    // -- Central Composite Design (Response Surface) --

    public static double[][] centralCompositeDesign(int factors) {
        int factorial = 1 << factors;
        int axial = 2 * factors;
        int center = 1;
        int runs = factorial + axial + center;
        double[][] design = new double[runs][factors];
        for (int i = 0; i < factorial; i++) {
            for (int j = 0; j < factors; j++) {
                design[i][j] = ((i >> (factors - 1 - j)) & 1) == 0 ? -1.0 : 1.0;
            }
        }
        double alpha = Math.pow(Math.pow(2, factors), 0.25);
        for (int j = 0; j < factors; j++) {
            design[factorial + 2 * j][j] = alpha;
            design[factorial + 2 * j + 1][j] = -alpha;
        }
        return design;
    }

    // -- Effect Estimation --

    public record Effect(String name, double estimate, double ss) {}

    public static List<Effect> estimateEffects(double[][] design, double[] responses) {
        int runs = responses.length, k = design[0].length;
        double yBar = Arrays.stream(responses).average().orElseThrow();
        double ssTotal = Arrays.stream(responses).map(v -> Math.pow(v - yBar, 2)).sum();
        List<Effect> effects = new ArrayList<>();
        for (int j = 0; j < k; j++) {
            double contrast = 0;
            for (int i = 0; i < runs; i++) contrast += design[i][j] * responses[i];
            double est = contrast / (runs / 2.0);
            double ss = contrast * contrast / runs;
            effects.add(new Effect("X" + (j + 1), est, ss));
        }
        for (int i = 0; i < k; i++) {
            for (int j = i + 1; j < k; j++) {
                double contrast = 0;
                for (int r = 0; r < runs; r++) contrast += design[r][i] * design[r][j] * responses[r];
                double est = contrast / (runs / 2.0);
                double ss = contrast * contrast / runs;
                effects.add(new Effect("X" + (i + 1) + "xX" + (j + 1), est, ss));
            }
        }
        return effects;
    }

    // -- One-Way ANOVA --

    public record ANOVA(double ssBetween, double ssWithin, double ssTotal,
                        int dfBetween, int dfWithin,
                        double msBetween, double msWithin,
                        double fStat, double pValue) {}

    public static ANOVA oneWayANOVA(double[][] groups) {
        int k = groups.length;
        double grandMean = 0;
        int n = 0;
        for (double[] g : groups) { for (double v : g) grandMean += v; n += g.length; }
        grandMean /= n;
        double ssBetween = 0, ssWithin = 0;
        for (int j = 0; j < k; j++) {
            double gm = Arrays.stream(groups[j]).average().orElseThrow();
            ssBetween += groups[j].length * Math.pow(gm - grandMean, 2);
            for (double v : groups[j]) ssWithin += Math.pow(v - gm, 2);
        }
        int dfB = k - 1, dfW = n - k;
        double msB = ssBetween / dfB, msW = ssWithin / dfW;
        double f = msB / msW;
        double p = 1.0 - fCdf(f, dfB, dfW);
        return new ANOVA(ssBetween, ssWithin, ssBetween + ssWithin, dfB, dfW, msB, msW, f, p);
    }

    // -- Utility: F-distribution CDF --

    private static double fCdf(double f, int df1, int df2) {
        double x = df1 * f / (df1 * f + df2);
        return incompleteBeta(df1 / 2.0, df2 / 2.0, x);
    }

    private static double incompleteBeta(double a, double b, double x) {
        if (x < 0 || x > 1) return 0;
        if (x == 0 || x == 1) return x;
        double bt = Math.exp(logGamma(a + b) - logGamma(a) - logGamma(b)
            + a * Math.log(x) + b * Math.log(1.0 - x));
        if (x < (a + 1) / (a + b + 2)) return bt * betaCF(a, b, x) / a;
        return 1.0 - bt * betaCF(b, a, 1.0 - x) / b;
    }

    private static double betaCF(double a, double b, double x) {
        int maxIter = 200;
        double eps = 3e-12;
        double qab = a + b, qap = a + 1.0, qam = a - 1.0;
        double c = 1.0, d = 1.0 - qab * x / qap;
        if (Math.abs(d) < 1e-30) d = 1e-30;
        d = 1.0 / d;
        double h = d;
        for (int m = 1; m <= maxIter; m++) {
            int m2 = 2 * m;
            double aa = m * (b - m) * x / ((qam + m2) * (a + m2));
            d = 1.0 + aa * d;
            if (Math.abs(d) < 1e-30) d = 1e-30;
            c = 1.0 + aa / c;
            if (Math.abs(c) < 1e-30) c = 1e-30;
            d = 1.0 / d;
            h *= d * c;
            aa = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2));
            d = 1.0 + aa * d;
            if (Math.abs(d) < 1e-30) d = 1e-30;
            c = 1.0 + aa / c;
            if (Math.abs(c) < 1e-30) c = 1e-30;
            d = 1.0 / d;
            double del = d * c;
            h *= del;
            if (Math.abs(del - 1.0) < eps) break;
        }
        return h;
    }

    private static double logGamma(double x) {
        double[] cof = {76.18009172947146, -86.50532032941677, 24.01409824083091,
                        -1.231739572450155, 0.1208650973866179e-2, -0.5395239384953e-5};
        double y = x, tmp = x + 5.5;
        tmp -= (x + 0.5) * Math.log(tmp);
        double ser = 1.000000000190015;
        for (int j = 0; j < 6; j++) ser += cof[j] / ++y;
        return -tmp + Math.log(2.5066282746310005 * ser / x);
    }
}
