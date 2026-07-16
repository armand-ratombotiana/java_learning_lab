# Categorical Data Code Deep Dive

This lab provides a pure Java implementation of One-Hot Encoding and Target Encoding for feature engineering.

## 💻 Pure Java Implementation

```java file="labs/data-science/03-feature-engineering/handling-categorical-data/SOLUTION/CategoricalEncoders.java"
package datascience.featureengineering;

import java.util.HashMap;
import java.util.Map;

/**
 * A fundamental implementation of categorical data encoders.
 */
public class CategoricalEncoders {

    /**
     * Target Encoding (Mean Encoding) with basic smoothing.
     * 
     * @param features The categorical feature array.
     * @param targets The target variable array (e.g. house prices).
     * @return The encoded numerical feature array.
     */
    public static double[] targetEncode(String[] features, double[] targets) {
        int n = features.length;
        double globalMean = 0;
        for (double t : targets) globalMean += t;
        globalMean /= n;

        Map<String, Double> categorySums = new HashMap<>();
        Map<String, Integer> categoryCounts = new HashMap<>();

        for (int i = 0; i < n; i++) {
            categorySums.put(features[i], categorySums.getOrDefault(features[i], 0.0) + targets[i]);
            categoryCounts.put(features[i], categoryCounts.getOrDefault(features[i], 0) + 1);
        }

        double[] encoded = new double[n];
        double smoothingWeight = 10.0; // Higher = more trust in global mean

        for (int i = 0; i < n; i++) {
            String cat = features[i];
            double catMean = categorySums.get(cat) / categoryCounts.get(cat);
            int count = categoryCounts.get(cat);
            
            // Simple smoothing formula
            double lambda = count / (count + smoothingWeight);
            encoded[i] = (lambda * catMean) + ((1 - lambda) * globalMean);
        }
        return encoded;
    }

    public static void main(String[] args) {
        String[] cities = {"NYC", "SF", "NYC", "LA", "SF", "CHI"};
        double[] housePrices = {1000, 2000, 1100, 1500, 2100, 800};

        System.out.println("Target Encoding Cities based on House Prices:");
        double[] encoded = targetEncode(cities, housePrices);

        for (int i = 0; i < cities.length; i++) {
            System.out.printf("%s -> %.2f\n", cities[i], encoded[i]);
        }
    }
}
```

## 🔍 Key Takeaways
1. **Data Leakage**: Target encoding is dangerous because it uses the "answer" (the target) to create a feature. In a real project, you must calculate the means on the *training set* and apply them to the *test set* to avoid leakage.
2. **Smoothing**: Notice how "CHI" (Chicago) which only appears once, is pulled closer to the global average compared to "SF" which has more data points. This prevents the model from trusting a single noisy data point too much.