package experiment;

import java.util.*;

public class ABTestExperiment {
    public static void main(String[] args) {
        System.out.println("=== A/B Test Experiment Analysis ===\n");
        
        double baselineRate = 0.10;
        double minimumDetectableEffect = 0.02;
        double alpha = 0.05;
        double power = 0.80;
        
        System.out.println("=== Design Phase ===");
        System.out.printf("Baseline conversion rate: %.1f%%%n", baselineRate * 100);
        System.out.printf("Minimum detectable effect: %.1f%%%n", minimumDetectableEffect * 100);
        
        double effectSize = Experiment.PowerAnalysis.calculateEffectSize(
            baselineRate + minimumDetectableEffect, baselineRate, Math.sqrt(baselineRate * (1 - baselineRate)));
        
        double nPerGroup = Experiment.PowerAnalysis.calculateSampleSize(effectSize, alpha, power);
        System.out.printf("Required sample size per group: %.0f%n", nPerGroup);
        System.out.printf("Total sample size: %.0f%n", 2 * nPerGroup);
        
        System.out.println("\n=== Analysis Phase ===");
        
        Random rand = new Random(42);
        int nControl = 2500;
        int nTreatment = 2500;
        
        int controlConversions = 0;
        int treatmentConversions = 0;
        
        for (int i = 0; i < nControl; i++) {
            if (rand.nextDouble() < baselineRate) controlConversions++;
        }
        
        for (int i = 0; i < nTreatment; i++) {
            if (rand.nextDouble() < baselineRate + minimumDetectableEffect) treatmentConversions++;
        }
        
        System.out.println("Control conversions: " + controlConversions + "/" + nControl);
        System.out.println("Treatment conversions: " + treatmentConversions + "/" + nTreatment);
        
        double pControl = (double) controlConversions / nControl;
        double pTreatment = (double) treatmentConversions / nTreatment;
        
        System.out.printf("Control rate: %.3f%%%n", pControl * 100);
        System.out.printf("Treatment rate: %.3f%%%n", pTreatment * 100);
        System.out.printf("Observed lift: %.2f%%%n", (pTreatment - pControl) * 100);
        
        System.out.println("\n=== Statistical Test ===");
        
        double pooledP = (controlConversions + treatmentConversions) / (double) (nControl + nTreatment);
        double se = Math.sqrt(pooledP * (1 - pooledP) * (1.0 / nControl + 1.0 / nTreatment));
        double z = (pTreatment - pControl) / se;
        
        System.out.printf("Z-statistic: %.4f%n", z);
        System.out.printf("P-value: %.6f%n", 2 * (1 - normalCDF(Math.abs(z))));
        
        boolean significant = 2 * (1 - normalCDF(Math.abs(z))) < 0.05;
        System.out.println("Significant at α=0.05: " + significant);
        
        System.out.println("\n=== Confidence Interval ===");
        double[] ci = Experiment.ConfidenceInterval.proportionCI(
            treatmentConversions - controlConversions, nControl + nTreatment, 0.95);
        System.out.printf("95%% CI for difference: [%.3f%%, %.3f%%]%n", ci[0] * 100, ci[1] * 100);
        
        System.out.println("\n=== Power Analysis (Retrospective) ===");
        double achievedPower = Experiment.PowerAnalysis.calculatePower(
            (pTreatment - pControl) / Math.sqrt(pooledP * (1 - pooledP)),
            alpha,
            nControl);
        System.out.printf("Achieved power: %.2f%%%n", achievedPower * 100);
        
        System.out.println("\n=== Recommendation ===");
        if (significant && (pTreatment - pControl) > 0) {
            System.out.println("✓ ROLL OUT new design");
            System.out.printf("Expected annual revenue increase: $%.2f%n", 
                (pTreatment - pControl) * 100000 * 50);
        } else {
            System.out.println("✗ KEEP current design");
            System.out.println("Insufficient evidence to support change");
        }
    }
    
    private static double normalCDF(double x) {
        return 0.5 * (1 + Experiment.class$java$util$Random != null ? 
            erf(x / Math.sqrt(2)) : 0.5);
    }
    
    private static double erf(double x) {
        double t = 1 / (1 + 0.5 * Math.abs(x));
        return 1 - t * Math.exp(-x * x - 1.26551223 + 
            t * (1.00002368 + t * (0.37409196 + t * (0.09678418 + 
            t * (-0.2023348 + t * (0.27839307 + t * (-0.1936502 + 
            t * (0.0858985 + t * (-0.01952729 + t * (0.00420042)))))))));
    }
}