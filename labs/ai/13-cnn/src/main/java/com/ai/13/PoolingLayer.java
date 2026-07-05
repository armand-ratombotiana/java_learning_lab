package com.ai13;

public class PoolingLayer {

    public enum PoolType { MAX, AVERAGE }

    public static double[][][] maxPool(double[][][] input, int poolSize, int stride) {
        int inH = input.length, inW = input[0].length, channels = input[0][0].length;
        int outH = (inH - poolSize) / stride + 1;
        int outW = (inW - poolSize) / stride + 1;
        double[][][] output = new double[outH][outW][channels];
        for (int c = 0; c < channels; c++) {
            for (int h = 0; h < outH; h++) {
                for (int w = 0; w < outW; w++) {
                    double max = -Double.MAX_VALUE;
                    for (int ph = 0; ph < poolSize; ph++)
                        for (int pw = 0; pw < poolSize; pw++)
                            max = Math.max(max, input[h * stride + ph][w * stride + pw][c]);
                    output[h][w][c] = max;
                }
            }
        }
        return output;
    }

    public static double[][][] avgPool(double[][][] input, int poolSize, int stride) {
        int inH = input.length, inW = input[0].length, channels = input[0][0].length;
        int outH = (inH - poolSize) / stride + 1;
        int outW = (inW - poolSize) / stride + 1;
        double[][][] output = new double[outH][outW][channels];
        for (int c = 0; c < channels; c++) {
            for (int h = 0; h < outH; h++) {
                for (int w = 0; w < outW; w++) {
                    double sum = 0;
                    int count = 0;
                    for (int ph = 0; ph < poolSize; ph++)
                        for (int pw = 0; pw < poolSize; pw++) {
                            sum += input[h * stride + ph][w * stride + pw][c];
                            count++;
                        }
                    output[h][w][c] = sum / count;
                }
            }
        }
        return output;
    }

    public static double[][][] globalAvgPool(double[][][] input) {
        int h = input.length, w = input[0].length, c = input[0][0].length;
        double[][][] output = new double[1][1][c];
        for (int k = 0; k < c; k++) {
            double sum = 0;
            for (int i = 0; i < h; i++)
                for (int j = 0; j < w; j++)
                    sum += input[i][j][k];
            output[0][0][k] = sum / (h * w);
        }
        return output;
    }

    public static void main(String[] args) {
        System.out.println("=== Pooling Demo ===");
        double[][][] input = new double[4][4][1];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                input[i][j][0] = i * 4 + j;
        double[][][] maxPooled = maxPool(input, 2, 2);
        double[][][] avgPooled = avgPool(input, 2, 2);
        System.out.println("Max pool (2x2, stride 2): " + maxPooled.length + "x" + maxPooled[0].length);
        System.out.println("Avg pool (2x2, stride 2): " + avgPooled.length + "x" + avgPooled[0].length);
        System.out.println("Max value: " + maxPooled[0][0][0]);
        System.out.println("Avg value: " + avgPooled[0][0][0]);
        double[][][] gap = globalAvgPool(input);
        System.out.println("Global avg pool: " + gap[0][0][0]);
    }
}
