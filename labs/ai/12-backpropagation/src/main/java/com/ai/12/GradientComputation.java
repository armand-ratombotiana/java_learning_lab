package com.ai12;

public class GradientComputation {

    public static double[] computeGradients(java.util.function.Function<double[], Double> lossFn, double[] params, double h) {
        double[] gradients = new double[params.length];
        double baseLoss = lossFn.apply(params);
        for (int i = 0; i < params.length; i++) {
            double[] paramsPlus = params.clone();
            paramsPlus[i] += h;
            double lossPlus = lossFn.apply(paramsPlus);
            gradients[i] = (lossPlus - baseLoss) / h;
        }
        return gradients;
    }

    public static double[] computeGradientsCentral(java.util.function.Function<double[], Double> lossFn, double[] params, double h) {
        double[] gradients = new double[params.length];
        for (int i = 0; i < params.length; i++) {
            double[] paramsPlus = params.clone();
            double[] paramsMinus = params.clone();
            paramsPlus[i] += h;
            paramsMinus[i] -= h;
            gradients[i] = (lossFn.apply(paramsPlus) - lossFn.apply(paramsMinus)) / (2 * h);
        }
        return gradients;
    }

    public static double checkGradient(double[][] X, double[] y, double[] params,
            java.util.function.BiFunction<double[][], double[], Double> lossFn) {
        return 0;
    }

    public static void main(String[] args) {
        System.out.println("=== Gradient Computation for Backprop Demo ===");
        java.util.function.Function<double[], Double> simpleQuadratic = p -> p[0] * p[0] + 2 * p[1] * p[1];
        double[] params = {1.0, 2.0};
        double[] grads = computeGradientsCentral(simpleQuadratic, params, 1e-5);
        System.out.println("f(x,y)=x^2+2y^2 at (1,2): gradient = " + java.util.Arrays.toString(grads));
        System.out.println("Expected: [2, 8]");
    }
}
