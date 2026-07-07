package com.algo.lab32;

import java.util.Arrays;

public class FastSubsetConvolution {

    public static double[] zetaTransform(double[] f, int n) {
        double[] F = f.clone();
        for (int i = 0; i < n; i++) {
            for (int mask = 0; mask < (1 << n); mask++) {
                if ((mask & (1 << i)) != 0) {
                    F[mask] += F[mask ^ (1 << i)];
                }
            }
        }
        return F;
    }

    public static double[] moebiusTransform(double[] F, int n) {
        double[] f = F.clone();
        for (int i = 0; i < n; i++) {
            for (int mask = 0; mask < (1 << n); mask++) {
                if ((mask & (1 << i)) != 0) {
                    f[mask] -= f[mask ^ (1 << i)];
                }
            }
        }
        return f;
    }

    public static double[] convolve(double[] a, double[] b, int n) {
        int size = 1 << n;
        double[][] fa = new double[n + 1][size];
        double[][] fb = new double[n + 1][size];
        double[][] fc = new double[n + 1][size];

        for (int mask = 0; mask < size; mask++) {
            int bits = Integer.bitCount(mask);
            fa[bits][mask] = a[mask];
            fb[bits][mask] = b[mask];
        }

        for (int k = 0; k <= n; k++) {
            fa[k] = zetaTransform(fa[k], n);
            fb[k] = zetaTransform(fb[k], n);
        }

        for (int mask = 0; mask < size; mask++) {
            for (int k = 0; k <= n; k++) {
                for (int j = 0; j <= n - k; j++) {
                    fc[k + j][mask] += fa[k][mask] * fb[j][mask];
                }
            }
        }

        for (int k = 0; k <= n; k++) {
            fc[k] = moebiusTransform(fc[k], n);
        }

        double[] result = new double[size];
        for (int mask = 0; mask < size; mask++) {
            result[mask] = fc[Integer.bitCount(mask)][mask];
        }
        return result;
    }
}
