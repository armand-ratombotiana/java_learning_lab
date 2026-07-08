package com.ai26;

import java.util.*;

public class MultiTaskModel {
    private final SharedEncoder encoder;
    private final List<TaskHead> taskHeads;
    private final Random random;
    private final int inputDim, sharedDim;

    public MultiTaskModel(int inputDim, int sharedDim, List<Integer> taskOutputDims) {
        this.inputDim = inputDim;
        this.sharedDim = sharedDim;
        this.random = new Random(42);
        this.encoder = new SharedEncoder(inputDim, sharedDim);
        this.taskHeads = new ArrayList<>();
        for (int outDim : taskOutputDims) {
            taskHeads.add(new TaskHead(sharedDim, outDim));
        }
    }

    public double[] encode(double[] input) {
        return encoder.forward(input);
    }

    public double[] forward(int taskIdx, double[] input) {
        double[] shared = encode(input);
        return taskHeads.get(taskIdx).forward(shared);
    }

    public void train(double[][] inputs, List<double[][]> taskTargets, List<Double> taskWeights, int epochs, double lr) {
        int numTasks = taskHeads.size();
        int n = inputs.length;
        for (int epoch = 0; epoch < epochs; epoch++) {
            double totalLoss = 0;
            for (int i = 0; i < n; i++) {
                double[] shared = encoder.forward(inputs[i]);
                for (int t = 0; t < numTasks; t++) {
                    double[] pred = taskHeads.get(t).forward(shared);
                    double[] target = taskTargets.get(t)[i];
                    double weight = taskWeights.get(t);
                    double loss = 0;
                    for (int j = 0; j < pred.length; j++) {
                        double diff = pred[j] - target[j];
                        loss += diff * diff;
                    }
                    totalLoss += weight * loss;

                    double[] grad = new double[pred.length];
                    for (int j = 0; j < pred.length; j++) {
                        grad[j] = 2 * weight * (pred[j] - target[j]);
                    }
                    taskHeads.get(t).backward(shared, grad, lr);
                    encoder.backward(inputs[i], taskHeads.get(t), grad, t, lr);
                }
            }
            if (epoch % 100 == 0) System.out.printf("Epoch %d: Total Loss = %.4f%n", epoch, totalLoss / n);
        }
    }

    static class SharedEncoder {
        double[][] w1, w2; double[] b1, b2;
        final int inputDim, sharedDim;

        SharedEncoder(int id, int sd) {
            inputDim = id; sharedDim = sd;
            Random r = new Random(42);
            w1 = new double[id][64]; b1 = new double[64];
            w2 = new double[64][sd]; b2 = new double[sd];
            for (int i = 0; i < id; i++) for (int j = 0; j < 64; j++) w1[i][j] = r.nextGaussian() * 0.1;
            for (int j = 0; j < 64; j++) for (int s = 0; s < sd; s++) w2[j][s] = r.nextGaussian() * 0.1;
        }

        double[] forward(double[] input) {
            double[] h = new double[64];
            for (int j = 0; j < 64; j++) { double s = b1[j]; for (int i = 0; i < inputDim; i++) s += input[i] * w1[i][j]; h[j] = Math.max(0, s); }
            double[] out = new double[sharedDim];
            for (int s = 0; s < sharedDim; s++) { double v = b2[s]; for (int j = 0; j < 64; j++) v += h[j] * w2[j][s]; out[s] = Math.max(0, v); }
            return out;
        }

        void backward(double[] input, TaskHead head, double[] taskGrad, int taskIdx, double lr) {
            double[] shared = forward(input);
            double[] dHead = new double[sharedDim];
            for (int s = 0; s < sharedDim; s++) {
                for (int o = 0; o < head.outputDim; o++) {
                    dHead[s] += head.w[s][o] * taskGrad[o];
                }
            }

            double[] h = new double[64];
            for (int j = 0; j < 64; j++) { double s = b1[j]; for (int i = 0; i < inputDim; i++) s += input[i] * w1[i][j]; h[j] = Math.max(0, s); }

            double[] dShared = new double[sharedDim];
            System.arraycopy(dHead, 0, dShared, 0, sharedDim);
            for (int s = 0; s < sharedDim; s++) if (shared[s] <= 0) dShared[s] = 0;

            double[] dHidden = new double[64];
            for (int j = 0; j < 64; j++) {
                for (int s = 0; s < sharedDim; s++) dHidden[j] += w2[j][s] * dShared[s];
                if (h[j] <= 0) dHidden[j] = 0;
            }

            for (int i = 0; i < inputDim; i++) for (int j = 0; j < 64; j++) w1[i][j] -= lr * input[i] * dHidden[j];
            for (int j = 0; j < 64; j++) b1[j] -= lr * dHidden[j];
            for (int j = 0; j < 64; j++) for (int s = 0; s < sharedDim; s++) w2[j][s] -= lr * h[j] * dShared[s];
            for (int s = 0; s < sharedDim; s++) b2[s] -= lr * dShared[s];
        }
    }

    static class TaskHead {
        double[][] w; double[] b;
        final int inputDim, outputDim;

        TaskHead(int id, int od) {
            inputDim = id; outputDim = od;
            Random r = new Random(42);
            w = new double[id][od]; b = new double[od];
            for (int i = 0; i < id; i++) for (int o = 0; o < od; o++) w[i][o] = r.nextGaussian() * 0.1;
        }

        double[] forward(double[] input) {
            double[] out = new double[outputDim];
            for (int o = 0; o < outputDim; o++) { double s = b[o]; for (int i = 0; i < inputDim; i++) s += input[i] * w[i][o]; out[o] = s; }
            return out;
        }

        void backward(double[] input, double[] grad, double lr) {
            for (int i = 0; i < inputDim; i++) for (int o = 0; o < outputDim; o++) w[i][o] -= lr * input[i] * grad[o];
            for (int o = 0; o < outputDim; o++) b[o] -= lr * grad[o];
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Multi-Task Learning Demo ===");
        Random rng = new Random(42);
        int n = 100;
        double[][] inputs = new double[n][5];
        for (int i = 0; i < n; i++) for (int j = 0; j < 5; j++) inputs[i][j] = rng.nextDouble();
        List<double[][]> targets = new ArrayList<>();
        double[][] t1 = new double[n][1];
        double[][] t2 = new double[n][2];
        for (int i = 0; i < n; i++) {
            t1[i][0] = inputs[i][0] * 2 + inputs[i][1] * 3 + 0.1 * rng.nextGaussian();
            t2[i][0] = Math.sin(inputs[i][2]) + 0.1 * rng.nextGaussian();
            t2[i][1] = Math.cos(inputs[i][3]) + 0.1 * rng.nextGaussian();
        }
        targets.add(t1); targets.add(t2);

        MultiTaskModel model = new MultiTaskModel(5, 16, Arrays.asList(1, 2));
        model.train(inputs, targets, Arrays.asList(0.5, 0.5), 500, 0.01);
        double[] testIn = {0.5, 0.3, 0.8, 0.2, 0.7};
        double[] out1 = model.forward(0, testIn);
        double[] out2 = model.forward(1, testIn);
        System.out.printf("Task 1 output: %.4f (expected ~%.4f)%n", out1[0], testIn[0]*2 + testIn[1]*3);
        System.out.printf("Task 2 outputs: %.4f, %.4f%n", out2[0], out2[1]);
    }
}
