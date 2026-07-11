# Data Wrangling Code Deep Dive

This lab provides a pure Java implementation of a data preprocessing pipeline, demonstrating how to handle missing values, normalize data, and encode categories without relying on external libraries like Pandas or Scikit-Learn.

## 💻 Pure Java Implementation

```java file="labs/data-science/01-data-wrangling/SOLUTION/DataPipeline.java"
package datascience.wrangling;

import java.util.Arrays;

/**
 * A fundamental implementation of data wrangling techniques.
 */
public class DataPipeline {

    /**
     * Imputes (fills) missing values (represented as Double.NaN) with the mean of the column.
     */
    public static void imputeMean(double[] column) {
        double sum = 0;
        int count = 0;
        
        // First pass: calculate mean of valid data
        for (double val : column) {
            if (!Double.isNaN(val)) {
                sum += val;
                count++;
            }
        }
        
        if (count == 0) return; // Cannot impute if all are NaN
        double mean = sum / count;
        
        // Second pass: replace NaNs with mean
        for (int i = 0; i < column.length; i++) {
            if (Double.isNaN(column[i])) {
                column[i] = mean;
            }
        }
    }

    /**
     * Applies Z-Score Standardization to a column in-place.
     */
    public static void standardize(double[] column) {
        double sum = 0;
        for (double val : column) {
            sum += val;
        }
        double mean = sum / column.length;
        
        double varianceSum = 0;
        for (double val : column) {
            varianceSum += Math.pow(val - mean, 2);
        }
        double stdDev = Math.sqrt(varianceSum / column.length);
        
        if (stdDev == 0) return; // Prevent division by zero if all values are identical
        
        for (int i = 0; i < column.length; i++) {
            column[i] = (column[i] - mean) / stdDev;
        }
    }

    /**
     * Applies Min-Max Normalization to a column in-place.
     */
    public static void normalize(double[] column) {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        
        for (double val : column) {
            if (val < min) min = val;
            if (val > max) max = val;
        }
        
        double range = max - min;
        if (range == 0) return;
        
        for (int i = 0; i < column.length; i++) {
            column[i] = (column[i] - min) / range;
        }
    }

    /**
     * Creates a One-Hot Encoded matrix from an array of categorical strings.
     * Assumes categories are known in advance.
     */
    public static double[][] oneHotEncode(String[] column, String[] uniqueCategories) {
        double[][] encoded = new double[column.length][uniqueCategories.length];
        
        for (int i = 0; i < column.length; i++) {
            for (int j = 0; j < uniqueCategories.length; j++) {
                if (column[i].equals(uniqueCategories[j])) {
                    encoded[i][j] = 1.0;
                    break;
                }
            }
        }
        return encoded;
    }

    public static void main(String[] args) {
        // 1. Imputation Example
        double[] ages = {25.0, 30.0, Double.NaN, 40.0, 50.0};
        System.out.println("Original Ages: " + Arrays.toString(ages));
        imputeMean(ages);
        System.out.println("Imputed Ages:  " + Arrays.toString(ages));
        
        // 2. Standardization Example
        double[] salaries = {50000, 60000, 70000, 80000, 200000}; // Note the outlier
        System.out.println("\nOriginal Salaries: " + Arrays.toString(salaries));
        standardize(salaries);
        System.out.println("Standardized Salaries (Z-score):");
        for(double s : salaries) System.out.printf("%.2f ", s);
        System.out.println();
        
        // 3. One-Hot Encoding Example
        String[] colors = {"Red", "Blue", "Green", "Red", "Blue"};
        String[] categories = {"Red", "Green", "Blue"};
        double[][] encodedColors = oneHotEncode(colors, categories);
        
        System.out.println("\nOne-Hot Encoded Colors:");
        for (int i = 0; i < colors.length; i++) {
            System.out.println(colors[i] + " -> " + Arrays.toString(encodedColors[i]));
        }
    }
}
```

## 🔍 Key Takeaways
1. **In-Place Modification**: Notice that `standardize` and `normalize` modify the array in-place. When dealing with gigabytes of data, creating new arrays for every transformation will quickly cause an `OutOfMemoryError`.
2. **The Outlier Effect**: Look at the `salaries` array in the `main` method. The 200,000 value is an outlier. Because standardization relies on the mean (which is pulled up by the outlier), the standard Z-scores for the normal salaries (50k-80k) will all be negative. This demonstrates why outlier removal is often a necessary precursor to scaling.