# Problem Walkthrough: Logistic Regression

## Problem 1: Real-Time Credit Card Fraud Detector — Company: Stripe

### Interview Scenario
"You're at Stripe. The risk team needs a first-generation fraud model for card
transactions: given two real-time features — transaction amount and the number of
transactions the card performed in the last hour — decide whether to approve,
review, or block. Latency budget is under 10 ms, and the model must be explainable
in front of the risk committee."

### The Problem
Build a logistic regression fraud classifier. It must: (1) Train with gradient
descent on cross-entropy loss over labeled transaction history, (2) Report the
learned weights and the decision boundary line in feature space, (3) Score new
transactions with a probability, not just a flag, (4) Map probabilities to
approve / review / block actions, (5) Report training accuracy so the team can
sanity-check the fit.

### Solution Walkthrough
- Step 1: Encode transactions as rows `[bias=1, amount in $100s, velocity in
  txns/hour]` with labels 0 (safe) / 1 (fraud) — the same layout as the lab's X.
- Step 2: Run `fit(X, y, 0.1, 5000)` — the lab's batch gradient descent, where each
  epoch computes `err = pred - y` and accumulates `grad[j] += err * X[i][j]`.
- Step 3: Print the three weights; the first is the bias, the other two are
  log-odds contributions per feature unit.
- Step 4: Derive the decision boundary `w0 + w1*amount + w2*velocity = 0` — the
  line where σ(z) = 0.5 — and print it for the risk committee.
- Step 5: Score two new transactions with `predictProb` and `predictClass`.
- Step 6: Map scores to policy: < 0.3 auto-approve, 0.3–0.7 human review,
  > 0.7 block — threshold is policy, the probability is the model.

### Code
```java
package com.ml.lab02;

/**
 * Stripe-style credit card fraud detector.
 * <p>
 * Binary logistic regression on two engineered features — transaction
 * amount (in $100s) and hourly transaction velocity — using the Lab 02
 * sigmoid, cross-entropy gradient descent and decision-boundary logic.
 */
public class FraudDetector {

    public static double sigmoid(double z) {
        return 1.0 / (1.0 + Math.exp(-z));
    }

    public static double predictProb(double[] weights, double[] features) {
        double z = 0;
        for (int i = 0; i < weights.length; i++) {
            z += weights[i] * features[i];
        }
        return sigmoid(z);
    }

    public static double[] fit(double[][] X, double[] y, double lr, int epochs) {
        int m = X.length, n = X[0].length;
        double[] w = new double[n];
        for (int ep = 0; ep < epochs; ep++) {
            double[] grad = new double[n];
            for (int i = 0; i < m; i++) {
                double pred = predictProb(w, X[i]);
                double err = pred - y[i];
                for (int j = 0; j < n; j++) {
                    grad[j] += err * X[i][j];
                }
            }
            for (int j = 0; j < n; j++) {
                w[j] -= lr * grad[j] / m;
            }
        }
        return w;
    }

    public static int predictClass(double[] weights, double[] features) {
        return predictProb(weights, features) >= 0.5 ? 1 : 0;
    }

    public static double accuracy(double[][] X, double[] y, double[] w) {
        int correct = 0;
        for (int i = 0; i < X.length; i++) {
            if (predictClass(w, X[i]) == (int) y[i]) correct++;
        }
        return (double) correct / X.length;
    }

    public static void main(String[] args) {
        System.out.println("=== Stripe-style Fraud Detector ===");

        // Each row: [bias=1, amount in $100s, velocity in txn/hour]
        double[][] X = {
            {1, 2.0, 1.0}, {1, 1.0, 2.0}, {1, 1.5, 0.5}, {1, 3.0, 1.5},
            {1, 6.0, 4.0}, {1, 5.0, 5.0}, {1, 7.0, 4.5}, {1, 6.5, 5.5}
        };
        double[] y = {0, 0, 0, 0, 1, 1, 1, 1};

        double[] w = fit(X, y, 0.1, 5000);
        System.out.print("Weights: ");
        for (double v : w) System.out.printf("%.4f ", v);
        System.out.println();

        double acc = accuracy(X, y, w);
        System.out.printf("Training accuracy = %.2f%n", acc);

        // Decision boundary: w0 + w1*amount + w2*velocity = 0
        System.out.printf("Boundary: %.2f + %.2f*amount + %.2f*velocity = 0%n",
                w[0], w[1], w[2]);

        double[][] newTxns = {{1, 2.5, 1.2}, {1, 6.0, 4.8}};
        for (double[] t : newTxns) {
            System.out.printf("Txn (amount=%.1f, velocity=%.1f) -> prob=%.4f class=%d%n",
                    t[1], t[2], predictProb(w, t), predictClass(w, t));
        }
    }
}
```

### Expected Output
```
=== Stripe-style Fraud Detector ===
Weights: -8.9486 0.8632 1.9935 
Training accuracy = 1.00
Boundary: -8.95 + 0.86*amount + 1.99*velocity = 0
Txn (amount=2.5, velocity=1.2) -> prob=0.0121 class=0
Txn (amount=6.0, velocity=4.8) -> prob=0.9970 class=1
```

---

## Problem 2: Ad Click Prediction — Company: Google

### Interview Scenario
"You're at Google Ads. A new display campaign needs a fast click-through model.
You have two signals per impression — ad relevance score and expected engagement
lift — and a pilot dataset of labeled impressions. Prove the concept with a
logistic regression before investing in a DNN."

### The Problem
Build a CTR classifier and: (1) Train with cross-entropy gradient descent,
(2) Report weights and accuracy, (3) Score a new ad's probability of click,
(4) Classify it against the 0.5 threshold.

### Solution Walkthrough
- Step 1: Features per impression: `[bias, relevance, engagement lift]`, labels
  0 = no click, 1 = click.
- Step 2: `fit(X, y, 0.1, 5000)` with the lab's gradient descent.
- Step 3: Print weights — positive relevance weight means higher relevance raises
  click log-odds.
- Step 4: Score the candidate ad with `predictProb` and label with `predictClass`.

### Code
```java
// Ad rows: [bias, relevance 0..1, engagement lift 0..1]
double[][] X = {
    {1, 0.2, 0.3}, {1, 0.4, 0.2}, {1, 0.3, 0.6}, {1, 0.8, 0.7},
    {1, 0.9, 0.5}, {1, 0.7, 0.9}, {1, 0.6, 0.8}, {1, 0.9, 1.0}
};
double[] y = {0, 0, 0, 1, 1, 1, 1, 1};

double[] w = FraudDetector.fit(X, y, 0.1, 5000);
System.out.print("Weights: ");
for (double v : w) System.out.printf("%.4f ", v);
System.out.println();
System.out.printf("Accuracy = %.2f%n", FraudDetector.accuracy(X, y, w));

double[] newAd = {1, 0.75, 0.6};
System.out.printf("New ad (rel=0.75, lift=0.60) -> prob=%.4f class=%d%n",
        FraudDetector.predictProb(w, newAd), FraudDetector.predictClass(w, newAd));
```

### Expected Output
```
Weights: -7.6499 9.2771 5.3779
Accuracy = 1.00
New ad (rel=0.75, lift=0.60) -> prob=0.9265 class=1
```

---

## Problem 3: Spam Account Detection — Company: Twitter

### Interview Scenario
"You're at Twitter. Trust & Safety needs a triage model that flags accounts likely
to be spam bots: bots post a high fraction of link-bearing posts and mention many
users per post. The model only needs to order accounts by risk for the review
queue — a probability is enough, but it has to be explainable."

### The Problem
Train a logistic regression over account features and: (1) Fit weights with
gradient descent, (2) Report accuracy on the pilot set, (3) Score a suspect
account's probability, (4) Show the class at the 0.5 boundary.

### Solution Walkthrough
- Step 1: Rows `[bias, link-fraction, mention-rate]`, labels 0 = human,
  1 = bot — same layout as the lab's two-cluster data.
- Step 2: `fit(X, y, 0.1, 5000)`; both features should get clearly positive
  weights since bots sit in the high-corner cluster.
- Step 3: Print weights and accuracy.
- Step 4: `predictProb` on the suspect and `predictClass` for the queue flag.

### Code
```java
// Account rows: [bias, link-fraction 0..1, mention-rate 0..1]
double[][] X = {
    {1, 0.1, 0.3}, {1, 0.2, 0.1}, {1, 0.05, 0.4}, {1, 0.3, 0.2},
    {1, 0.8, 0.9}, {1, 0.9, 0.7}, {1, 0.7, 1.0}, {1, 0.85, 0.6}
};
double[] y = {0, 0, 0, 0, 1, 1, 1, 1};

double[] w = FraudDetector.fit(X, y, 0.1, 5000);
System.out.print("Weights: ");
for (double v : w) System.out.printf("%.4f ", v);
System.out.println();
System.out.printf("Accuracy = %.2f%n", FraudDetector.accuracy(X, y, w));

double[] suspect = {1, 0.75, 0.8};
System.out.printf("Suspect (links=0.75, mentions=0.80) -> prob=%.4f class=%d%n",
        FraudDetector.predictProb(w, suspect), FraudDetector.predictClass(w, suspect));
```

### Expected Output
```
Weights: -6.5677 7.8458 5.5949
Accuracy = 1.00
Suspect (links=0.75, mentions=0.80) -> prob=0.9780 class=1
```
