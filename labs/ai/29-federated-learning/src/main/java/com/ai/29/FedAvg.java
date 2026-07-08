package com.ai29;

import java.util.*;

public class FedAvg {
    private final GlobalModel globalModel;
    private final int numClients;
    private final double learningRate;
    private final int localEpochs;
    private final Random random;

    public FedAvg(int inputDim, int hiddenDim, int outputDim, int numClients, double learningRate, int localEpochs) {
        this.numClients = numClients;
        this.learningRate = learningRate;
        this.localEpochs = localEpochs;
        this.random = new Random(42);
        this.globalModel = new GlobalModel(inputDim, hiddenDim, outputDim);
    }

    public void train(List<List<double[]>> clientData, int rounds, double clientFraction) {
        for (int round = 0; round < rounds; round++) {
            int numSelected = Math.max(1, (int)(numClients * clientFraction));
            Set<Integer> selectedClients = new HashSet<>();
            while (selectedClients.size() < numSelected) {
                selectedClients.add(random.nextInt(numClients));
            }

            List<LocalModel> localModels = new ArrayList<>();
            for (int clientIdx : selectedClients) {
                LocalModel localModel = new LocalModel(globalModel);
                List<double[]> data = clientData.get(clientIdx);
                localModel.train(data, localEpochs, learningRate);
                localModels.add(localModel);
            }

            globalModel.aggregate(localModels);

            if (round % 20 == 0) {
                double avgLoss = 0;
                int count = 0;
                for (int c = 0; c < numClients; c++) {
                    for (double[] example : clientData.get(c)) {
                        double[] pred = globalModel.forward(Arrays.copyOfRange(example, 1, example.length));
                        double target = example[0];
                        avgLoss += (pred[0] - target) * (pred[0] - target);
                        count++;
                    }
                }
                System.out.printf("Round %d: Avg Loss = %.4f%n", round, avgLoss / count);
            }
        }
    }

    public double predict(double[] input) {
        return globalModel.forward(input)[0];
    }

    static class GlobalModel {
        double[][] w1, w2; double[] b1, b2;
        final int inputDim, hiddenDim, outputDim;

        GlobalModel(int id, int hd, int od) {
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

        void aggregate(List<LocalModel> localModels) {
            int n = localModels.size();
            double[][] w1Acc = new double[inputDim][hiddenDim];
            double[] b1Acc = new double[hiddenDim];
            double[][] w2Acc = new double[hiddenDim][outputDim];
            double[] b2Acc = new double[outputDim];

            for (LocalModel lm : localModels) {
                for (int i = 0; i < inputDim; i++)
                    for (int h = 0; h < hiddenDim; h++) w1Acc[i][h] += lm.w1[i][h];
                for (int h = 0; h < hiddenDim; h++) b1Acc[h] += lm.b1[h];
                for (int h = 0; h < hiddenDim; h++)
                    for (int o = 0; o < outputDim; o++) w2Acc[h][o] += lm.w2[h][o];
                for (int o = 0; o < outputDim; o++) b2Acc[o] += lm.b2[o];
            }

            for (int i = 0; i < inputDim; i++)
                for (int h = 0; h < hiddenDim; h++) w1[i][h] = w1Acc[i][h] / n;
            for (int h = 0; h < hiddenDim; h++) b1[h] = b1Acc[h] / n;
            for (int h = 0; h < hiddenDim; h++)
                for (int o = 0; o < outputDim; o++) w2[h][o] = w2Acc[h][o] / n;
            for (int o = 0; o < outputDim; o++) b2[o] = b2Acc[o] / n;
        }
    }

    static class LocalModel {
        double[][] w1, w2; double[] b1, b2;
        final int inputDim, hiddenDim, outputDim;

        LocalModel(GlobalModel gm) {
            inputDim = gm.inputDim; hiddenDim = gm.hiddenDim; outputDim = gm.outputDim;
            w1 = new double[inputDim][hiddenDim]; b1 = new double[hiddenDim];
            w2 = new double[hiddenDim][outputDim]; b2 = new double[outputDim];
            copyFrom(gm);
        }

        void copyFrom(GlobalModel gm) {
            for (int i = 0; i < inputDim; i++) System.arraycopy(gm.w1[i], 0, w1[i], 0, hiddenDim);
            System.arraycopy(gm.b1, 0, b1, 0, hiddenDim);
            for (int h = 0; h < hiddenDim; h++) System.arraycopy(gm.w2[h], 0, w2[h], 0, outputDim);
            System.arraycopy(gm.b2, 0, b2, 0, outputDim);
        }

        void train(List<double[]> data, int epochs, double lr) {
            for (int ep = 0; ep < epochs; ep++) {
                for (double[] example : data) {
                    double[] input = Arrays.copyOfRange(example, 1, example.length);
                    double target = example[0];
                    double[] h = new double[hiddenDim];
                    for (int j = 0; j < hiddenDim; j++) { double s = b1[j]; for (int i = 0; i < inputDim; i++) s += input[i] * w1[i][j]; h[j] = Math.max(0, s); }
                    double pred = b2[0]; for (int j = 0; j < hiddenDim; j++) pred += h[j] * w2[j][0];
                    double dOut = 2 * (pred - target);
                    for (int j = 0; j < hiddenDim; j++) w2[j][0] -= lr * h[j] * dOut;
                    b2[0] -= lr * dOut;
                    for (int j = 0; j < hiddenDim; j++) {
                        if (h[j] > 0) {
                            for (int i = 0; i < inputDim; i++) w1[i][j] -= lr * input[i] * w2[j][0] * dOut;
                            b1[j] -= lr * w2[j][0] * dOut;
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Federated Averaging (FedAvg) Demo ===");
        Random rng = new Random(42);
        int numClients = 5;
        List<List<double[]>> clientData = new ArrayList<>();
        for (int c = 0; c < numClients; c++) {
            List<double[]> data = new ArrayList<>();
            double w = rng.nextDouble() * 2 - 1;
            double b = rng.nextDouble() * 2 - 1;
            for (int i = 0; i < 20; i++) {
                double x = rng.nextDouble() * 4 - 2;
                double y = w * x + b + 0.1 * rng.nextGaussian();
                data.add(new double[]{y, x});
            }
            clientData.add(data);
        }

        FedAvg fedAvg = new FedAvg(1, 10, 1, numClients, 0.01, 3);
        fedAvg.train(clientData, 100, 0.6);

        double testX = 1.0;
        double pred = fedAvg.predict(new double[]{testX});
        System.out.printf("Global model prediction for x=%.1f: %.4f%n", testX, pred);
    }
}
