# Probability for ML - CODE DEEP DIVE

## Java Implementations

### 1. Gaussian Distribution

```java
public class GaussianDistribution {
    private final double mean;
    private final double stdDev;
    
    public GaussianDistribution(double mean, double stdDev) {
        this.mean = mean;
        this.stdDev = stdDev;
    }
    
    public double pdf(double x) {
        double coeff = 1.0 / (stdDev * Math.sqrt(2 * Math.PI));
        double exponent = -Math.pow(x - mean, 2) / (2 * stdDev * stdDev);
        return coeff * Math.exp(exponent);
    }
    
    public double cdf(double x) {
        return 0.5 * (1 + erf((x - mean) / (stdDev * Math.sqrt(2))));
    }
    
    public static double erf(double z) {
        double t = 1.0 / (1.0 + 0.5 * Math.abs(z));
        double tau = t * Math.exp(-z * z - 1.26551223 +
            t * (1.00002368 +
            t * (0.37409196 +
            t * (0.09678418 +
            t * (-0.20230220 +
            t * (0.28709287 +
            t * (-0.13504837 +
            t * (0.27886803 +
            t * (-0.04219261)))))))));
        return z >= 0 ? 1 - tau : tau - 1;
    }
    
    public double sample() {
        Random random = new Random();
        double u1 = random.nextDouble();
        double u2 = random.nextDouble();
        double z = Math.sqrt(-2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
        return mean + stdDev * z;
    }
}
```

### 2. Bayes Classifier

```java
public class NaiveBayesClassifier {
    private final Map<String, double[]> classPriors;
    private final Map<String, GaussianDistribution[]> featureDistributions;
    
    public void fit(double[][] X, String[] y) {
        classPriors = new HashMap<>();
        featureDistributions = new HashMap<>();
        
        Set<String> classes = Arrays.stream(y).collect(Collectors.toSet());
        int nFeatures = X[0].length;
        
        for (String c : classes) {
            double[][] classData = filterByClass(X, y, c);
            classPriors.put(c, (double) classData.length / X.length);
            
            GaussianDistribution[] dists = new GaussianDistribution[nFeatures];
            for (int i = 0; i < nFeatures; i++) {
                double[] featureValues = Arrays.stream(classData)
                    .map(row -> row[i])
                    .mapToObj(Double::doubleValue)
                    .mapToDouble(Double::doubleValue)
                    .toArray();
                double mean = mean(featureValues);
                double std = stddev(featureValues, mean);
                dists[i] = new GaussianDistribution(mean, std);
            }
            featureDistributions.put(c, dists);
        }
    }
    
    public String predict(double[] x) {
        double maxLogProb = Double.NEGATIVE_INFINITY;
        String predictedClass = null;
        
        for (String c : classPriors.keySet()) {
            double logProb = Math.log(classPriors.get(c)[0]);
            GaussianDistribution[] dists = featureDistributions.get(c);
            
            for (int i = 0; i < x.length; i++) {
                logProb += Math.log(dists[i].pdf(x[i]) + 1e-10);
            }
            
            if (logProb > maxLogProb) {
                maxLogProb = logProb;
                predictedClass = c;
            }
        }
        return predictedClass;
    }
    
    private double[][] filterByClass(double[][] X, String[] y, String c) {
        return IntStream.range(0, X.length)
            .filter(i -> y[i].equals(c))
            .mapToObj(i -> X[i])
            .toArray(double[][]::new);
    }
    
    private double mean(double[] data) {
        return Arrays.stream(data).average().orElse(0);
    }
    
    private double stddev(double[] data, double mean) {
        double variance = Arrays.stream(data)
            .map(x -> (x - mean) * (x - mean))
            .average().orElse(0);
        return Math.sqrt(variance);
    }
}
```

### 3. Entropy Calculator

```java
public class EntropyCalculator {
    public double entropy(int[] counts) {
        int total = Arrays.stream(counts).sum();
        return Arrays.stream(counts)
            .filter(c -> c > 0)
            .mapToDouble(c -> {
                double p = (double) c / total;
                return -p * Math.log(p) / Math.log(2);
            })
            .sum();
    }
    
    public double conditionalEntropy(int[][] contingency) {
        double total = Arrays.stream(contingency)
            .flatMapToInt(Arrays::stream)
            .sum();
        double h = 0;
        
        for (int i = 0; i < contingency.length; i++) {
            int rowSum = Arrays.stream(contingency[i]).sum();
            if (rowSum > 0) {
                for (int j = 0; j < contingency[i].length; j++) {
                    if (contingency[i][j] > 0) {
                        double p = (double) contingency[i][j] / total;
                        double pCond = (double) contingency[i][j] / rowSum;
                        h += -p * Math.log(pCond) / Math.log(2);
                    }
                }
            }
        }
        return h;
    }
    
    public double informationGain(double hBefore, double hAfter) {
        return hBefore - hAfter;
    }
}
```

### 4. MLE Implementation

```java
public class MaximumLikelihoodEstimation {
    public double[] normalMLE(double[] data) {
        double mean = mean(data);
        double variance = variance(data, mean);
        return new double[]{mean, Math.sqrt(variance)};
    }
    
    public double exponentialMLE(double[] data) {
        double mean = mean(data);
        return new double[]{1.0 / mean};
    }
    
    public double bernoulliMLE(int successes, int trials) {
        return (double) successes / trials;
    }
    
    private double mean(double[] data) {
        return Arrays.stream(data).average().orElse(0);
    }
    
    private double variance(double[] data, double mean) {
        return Arrays.stream(data)
            .map(x -> (x - mean) * (x - mean))
            .average().orElse(0);
    }
}
```

### 5. Sampling Methods

```java
public class SamplingMethods {
    private final Random random = new Random();
    
    public double[] rejectionSampling(Function<Double, Double> targetPdf, 
                                       double xMin, double xMax) {
        double maxY = 2.0; // Upper bound estimate
        int samples = 1000;
        List<Double> result = new ArrayList<>();
        
        while (result.size() < samples) {
            double x = xMin + random.nextDouble() * (xMax - xMin);
            double y = random.nextDouble() * maxY;
            
            if (y <= targetPdf.apply(x)) {
                result.add(x);
            }
        }
        return result.stream().mapToDouble(Double::doubleValue).toArray();
    }
    
    public double[] importanceSampling(Function<Double, Double> targetPdf,
                                        Function<Double, Double> proposalPdf,
                                        int samples) {
        double[] result = new double[samples];
        
        for (int i = 0; i < samples; i++) {
            double x = sampleFromProposal(proposalPdf);
            double weight = targetPdf.apply(x) / proposalPdf.apply(x);
            result[i] = x * weight;
        }
        
        return result;
    }
    
    private double sampleFromProposal(Function<Double, Double> pdf) {
        // Simplified: return uniform sample
        return random.nextDouble() * 10;
    }
}
```