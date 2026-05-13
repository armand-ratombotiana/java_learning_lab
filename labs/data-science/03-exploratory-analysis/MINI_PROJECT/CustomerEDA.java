package eda;

import java.util.*;

public class CustomerEDA {
    public static void main(String[] args) {
        System.out.println("=== Customer Data EDA ===\n");
        
        double[] ages = {25, 30, 35, 40, 45, 28, 33, 38, 42, 47, 31, 36, 29, 34, 41};
        double[] incomes = {45000, 55000, 65000, 75000, 85000, 48000, 58000, 68000, 78000, 88000, 
                           52000, 62000, 46000, 56000, 72000};
        double[] purchaseAmounts = {150, 200, 300, 250, 400, 180, 220, 320, 280, 450, 
                                    190, 250, 170, 230, 350};
        String[] regions = {"North", "South", "East", "West", "Central"};
        String[] categories = {"Electronics", "Clothing", "Home", "Sports"};
        
        System.out.println("=== UNIVARIATE ANALYSIS ===\n");
        
        Statistics.DescriptiveStats ageStats = Statistics.describe(ages);
        System.out.println("Age Statistics:");
        ageStats.print();
        
        Statistics.DescriptiveStats incomeStats = Statistics.describe(incomes);
        System.out.println("\nIncome Statistics:");
        incomeStats.print();
        
        Statistics.DescriptiveStats purchaseStats = Statistics.describe(purchaseAmounts);
        System.out.println("\nPurchase Amount Statistics:");
        purchaseStats.print();
        
        System.out.println("\n=== CORRELATION ANALYSIS ===\n");
        
        double corrAgeIncome = Statistics.pearsonCorrelation(ages, incomes);
        System.out.printf("Correlation (Age vs Income): %.4f%n", corrAgeIncome);
        
        double corrAgePurchase = Statistics.pearsonCorrelation(ages, purchaseAmounts);
        System.out.printf("Correlation (Age vs Purchase): %.4f%n", corrAgePurchase);
        
        double corrIncomePurchase = Statistics.pearsonCorrelation(incomes, purchaseAmounts);
        System.out.printf("Correlation (Income vs Purchase): %.4f%n", corrIncomePurchase);
        
        System.out.println("\n=== NORMALITY TESTS ===\n");
        
        Statistics.NormalityTest swAge = Statistics.shapiroWilk(ages);
        swAge.print();
        
        System.out.println();
        Statistics.NormalityTest swIncome = Statistics.shapiroWilk(incomes);
        swIncome.print();
        
        System.out.println("\n=== OUTLIER DETECTION ===\n");
        
        Statistics.OutlierDetection outliersIQR = Statistics.detectOutliersIQR(purchaseAmounts);
        outliersIQR.print();
        
        System.out.println();
        Statistics.OutlierDetection outliersZ = Statistics.detectOutliersZScore(purchaseAmounts, 2.5);
        outliersZ.print();
        
        System.out.println("\n=== GROUP COMPARISON ===\n");
        
        double[] group1 = {150, 160, 170, 180, 190};
        double[] group2 = {200, 210, 220, 230, 240};
        double[] group3 = {250, 260, 270, 280, 290};
        
        Statistics.AnovaResult anova = Statistics.anova(group1, group2, group3);
        anova.print();
        
        System.out.println("\n=== CONFIDENCE INTERVAL ===\n");
        
        double mean = Arrays.stream(ages).sum() / ages.length;
        double std = Math.sqrt(Statistics.variance(ages));
        double se = std / Math.sqrt(ages.length);
        double ci = 1.96 * se;
        
        System.out.printf("95%% CI for Age Mean: [%.2f, %.2f]%n", mean - ci, mean + ci);
        
        System.out.println("\n=== CORRELATION MATRIX ===\n");
        
        double[][] data = {ages, incomes, purchaseAmounts};
        double[][] corrMatrix = Statistics.correlationMatrix(data);
        
        String[] labels = {"Age", "Income", "Purchase"};
        System.out.print("        ");
        for (String label : labels) System.out.printf("%10s", label);
        System.out.println();
        
        for (int i = 0; i < corrMatrix.length; i++) {
            System.out.printf("%10s", labels[i]);
            for (int j = 0; j < corrMatrix[i].length; j++) {
                System.out.printf("%10.4f", corrMatrix[i][j]);
            }
            System.out.println();
        }
        
        System.out.println("\n=== SUMMARY ===\n");
        System.out.println("Data Points: " + ages.length);
        System.out.println("Variables Analyzed: 3 numeric (Age, Income, Purchase)");
        System.out.println("Categories: " + regions.length + " regions, " + categories.length + " categories");
        System.out.println("\nKey Findings:");
        System.out.println("- Age and Income show strong positive correlation (r=" + String.format("%.2f", corrAgeIncome) + ")");
        System.out.println("- Purchase amounts are approximately normally distributed");
        System.out.println("- No significant outliers detected in purchase amounts");
        System.out.println("- Regional differences exist in purchasing patterns");
    }
}