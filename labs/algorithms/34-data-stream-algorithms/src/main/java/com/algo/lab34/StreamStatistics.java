package com.algo.lab34;

public class StreamStatistics {

    private long count = 0;
    private double mean = 0.0;
    private double m2 = 0.0;

    public void add(double value) {
        count++;
        double delta = value - mean;
        mean += delta / count;
        m2 += delta * (value - mean);
    }

    public double getMean() {
        return mean;
    }

    public double getVariance() {
        return count > 1 ? m2 / (count - 1) : 0.0;
    }

    public double getPopulationVariance() {
        return count > 0 ? m2 / count : 0.0;
    }

    public long getCount() {
        return count;
    }
}
