package com.ai27;

import java.util.*;

public class MAML {
    private final Model model;
    private final double innerLr;
    private final double outerLr;
    private final int innerSteps;
    private final Random random;

    public MAML(int inputDim, int hiddenDim, int outputDim, double innerLr, double outerLr, int innerSteps) {
        this.innerLr = innerLr;
        this.outerLr = outerLr;
        this.innerSteps = innerSteps;
        this.random = new Random(42);
        this.model = new Model(inputDim, hiddenDim, outputDim);
    }

    public void metaTrain(List<List<double[]>> tasks, int metaEpochs) {
        for (int epoch = 0; epoch < metaEpochs; epoch++) {
            double metaLoss = 0;
            double[][] gradAccum = new double[model.numParams()][1];
            for (var task : tasks) {
                var taskData = splitTask(task);
                List<double[]> supportSet = taskData.support();
                List<double[]> querySet = taskData.query();

                Model fastModel = model.clone();
                for (int step = 0; step < innerSteps; step++) {
                    for (double[] example : supportSet) {
                        double[] input = Arrays.copyOfRange(example, 1, example.length);
                        double[] pred = fastModel.forward(input);
                        double target = example[0];
                        double loss = (pred[0] - target) * (pred[0] - target);
                        fastModel.update(input, pred[0], target, innerLr);
                    }
                }

                for (double[] example : querySet) {
                    double[] input = Arrays.copyOfRange(example, 1, example.length);
                    double[] pred = fastModel.forward(input);
                    double target = example[0];
                    double loss = (pred[0] - target) * (pred[0] - target);
                    metaLoss += loss;
                }
            }
            metaLoss /= tasks.size();
            if (epoch % 100 == 0) System.out.printf("Meta Epoch %d: Meta Loss = %.4f%n", epoch, metaLoss);
        }
    }

    public record TaskSplit(List<double[]> support, List<double[]> query) {}

    private TaskSplit splitTask(List<double[]> task) {
        Collections.shuffle(task, random);
        int split = task.size() / 2;
        return new TaskSplit(task.subList(0, split), task.subList(split, task.size()));
    }

    public double predict(double[] input) {
        return model.forward(input)[0];
    }

    static class Model {
        double[][] w1, w2; double[] b1, b2;
        final int inputDim, hiddenDim, outputDim;

        Model(int id, int hd, int od) {
            inputDim = id; hiddenDim = hd; outputDim = od;
            Random r = new Random(42);
            w1 = new double[id][hd]; b1 = new double[hd];
            w2 = new double[hd][od]; b2 = new double[od];
            for (int i = 0; i < id; i++) for (int h = 0; h < hd; h++) w1[i][h] = r.nextGaussian() * 0.1;
            for (int h = 0; h < hd; h++) for (int o = 0; o < od; o++) w2[h][o] = r.nextGaussian() * 0.1;
        }

        double[] forward(double[] input) {
            double[] h = new double[hiddenDim];
            for (int j = 0; j < hiddenDim; j++) { double s = b1[j]; for (int i = 0; i < inputDim; i++) s += input[i] * w1[i][j]; h[j] = Math.max(0, s); }
            double[] out = new double[outputDim];
            for (int o = 0; o < outputDim; o++) { double s = b2[o]; for (int j = 0; j < hiddenDim; j++) s += h[j] * w2[j][o]; out[o] = s; }
            return out;
        }

        void update(double[] input, double pred, double target, double lr) {
            double[] h = new double[hiddenDim];
            for (int j = 0; j < hiddenDim; j++) { double s = b1[j]; for (int i = 0; i < inputDim; i++) s += input[i] * w1[i][j]; h[j] = Math.max(0, s); }
            double dOut = 2 * (pred - target);
            for (int hh = 0; hh < hiddenDim; hh++) for (int o = 0; o < outputDim; o++) w2[hh][o] -= lr * h[hh] * dOut;
            for (int o = 0; o < outputDim; o++) b2[o] -= lr * dOut;
            for (int j = 0; j < hiddenDim; j++) {
                if (h[j] > 0) {
                    for (int i = 0; i < inputDim; i++) w1[i][j] -= lr * input[i] * w2[j][0] * dOut;
                    b1[j] -= lr * w2[j][0] * dOut;
                }
            }
        }

        int numParams() { return inputDim * hiddenDim + hiddenDim + hiddenDim * outputDim + outputDim; }

        Model clone() {
            Model m = new Model(inputDim, hiddenDim, outputDim);
            for (int i = 0; i < inputDim; i++) System.arraycopy(w1[i], 0, m.w1[i], 0, hiddenDim);
            System.arraycopy(b1, 0, m.b1, 0, hiddenDim);
            for (int h = 0; h < hiddenDim; h++) System.arraycopy(w2[h], 0, m.w2[h], 0, outputDim);
            System.arraycopy(b2, 0, m.b2, 0, outputDim);
            return m;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== MAML Demo ===");
        Random rng = new Random(42);
        List<List<double[]>> tasks = new ArrayList<>();
        for (int t = 0; t < 20; t++) {
            List<double[]> task = new ArrayList<>();
            double a = rng.nextDouble() * 4 - 2;
            double b = rng.nextDouble() * 4 - 2;
            for (int i = 0; i < 10; i++) {
                double x = rng.nextDouble() * 4 - 2;
                double y = a * x + b + 0.1 * rng.nextGaussian();
                task.add(new double[]{y, x});
            }
            tasks.add(task);
        }

        MAML maml = new MAML(1, 16, 1, 0.01, 0.001, 3);
        maml.metaTrain(tasks, 500);

        double testX = 1.5;
        double pred = maml.predict(new double[]{testX});
        System.out.printf("Prediction for x=%.1f: %.4f%n", testX, pred);
    }
}
