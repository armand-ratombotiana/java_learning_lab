package com.algo.lab31;

import java.util.Random;

public class SecretaryProblem {

    private static final Random random = new Random();

    public static int simulate(int n) {
        double[] scores = new double[n];
        for (int i = 0; i < n; i++) scores[i] = random.nextDouble();

        int rejectCount = (int) (n / Math.E);
        double bestObserved = 0;
        for (int i = 0; i < rejectCount; i++) {
            bestObserved = Math.max(bestObserved, scores[i]);
        }

        double globalBest = 0;
        int bestPos = -1;
        for (int i = 0; i < n; i++) {
            if (scores[i] > globalBest) {
                globalBest = scores[i];
                bestPos = i;
            }
        }

        int selected = -1;
        for (int i = rejectCount; i < n; i++) {
            if (scores[i] > bestObserved) {
                selected = i;
                break;
            }
        }

        return selected == bestPos ? 1 : 0;
    }

    public static double successProbability(int n, int trials) {
        int successes = 0;
        for (int t = 0; t < trials; t++) {
            successes += simulate(n);
        }
        return (double) successes / trials;
    }
}
