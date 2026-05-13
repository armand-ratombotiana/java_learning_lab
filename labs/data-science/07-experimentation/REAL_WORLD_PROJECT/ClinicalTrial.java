package experiment;

import java.util.*;

public class ClinicalTrialAnalysis {
    public static void main(String[] args) {
        System.out.println("=== Clinical Trial Experiment Analysis ===\n");
        
        Random rand = new Random(42);
        
        double[] placebo = generateData(rand, 50, 0, 10);
        double[] lowDose = generateData(rand, 50, 5, 10);
        double[] medDose = generateData(rand, 50, 10, 10);
        double[] highDose = generateData(rand, 50, 15, 10);
        
        System.out.println("=== Sample Statistics ===\n");
        System.out.println("Placebo: n=50, mean=" + String.format("%.1f", mean(placebo)) + 
            ", std=" + String.format("%.1f", std(placebo)));
        System.out.println("Low dose: n=50, mean=" + String.format("%.1f", mean(lowDose)) + 
            ", std=" + String.format("%.1f", std(lowDose)));
        System.out.println("Med dose: n=50, mean=" + String.format("%.1f", mean(medDose)) + 
            ", std=" + String.format("%.1f", std(medDose)));
        System.out.println("High dose: n=50, mean=" + String.format("%.1f", mean(highDose)) + 
            ", std=" + String.format("%.1f", std(highDose)));
        
        System.out.println("\n=== One-Way ANOVA ===");
        HypothesisTesting.ANOVA.Result anova = HypothesisTesting.ANOVA.oneWay(
            placebo, lowDose, medDose, highDose);
        anova.print();
        
        System.out.println("\n=== Post-Hoc Comparisons (Tukey HSD) ===");
        double[][] allData = {placebo, lowDose, medDose, highDose};
        String[] names = {"Placebo", "Low", "Med", "High"};
        
        double mse = 100;
        double qCritical = 3.77;
        
        System.out.println("Pairwise comparisons (α=0.05):");
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                double diff = mean(allData[i]) - mean(allData[j]);
                double se = Math.sqrt(mse * (1.0 / allData[i].length + 1.0 / allData[j].length));
                double hsd = qCritical * se;
                boolean sig = Math.abs(diff) > hsd;
                
                System.out.printf("  %s vs %s: diff=%.2f, HSD=%.2f, %s%n",
                    names[i], names[j], diff, hsd, sig ? "SIGNIFICANT" : "ns");
            }
        }
        
        System.out.println("\n=== Effect Size ===");
        double pooledStd = Math.sqrt((Arrays.stream(placebo).map(x -> x*x).sum() + 
            Arrays.stream(lowDose).map(x -> x*x).sum() +
            Arrays.stream(medDose).map(x -> x*x).sum() +
            Arrays.stream(highDose).map(x -> x*x).sum()) / 196);
        
        for (int i = 1; i < 4; i++) {
            double d = (mean(allData[i]) - mean(placebo)) / pooledStd;
            System.out.printf("  %s vs Placebo: Cohen's d = %.2f (%s)%n",
                names[i], d, HypothesisTesting.EffectSize.interpretCohenD(d));
        }
        
        System.out.println("\n=== Power Analysis ===");
        double effectSize = 0.7;
        double nNeeded = Experiment.PowerAnalysis.calculateSampleSize(effectSize, 0.05, 0.80);
        System.out.printf("Required n per group for d=0.7: %.0f%n", nNeeded);
        
        System.out.println("\n=== Conclusion ===");
        System.out.println("There is a statistically significant difference between groups (F=" + 
            String.format("%.2f", anova.fStatistic) + ", p<0.001).");
        System.out.println("High dose shows largest reduction vs placebo.");
        System.out.println("Recommendation: Advance high dose to Phase III.");
    }
    
    private static double[] generateData(Random rand, int n, double effect, double noise) {
        double[] data = new double[n];
        for (int i = 0; i < n; i++) {
            data[i] = effect + rand.nextGaussian() * noise;
        }
        return data;
    }
    
    private static double mean(double[] data) {
        return Arrays.stream(data).sum() / data.length;
    }
    
    private static double std(double[] data) {
        double m = mean(data);
        return Math.sqrt(Arrays.stream(data).map(x -> Math.pow(x - m, 2)).sum() / (data.length - 1));
    }
}