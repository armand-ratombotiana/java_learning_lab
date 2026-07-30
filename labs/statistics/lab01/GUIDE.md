# Guide: Descriptive Statistics in Java

## Step 1: Compute Mean
Sum all values and divide by count. Use `double` for precision.

## Step 2: Compute Median
Sort the array. If odd length, take middle element. If even, average the two middle elements.

## Step 3: Compute Mode
Use a `HashMap<Double, Integer>` to count frequencies. Return the value(s) with max count.

## Step 4: Compute Variance & Standard Deviation
- Population variance: average of squared deviations from mean
- Sample variance: divide by (n-1) for unbiased estimate
- Standard deviation: sqrt of variance

## Step 5: Compute Quartiles & IQR
- Q1: median of lower half (excluding median if odd count)
- Q3: median of upper half
- IQR = Q3 - Q1

## Step 6: Java Implementation
```java
public class DescriptiveStatistics {
    public static double mean(double[] data) {
        double sum = 0;
        for (double v : data) sum += v;
        return sum / data.length;
    }
    // ... see source file for full implementation
}
```

## Test Cases
- Dataset: {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
  - Mean = 5.5, Median = 5.5, Variance = 8.25, StdDev ≈ 2.872
- Dataset: {1, 1, 2, 3, 4, 4, 4, 5}
  - Mode = 4, Q1 = 1.5, Q3 = 4, IQR = 2.5
