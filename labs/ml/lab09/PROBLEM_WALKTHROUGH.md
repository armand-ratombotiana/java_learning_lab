# Problem Walkthrough: Gradient Boosting

## Problem 1: Uber Late-Arrival Predictor — Company: Uber

### Interview Scenario
"You're at Uber. Dispatch wants to predict which trips will arrive late, using
two live features — trip distance in miles and a traffic index — so late trips
can be re-routed or the rider warned. The team is skeptical of complex models,
so you'll build the simplest member of the boosting family: an ensemble of
decision stumps that keeps correcting its own mistakes."

### The Problem
Build a gradient boosting classifier. It must: (1) Initialize raw predictions
and convert to probabilities via the sigmoid, (2) Compute pseudo-residuals
y − p and per-sample weights p(1−p), (3) Fit a depth-1 stump to the weighted
residuals, scanning every feature and midpoint threshold, (4) Shrink each
stump's contribution by a learning rate and repeat for 50 rounds, (5) Predict
new trips by summing the ensemble and thresholding at 0.5.

### Solution Walkthrough
- Step 1: Encode 6 trips as `(distance, traffic index)` with labels 0 = on
  time, 1 = late — the lab's two-cluster data.
- Step 2: `new GBC(0.5, 50)` — lr 0.5, 50 stumps — and `fit(X, y)`: each round
  recomputes `prob = sigmoid(rawPred)`, residuals `y − prob`, weights
  `prob*(1−prob)`.
- Step 3: `Stump.fit` sorts each feature's values, tests midpoints, and keeps
  the split with minimum weighted squared loss on the residuals.
- Step 4: `rawPred[i] += 0.5 * stump.predict(X[i])` accumulates the ensemble.
- Step 5: Verify 6/6 training accuracy, then predict the new (4.0, 4.0) trip.

### Code
```java
package com.ml.lab09;

import java.util.*;

/**
 * Uber-style late-arrival predictor (gradient boosting of stumps).
 * <p>
 * Builds the Lab 09 GBC ensemble — depth-1 stumps fit to weighted
 * pseudo-residuals with a configurable learning rate — and evaluates
 * it on trip features (distance, traffic index).
 */
public class EtaBoost {

    static class Stump {
        int feature;
        double threshold;
        double leftVal, rightVal;

        void fit(double[][] X, double[] residuals, double[] weights) {
            int n = X.length, m = X[0].length;
            double bestLoss = Double.MAX_VALUE;
            for (int f = 0; f < m; f++) {
                double[] vals = new double[n];
                for (int i = 0; i < n; i++) vals[i] = X[i][f];
                Arrays.sort(vals);
                for (int t = 0; t < n - 1; t++) {
                    double thresh = (vals[t] + vals[t + 1]) / 2;
                    double lSum = 0, lW = 0, rSum = 0, rW = 0;
                    for (int i = 0; i < n; i++) {
                        if (X[i][f] <= thresh) {
                            lSum += residuals[i] * weights[i];
                            lW   += weights[i];
                        } else {
                            rSum += residuals[i] * weights[i];
                            rW   += weights[i];
                        }
                    }
                    if (lW == 0 || rW == 0) continue;
                    double lVal = lSum / lW;
                    double rVal = rSum / rW;
                    double loss = 0;
                    for (int i = 0; i < n; i++) {
                        double err = residuals[i] - (X[i][f] <= thresh ? lVal : rVal);
                        loss += weights[i] * err * err;
                    }
                    if (loss < bestLoss) {
                        bestLoss = loss;
                        this.feature = f;
                        this.threshold = thresh;
                        this.leftVal = lVal;
                        this.rightVal = rVal;
                    }
                }
            }
        }

        double predict(double[] x) {
            return x[feature] <= threshold ? leftVal : rightVal;
        }
    }

    static class GBC {
        List<Stump> stumps = new ArrayList<>();
        double lr;
        int nEstimators;

        GBC(double lr, int nEstimators) {
            this.lr = lr;
            this.nEstimators = nEstimators;
        }

        void fit(double[][] X, double[] y) {
            int n = X.length;
            double[] rawPred = new double[n];
            for (int iter = 0; iter < nEstimators; iter++) {
                double[] prob = new double[n];
                for (int i = 0; i < n; i++) prob[i] = 1.0 / (1.0 + Math.exp(-rawPred[i]));

                double[] residuals = new double[n];
                double[] weights = new double[n];
                for (int i = 0; i < n; i++) {
                    residuals[i] = y[i] - prob[i];
                    weights[i] = prob[i] * (1 - prob[i]);
                }

                Stump stump = new Stump();
                stump.fit(X, residuals, weights);
                stumps.add(stump);

                for (int i = 0; i < n; i++) {
                    rawPred[i] += lr * stump.predict(X[i]);
                }
            }
        }

        int predict(double[] x) {
            double raw = 0;
            for (Stump s : stumps) raw += lr * s.predict(x);
            return 1.0 / (1.0 + Math.exp(-raw)) >= 0.5 ? 1 : 0;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Uber-style Late-Arrival Booster ===");

        // Trips: [distance miles, traffic index]; 0 = on time, 1 = late
        double[][] X = {
            {1.0, 2.0}, {2.0, 1.0}, {1.5, 1.5},
            {5.0, 5.0}, {6.0, 5.5}, {5.5, 4.5}
        };
        double[] y = {0, 0, 0, 1, 1, 1};

        GBC gbc = new GBC(0.5, 50);
        gbc.fit(X, y);

        int correct = 0;
        for (int i = 0; i < X.length; i++) {
            int pred = gbc.predict(X[i]);
            if (pred == (int) y[i]) correct++;
            System.out.printf("True=%d Pred=%d%n", (int) y[i], pred);
        }
        System.out.printf("Accuracy = %d/%d = %.2f%n", correct, X.length,
                (double) correct / X.length);

        double[] trip = {4.0, 4.0};
        System.out.printf("Trip (dist=4.0, traffic=4.0) -> %d%n", gbc.predict(trip));
    }
}
```

### Expected Output
```
=== Uber-style Late-Arrival Booster ===
True=0 Pred=0
True=0 Pred=0
True=0 Pred=0
True=1 Pred=1
True=1 Pred=1
True=1 Pred=1
Accuracy = 6/6 = 1.00
Trip (dist=4.0, traffic=4.0) -> 1
```

---

## Problem 2: Airbnb Booking-Score Booster — Company: Airbnb

### Interview Scenario
"You're at Airbnb. The search team wants a booking-probability model over two
listing signals — a price-tier score and a review score — built from the same
stump ensemble pattern as the Uber model. Prove the pattern transfers."

### The Problem
Train a boosted stump ensemble on listing data and: (1) Fit with lr=0.3 over
100 rounds, (2) Report training accuracy, (3) Score a mid-tier listing,
(4) Note how the residual loop transfers across domains.

### Solution Walkthrough
- Step 1: 8 listings as `(price-tier, review-score)` with booked labels.
- Step 2: `new GBC(0.3, 100)` — smaller lr, more rounds, the shrinkage tradeoff
  from the GUIDE.
- Step 3: Verify 8/8 accuracy; the mid-tier (3.0, 3.0) listing scores below the
  0.5 threshold and is not predicted booked.

### Code
```java
// Listings: [price-tier score, review score]; 0 = not booked, 1 = booked
double[][] X = {
    {1.0, 1.0}, {1.5, 0.5}, {0.5, 1.5}, {2.0, 2.0},
    {5.0, 5.0}, {6.0, 4.5}, {4.5, 5.5}, {5.5, 4.0}
};
double[] y = {0, 0, 0, 0, 1, 1, 1, 1};

EtaBoost.GBC gbc = new EtaBoost.GBC(0.3, 100);
gbc.fit(X, y);

int correct = 0;
for (int i = 0; i < X.length; i++) {
    if (gbc.predict(X[i]) == (int) y[i]) correct++;
}
System.out.printf("Accuracy = %d/%d = %.2f%n", correct, X.length,
        (double) correct / X.length);

double[] listing = {3.0, 3.0};
System.out.printf("Listing (price=3.0, reviews=3.0) -> %d%n", gbc.predict(listing));
```

### Expected Output
```
Accuracy = 8/8 = 1.00
Listing (price=3.0, reviews=3.0) -> 0
```

---

## Problem 3: Learning-Rate vs Tree Count — Company: Google

### Interview Scenario
"You're at Google, tuning a boosted classifier for a search feature. The team
wants proof of the shrinkage rule of thumb: halve the learning rate and roughly
double the number of trees. Run the comparison on the lab's data."

### The Problem
Compare (lr, trees) configurations and: (1) Train (0.1, 200), (0.5, 50), and
(1.0, 20), (2) Report accuracy for each, (3) Explain why all converge on
separable data, (4) State the generalization caveat for noisy data.

### Solution Walkthrough
- Step 1: The lab's 6-trip data — clean, separable clusters.
- Step 2: All three configs reach 1.00: the products η·(trees) are comparable,
  and separable data converges under any of them.
- Step 3: The honest caveat: on noisy data the small-lr config wins — large
  steps chase training noise, which is why shrinkage exists.

### Code
```java
double[][] X = {
    {1.0, 2.0}, {2.0, 1.0}, {1.5, 1.5},
    {5.0, 5.0}, {6.0, 5.5}, {5.5, 4.5}
};
double[] y = {0, 0, 0, 1, 1, 1};

double[][] cfgs = {{0.1, 200}, {0.5, 50}, {1.0, 20}};
for (double[] cfg : cfgs) {
    EtaBoost.GBC gbc = new EtaBoost.GBC(cfg[0], (int) cfg[1]);
    gbc.fit(X, y);
    int correct = 0;
    for (int i = 0; i < X.length; i++) {
        if (gbc.predict(X[i]) == (int) y[i]) correct++;
    }
    System.out.printf("lr=%.1f trees=%d -> accuracy %.2f%n",
            cfg[0], (int) cfg[1], (double) correct / X.length);
}
```

### Expected Output
```
lr=0.1 trees=200 -> accuracy 1.00
lr=0.5 trees=50 -> accuracy 1.00
lr=1.0 trees=20 -> accuracy 1.00
```
