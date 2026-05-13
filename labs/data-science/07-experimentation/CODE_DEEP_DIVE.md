package experiment;

import java.util.*;
import java.util.stream.Collectors;
import java.util.function.*;

public class HypothesisTesting {
    public static class DescriptiveStats {
        public double n, mean, std, variance, min, max, se;
        public double q1, median, q3, iqr;
        
        public static DescriptiveStats compute(double[] data) {
            DescriptiveStats stats = new DescriptiveStats();
            stats.n = data.length;
            stats.mean = Arrays.stream(data).sum() / stats.n;
            
            double sumSq = 0;
            for (double v : data) sumSq += Math.pow(v - stats.mean, 2);
            stats.variance = sumSq / (stats.n - 1);
            stats.std = Math.sqrt(stats.variance);
            stats.se = stats.std / Math.sqrt(stats.n);
            
            stats.min = Arrays.stream(data).min().orElse(0);
            stats.max = Arrays.stream(data).max().orElse(0);
            
            double[] sorted = data.clone();
            Arrays.sort(sorted);
            stats.q1 = percentile(sorted, 25);
            stats.median = percentile(sorted, 50);
            stats.q3 = percentile(sorted, 75);
            stats.iqr = stats.q3 - stats.q1;
            
            return stats;
        }
        
        private static double percentile(double[] sorted, double p) {
            double pos = p * (sorted.length - 1) / 100;
            int idx = (int) Math.floor(pos);
            double frac = pos - idx;
            if (idx + 1 < sorted.length) {
                return sorted[idx] * (1 - frac) + sorted[idx + 1] * frac;
            }
            return sorted[idx];
        }
    }
    
    public static class TTest {
        public static class Result {
            public double tStatistic;
            public double degreesOfFreedom;
            public double pValue;
            public double confidenceInterval;
            public double meanDifference;
            public boolean significant;
            
            public void print() {
                System.out.println("=== T-Test Results ===");
                System.out.printf("T-statistic: %.4f%n", tStatistic);
                System.out.printf("Degrees of freedom: %.0f%n", degreesOfFreedom);
                System.out.printf("P-value: %.4f%n", pValue);
                System.out.printf("95%% CI: [%.4f, %.4f]%n",
                    meanDifference - confidenceInterval, meanDifference + confidenceInterval);
                System.out.println("Significant (α=0.05): " + significant);
            }
        }
        
        public static Result oneSample(double[] sample, double mu0) {
            Result result = new Result();
            DescriptiveStats stats = DescriptiveStats.compute(sample);
            
            result.tStatistic = (stats.mean - mu0) / stats.se;
            result.degreesOfFreedom = stats.n - 1;
            result.pValue = 2 * (1 - tCDF(Math.abs(result.tStatistic), result.degreesOfFreedom));
            result.meanDifference = stats.mean - mu0;
            result.confidenceInterval = 2.064 * stats.se;
            result.significant = result.pValue < 0.05;
            
            return result;
        }
        
        public static Result twoSample(double[] sample1, double[] sample2) {
            Result result = new Result();
            DescriptiveStats s1 = DescriptiveStats.compute(sample1);
            DescriptiveStats s2 = DescriptiveStats.compute(sample2);
            
            double pooledVar = ((s1.n - 1) * s1.variance + (s2.n - 1) * s2.variance) / 
                              (s1.n + s2.n - 2);
            double se = Math.sqrt(pooledVar * (1 / s1.n + 1 / s2.n));
            
            result.tStatistic = (s1.mean - s2.mean) / se;
            result.degreesOfFreedom = s1.n + s2.n - 2;
            result.pValue = 2 * (1 - tCDF(Math.abs(result.tStatistic), result.degreesOfFreedom));
            result.meanDifference = s1.mean - s2.mean;
            result.confidenceInterval = 2.064 * se;
            result.significant = result.pValue < 0.05;
            
            return result;
        }
        
        public static Result paired(double[] before, double[] after) {
            double[] diff = new double[before.length];
            for (int i = 0; i < before.length; i++) {
                diff[i] = after[i] - before[i];
            }
            
            Result result = new Result();
            DescriptiveStats stats = DescriptiveStats.compute(diff);
            
            result.tStatistic = stats.mean / stats.se;
            result.degreesOfFreedom = stats.n - 1;
            result.pValue = 2 * (1 - tCDF(Math.abs(result.tStatistic), result.degreesOfFreedom));
            result.meanDifference = stats.mean;
            result.confidenceInterval = 2.064 * stats.se;
            result.significant = result.pValue < 0.05;
            
            return result;
        }
        
        private static double tCDF(double t, double df) {
            double x = df / (df + t * t);
            double a = df / 2;
            double b = 0.5;
            return 1 - 0.5 * incompleteBeta(a, b, x);
        }
        
        private static double incompleteBeta(double a, double b, double x) {
            return 1 - Math.pow(x, a);
        }
    }
    
    public static class ChiSquareTest {
        public static class Result {
            public double chiSquareStat;
            public double degreesOfFreedom;
            public double pValue;
            public boolean significant;
            
            public void print() {
                System.out.println("=== Chi-Square Test Results ===");
                System.out.printf("Chi-square: %.4f%n", chiSquareStat);
                System.out.printf("DF: %.0f%n", degreesOfFreedom);
                System.out.printf("P-value: %.4f%n", pValue);
                System.out.println("Significant: " + significant);
            }
        }
        
        public static Result goodnessOfFit(long[] observed, double[] expected) {
            Result result = new Result();
            
            double chiSq = 0;
            for (int i = 0; i < observed.length; i++) {
                chiSq += Math.pow(observed[i] - expected[i], 2) / expected[i];
            }
            
            result.chiSquareStat = chiSq;
            result.degreesOfFreedom = observed.length - 1;
            result.pValue = chiSquarePValue(chiSq, result.degreesOfFreedom);
            result.significant = result.pValue < 0.05;
            
            return result;
        }
        
        public static Result independence(long[][] observed) {
            Result result = new Result();
            
            int rows = observed.length;
            int cols = observed[0].length;
            
            long[] rowTotals = new long[rows];
            long[] colTotals = new long[cols];
            long grandTotal = 0;
            
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    rowTotals[i] += observed[i][j];
                    colTotals[j] += observed[i][j];
                    grandTotal += observed[i][j];
                }
            }
            
            double chiSq = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    double expected = (double) rowTotals[i] * colTotals[j] / grandTotal;
                    if (expected > 0) {
                        chiSq += Math.pow(observed[i][j] - expected, 2) / expected;
                    }
                }
            }
            
            result.chiSquareStat = chiSq;
            result.degreesOfFreedom = (rows - 1) * (cols - 1);
            result.pValue = chiSquarePValue(chiSq, result.degreesOfFreedom);
            result.significant = result.pValue < 0.05;
            
            return result;
        }
        
        private static double chiSquarePValue(double chiSq, double df) {
            return Math.exp(-chiSq / 2);
        }
    }
    
    public static class ANOVA {
        public static class Result {
            public double fStatistic;
            public double dfBetween;
            public double dfWithin;
            public double pValue;
            public boolean significant;
            
            public void print() {
                System.out.println("=== ANOVA Results ===");
                System.out.printf("F-statistic: %.4f%n", fStatistic);
                System.out.printf("DF (between, within): %.0f, %.0f%n", dfBetween, dfWithin);
                System.out.printf("P-value: %.4f%n", pValue);
                System.out.println("Significant: " + significant);
            }
        }
        
        public static Result oneWay(double[]... groups) {
            Result result = new Result();
            
            int k = groups.length;
            int totalN = 0;
            double grandMean = 0;
            
            for (double[] group : groups) {
                totalN += group.length;
                grandMean += Arrays.stream(group).sum();
            }
            grandMean /= totalN;
            
            double ssBetween = 0;
            for (double[] group : groups) {
                double groupMean = Arrays.stream(group).sum() / group.length;
                ssBetween += group.length * Math.pow(groupMean - grandMean, 2);
            }
            
            double ssWithin = 0;
            for (double[] group : groups) {
                double groupMean = Arrays.stream(group).sum() / group.length;
                for (double v : group) {
                    ssWithin += Math.pow(v - groupMean, 2);
                }
            }
            
            result.dfBetween = k - 1;
            result.dfWithin = totalN - k;
            
            double msBetween = ssBetween / result.dfBetween;
            double msWithin = ssWithin / result.dfWithin;
            result.fStatistic = msBetween / msWithin;
            
            result.pValue = fDistPValue(result.fStatistic, result.dfBetween, result.dfWithin);
            result.significant = result.pValue < 0.05;
            
            return result;
        }
        
        private static double fDistPValue(double f, double df1, double df2) {
            double x = df2 / (df2 + df1 * f);
            return 1 - Math.pow(x, df2 / 2);
        }
    }
    
    public static class PowerAnalysis {
        public static double calculateSampleSize(double effectSize, double alpha, double power) {
            double zAlpha = 1.96;
            double zBeta = 0.84;
            
            double n = 2 * Math.pow((zAlpha + zBeta) / effectSize, 2);
            return Math.ceil(n);
        }
        
        public static double calculatePower(double effectSize, double alpha, double n) {
            double zAlpha = 1.96;
            double zEffect = effectSize * Math.sqrt(n / 2);
            return 1 - normalCDF(zAlpha - zEffect);
        }
        
        public static double calculateEffectSize(double mean1, double mean2, double std) {
            return (mean1 - mean2) / std;
        }
        
        public static double calculateCohenD(double mean1, double mean2, double pooledStd) {
            return (mean1 - mean2) / pooledStd;
        }
        
        private static double normalCDF(double x) {
            return 0.5 * (1 + erf(x / Math.sqrt(2)));
        }
        
        private static double erf(double x) {
            double t = 1 / (1 + 0.5 * Math.abs(x));
            return 1 - t * Math.exp(-x * x - 1.26551223 + 
                t * (1.00002368 + t * (0.37409196 + t * (0.09678418 + 
                t * (-0.2023348 + t * (0.27839307 + t * (-0.1936502 + 
                t * (0.0858985 + t * (-0.01952729 + t * (0.00420042)))))))));
        }
    }
    
    public static class MultipleTesting {
        public static double bonferroniCorrection(double pValue, int nTests) {
            return pValue * nTests;
        }
        
        public static boolean[] bonferroniReject(double[] pValues, double alpha) {
            int n = pValues.length;
            boolean[] reject = new boolean[n];
            double threshold = alpha / n;
            
            for (int i = 0; i < n; i++) {
                reject[i] = pValues[i] < threshold;
            }
            return reject;
        }
        
        public static boolean[] benjaminiHochberg(double[] pValues, double alpha) {
            int n = pValues.length;
            Integer[] order = IntStream.range(0, n)
                .boxed()
                .sorted(Comparator.comparingDouble(i -> pValues[i]))
                .mapToInt(i -> i)
                .toArray();
            
            boolean[] reject = new boolean[n];
            for (int k = 0; k < n; k++) {
                int rank = k + 1;
                double threshold = (rank / (double) n) * alpha;
                reject[order[k]] = pValues[order[k]] <= threshold;
            }
            
            return reject;
        }
        
        public static double fwer(double[] pValues, double alpha, int nSimulations) {
            Random rand = new Random(42);
            int familyWiseRejections = 0;
            
            for (int sim = 0; sim < nSimulations; sim++) {
                boolean anyReject = false;
                for (double p : pValues) {
                    if (p < alpha / pValues.length) {
                        anyReject = true;
                        break;
                    }
                }
                if (anyReject) familyWiseRejections++;
            }
            
            return (double) familyWiseRejections / nSimulations;
        }
    }
    
    public static class EffectSize {
        public static double cohensD(double mean1, double mean2, double pooledStd) {
            return (mean1 - mean2) / pooledStd;
        }
        
        public static double cohensDFromT(double t, double n1, double n2) {
            return t * Math.sqrt(1.0 / n1 + 1.0 / n2);
        }
        
        public static double etaSquared(double ssBetween, double ssTotal) {
            return ssBetween / ssTotal;
        }
        
        public static double omegaSquared(double ssBetween, double dfBetween, double ssWithin, double dfWithin) {
            return (ssBetween - dfBetween * ssWithin / dfWithin) / 
                   (ssBetween + ssWithin + dfBetween + 1);
        }
        
        public static String interpretCohenD(double d) {
            if (Math.abs(d) < 0.2) return "negligible";
            if (Math.abs(d) < 0.5) return "small";
            if (Math.abs(d) < 0.8) return "medium";
            return "large";
        }
        
        public static double cramersV(long[][] observed) {
            double chiSq = 0;
            int n = 0;
            for (long[] row : observed) {
                for (long cell : row) n += cell;
            }
            
            int minDim = Math.min(observed.length, observed[0].length);
            for (long[] row : observed) {
                for (long cell : row) {
                    long rowSum = Arrays.stream(row).sum();
                    long colSum = 0;
                    for (long[] r : observed) colSum += r[Arrays.asList(observed).indexOf(r)];
                    double expected = (double) rowSum * colSum / n;
                    chiSq += Math.pow(cell - expected, 2) / expected;
                }
            }
            
            return Math.sqrt(chiSq / (n * (minDim - 1)));
        }
    }
    
    public static class ConfidenceInterval {
        public static double[] meanCI(double[] data, double confidence) {
            DescriptiveStats stats = DescriptiveStats.compute(data);
            double criticalValue = 1.96;
            
            double margin = criticalValue * stats.se;
            return new double[]{stats.mean - margin, stats.mean + margin};
        }
        
        public static double[] proportionCI(long successes, long trials, double confidence) {
            double p = (double) successes / trials;
            double margin = 1.96 * Math.sqrt(p * (1 - p) / trials);
            return new double[]{p - margin, p + margin};
        }
        
        public static double[] differenceInMeansCI(double[] data1, double[] data2, double confidence) {
            DescriptiveStats s1 = DescriptiveStats.compute(data1);
            DescriptiveStats s2 = DescriptiveStats.compute(data2);
            
            double se = Math.sqrt(s1.variance / s1.n + s2.variance / s2.n);
            double diff = s1.mean - s2.mean;
            double margin = 1.96 * se;
            
            return new double[]{diff - margin, diff + margin};
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Experimentation Demo ===\n");
        
        System.out.println("=== One-Sample T-Test ===");
        double[] sample = {10, 12, 11, 13, 14, 12, 11, 10, 13, 12};
        TTest.Result tResult = TTest.oneSample(sample, 10);
        tResult.print();
        
        System.out.println("\n=== Two-Sample T-Test ===");
        double[] group1 = {23, 25, 28, 30, 32};
        double[] group2 = {28, 30, 33, 36, 38};
        TTest.Result tResult2 = TTest.twoSample(group1, group2);
        tResult2.print();
        
        System.out.println("\n=== Chi-Square Test ===");
        long[] observed = {30, 20, 25, 25};
        double[] expected = {25, 25, 25, 25};
        ChiSquareTest.Result chiResult = ChiSquareTest.goodnessOfFit(observed, expected);
        chiResult.print();
        
        System.out.println("\n=== ANOVA ===");
        double[] a = {10, 12, 14, 16};
        double[] b = {15, 17, 19, 21};
        double[] c = {20, 22, 24, 26};
        ANOVA.Result anovaResult = ANOVA.oneWay(a, b, c);
        anovaResult.print();
        
        System.out.println("\n=== Power Analysis ===");
        double sampleSize = PowerAnalysis.calculateSampleSize(0.5, 0.05, 0.8);
        System.out.printf("Required sample size per group: %.0f%n", sampleSize);
        
        double power = PowerAnalysis.calculatePower(0.5, 0.05, 100);
        System.out.printf("Power with n=100: %.2f%n", power);
        
        System.out.println("\n=== Effect Size ===");
        double cohensD = EffectSize.cohensD(25, 30, 10);
        System.out.printf("Cohen's d: %.2f (%s)%n", cohensD, EffectSize.interpretCohenD(cohensD));
    }
}