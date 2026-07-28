# Hypothesis Testing Guide

## 1. t-Tests

### One-Sample t-Test
```java
public record OneSampleTTest(double tStat, double pValue, double ciLower, double ciUpper) {
    public static OneSampleTTest test(double[] sample, double mu0, double alpha) {
        int n = sample.length;
        double mean = Arrays.stream(sample).average().orElseThrow();
        double var = variance(sample, mean);
        double se = Math.sqrt(var / n);
        double t = (mean - mu0) / se;
        double df = n - 1;
        double p = 2.0 * (1.0 - studentTCdf(Math.abs(t), df));
        double me = studentTQuantile(1.0 - alpha / 2.0, df) * se;
        return new OneSampleTTest(t, p, mean - me, mean + me);
    }
}
```

### Two-Sample Welch t-Test
```java
public record WelchTTest(double tStat, double pValue, double df, double ciLower, double ciUpper) {
    public static WelchTTest test(double[] x, double[] y, double alpha) {
        double n1 = x.length, n2 = y.length;
        double m1 = mean(x), m2 = mean(y);
        double v1 = variance(x, m1), v2 = variance(y, m2);
        double se = Math.sqrt(v1 / n1 + v2 / n2);
        double t = (m1 - m2) / se;
        double df = Math.pow(v1 / n1 + v2 / n2, 2) 
            / (Math.pow(v1 / n1, 2) / (n1 - 1) + Math.pow(v2 / n2, 2) / (n2 - 1));
        double p = 2.0 * (1.0 - studentTCdf(Math.abs(t), df));
        double me = studentTQuantile(1.0 - alpha / 2.0, df) * se;
        return new WelchTTest(t, p, df, (m1 - m2) - me, (m1 - m2) + me);
    }
}
```

## 2. Chi-Square Tests

### Test of Independence
```java
public record ChiSquareTest(double chiSq, double pValue, int df, double cramersV) {
    public static ChiSquareTest independence(int[][] contingencyTable) {
        int rows = contingencyTable.length;
        int cols = contingencyTable[0].length;
        int n = 0;
        double[] rowSums = new double[rows];
        double[] colSums = new double[cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rowSums[i] += contingencyTable[i][j];
                colSums[j] += contingencyTable[i][j];
                n += contingencyTable[i][j];
            }
        }

        double chiSq = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double expected = rowSums[i] * colSums[j] / n;
                if (expected > 0) {
                    chiSq += Math.pow(contingencyTable[i][j] - expected, 2) / expected;
                }
            }
        }

        int df = (rows - 1) * (cols - 1);
        double p = 1.0 - chiSquareCdf(chiSq, df);
        double cramersV = Math.sqrt(chiSq / (n * Math.min(rows - 1, cols - 1)));
        return new ChiSquareTest(chiSq, p, df, cramersV);
    }
}
```

## 3. Mann-Whitney U Test

```java
public record MannWhitneyUTest(double uStat, double pValue, double rankBiserial) {
    public static MannWhitneyUTest test(double[] x, double[] y) {
        int n1 = x.length, n2 = y.length;
        double[] combined = new double[n1 + n2];
        System.arraycopy(x, 0, combined, 0, n1);
        System.arraycopy(y, 0, combined, n1, n2);
        double[] ranks = rank(combined);
        
        double r1 = 0;
        for (int i = 0; i < n1; i++) r1 += ranks[i];
        double u1 = r1 - (double) n1 * (n1 + 1) / 2.0;
        double u2 = (double) n1 * n2 - u1;
        double u = Math.min(u1, u2);
        double mu = (double) n1 * n2 / 2.0;
        double su = Math.sqrt((double) n1 * n2 * (n1 + n2 + 1) / 12.0);
        double z = (u - mu) / su;
        double p = 2.0 * (1.0 - normalCDF(Math.abs(z)));
        double rb = 1.0 - 2.0 * u / (n1 * n2);
        return new MannWhitneyUTest(u, p, rb);
    }
}
```

## 4. Kruskal-Wallis (One-Way ANOVA on Ranks)

```java
public record KruskalWallisTest(double hStat, double pValue, int df) {
    public static KruskalWallisTest test(double[][] groups) {
        int k = groups.length;
        int n = Arrays.stream(groups).mapToInt(g -> g.length).sum();
        double[] all = new double[n];
        int[] groupIds = new int[n];
        int idx = 0;
        for (int j = 0; j < k; j++) {
            for (double v : groups[j]) { all[idx] = v; groupIds[idx] = j; idx++; }
        }
        double[] ranks = rank(all);
        double[] rSum = new double[k];
        for (int i = 0; i < n; i++) rSum[groupIds[i]] += ranks[i];
        double h = 12.0 / (n * (n + 1.0)) * 
            Arrays.stream(rSum).map(r -> r * r / (double) n * k).sum() - 3.0 * (n + 1);
        double p = 1.0 - chiSquareCdf(h, k - 1);
        return new KruskalWallisTest(h, p, k - 1);
    }
}
```

## 5. Bootstrap Hypothesis Test

```java
public class BootstrapTest {
    public static double twoSampleBootstrap(double[] x, double[] y, int nResamples) {
        double observedDiff = mean(x) - mean(y);
        double[] combined = new double[x.length + y.length];
        System.arraycopy(x, 0, combined, 0, x.length);
        System.arraycopy(y, 0, combined, x.length, y.length);
        int n1 = x.length;
        
        int extreme = 0;
        Random rng = new Random(42L);
        for (int r = 0; r < nResamples; r++) {
            double[] resample = new double[combined.length];
            for (int i = 0; i < combined.length; i++) resample[i] = combined[rng.nextInt(combined.length)];
            double m1 = mean(Arrays.copyOf(resample, n1));
            double m2 = mean(Arrays.copyOfRange(resample, n1, resample.length));
            if (Math.abs(m1 - m2) >= Math.abs(observedDiff)) extreme++;
        }
        return (double) extreme / nResamples;
    }
}
```
