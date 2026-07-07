package com.algo.lab31;

import java.util.Random;

public class MultiArmedBandit {

    private final int k;
    private final double epsilon;
    private final int[] counts;
    private final double[] rewards;
    private final Random random = new Random();

    public MultiArmedBandit(int k, double epsilon) {
        this.k = k;
        this.epsilon = epsilon;
        this.counts = new int[k];
        this.rewards = new double[k];
    }

    public int selectArm() {
        if (random.nextDouble() < epsilon) {
            return random.nextInt(k);
        }
        int best = 0;
        for (int i = 1; i < k; i++) {
            double avgI = counts[i] == 0 ? 0 : rewards[i] / counts[i];
            double avgBest = counts[best] == 0 ? 0 : rewards[best] / counts[best];
            if (avgI > avgBest) best = i;
        }
        return best;
    }

    public void update(int arm, double reward) {
        counts[arm]++;
        rewards[arm] += reward;
    }

    public int getCount(int arm) {
        return counts[arm];
    }

    public double getAverageReward(int arm) {
        return counts[arm] == 0 ? 0 : rewards[arm] / counts[arm];
    }
}
