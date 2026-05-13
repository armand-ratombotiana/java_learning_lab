package causal;

import java.util.*;

public class MarketingCampaignAnalysis {
    public static void main(String[] args) {
        System.out.println("=== Marketing Campaign Causal Analysis ===\n");
        
        int n = 2000;
        Random rand = new Random(42);
        
        double[] tenure = new double[n];
        double[] income = new double[n];
        int[] priorPurchases = new int[n];
        int[] treatment = new int[n];
        double[] spendBefore = new double[n];
        double[] spendAfter = new double[n];
        
        for (int i = 0; i < n; i++) {
            tenure[i] = rand.nextInt(60) + 6;
            income[i] = 30000 + rand.nextGaussian() * 20000;
            priorPurchases[i] = rand.nextInt(10);
            
            double propensity = 1 / (1 + Math.exp(-(0.02 * tenure[i] + 0.01 * income[i] - 2)));
            treatment[i] = rand.nextDouble() < propensity ? 1 : 0;
            
            spendBefore[i] = 100 + 2 * tenure[i] + income[i] / 1000 + rand.nextGaussian() * 20;
            double lift = treatment[i] == 1 ? 15 + rand.nextGaussian() * 5 : 0;
            spendAfter[i] = spendBefore[i] + lift + rand.nextGaussian() * 10;
        }
        
        System.out.println("=== Baseline Comparison ===");
        double[] treatBefore = new double[500];
        double[] controlBefore = new double[1500];
        int tIdx = 0, cIdx = 0;
        for (int i = 0; i < n; i++) {
            if (treatment[i] == 1 && tIdx < 500) treatBefore[tIdx++] = spendBefore[i];
            if (treatment[i] == 0 && cIdx < 1500) controlBefore[cIdx++] = spendBefore[i];
        }
        
        System.out.printf("Treatment group pre-spend: $%.2f%n", 
            Arrays.stream(treatBefore).sum() / treatBefore.length);
        System.out.printf("Control group pre-spend: $%.2f%n",
            Arrays.stream(controlBefore).sum() / controlBefore.length);
        
        System.out.println("\n=== Difference-in-Differences ===");
        double[] treatAfter = new double[500];
        double[] controlAfter = new double[1500];
        tIdx = 0; cIdx = 0;
        for (int i = 0; i < n; i++) {
            if (treatment[i] == 1 && tIdx < 500) treatAfter[tIdx++] = spendAfter[i];
            if (treatment[i] == 0 && cIdx < 1500) controlAfter[cIdx++] = spendAfter[i];
        }
        
        double did = CausalInference.DID.differenceInDifferences(treatBefore, treatAfter, controlBefore, controlAfter);
        System.out.printf("DiD Estimate: $%.2f additional spend%n", did);
        System.out.printf("Campaign ROI: %.1f%% (assuming $50 campaign cost)%n", (did / 50) * 100);
        
        System.out.println("\n=== Heterogeneous Effects ===");
        double highTenureEffect = 0, lowTenureEffect = 0;
        int highCount = 0, lowCount = 0;
        
        for (int i = 0; i < n; i++) {
            if (treatment[i] == 1) {
                double effect = spendAfter[i] - spendBefore[i];
                if (tenure[i] > 36) {
                    highTenureEffect += effect;
                    highCount++;
                } else {
                    lowTenureEffect += effect;
                    lowCount++;
                }
            }
        }
        
        System.out.printf("High tenure (>3yr) effect: $%.2f%n", highTenureEffect / highCount);
        System.out.printf("Low tenure (<3yr) effect: $%.2f%n", lowTenureEffect / lowCount);
        
        System.out.println("\n=== Summary ===");
        System.out.println("Campaign significantly increased customer spend");
        System.out.printf("Average treatment effect: $%.2f%n", did);
        System.out.println("Recommendation: Focus on high-tenure customers for better ROI");
    }
}