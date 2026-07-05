package com.ai04;

public class AdamOptimizer {
    private double[] m, v, params;
    private double beta1, beta2, epsilon, learningRate;
    private int t;

    public AdamOptimizer(int paramCount, double learningRate, double beta1, double beta2, double epsilon) {
        this.learningRate = learningRate;
        this.beta1 = beta1;
        this.beta2 = beta2;
        this.epsilon = epsilon;
        this.params = new double[paramCount];
        this.m = new double[paramCount];
        this.v = new double[paramCount];
        this.t = 0;
    }

    public void setParams(double[] p) {
        System.arraycopy(p, 0, params, 0, params.length);
    }

    public double[] getParams() {
        return params.clone();
    }

    public void update(double[] gradients) {
        t++;
        for (int i = 0; i < params.length; i++) {
            m[i] = beta1 * m[i] + (1 - beta1) * gradients[i];
            v[i] = beta2 * v[i] + (1 - beta2) * gradients[i] * gradients[i];
            double mHat = m[i] / (1 - Math.pow(beta1, t));
            double vHat = v[i] / (1 - Math.pow(beta2, t));
            params[i] -= learningRate * mHat / (Math.sqrt(vHat) + epsilon);
        }
    }

    public double computeLoss(double[][] X, double[] y) {
        int n = X.length;
        double loss = 0;
        for (int i = 0; i < n; i++) {
            double pred = 0;
            for (int j = 0; j < params.length; j++)
                pred += params[j] * X[i][j];
            double err = pred - y[i];
            loss += err * err;
        }
        return loss / (2 * n);
    }

    public double[] computeGradients(double[][] X, double[] y) {
        int n = X.length;
        double[] grads = new double[params.length];
        for (int i = 0; i < n; i++) {
            double pred = 0;
            for (int j = 0; j < params.length; j++)
                pred += params[j] * X[i][j];
            double err = pred - y[i];
            for (int j = 0; j < params.length; j++)
                grads[j] += err * X[i][j];
        }
        for (int j = 0; j < params.length; j++)
            grads[j] /= n;
        return grads;
    }

    public static void main(String[] args) {
        System.out.println("=== Adam Optimizer Demo ===");
        double[][] X = {{1, 1}, {1, 2}, {1, 3}, {1, 4}};
        double[] y = {2, 4, 6, 8};
        AdamOptimizer adam = new AdamOptimizer(2, 0.1, 0.9, 0.999, 1e-8);
        adam.setParams(new double[]{0, 0});
        for (int iter = 0; iter < 100; iter++) {
            double[] grads = adam.computeGradients(X, y);
            adam.update(grads);
            if (iter % 20 == 0)
                System.out.println("Iter " + iter + " loss: " + adam.computeLoss(X, y) + " params: " + java.util.Arrays.toString(adam.getParams()));
        }
        System.out.println("Final params: " + java.util.Arrays.toString(adam.getParams()));
    }
}
