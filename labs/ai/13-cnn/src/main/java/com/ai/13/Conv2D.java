package com.ai13;

public class Conv2D {
    private double[][][] filters;
    private double[] biases;
    private int kernelSize;
    private int stride;
    private int padding;
    private int inChannels;

    public Conv2D(int inChannels, int outChannels, int kernelSize, int stride, int padding) {
        this.inChannels = inChannels;
        this.kernelSize = kernelSize;
        this.stride = stride;
        this.padding = padding;
        this.filters = new double[outChannels][kernelSize * kernelSize * inChannels][1];
        this.biases = new double[outChannels];
        java.util.Random rng = new java.util.Random(42);
        for (int f = 0; f < outChannels; f++) {
            biases[f] = rng.nextDouble() * 0.1 - 0.05;
            for (int i = 0; i < kernelSize * kernelSize * inChannels; i++)
                filters[f][i][0] = rng.nextDouble() * 0.1 - 0.05;
        }
    }

    public double[][][] forward(double[][][] input) {
        int inH = input.length, inW = input[0].length;
        int outH = (inH + 2 * padding - kernelSize) / stride + 1;
        int outW = (inW + 2 * padding - kernelSize) / stride + 1;
        int outChannels = filters.length;
        double[][][] output = new double[outH][outW][outChannels];
        double[][][] paddedInput = padInput(input, padding);

        for (int c = 0; c < outChannels; c++) {
            for (int h = 0; h < outH; h++) {
                for (int w = 0; w < outW; w++) {
                    double sum = biases[c];
                    for (int kh = 0; kh < kernelSize; kh++) {
                        for (int kw = 0; kw < kernelSize; kw++) {
                            for (int ic = 0; ic < inChannels; ic++) {
                                int ph = h * stride + kh;
                                int pw = w * stride + kw;
                                int filterIdx = ic * kernelSize * kernelSize + kh * kernelSize + kw;
                                sum += paddedInput[ph][pw][ic] * filters[c][filterIdx][0];
                            }
                        }
                    }
                    output[h][w][c] = sum;
                }
            }
        }
        return output;
    }

    private double[][][] padInput(double[][][] input, int pad) {
        int h = input.length, w = input[0].length, c = input[0][0].length;
        double[][][] padded = new double[h + 2 * pad][w + 2 * pad][c];
        for (int i = 0; i < h; i++)
            for (int j = 0; j < w; j++)
                for (int k = 0; k < c; k++)
                    padded[i + pad][j + pad][k] = input[i][j][k];
        return padded;
    }

    public static void main(String[] args) {
        System.out.println("=== Conv2D Demo ===");
        int h = 5, w = 5, channels = 1;
        double[][][] input = new double[h][w][channels];
        for (int i = 0; i < h; i++)
            for (int j = 0; j < w; j++)
                input[i][j][0] = (i + j) % 3;

        Conv2D conv = new Conv2D(1, 1, 3, 1, 0);
        double[][][] output = conv.forward(input);
        System.out.println("Input: " + h + "x" + w + "x" + channels);
        System.out.println("Output: " + output.length + "x" + output[0].length + "x" + output[0][0].length);
        System.out.println("Output sample (center): " + output[1][1][0]);
    }
}
