# Experimental Design Guide

## 1. Factorial Designs

### 2^k Full Factorial
```java
public record FactorialDesign(int factors, int levels) {
    public double[][] buildDesignMatrix() {
        int runs = (int) Math.pow(levels, factors);
        double[][] design = new double[runs][factors];
        for (int i = 0; i < runs; i++) {
            int tmp = i;
            for (int j = 0; j < factors; j++) {
                design[i][j] = (tmp % levels) == 0 ? -1.0 : 1.0;
                tmp /= levels;
            }
        }
        return design;
    }

    public double[] estimateEffects(double[][] design, double[] responses) {
        int runs = design.length;
        double[] effects = new double[factors];
        for (int j = 0; j < factors; j++) {
            double sumPos = 0, sumNeg = 0;
            int nPos = 0, nNeg = 0;
            for (int i = 0; i < runs; i++) {
                if (design[i][j] > 0) { sumPos += responses[i]; nPos++; }
                else { sumNeg += responses[i]; nNeg++; }
            }
            effects[j] = (sumPos / nPos - sumNeg / nNeg);
        }
        return effects;
    }
}
```

### 2^(k-p) Fractional Factorial

```java
public record FractionalFactorial(int factors, int fraction) {
    public double[][] buildFractionalDesign() {
        int runs = (int) Math.pow(2, factors - fraction);
        double[][] design = new double[runs][factors];
        // Full factorial on first (k-p) factors
        for (int i = 0; i < runs; i++) {
            int tmp = i;
            for (int j = 0; j < factors - fraction; j++) {
                design[i][j] = (tmp % 2 == 0) ? -1.0 : 1.0;
                tmp /= 2;
            }
        }
        // Generate aliased factors via generators
        for (int j = factors - fraction; j < factors; j++) {
            for (int i = 0; i < runs; i++) {
                design[i][j] = design[i][gen1(j)] * design[i][gen2(j)];
            }
        }
        return design;
    }
}
```

## 2. Blocking and Randomization

```java
public record BlockDesign(int blocks, int treatments) {
    public int[] randomize() {
        int n = blocks * treatments;
        int[] assignments = new int[n];
        for (int b = 0; b < blocks; b++) {
            int[] order = new int[treatments];
            for (int t = 0; t < treatments; t++) order[t] = t;
            // Fisher-Yates shuffle
            Random rng = new Random();
            for (int t = treatments - 1; t > 0; t--) {
                int k = rng.nextInt(t + 1);
                int tmp = order[t]; order[t] = order[k]; order[k] = tmp;
            }
            for (int t = 0; t < treatments; t++) {
                assignments[b * treatments + t] = order[t];
            }
        }
        return assignments;
    }
}
```

## 3. ANOVA

```java
public record ANOVA(double[] ss, double[] df, double[] ms, double fStat, double pValue) {
    public static ANOVA oneWay(double[][] groups) {
        int k = groups.length;
        int n = Arrays.stream(groups).mapToInt(g -> g.length).sum();
        
        // Grand mean
        double grandMean = 0;
        for (double[] g : groups) grandMean += Arrays.stream(g).sum();
        grandMean /= n;
        
        double ssBetween = 0, ssWithin = 0;
        for (int j = 0; j < k; j++) {
            double groupMean = Arrays.stream(groups[j]).average().orElseThrow();
            ssBetween += groups[j].length * Math.pow(groupMean - grandMean, 2);
            for (double v : groups[j]) {
                ssWithin += Math.pow(v - groupMean, 2);
            }
        }
        
        int dfBetween = k - 1;
        int dfWithin = n - k;
        double msBetween = ssBetween / dfBetween;
        double msWithin = ssWithin / dfWithin;
        double f = msBetween / msWithin;
        double p = 1.0 - fCdf(f, dfBetween, dfWithin);
        
        return new ANOVA(
            new double[]{ssBetween, ssWithin, ssBetween + ssWithin},
            new double[]{dfBetween, dfWithin, n - 1},
            new double[]{msBetween, msWithin, Double.NaN},
            f, p
        );
    }
}
```

## 4. Response Surface Designs

```java
public record CentralCompositeDesign(int factors) {
    public double[][] build() {
        int factorial = (int) Math.pow(2, factors);
        int axial = 2 * factors;
        int center = 1;
        int runs = factorial + axial + center;
        double[][] design = new double[runs][factors];
        // Factorial portion
        for (int i = 0; i < factorial; i++) {
            int tmp = i;
            for (int j = 0; j < factors; j++) {
                design[i][j] = (tmp % 2 == 0) ? -1.0 : 1.0;
                tmp /= 2;
            }
        }
        // Axial (star) points at distance α = (2^k)^(1/4)
        double alpha = Math.pow(Math.pow(2, factors), 0.25);
        for (int j = 0; j < factors; j++) {
            design[factorial + 2 * j][j] = alpha;
            design[factorial + 2 * j + 1][j] = -alpha;
        }
        // Center point
        for (int j = 0; j < factors; j++) {
            design[runs - 1][j] = 0.0;
        }
        return design;
    }
}
```
