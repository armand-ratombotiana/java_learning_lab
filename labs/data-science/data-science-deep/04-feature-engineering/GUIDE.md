# Feature Engineering Guide

## 1. Polynomial Features

```java
public record PolynomialFeatures(int degree, boolean includeInteractions, boolean includeBias) {
    public double[][] transform(double[][] X) {
        int n = X.length, p = X[0].length;
        List<int[]> powerTuples = generatePowerTuples(p, degree);
        double[][] result = new double[n][powerTuples.size()];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < powerTuples.size(); j++) {
                double val = 1.0;
                int[] powers = powerTuples.get(j);
                for (int k = 0; k < p; k++) {
                    val *= Math.pow(X[i][k], powers[k]);
                }
                result[i][j] = val;
            }
        }
        return result;
    }
    
    private List<int[]> generatePowerTuples(int p, int degree) {
        List<int[]> tuples = new ArrayList<>();
        if (includeBias) tuples.add(new int[p]); // bias term: all zeros
        for (int d = 1; d <= degree; d++) {
            generateRecursive(tuples, new int[p], 0, d, p);
        }
        return tuples;
    }
}
```

## 2. Binning

```java
public sealed interface Binning permits EqualWidthBinning, EqualFrequencyBinning, AdaptiveBinning {
    int[] fitTransform(double[] values);
}

public record EqualFrequencyBinning(int bins) implements Binning {
    @Override
    public int[] fitTransform(double[] values) {
        int n = values.length;
        double[] sorted = Arrays.copyOf(values, n);
        Arrays.sort(sorted);
        int[] labels = new int[n];
        for (int i = 0; i < n; i++) {
            int rank = 0;
            while (rank < n && values[i] > sorted[rank]) rank++;
            labels[i] = Math.min(bins - 1, rank * bins / n);
        }
        return labels;
    }
}
```

## 3. Target Encoding

```java
public record TargetEncoding(double smooth, double[] encoded) {
    public static TargetEncoding fit(double[] categories, double[] target) {
        int n = categories.length;
        Map<Double, double[]> stats = new HashMap<>();
        for (int i = 0; i < n; i++) {
            stats.computeIfAbsent(categories[i], k -> new double[2]);
            stats.get(categories[i])[0]++;
            stats.get(categories[i])[1] += target[i];
        }
        double globalMean = Arrays.stream(target).average().orElseThrow();
        double[] encoded = new double[n];
        for (int i = 0; i < n; i++) {
            double[] s = stats.get(categories[i]);
            double count = s[0], sum = s[1];
            encoded[i] = (sum + smooth * globalMean) / (count + smooth);
        }
        return new TargetEncoding(smooth, encoded);
    }
}
```

## 4. Feature Selection

```java
public sealed interface FeatureSelector permits VarianceThreshold, CorrelationFilter, MutualInfoSelector {
    boolean[] select(double[][] X, double[] y);
}

public record VarianceThreshold(double threshold) implements FeatureSelector {
    @Override
    public boolean[] select(double[][] X, double[] y) {
        int p = X[0].length;
        boolean[] keep = new boolean[p];
        for (int j = 0; j < p; j++) {
            double[] col = getColumn(X, j);
            double mean = Arrays.stream(col).average().orElseThrow();
            double var = Arrays.stream(col).map(v -> Math.pow(v - mean, 2)).sum() / (col.length - 1);
            keep[j] = var >= threshold;
        }
        return keep;
    }
}

public record CorrelationFilter(double threshold) implements FeatureSelector {
    @Override
    public boolean[] select(double[][] X, double[] y) {
        int p = X[0].length;
        boolean[] keep = new boolean[p];
        Arrays.fill(keep, true);
        for (int i = 0; i < p; i++) {
            for (int j = i + 1; j < p; j++) {
                double corr = pearsonCorrelation(getColumn(X, i), getColumn(X, j));
                if (Math.abs(corr) > threshold) keep[j] = false;
            }
        }
        return keep;
    }
}
```

## 5. Feature Engineering Pipeline

```java
public record FeaturePipeline(List<Step> steps) {
    public sealed interface Step permits PolynomialStep, BinningStep, EncodingStep, SelectorStep {}
    
    public double[][] fitTransform(double[][] X, double[] y) {
        double[][] current = X;
        for (Step step : steps) {
            current = switch (step) {
                case PolynomialStep ps -> ps.transform(current);
                case BinningStep bs -> bs.transform(current);
                case EncodingStep es -> es.transform(current, y);
                case SelectorStep ss -> {
                    boolean[] mask = ss.select(current, y);
                    yield applyMask(current, mask);
                }
            };
        }
        return current;
    }
}
```
