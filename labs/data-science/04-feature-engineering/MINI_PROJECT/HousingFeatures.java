package feature;

import java.util.*;

public class HousingFeatureEngineering {
    public static void main(String[] args) {
        System.out.println("=== Housing Feature Engineering Pipeline ===\n");
        
        String[] neighborhoods = {"A", "A", "B", "B", "C", "C", "A", "B", "C", "A"};
        double[] sqft = {1500, 1800, 2000, 1600, 2200, 2400, 1700, 1900, 2100, 1550};
        double[] bedrooms = {3, 3, 4, 3, 4, 5, 3, 4, 4, 3};
        double[] bathrooms = {2, 2.5, 2, 2, 3, 3.5, 2, 2.5, 3, 2};
        double[] yearBuilt = {1990, 1985, 2000, 1995, 2010, 2015, 1988, 1998, 2005, 1992};
        double[] lotSize = {8000, 10000, 12000, 8500, 15000, 18000, 9000, 11000, 14000, 8200};
        double[] salePrice = {250000, 320000, 380000, 280000, 450000, 520000, 295000, 350000, 410000, 265000};
        
        System.out.println("=== Step 1: Numerical Feature Scaling ===\n");
        
        double[] sqftScaled = FeatureEngineering.Scaler.standardize(sqft);
        System.out.println("Square footage (standardized):");
        for (int i = 0; i < sqft.length; i++) {
            System.out.printf("  House %d: %.2f (original: %.0f)%n", i + 1, sqftScaled[i], sqft[i]);
        }
        
        double[] sqftMinMax = FeatureEngineering.Scaler.minMaxScale(sqft);
        System.out.println("\nSquare footage (min-max scaled to [0,1]):");
        for (int i = 0; i < sqft.length; i++) {
            System.out.printf("  House %d: %.3f%n", i + 1, sqftMinMax[i]);
        }
        
        System.out.println("\n=== Step 2: Polynomial Features ===\n");
        
        double[] sqftSquared = FeatureEngineering.PolynomialFeatures.createPolynomialFeatures(sqft, 2)[0];
        System.out.println("Square footage squared feature:");
        for (int i = 0; i < sqft.length; i++) {
            System.out.printf("  House %d: sqft^2 = %.0f%n", i + 1, sqftSquared[i]);
        }
        
        System.out.println("\n=== Step 3: Interaction Features ===\n");
        
        double[][] interactions = FeatureEngineering.PolynomialFeatures.createInteractionFeatures(bedrooms, bathrooms);
        System.out.println("Bedroom x Bathroom interaction:");
        for (int i = 0; i < interactions.length; i++) {
            System.out.printf("  House %d: beds * baths = %.1f%n", i + 1, interactions[i][3]);
        }
        
        System.out.println("\n=== Step 4: Categorical Encoding ===\n");
        
        System.out.println("Label encoding for neighborhoods:");
        int[] neighborhoodEncoded = FeatureEngineering.Encoder.labelEncode(neighborhoods);
        for (int i = 0; i < neighborhoods.length; i++) {
            System.out.printf("  %s -> %d%n", neighborhoods[i], neighborhoodEncoded[i]);
        }
        
        System.out.println("\nOne-hot encoding for neighborhoods:");
        double[][] oneHot = FeatureEngineering.Encoder.oneHotEncode(neighborhoods);
        String[] categories = Arrays.stream(neighborhoods).distinct().toArray(String[]::new);
        System.out.print("         ");
        for (String cat : categories) System.out.printf("%-8s", cat);
        System.out.println();
        for (int i = 0; i < oneHot.length; i++) {
            System.out.printf("House %d:  ", i + 1);
            for (double v : oneHot[i]) System.out.printf("%-8.0f", v);
            System.out.println();
        }
        
        System.out.println("\nTarget encoding for neighborhoods:");
        double[] neighborhoodTarget = FeatureEngineering.Encoder.targetEncode(neighborhoods, salePrice);
        Map<String, Double> targetByNeighborhood = new LinkedHashMap<>();
        for (int i = 0; i < neighborhoods.length; i++) {
            targetByNeighborhood.merge(neighborhoods[i], salePrice[i], (a, b) -> (a + b) / 2);
        }
        System.out.println("Neighborhood target means:");
        targetByNeighborhood.forEach((k, v) -> System.out.printf("  %s: $%.0f%n", k, v));
        
        System.out.println("\n=== Step 5: Date Features ===\n");
        
        java.time.LocalDate[] saleDates = {
            java.time.LocalDate.of(2024, 3, 15),
            java.time.LocalDate.of(2024, 5, 22),
            java.time.LocalDate.of(2024, 7, 8),
            java.time.LocalDate.of(2024, 9, 30),
            java.time.LocalDate.of(2024, 11, 12),
            java.time.LocalDate.of(2024, 1, 25),
            java.time.LocalDate.of(2024, 4, 18),
            java.time.LocalDate.of(2024, 6, 5),
            java.time.LocalDate.of(2024, 8, 28),
            java.time.LocalDate.of(2024, 10, 10)
        };
        
        System.out.println("Date feature extraction:");
        for (int i = 0; i < saleDates.length; i++) {
            double[] features = FeatureEngineering.DateTimeFeatures.extractFeatures(saleDates[i]);
            System.out.printf("  House %d: Year=%d, Month=%d, Day=%d, DayOfWeek=%d, IsWeekend=%d%n",
                i + 1, (int) features[0], (int) features[1], (int) features[2], (int) features[3], (int) features[5]);
        }
        
        System.out.println("\nHouse age calculation (from year built):");
        java.time.LocalDate referenceDate = java.time.LocalDate.of(2024, 12, 31);
        for (int i = 0; i < yearBuilt.length; i++) {
            double age = referenceDate.getYear() - yearBuilt[i];
            System.out.printf("  House %d: Age = %.0f years%n", i + 1, age);
        }
        
        System.out.println("\n=== Step 6: Binning ===\n");
        
        System.out.println("Price binning (quartiles):");
        int[] priceBins = FeatureEngineering.Binner.equalFrequencyBins(salePrice, 4);
        String[] priceLabels = {"Low", "Medium-Low", "Medium-High", "High"};
        for (int i = 0; i < salePrice.length; i++) {
            System.out.printf("  House %d: Price=$%.0f -> Bin %d (%s)%n",
                i + 1, salePrice[i], priceBins[i], priceLabels[priceBins[i]]);
        }
        
        System.out.println("\nSqft binning (equal width):");
        int[] sqftBins = FeatureEngineering.Binner.equalWidthBins(sqft, 3);
        String[] sqftLabels = {"Small", "Medium", "Large"};
        for (int i = 0; i < sqft.length; i++) {
            System.out.printf("  House %d: Sqft=%.0f -> Bin %d (%s)%n",
                i + 1, sqft[i], sqftBins[i], sqftLabels[sqftBins[i]]);
        }
        
        System.out.println("\n=== Step 7: Feature Correlation ===\n");
        
        double[] sqftLog = FeatureEngineering.Scaler.logTransform(sqft);
        double corrSqftPrice = correlation(sqft, salePrice);
        double corrSqftLogPrice = correlation(sqftLog, salePrice);
        System.out.printf("Correlation (sqft vs price): %.4f%n", corrSqftPrice);
        System.out.printf("Correlation (log(sqft) vs price): %.4f%n", corrSqftLogPrice);
        
        System.out.println("\n=== Summary ===\n");
        System.out.println("Features engineered:");
        System.out.println("  - Standardized numerical features");
        System.out.println("  - Min-Max scaled features");
        System.out.println("  - Polynomial features (squared)");
        System.out.println("  - Interaction features (beds * baths)");
        System.out.println("  - Label encoding for categorical");
        System.out.println("  - One-hot encoding for low cardinality");
        System.out.println("  - Target encoding with smoothing");
        System.out.println("  - Date features (year, month, day, weekend)");
        System.out.println("  - House age feature");
        System.out.println("  - Binned features for price and sqft");
        System.out.println("\nTotal features created: " + (sqft.length + 1 + 1 + 7 + 1 + 4 + 3));
    }
    
    private static double correlation(double[] x, double[] y) {
        int n = Math.min(x.length, y.length);
        double meanX = Arrays.stream(x).sum() / n;
        double meanY = Arrays.stream(y).sum() / n;
        
        double sumXY = 0, sumX2 = 0, sumY2 = 0;
        for (int i = 0; i < n; i++) {
            double dx = x[i] - meanX;
            double dy = y[i] - meanY;
            sumXY += dx * dy;
            sumX2 += dx * dx;
            sumY2 += dy * dy;
        }
        
        return sumXY / Math.sqrt(sumX2 * sumY2);
    }
}