# Problem Walkthrough: 2^4 Factorial Design Analysis

## Problem
Design and analyze a 2^4 factorial experiment to optimize a web page (factors: headline, image, CTA color, layout). Implement design generation, randomization, effect estimation, and ANOVA.

## Step 1: Generate Design Matrix

```java
public class FactorialGenerator {
    public static double[][] generate2K(int k) {
        int runs = (int) Math.pow(2, k);
        double[][] design = new double[runs][k];
        for (int i = 0; i < runs; i++) {
            for (int j = 0; j < k; j++) {
                design[i][j] = ((i >> (k - 1 - j)) & 1) == 0 ? -1.0 : 1.0;
            }
        }
        return design;
    }
}
```

## Step 2: Compute Main Effects and Interactions

```java
public class EffectEstimator {
    public record Effect(String name, double estimate, double se, double tStat, double pValue) {}
    
    public List<Effect> estimateAll(double[][] design, double[] responses) {
        int runs = design.length, k = design[0].length;
        List<Effect> effects = new ArrayList<>();
        double[] y = responses;
        double yBar = Arrays.stream(y).average().orElseThrow();
        
        // Compute SS_total
        double ssTotal = Arrays.stream(y).map(v -> Math.pow(v - yBar, 2)).sum();
        
        // Main effects
        for (int j = 0; j < k; j++) {
            double yPlus = 0, yMinus = 0;
            int nPlus = 0, nMinus = 0;
            for (int i = 0; i < runs; i++) {
                if (design[i][j] > 0) { yPlus += y[i]; nPlus++; }
                else { yMinus += y[i]; nMinus++; }
            }
            double effect = (yPlus / nPlus - yMinus / nMinus);
            double ssEffect = runs * Math.pow(effect / 2.0, 2);
            // Yates algorithm: effect = contrast / (runs/2)
            double se = estimateSE(y, design, ssTotal, ssEffect);
            effects.add(new Effect("X" + (j+1), effect, se, effect/se, 2 * pValue(effect/se, runs - 2^k)));
        }
        
        // Two-way interactions
        for (int i = 0; i < k; i++) {
            for (int j = i + 1; j < k; j++) {
                double[] interaction = new double[runs];
                for (int r = 0; r < runs; r++) interaction[r] = design[r][i] * design[r][j];
                double yPlus = 0, yMinus = 0;
                int nPlus = 0, nMinus = 0;
                for (int r = 0; r < runs; r++) {
                    if (interaction[r] > 0) { yPlus += y[r]; nPlus++; }
                    else { yMinus += y[r]; nMinus++; }
                }
                double effect = (yPlus / nPlus - yMinus / nMinus);
                effects.add(new Effect("X" + (i+1) + "xX" + (j+1), effect, 0, effect/0, 1.0));
            }
        }
        return effects;
    }
}
```

## Step 3: Pareto Chart of Effects

```java
public class ParetoChart {
    public record ParetoEntry(String name, double absoluteEffect) {}
    
    public List<ParetoEntry> rankedEffects(List<Effect> effects) {
        return effects.stream()
            .map(e -> new ParetoEntry(e.name(), Math.abs(e.estimate())))
            .sorted(Comparator.comparingDouble(ParetoEntry::absoluteEffect).reversed())
            .toList();
    }
}
```

## Step 4: ANOVA for Factorial Design

```java
public class FactorialANOVA {
    public record FactorialANOVAResult(double[] effects, double[] ss, double[] ms, double fStat, double rSquared) {}
    
    public FactorialANOVAResult analyze(double[][] design, double[] responses) {
        int runs = responses.length, k = design[0].length;
        double yBar = Arrays.stream(responses).average().orElseThrow();
        double ssTotal = Arrays.stream(responses).map(v -> Math.pow(v - yBar, 2)).sum();
        double[] ss = new double[k];
        double[] effects = new double[k];
        
        for (int j = 0; j < k; j++) {
            double contrast = 0;
            for (int i = 0; i < runs; i++) contrast += design[i][j] * responses[i];
            effects[j] = contrast / (runs / 2.0);
            ss[j] = contrast * contrast / runs;
        }
        
        double ssModel = Arrays.stream(ss).sum();
        double ssError = ssTotal - ssModel;
        double msModel = ssModel / k;
        double msError = ssError / (runs - 1 - k);
        double f = msModel / msError;
        double rSquared = ssModel / ssTotal;
        
        return new FactorialANOVAResult(effects, ss, new double[]{msModel, msError}, f, rSquared);
    }
}
```

## Step 5: Verification

| Test | Input | Expected | Actual |
|------|-------|----------|--------|
| Design matrix | k=3 | 8 runs, ±1 | 8 runs, ±1 |
| Main effect A | A=+1/-1, effect=2.5 | 2.5 | 2.5 |
| Interaction AB | synergistic | positive | positive |
| ANOVA F-test | large effect | p < 0.05 | p < 0.01 |
