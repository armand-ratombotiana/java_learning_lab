package com.ai10;

public class MatrixFactorization {
    private double[][] P, Q;
    private double learningRate;
    private double regularization;
    private int numFeatures;
    private int numUsers, numItems;

    public MatrixFactorization(int numFeatures, double learningRate, double regularization) {
        this.numFeatures = numFeatures;
        this.learningRate = learningRate;
        this.regularization = regularization;
    }

    public void fit(double[][] ratings, int maxEpochs) {
        numUsers = ratings.length;
        numItems = ratings[0].length;
        P = new double[numUsers][numFeatures];
        Q = new double[numItems][numFeatures];
        java.util.Random rng = new java.util.Random(42);
        for (int u = 0; u < numUsers; u++)
            for (int f = 0; f < numFeatures; f++)
                P[u][f] = rng.nextDouble() * 0.1;
        for (int i = 0; i < numItems; i++)
            for (int f = 0; f < numFeatures; f++)
                Q[i][f] = rng.nextDouble() * 0.1;

        for (int epoch = 0; epoch < maxEpochs; epoch++) {
            double totalError = 0;
            int count = 0;
            for (int u = 0; u < numUsers; u++) {
                for (int i = 0; i < numItems; i++) {
                    if (ratings[u][i] == 0) continue;
                    double prediction = predict(u, i);
                    double error = ratings[u][i] - prediction;
                    totalError += error * error;
                    count++;
                    for (int f = 0; f < numFeatures; f++) {
                        double puf = P[u][f];
                        double qif = Q[i][f];
                        P[u][f] += learningRate * (error * qif - regularization * puf);
                        Q[i][f] += learningRate * (error * puf - regularization * qif);
                    }
                }
            }
            if (epoch % 20 == 0)
                System.out.println("Epoch " + epoch + " RMSE: " + Math.sqrt(totalError / count));
        }
    }

    public double predict(int user, int item) {
        double pred = 0;
        for (int f = 0; f < numFeatures; f++)
            pred += P[user][f] * Q[item][f];
        return pred;
    }

    public double[][] getP() { return P; }
    public double[][] getQ() { return Q; }

    public static void main(String[] args) {
        System.out.println("=== Matrix Factorization Demo ===");
        double[][] ratings = {
            {5, 3, 0, 1, 4},
            {4, 0, 0, 1, 5},
            {1, 1, 0, 5, 0},
            {1, 0, 0, 4, 0},
            {0, 1, 5, 4, 0}
        };
        MatrixFactorization mf = new MatrixFactorization(3, 0.01, 0.02);
        mf.fit(ratings, 100);
        System.out.println("Predicted rating user 0, item 2: " + mf.predict(0, 2));
        System.out.println("Full predicted matrix:");
        for (int u = 0; u < ratings.length; u++) {
            for (int i = 0; i < ratings[0].length; i++)
                System.out.printf("%.1f ", mf.predict(u, i));
            System.out.println();
        }
    }
}
