package com.statistics.lab09;

import java.util.Arrays;

/**
 * Implements non-parametric statistical tests: Mann-Whitney U,
 * Wilcoxon signed-rank, Kruskal-Wallis H, and Friedman test.
 * <p>
 * All tests use rank-based methods and provide test statistics.
 */
public final class NonParametricTests {

    private NonParametricTests() {
    }

    // -----------------------------------------------------------------
    // Helper: ranking
    // -----------------------------------------------------------------

    /**
     * Assigns ranks to the data array (1-based, averaging ties).
     */
    public static double[] rank(double[] data) {
        int n = data.length;
        double[] sorted = data.clone();
        Arrays.sort(sorted);
        double[] ranks = new double[n];
        for (int i = 0; i < n; i++) {
            double v = data[i];
            int first = 0, last = 0;
            for (int j = 0; j < n; j++) {
                if (sorted[j] == v) {
                    first = j;
                    break;
                }
            }
            for (int j = n - 1; j >= 0; j--) {
                if (sorted[j] == v) {
                    last = j;
                    break;
                }
            }
            ranks[i] = 1.0 + (first + last) / 2.0;
        }
        return ranks;
    }

    /**
     * Merges two arrays.
     */
    public static double[] concat(double[] a, double[] b) {
        double[] result = new double[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    // -----------------------------------------------------------------
    // Mann-Whitney U Test
    // -----------------------------------------------------------------

    /**
     * Result of a Mann-Whitney U test.
     */
    public record MannWhitneyResult(double u1, double u2, double n1, double n2) {
        public double smallerU() {
            return Math.min(u1, u2);
        }
    }

    /**
     * Performs the Mann-Whitney U test on two independent samples.
     *
     * @param group1 first group values
     * @param group2 second group values
     * @return U statistics for both groups
     */
    public static MannWhitneyResult mannWhitneyU(double[] group1, double[] group2) {
        double[] combined = concat(group1, group2);
        double[] ranks = rank(combined);
        double r1 = 0;
        for (int i = 0; i < group1.length; i++) {
            r1 += ranks[i];
        }
        double n1 = group1.length;
        double n2 = group2.length;
        double u1 = r1 - n1 * (n1 + 1) / 2.0;
        double u2 = n1 * n2 - u1;
        return new MannWhitneyResult(u1, u2, n1, n2);
    }

    // -----------------------------------------------------------------
    // Wilcoxon Signed-Rank Test
    // -----------------------------------------------------------------

    /**
     * Result of a Wilcoxon signed-rank test.
     */
    public record WilcoxonResult(double wPlus, double wMinus, int n) {
    }

    /**
     * Performs the Wilcoxon signed-rank test on paired samples.
     *
     * @param before first measurement
     * @param after  second measurement (paired)
     * @return Wilcoxon signed-rank result
     */
    public static WilcoxonResult wilcoxonSignedRank(double[] before, double[] after) {
        int n = before.length;
        // Compute non-zero absolute differences
        int count = 0;
        for (int i = 0; i < n; i++) {
            if (before[i] != after[i]) count++;
        }
        double[] diffs = new double[count];
        int[] signs = new int[count];
        int idx = 0;
        for (int i = 0; i < n; i++) {
            double d = after[i] - before[i];
            if (d != 0) {
                diffs[idx] = Math.abs(d);
                signs[idx] = d > 0 ? 1 : -1;
                idx++;
            }
        }
        double[] ranks = rank(diffs);
        double wPlus = 0, wMinus = 0;
        for (int i = 0; i < count; i++) {
            if (signs[i] > 0) {
                wPlus += ranks[i];
            } else {
                wMinus += ranks[i];
            }
        }
        return new WilcoxonResult(wPlus, wMinus, count);
    }

    // -----------------------------------------------------------------
    // Kruskal-Wallis H Test
    // -----------------------------------------------------------------

    /**
     * Performs the Kruskal-Wallis test for k independent samples.
     *
     * @param groups array of groups (each is a double[])
     * @return H statistic
     */
    public static double kruskalWallis(double[][] groups) {
        int k = groups.length;
        int totalN = 0;
        for (double[] g : groups) totalN += g.length;
        double[] all = new double[totalN];
        int[] groupIdx = new int[totalN];
        int pos = 0;
        for (int g = 0; g < k; g++) {
            for (double v : groups[g]) {
                all[pos] = v;
                groupIdx[pos] = g;
                pos++;
            }
        }
        double[] ranks = rank(all);
        double h = 0;
        pos = 0;
        for (int g = 0; g < k; g++) {
            double sumR = 0;
            int nG = groups[g].length;
            for (int i = 0; i < nG; i++) {
                sumR += ranks[pos++];
            }
            h += sumR * sumR / nG;
        }
        h = 12.0 / (totalN * (totalN + 1)) * h - 3.0 * (totalN + 1);
        return h;
    }

    // -----------------------------------------------------------------
    // Friedman Test
    // -----------------------------------------------------------------

    /**
     * Performs the Friedman test for a randomized block design.
     *
     * @param data rows = blocks, columns = treatments
     * @return Friedman Q statistic
     */
    public static double friedman(double[][] data) {
        int b = data.length;    // blocks
        int k = data[0].length; // treatments
        // Rank within each block
        double[][] blockRanks = new double[b][k];
        for (int i = 0; i < b; i++) {
            double[] ranks = rank(data[i]);
            System.arraycopy(ranks, 0, blockRanks[i], 0, k);
        }
        // Sum ranks per treatment
        double[] rj = new double[k];
        for (int j = 0; j < k; j++) {
            double sum = 0;
            for (int i = 0; i < b; i++) {
                sum += blockRanks[i][j];
            }
            rj[j] = sum;
        }
        // Compute Q
        double sumRj2 = 0;
        for (double v : rj) sumRj2 += v * v;
        double q = 12.0 / (b * k * (k + 1)) * sumRj2 - 3.0 * b * (k + 1);
        return q;
    }

    /**
     * Runs test cases for non-parametric tests.
     */
    public static void main(String[] args) {
        System.out.println("=== Mann-Whitney U Test ===");
        double[] g1 = {2, 4, 6, 8};
        double[] g2 = {1, 3, 5, 7};
        MannWhitneyResult mw = mannWhitneyU(g1, g2);
        System.out.printf("Group1: %s, Group2: %s%n",
            Arrays.toString(g1), Arrays.toString(g2));
        System.out.printf("U1=%.1f, U2=%.1f, smaller U=%.1f%n",
            mw.u1(), mw.u2(), mw.smallerU());

        double[] g3 = {10, 12, 14, 16};
        MannWhitneyResult mw2 = mannWhitneyU(g1, g3);
        System.out.printf("Group1: %s, Group3: %s%n",
            Arrays.toString(g1), Arrays.toString(g3));
        System.out.printf("U1=%.1f, U2=%.1f, smaller U=%.1f%n",
            mw2.u1(), mw2.u2(), mw2.smallerU());

        System.out.println("\n=== Wilcoxon Signed-Rank Test ===");
        double[] before = {5, 6, 7, 8, 9};
        double[] after  = {4, 3, 5, 6, 7};
        WilcoxonResult wx = wilcoxonSignedRank(before, after);
        System.out.printf("Before: %s%n", Arrays.toString(before));
        System.out.printf("After:  %s%n", Arrays.toString(after));
        System.out.printf("W+ = %.1f, W- = %.1f, n = %d%n",
            wx.wPlus(), wx.wMinus(), wx.n());

        double[] before2 = {10, 20, 30, 40, 50};
        double[] after2  = {11, 21, 31, 41, 51};
        WilcoxonResult wx2 = wilcoxonSignedRank(before2, after2);
        System.out.printf("Before: %s, After: %s%n",
            Arrays.toString(before2), Arrays.toString(after2));
        System.out.printf("W+ = %.1f, W- = %.1f, n = %d%n",
            wx2.wPlus(), wx2.wMinus(), wx2.n());

        System.out.println("\n=== Kruskal-Wallis H Test ===");
        double[][] kwGroups = {
            {2, 4, 6},
            {8, 10, 12},
            {14, 16, 18}
        };
        double h = kruskalWallis(kwGroups);
        System.out.printf("Groups: A=%s B=%s C=%s%n",
            Arrays.toString(kwGroups[0]),
            Arrays.toString(kwGroups[1]),
            Arrays.toString(kwGroups[2]));
        System.out.printf("H statistic = %.6f%n", h);

        // Groups with similar medians
        double[][] kwSimilar = {
            {5, 6, 7},
            {6, 7, 8},
            {5, 6, 9}
        };
        double h2 = kruskalWallis(kwSimilar);
        System.out.printf("Similar groups H = %.6f%n", h2);

        System.out.println("\n=== Friedman Test ===");
        double[][] friedmanData = {
            {10, 12, 15},
            {11, 13, 16},
            {9, 11, 14},
            {10, 12, 15}
        };
        double q = friedman(friedmanData);
        System.out.println("Block data (rows=subjects, cols=treatments):");
        for (double[] row : friedmanData) {
            System.out.println(Arrays.toString(row));
        }
        System.out.printf("Friedman Q statistic = %.6f%n", q);
    }
}
