package com.ai06;

public class KNearestNeighbors {
    private double[][] Xtrain;
    private int[] ytrain;
    private int k;

    public KNearestNeighbors(int k) {
        this.k = k;
    }

    public void fit(double[][] X, int[] y) {
        this.Xtrain = X;
        this.ytrain = y;
    }

    public int predict(double[] x) {
        int n = Xtrain.length;
        double[] distances = new double[n];
        int[] indices = new int[n];
        for (int i = 0; i < n; i++) {
            distances[i] = euclideanDistance(x, Xtrain[i]);
            indices[i] = i;
        }
        for (int i = 0; i < n - 1; i++)
            for (int j = 0; j < n - i - 1; j++)
                if (distances[j] > distances[j + 1]) {
                    double tmpD = distances[j];
                    distances[j] = distances[j + 1];
                    distances[j + 1] = tmpD;
                    int tmpI = indices[j];
                    indices[j] = indices[j + 1];
                    indices[j + 1] = tmpI;
                }
        int[] votes = new int[10];
        for (int i = 0; i < k; i++)
            votes[ytrain[indices[i]]]++;
        int best = 0;
        for (int i = 1; i < votes.length; i++)
            if (votes[i] > votes[best]) best = i;
        return best;
    }

    private double euclideanDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    public int[] predict(double[][] X) {
        int[] predictions = new int[X.length];
        for (int i = 0; i < X.length; i++)
            predictions[i] = predict(X[i]);
        return predictions;
    }

    public static void main(String[] args) {
        System.out.println("=== K-Nearest Neighbors Demo ===");
        double[][] X = {
            {1.0, 2.0}, {2.0, 1.0}, {2.0, 3.0},
            {5.0, 4.0}, {6.0, 5.0}, {7.0, 6.0}
        };
        int[] y = {0, 0, 0, 1, 1, 1};
        KNearestNeighbors knn = new KNearestNeighbors(3);
        knn.fit(X, y);
        double[][] test = {{1.5, 2.0}, {5.5, 4.5}, {3, 3}};
        for (double[] t : test)
            System.out.println("Test " + java.util.Arrays.toString(t) + " -> " + knn.predict(t));
    }
}
