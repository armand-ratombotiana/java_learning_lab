package com.datasci.10;

import java.util.*;

public class DataPreprocessor {

    private final Map<Integer, Double> meanImputationValues;
    private final Map<Integer, Double> minValues;
    private final Map<Integer, Double> maxValues;
    private final Map<Integer, Double> stdValues;
    private final Map<Integer, Double> medianValues;
    private boolean fitted;

    public DataPreprocessor() {
        this.meanImputationValues = new HashMap<>();
        this.minValues = new HashMap<>();
        this.maxValues = new HashMap<>();
        this.stdValues = new HashMap<>();
        this.medianValues = new HashMap<>();
        this.fitted = false;
    }

    public void fit(double[][] features) {
        int cols = features[0].length;
        for (int j = 0; j < cols; j++) {
            List<Double> colVals = new ArrayList<>();
            double sum = 0, count = 0;
            for (double[] row : features) {
                if (!Double.isNaN(row[j])) {
                    colVals.add(row[j]);
                    sum += row[j];
                    count++;
                }
            }
            double mean = count > 0 ? sum / count : 0;
            meanImputationValues.put(j, mean);
            Collections.sort(colVals);
            double median = colVals.isEmpty() ? 0 :
                colVals.size() % 2 == 0 ?
                    (colVals.get(colVals.size()/2 - 1) + colVals.get(colVals.size()/2)) / 2.0 :
                    colVals.get(colVals.size()/2);
            medianValues.put(j, median);
            double min = colVals.stream().mapToDouble(d -> d).min().orElse(0);
            double max = colVals.stream().mapToDouble(d -> d).max().orElse(0);
            minValues.put(j, min);
            maxValues.put(j, max);
            double variance = colVals.stream().mapToDouble(d -> (d - mean) * (d - mean)).average().orElse(0);
            double std = Math.sqrt(variance);
            stdValues.put(j, std == 0 ? 1 : std);
        }
        this.fitted = true;
    }

    public double[][] transform(double[][] features) {
        if (!fitted) throw new IllegalStateException("Must call fit() before transform()");
        int rows = features.length, cols = features[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = features[i][j];
            }
        }
        return result;
    }

    public double[][] imputeMean(double[][] features) {
        int rows = features.length, cols = features[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = Double.isNaN(features[i][j]) ? meanImputationValues.get(j) : features[i][j];
            }
        }
        return result;
    }

    public double[][] standardize(double[][] features) {
        int rows = features.length, cols = features[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = (features[i][j] - meanImputationValues.get(j)) / stdValues.get(j);
            }
        }
        return result;
    }

    public double[][] minMaxScale(double[][] features) {
        int rows = features.length, cols = features[0].length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double min = minValues.get(j), max = maxValues.get(j);
                result[i][j] = (max - min) == 0 ? 0 : (features[i][j] - min) / (max - min);
            }
        }
        return result;
    }

    public static double[][] oneHotEncode(double[][] features, int colIndex) {
        Set<Double> uniqueVals = new HashSet<>();
        for (double[] row : features) uniqueVals.add(row[colIndex]);
        List<Double> sortedVals = new ArrayList<>(uniqueVals);
        Collections.sort(sortedVals);
        int newCols = features[0].length - 1 + sortedVals.size();
        double[][] result = new double[features.length][newCols];
        for (int i = 0; i < features.length; i++) {
            int idx = 0;
            for (int j = 0; j < features[0].length; j++) {
                if (j == colIndex) continue;
                result[i][idx++] = features[i][j];
            }
            int catIdx = sortedVals.indexOf(features[i][colIndex]);
            result[i][idx + catIdx] = 1.0;
        }
        return result;
    }

    public static SplitResult trainTestSplit(double[][] features, double[] labels,
                                              double testRatio, Random rng) {
        int n = features.length;
        int testSize = (int) (n * testRatio);
        int trainSize = n - testSize;
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;
        Collections.shuffle(Arrays.asList(indices), rng);
        double[][] trainFeatures = new double[trainSize][];
        double[] trainLabels = new double[trainSize];
        double[][] testFeatures = new double[testSize][];
        double[] testLabels = new double[testSize];
        for (int i = 0; i < trainSize; i++) {
            trainFeatures[i] = features[indices[i]];
            trainLabels[i] = labels[indices[i]];
        }
        for (int i = 0; i < testSize; i++) {
            testFeatures[i] = features[indices[trainSize + i]];
            testLabels[i] = labels[indices[trainSize + i]];
        }
        return new SplitResult(trainFeatures, trainLabels, testFeatures, testLabels);
    }

    public record SplitResult(double[][] trainFeatures, double[] trainLabels,
                              double[][] testFeatures, double[] testLabels) {}
}
