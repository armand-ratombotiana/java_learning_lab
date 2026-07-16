# Entropy Code Deep Dive

This lab provides a pure Java implementation of Shannon Entropy and Cross-Entropy calculations.

## 💻 Pure Java Implementation

```java file="labs/math/06-information-theory/entropy/SOLUTION/EntropyCalculators.java"
package math.informationtheory;

/**
 * A fundamental implementation of information theory metrics.
 */
public class EntropyCalculators {

    /**
     * Calculates Shannon Entropy in bits.
     */
    public static double calculateShannonEntropy(double[] probabilities) {
        double entropy = 0;
        for (double p : probabilities) {
            if (p > 0) {
                entropy -= p * (Math.log(p) / Math.log(2));
            }
        }
        return entropy;
    }

    /**
     * Calculates Binary Cross-Entropy (Log Loss).
     * 
     * @param yTrue The true label (0 or 1)
     * @param yPred The predicted probability (0 to 1)
     */
    public static double binaryCrossEntropy(double yTrue, double yPred) {
        // Clip yPred to avoid log(0)
        double epsilon = 1e-15;
        yPred = Math.max(epsilon, Math.min(1 - epsilon, yPred));
        
        return -(yTrue * Math.log(yPred) + (1 - yTrue) * Math.log(1 - yPred));
    }

    public static void main(String[] args) {
        // 1. Fair Coin: P(H)=0.5, P(T)=0.5. Entropy should be 1 bit.
        double[] fairCoin = {0.5, 0.5};
        System.out.println("Entropy of Fair Coin: " + calculateShannonEntropy(fairCoin) + " bits");

        // 2. Biased Coin: P(H)=0.9, P(T)=0.1. Entropy should be lower (less surprise).
        double[] biasedCoin = {0.9, 0.1};
        System.out.println("Entropy of Biased Coin: " + calculateShannonEntropy(biasedCoin) + " bits");
        
        // 3. Log Loss example
        System.out.println("Cross Entropy (y=1, pred=0.9): " + binaryCrossEntropy(1.0, 0.9));
        System.out.println("Cross Entropy (y=1, pred=0.1): " + binaryCrossEntropy(1.0, 0.1));
    }
}
```