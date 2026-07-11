# Bayes' Theorem Code Deep Dive

This lab provides a pure Java implementation of a Naive Bayes Text Classifier, demonstrating how Bayesian probability is used to filter spam.

## 💻 Pure Java Implementation

```java file="labs/math/03-probability/bayes-theorem/SOLUTION/NaiveBayesClassifier.java"
package math.probability.bayes;

import java.util.HashMap;
import java.util.Map;

/**
 * A fundamental implementation of a Naive Bayes Text Classifier.
 */
public class NaiveBayesClassifier {

    private int totalSpamEmails = 0;
    private int totalHamEmails = 0;
    
    private int totalSpamWords = 0;
    private int totalHamWords = 0;

    // Word frequency maps
    private final Map<String, Integer> spamWordCounts = new HashMap<>();
    private final Map<String, Integer> hamWordCounts = new HashMap<>();
    
    // Vocabulary for Laplace Smoothing
    private final Map<String, Boolean> vocabulary = new HashMap<>();

    /**
     * Trains the classifier with labeled text.
     */
    public void train(String text, boolean isSpam) {
        String[] words = text.toLowerCase().split("\\W+");
        
        if (isSpam) {
            totalSpamEmails++;
            totalSpamWords += words.length;
            for (String word : words) {
                spamWordCounts.put(word, spamWordCounts.getOrDefault(word, 0) + 1);
                vocabulary.put(word, true);
            }
        } else {
            totalHamEmails++;
            totalHamWords += words.length;
            for (String word : words) {
                hamWordCounts.put(word, hamWordCounts.getOrDefault(word, 0) + 1);
                vocabulary.put(word, true);
            }
        }
    }

    /**
     * Calculates the probability that a given text is Spam.
     */
    public double predictSpamProbability(String text) {
        String[] words = text.toLowerCase().split("\\W+");
        
        int totalEmails = totalSpamEmails + totalHamEmails;
        if (totalEmails == 0) return 0.0;

        // 1. Calculate Priors P(Spam) and P(Ham)
        double pSpam = (double) totalSpamEmails / totalEmails;
        double pHam = (double) totalHamEmails / totalEmails;

        // We use Log Probabilities to prevent floating-point underflow
        // when multiplying many small fractions together.
        // log(A * B) = log(A) + log(B)
        double logProbSpam = Math.log(pSpam);
        double logProbHam = Math.log(pHam);

        int vocabSize = vocabulary.size();

        // 2. Calculate Likelihoods P(Words | Spam)
        for (String word : words) {
            // Laplace Smoothing (Add-1) prevents probability from becoming 0 if a word was never seen in training
            int spamCount = spamWordCounts.getOrDefault(word, 0);
            double pWordGivenSpam = (double) (spamCount + 1) / (totalSpamWords + vocabSize);
            logProbSpam += Math.log(pWordGivenSpam);

            int hamCount = hamWordCounts.getOrDefault(word, 0);
            double pWordGivenHam = (double) (hamCount + 1) / (totalHamWords + vocabSize);
            logProbHam += Math.log(pWordGivenHam);
        }

        // 3. Apply Bayes Theorem to get Posterior P(Spam | Words)
        // Since we are in log space, we must exponentiate to get back to standard probabilities
        double probSpam = Math.exp(logProbSpam);
        double probHam = Math.exp(logProbHam);
        
        // Normalize the result so it sums to 1.0
        return probSpam / (probSpam + probHam);
    }

    public static void main(String[] args) {
        NaiveBayesClassifier classifier = new NaiveBayesClassifier();

        // Training Data
        classifier.train("Win a free lottery prize now", true); // Spam
        classifier.train("Free money fast", true);              // Spam
        classifier.train("Hi mom, please call me back", false); // Ham (Not Spam)
        classifier.train("Meeting schedule for tomorrow", false); // Ham

        // Test Data
        String test1 = "Win free money tomorrow";
        String test2 = "Hi mom, meeting tomorrow";

        System.out.printf("Probability '%s' is Spam: %.2f%%\n", test1, classifier.predictSpamProbability(test1) * 100);
        System.out.printf("Probability '%s' is Spam: %.2f%%\n", test2, classifier.predictSpamProbability(test2) * 100);
    }
}
```

## 🔍 Key Takeaways
1. **Log Space**: Notice the use of `Math.log()`. If an email has 100 words, multiplying 100 tiny probabilities together (e.g., $0.01 \times 0.05 \times \dots$) will quickly result in `0.0` due to floating-point underflow limitations in Java. By converting to Log Space, we can safely *add* the probabilities instead of multiplying them.
2. **Laplace Smoothing**: Look at `(spamCount + 1)`. If the test email contains the word "hello", but "hello" never appeared in any spam emails during training, $P(\text{"hello"} | \text{Spam})$ would be exactly 0. Because of the multiplication chain, this would instantly force the entire spam probability to 0, regardless of how many other spam words were present. Adding 1 to the numerator (and `vocabSize` to the denominator) mathematically guarantees no probability is ever exactly zero.