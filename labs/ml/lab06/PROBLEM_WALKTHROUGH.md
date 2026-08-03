# Problem Walkthrough: Naive Bayes Classifier

## Problem 1: Gmail-Style Spam Filter — Company: Google

### Interview Scenario
"You're at Google, on the Gmail team. You're prototyping a spam classifier that
must ship with an explainable per-class story. Each email is summarized by two
real-time features — the ratio of spammy words it contains and its link ratio.
The team needs a model trained from labeled email, an accuracy sanity check, and
a verdict on three incoming emails."

### The Problem
Build a Gaussian Naive Bayes spam filter. It must: (1) Estimate per-class priors,
(2) Estimate per-class per-feature means and variances, (3) Score emails with the
log-space Gaussian posterior, (4) Report training accuracy and classify three new
emails, (5) Handle degenerate variances with an epsilon guard.

### Solution Walkthrough
- Step 1: Encode 10 labeled emails as `(spam-word ratio, link ratio)` rows —
  5 ham, 5 spam.
- Step 2: `fit(X, y)` groups samples by class and computes priors
  `samples.size() / n`, means, and sample variances with the
  `Math.max(1, size - 1)` guard.
- Step 3: `gaussianPdf(x, mean, var)` — the normal density with `1e-9` variance
  epsilon — is the likelihood P(xᵢ|y).
- Step 4: `predict` sums `log(prior) + Σ log(pdf + 1e-12)` per class and takes
  the argmax; log space avoids underflow.
- Step 5: Report training accuracy and the three test emails — the borderline
  (0.50, 0.40) email is closer to the spam cluster's means and is classified
  spam.

### Code
```java
package com.ml.lab06;

import java.util.*;

/**
 * Gmail-style spam filter (Gaussian Naive Bayes).
 * <p>
 * Computes per-class mean and variance for each email feature,
 * then classifies via Bayes rule in log space with the Gaussian
 * PDF — mirroring Lab 06's GaussianNB inner class.
 */
public class SpamFilter {

    static class GaussianNB {
        private final Map<Integer, Double> priors = new HashMap<>();
        private final Map<Integer, double[]> means = new HashMap<>();
        private final Map<Integer, double[]> vars = new HashMap<>();
        private final Set<Integer> classes = new HashSet<>();

        public void fit(double[][] X, int[] y) {
            int n = X.length, m = X[0].length;
            Map<Integer, List<double[]>> byClass = new HashMap<>();
            for (int i = 0; i < n; i++) {
                byClass.computeIfAbsent(y[i], k -> new ArrayList<>()).add(X[i]);
                classes.add(y[i]);
            }
            for (int c : classes) {
                List<double[]> samples = byClass.get(c);
                priors.put(c, (double) samples.size() / n);
                double[] mu = new double[m];
                double[] var = new double[m];
                for (double[] s : samples) for (int j = 0; j < m; j++) mu[j] += s[j];
                for (int j = 0; j < m; j++) mu[j] /= samples.size();
                for (double[] s : samples)
                    for (int j = 0; j < m; j++)
                        var[j] += (s[j] - mu[j]) * (s[j] - mu[j]);
                for (int j = 0; j < m; j++) var[j] /= Math.max(1, samples.size() - 1);
                means.put(c, mu);
                vars.put(c, var);
            }
        }

        private double gaussianPdf(double x, double mean, double var) {
            double eps = 1e-9;
            return Math.exp(-(x - mean) * (x - mean) / (2 * var + eps))
                    / Math.sqrt(2 * Math.PI * var + eps);
        }

        public int predict(double[] x) {
            int bestClass = -1;
            double bestScore = Double.NEGATIVE_INFINITY;
            for (int c : classes) {
                double score = Math.log(priors.get(c));
                double[] mu = means.get(c);
                double[] v = vars.get(c);
                for (int j = 0; j < x.length; j++) {
                    score += Math.log(gaussianPdf(x[j], mu[j], v[j]) + 1e-12);
                }
                if (score > bestScore) {
                    bestScore = score;
                    bestClass = c;
                }
            }
            return bestClass;
        }

        public double accuracy(double[][] X, int[] y) {
            int ok = 0;
            for (int i = 0; i < X.length; i++) {
                if (predict(X[i]) == y[i]) ok++;
            }
            return (double) ok / X.length;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Gmail-style Spam Filter ===");

        // Email rows: [spam-word ratio, link ratio]; 0 = ham, 1 = spam
        double[][] X = {
            {0.10, 0.05}, {0.20, 0.10}, {0.15, 0.20}, {0.25, 0.10}, {0.30, 0.25},
            {0.70, 0.60}, {0.80, 0.50}, {0.60, 0.70}, {0.75, 0.65}, {0.90, 0.80}
        };
        int[] y = {0, 0, 0, 0, 0, 1, 1, 1, 1, 1};

        GaussianNB nb = new GaussianNB();
        nb.fit(X, y);

        System.out.printf("Training accuracy = %.2f%n", nb.accuracy(X, y));

        double[][] test = {{0.20, 0.15}, {0.85, 0.70}, {0.50, 0.40}};
        int[] expected = {0, 1, 1};
        for (int i = 0; i < test.length; i++) {
            int pred = nb.predict(test[i]);
            System.out.printf("Email %d -> predicted=%d expected=%d %s%n",
                    i, pred, expected[i], pred == expected[i] ? "OK" : "MISS");
        }
    }
}
```

### Expected Output
```
=== Gmail-style Spam Filter ===
Training accuracy = 1.00
Email 0 -> predicted=0 expected=0 OK
Email 1 -> predicted=1 expected=1 OK
Email 2 -> predicted=1 expected=1 OK
```

---

## Problem 2: Amazon Review Sentiment — Company: Amazon

### Interview Scenario
"You're at Amazon. The reviews team wants a sentiment triage model for product
reviews using two signals — positive-word ratio and negation ratio. Ship the
smallest probabilistic classifier that gives a per-class story."

### The Problem
Classify review sentiment with Gaussian NB and: (1) Fit means, variances, and
priors per class, (2) Report training accuracy, (3) Classify two new reviews,
(4) Note the effect of the negation feature's negative weight on positivity.

### Solution Walkthrough
- Step 1: 10 reviews as `(positive-word ratio, negation ratio)`; negatives carry
  high negation ratios.
- Step 2: `fit` per-class statistics; `predict` in log space.
- Step 3: Both test reviews classify correctly — the negation feature cleanly
  separates the clusters.

### Code
```java
// Review rows: [positive-word ratio, negation ratio]; 0 = negative, 1 = positive
double[][] X = {
    {0.10, 0.30}, {0.15, 0.25}, {0.05, 0.40}, {0.20, 0.20}, {0.12, 0.35},
    {0.60, 0.05}, {0.70, 0.10}, {0.55, 0.15}, {0.80, 0.05}, {0.65, 0.10}
};
int[] y = {0, 0, 0, 0, 0, 1, 1, 1, 1, 1};

SpamFilter.GaussianNB nb = new SpamFilter.GaussianNB();
nb.fit(X, y);
System.out.printf("Training accuracy = %.2f%n", nb.accuracy(X, y));

double[][] test = {{0.30, 0.25}, {0.75, 0.05}};
int[] expected = {0, 1};
for (int i = 0; i < test.length; i++) {
    int pred = nb.predict(test[i]);
    System.out.printf("Review %d -> predicted=%d expected=%d %s%n",
            i, pred, expected[i], pred == expected[i] ? "OK" : "MISS");
}
```

### Expected Output
```
Training accuracy = 1.00
Review 0 -> predicted=0 expected=0 OK
Review 1 -> predicted=1 expected=1 OK
```

---

## Problem 3: Laplace Smoothed Word Likelihoods — Company: Yahoo

### Interview Scenario
"You're at Yahoo, building a news-article topic classifier with a multinomial
word model. The team is worried about a vocabulary word that never appears in
the training data for a topic — one zero would zero out the whole posterior.
Show the Laplace fix with numbers."

### The Problem
Compute smoothed word likelihoods and: (1) Apply P = (count + α)/(N_c + α·|V|),
(2) Show a seen word's probability, (3) Show an unseen word still gets nonzero
mass, (4) Confirm no class score can ever hit zero.

### Solution Walkthrough
- Step 1: Vocabulary of 4 words; a spam class containing 10 total words, with
  'offer' seen 3 times and 'invoice' seen 0 times.
- Step 2: Seen word: (3+1)/(10+4) = 0.2857.
- Step 3: Unseen word: (0+1)/(10+4) = 0.0714 — nonzero, so a single unseen word
  can no longer zero a class's posterior.
- Step 4: The smoothing constant α trades prior confidence (α large → all
  words near-uniform) against likelihood fidelity.

### Code
```java
// Multinomial likelihood with Laplace smoothing:
// P(word | class) = (count + alpha) / (N_c + alpha * |V|)
public static double laplace(int count, int totalWords, int vocabSize, double alpha) {
    return (count + alpha) / (totalWords + alpha * vocabSize);
}

public static void main(String[] args) {
    int vocabSize = 4;   // {"offer", "free", "meeting", "invoice"}
    int spamWords = 10;  // total words in spam documents
    System.out.printf("P(offer | spam)  = %.4f%n", laplace(3, spamWords, vocabSize, 1.0));
    System.out.printf("P(invoice | spam)= %.4f%n", laplace(0, spamWords, vocabSize, 1.0));
}
```

### Expected Output
```
P(offer | spam)  = 0.2857
P(invoice | spam)= 0.0714
```
