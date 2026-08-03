# Problem Walkthrough: Support Vector Machines

## Problem 1: Twitter Content Moderation SVM — Company: Twitter

### Interview Scenario
"You're at Twitter. Trust & Safety needs a real-time triage model that separates
toxic content from safe content using two computed signals — a toxicity score and
a link density — both on a 0–7 scale. The moderation queue wants a crisp
classification and the model must be retrained hourly with minimal infrastructure.
Train the linear SVM, verify it actually converged, and score two incoming
tweets."

### The Problem
Build a linear SVM moderator. It must: (1) Train on the primal hinge-loss
objective via subgradient descent, (2) Use a C / learning-rate / epoch setting
that actually converges — the lab default (C=1.0, lr=0.01, 2000 epochs) stops at
50% accuracy on separable data, (3) Report the learned weights and accuracy,
(4) Classify new tweets with the sign of w·x.

### Solution Walkthrough
- Step 1: Encode tweets as `[bias=1, toxicity, link density]` with labels
  −1 (safe) / +1 (toxic) — the lab's 2D layout, renamed for the domain.
- Step 2: Call `fit(X, y, 5.0, 0.005, 20000)` — the lab's primal update
  `w -= lr*(w − C·y·x)` when `margin < 1`, `w -= lr*w` otherwise. C=5.0 gives
  the misclassification budget enough weight to overcome regularization.
- Step 3: Print the weights; `w[0] = -1.42` is the bias that balances the two
  clusters.
- Step 4: Report training accuracy — 1.00 once converged — as the convergence
  check the lab demo is missing.
- Step 5: Score two new tweets with `predict(w, x)` and map +1 → flag for review,
  −1 → allow.

### Code
```java
package com.ml.lab04;

/**
 * Twitter-style toxic-content moderator (linear SVM).
 * <p>
 * Trains a linear SVM on the primal hinge-loss objective
 * ½‖w‖² + C·Σ max(0, 1 − y·(w·x)) via subgradient descent, exactly
 * like Lab 04's fit(), but with a converged C / lr / epochs setting —
 * the lab's default C=1.0 with 2000 epochs stops at 50% accuracy
 * because the regularization term dominates the early gradient.
 */
public class TweetModerator {

    public static double[] fit(double[][] X, double[] y, double C, double lr, int epochs) {
        int m = X.length, n = X[0].length;
        double[] w = new double[n];
        for (int ep = 0; ep < epochs; ep++) {
            for (int i = 0; i < m; i++) {
                double dot = 0;
                for (int j = 0; j < n; j++) dot += w[j] * X[i][j];
                double margin = y[i] * dot;
                if (margin < 1) {
                    for (int j = 0; j < n; j++) {
                        w[j] -= lr * (w[j] - C * y[i] * X[i][j]);
                    }
                } else {
                    for (int j = 0; j < n; j++) {
                        w[j] -= lr * w[j];
                    }
                }
            }
        }
        return w;
    }

    public static int predict(double[] w, double[] x) {
        double dot = 0;
        for (int j = 0; j < w.length; j++) dot += w[j] * x[j];
        return dot >= 0 ? 1 : -1;
    }

    public static double accuracy(double[][] X, double[] y, double[] w) {
        int ok = 0;
        for (int i = 0; i < X.length; i++) {
            if (predict(w, X[i]) == (int) y[i]) ok++;
        }
        return (double) ok / X.length;
    }

    public static void main(String[] args) {
        System.out.println("=== Twitter-style Content Moderator ===");

        // Tweet rows: [bias=1, toxicity score 0..7, link density 0..7]
        double[][] X = {
            {1, 1.0, 2.0}, {1, 1.5, 1.8}, {1, 2.0, 1.0}, {1, 0.8, 2.5},
            {1, 5.0, 6.0}, {1, 6.0, 5.0}, {1, 5.5, 6.5}, {1, 4.8, 7.0}
        };
        double[] y = {-1, -1, -1, -1, 1, 1, 1, 1}; // -1 safe, +1 toxic

        double[] w = fit(X, y, 5.0, 0.005, 20000);
        System.out.print("Weights: ");
        for (double v : w) System.out.printf("%.4f ", v);
        System.out.println();

        double acc = accuracy(X, y, w);
        System.out.printf("Training accuracy = %.2f%n", acc);

        double[] test1 = {1, 2.5, 2.0};
        double[] test2 = {1, 5.0, 5.0};
        System.out.printf("Tweet (tox=2.5, links=2.0) -> %d%n", predict(w, test1));
        System.out.printf("Tweet (tox=5.0, links=5.0) -> %d%n", predict(w, test2));
    }
}
```

### Expected Output
```
=== Twitter-style Content Moderator ===
Weights: -1.4206 0.2380 0.2580 
Training accuracy = 1.00
Tweet (tox=2.5, links=2.0) -> -1
Tweet (tox=5.0, links=5.0) -> 1
```

---

## Problem 2: Amazon Review-Helpfulness SVM — Company: Amazon

### Interview Scenario
"You're at Amazon. The reviews team wants to rank which reviews are genuinely
helpful before showing them to shoppers. Two signals per review — the ratio of
up-votes it received and its sentiment polarity. Prove a linear SVM separates
helpful from unhelpful reviews."

### The Problem
Train a linear SVM and: (1) Fit with the converged settings, (2) Report weights
and accuracy, (3) Classify a new review at (votes=0.6, sentiment=0.4),
(4) Interpret which signal drives the decision.

### Solution Walkthrough
- Step 1: Rows `[bias, votes-ratio, sentiment]`, labels −1/1, two clean clusters.
- Step 2: `fit(X, y, 5.0, 0.005, 20000)`; sentiment gets the bigger weight,
  so polarity dominates helpfulness.
- Step 3: Accuracy check, then `predict` on the new review.

### Code
```java
// Review rows: [bias=1, votes-ratio 0..1, sentiment -1..1]
double[][] X = {
    {1, 0.1, -0.9}, {1, 0.2, -0.5}, {1, 0.05, -1.0}, {1, 0.3, -0.3},
    {1, 0.9, 0.8}, {1, 0.8, 1.0}, {1, 1.0, 0.6}, {1, 0.7, 0.9}
};
double[] y = {-1, -1, -1, -1, 1, 1, 1, 1};

double[] w = TweetModerator.fit(X, y, 5.0, 0.005, 20000);
System.out.print("Weights: ");
for (double v : w) System.out.printf("%.4f ", v);
System.out.println();
System.out.printf("Training accuracy = %.2f%n", TweetModerator.accuracy(X, y, w));

double[] review = {1, 0.6, 0.4};
System.out.printf("Review (votes=0.6, sent=0.4) -> %d%n",
        TweetModerator.predict(w, review));
```

### Expected Output
```
Weights: -0.3238 0.5779 1.1024
Training accuracy = 1.00
Review (votes=0.6, sent=0.4) -> 1
```

---

## Problem 3: RBF Kernel Similarity Check — Company: Google

### Interview Scenario
"You're at Google. Before shipping an RBF-kernel SVM for an image-feature
classifier, you want to demo the kernel math to the team: show how the RBF kernel
turns Euclidean distance into similarity, with the diagonal at 1.0 and far-apart
points decaying toward zero."

### The Problem
Compute the RBF kernel matrix and: (1) Implement K(xᵢ,xⱼ) = exp(−γ‖xᵢ−xⱼ‖²),
(2) Show the diagonal equals 1.0, (3) Show similarity decays with distance,
(4) Confirm the matrix is symmetric.

### Solution Walkthrough
- Step 1: Three 2D points: origin, (0.5, 0.5), and (3, 3), γ = 0.5.
- Step 2: For each pair, sum squared differences and exponentiate.
- Step 3: Nearby points score 0.7788; distant pairs decay to 0.0001.
- Step 4: K is symmetric and positive on the diagonal — the properties that make
  it a valid inner product in the implicit feature space.

### Code
```java
// RBF kernel: K(xi, xj) = exp(-gamma * ||xi - xj||^2)
public static double rbf(double[] a, double[] b, double gamma) {
    double s = 0;
    for (int i = 0; i < a.length; i++) {
        double d = a[i] - b[i];
        s += d * d;
    }
    return Math.exp(-gamma * s);
}

public static void main(String[] args) {
    double gamma = 0.5;
    double[][] points = {{0.0, 0.0}, {0.5, 0.5}, {3.0, 3.0}};
    for (int i = 0; i < points.length; i++) {
        for (int j = 0; j < points.length; j++) {
            System.out.printf("K(p%d,p%d) = %.4f%n", i, j,
                    rbf(points[i], points[j], gamma));
        }
    }
}
```

### Expected Output
```
K(p0,p0) = 1.0000
K(p0,p1) = 0.7788
K(p0,p2) = 0.0001
K(p1,p0) = 0.7788
K(p1,p1) = 1.0000
K(p1,p2) = 0.0019
K(p2,p0) = 0.0001
K(p2,p1) = 0.0019
K(p2,p2) = 1.0000
```
