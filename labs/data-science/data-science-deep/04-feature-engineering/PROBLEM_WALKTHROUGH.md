# Problem Walkthrough: Automated Feature Engineering

## Problem
Build a pipeline that automatically generates features from raw numeric data, applies target encoding for categoricals, and selects the top-k features by mutual information.

## Step 1: Polynomial + Interaction Generator

```java
public class FeatureGenerator {
    public double[][] generatePolynomialInteraction(double[][] X, int degree) {
        int n = X.length, p = X[0].length;
        List<double[]> features = new ArrayList<>(Arrays.asList(identity(X)));
        
        // Polynomial features (degree 2 and 3)
        for (int d = 2; d <= degree; d++) {
            for (int j = 0; j < p; j++) {
                double[] newCol = new double[n];
                for (int i = 0; i < n; i++) newCol[i] = Math.pow(X[i][j], d);
                features.add(newCol);
            }
        }
        
        // Pairwise interactions
        for (int j1 = 0; j1 < p; j1++) {
            for (int j2 = j1 + 1; j2 < p; j2++) {
                double[] newCol = new double[n];
                for (int i = 0; i < n; i++) newCol[i] = X[i][j1] * X[i][j2];
                features.add(newCol);
            }
        }
        
        return features.toArray(double[][]::new);
    }
}
```

## Step 2: Target Encoding with CV

```java
public class TargetEncoder {
    private final double smooth;
    
    public double[] fitTransform(double[] categories, double[] target, int kFolds) {
        int n = categories.length;
        double[] encoded = new double[n];
        double globalMean = mean(target);
        CrossValidationSplitter splitter = new CrossValidationSplitter(kFolds);
        
        for (int fold = 0; fold < kFolds; fold++) {
            boolean[] trainMask = splitter.getTrainMask(fold, n);
            Map<Double, double[]> foldStats = new HashMap<>();
            
            // Compute statistics on training fold only
            for (int i = 0; i < n; i++) {
                if (!trainMask[i]) continue;
                foldStats.computeIfAbsent(categories[i], k -> new double[2]);
                foldStats.get(categories[i])[0]++;
                foldStats.get(categories[i])[1] += target[i];
            }
            
            // Encode validation fold using training statistics
            for (int i = 0; i < n; i++) {
                if (trainMask[i]) continue;
                double[] stats = foldStats.getOrDefault(categories[i], new double[]{0, 0});
                double count = stats[0], sum = stats[1];
                encoded[i] = (sum + smooth * globalMean) / (count + smooth);
            }
        }
        return encoded;
    }
}
```

## Step 3: Feature Selection by Mutual Information

```java
public class MutualInformationSelection {
    private static final int BINS = 20;
    
    public int[] selectTopK(double[][] X, double[] y, int k) {
        int p = X[0].length;
        double[] miScores = new double[p];
        for (int j = 0; j < p; j++) {
            miScores[j] = mutualInformation(getColumn(X, j), y, BINS);
        }
        // Return indices of top-k features
        Integer[] indices = new Integer[p];
        for (int i = 0; i < p; i++) indices[i] = i;
        Arrays.sort(indices, Comparator.comparingDouble((Integer i) -> -miScores[i]));
        return Arrays.stream(indices).limit(k).mapToInt(Integer::intValue).toArray();
    }
}
```

## Step 4: Pipeline Orchestration

```java
public class AutoFeaturePipeline {
    private FeatureGenerator generator = new FeatureGenerator();
    private TargetEncoder encoder = new TargetEncoder(10.0);
    private MutualInformationSelection selector = new MutualInformationSelection();
    
    public double[][] fitTransform(double[][] XNum, double[] categories, double[] y) {
        // Step 1: Generate polynomial + interaction features
        double[][] polyFeatures = generator.generatePolynomialInteraction(XNum, 2);
        
        // Step 2: Target encode categorical
        double[] encoded = encoder.fitTransform(categories, y, 5);
        
        // Step 3: Combine
        int n = XNum.length;
        double[][] allFeatures = new double[n][polyFeatures[0].length + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(polyFeatures[i], 0, allFeatures[i], 0, polyFeatures[i].length);
            allFeatures[i][polyFeatures[0].length] = encoded[i];
        }
        
        // Step 4: Select top 20 features by mutual information
        int[] topK = selector.selectTopK(allFeatures, y, 20);
        return selectColumns(allFeatures, topK);
    }
}
```

## Step 5: Verification

```java
@Test
public void testFeaturePipeline() {
    double[][] X = RandomUtils.normal(100, 5, 0, 1);
    double[] y = simulateOutput(X);
    double[][] engineered = pipeline.fitTransform(X, new double[100], y);
    assertEquals(20, engineered[0].length); // top 20 selected
    assertTrue(mean(mutualInfos) > 0.1); // non-trivial features retained
}
```
