package com.ai13;

public class FeatureMap {

    public static double[][] applyFilter(double[][] input, double[][] kernel) {
        int inH = input.length, inW = input[0].length;
        int kH = kernel.length, kW = kernel[0].length;
        int outH = inH - kH + 1, outW = inW - kW + 1;
        double[][] output = new double[outH][outW];
        for (int i = 0; i < outH; i++)
            for (int j = 0; j < outW; j++)
                for (int ki = 0; ki < kH; ki++)
                    for (int kj = 0; kj < kW; kj++)
                        output[i][j] += input[i + ki][j + kj] * kernel[ki][kj];
        return output;
    }

    public static double[][] applyReLU(double[][] featureMap) {
        int h = featureMap.length, w = featureMap[0].length;
        double[][] result = new double[h][w];
        for (int i = 0; i < h; i++)
            for (int j = 0; j < w; j++)
                result[i][j] = Math.max(0, featureMap[i][j]);
        return result;
    }

    public static double[][][] concatenate(double[][][] fm1, double[][][] fm2) {
        int h = fm1.length, w = fm1[0].length;
        int c1 = fm1[0][0].length, c2 = fm2[0][0].length;
        double[][][] result = new double[h][w][c1 + c2];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                System.arraycopy(fm1[i][j], 0, result[i][j], 0, c1);
                System.arraycopy(fm2[i][j], 0, result[i][j], c1, c2);
            }
        }
        return result;
    }

    public static void visualizeFeatureMap(double[][] fm) {
        for (double[] row : fm) {
            for (double v : row)
                System.out.printf("%6.1f ", v);
            System.out.println();
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Feature Map Demo ===");
        double[][] input = {
            {1, 2, 3, 0},
            {0, 1, 2, 3},
            {3, 0, 1, 2},
            {2, 3, 0, 1}
        };
        double[][] edgeDetect = {
            {-1, -1, -1},
            {-1, 8, -1},
            {-1, -1, -1}
        };
        double[][] fm = applyFilter(input, edgeDetect);
        System.out.println("Edge detection filter applied:");
        visualizeFeatureMap(fm);
        double[][] activated = applyReLU(fm);
        System.out.println("After ReLU:");
        visualizeFeatureMap(activated);
    }
}
