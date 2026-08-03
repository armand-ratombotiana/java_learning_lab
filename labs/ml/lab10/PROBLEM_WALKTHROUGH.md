# Problem Walkthrough: Model Evaluation

## Problem 1: Amazon Seller-Fraud Model Evaluation — Company: Amazon

### Interview Scenario
"You're at Amazon. Marketplace risk has trained a seller-fraud model and the
review board needs proof it works before it gates any seller accounts. You have
the model's predictions, its raw scores, and a random feature set for a baseline
— run the full evaluation: confusion matrix, derived metrics, ROC/AUC, and
5-fold cross-validation, and present the numbers with their caveats."

### The Problem
Build the evaluation dashboard. It must: (1) Compute the confusion matrix
(TP/FP/FN/TN), (2) Derive accuracy, precision, recall, and F1 from the matrix
cells, (3) Compute AUC via the sorted-score sweep with tie handling, (4) Run
5-fold cross-validation with a majority-class baseline and report mean ± std,
(5) Read the results honestly — including what the CV number does and doesn't
mean.

### Solution Walkthrough
- Step 1: The 10 seller predictions: `actual`, `predicted`, and `scores` arrays.
- Step 2: `confusionMatrix` returns TP=3 FP=1 FN=2 TN=4; every metric derives
  from these cells.
- Step 3: `accuracy / precision / recall / f1` give 0.70 / 0.75 / 0.60 / 0.67 —
  recall is the weak link: 2 of 5 real fraud cases were missed.
- Step 4: `auc` sorts scores descending, sweeps the threshold, trapezoid-sums
  the ROC area with tied scores grouped — 0.9600, a strong ranking.
- Step 5: `crossVal` with the seeded shuffle and majority-class dummy gives
  5-fold accuracy 0.35 ± 0.255 — the baseline floor on random data, and a
  warning that 20 samples give noisy estimates.

### Code
```java
package com.ml.lab10;

import java.util.*;

/**
 * Amazon-style fraud-model evaluation dashboard.
 * <p>
 * Reuses Lab 10's confusionMatrix, accuracy / precision / recall / f1,
 * auc (trapezoidal ROC) and crossVal on marketplace seller-fraud
 * predictions, so the metrics — not a single accuracy number — decide
 * whether the model ships.
 */
public class FraudEvaluation {

    public static int[] confusionMatrix(int[] actual, int[] predicted) {
        int tp = 0, fp = 0, fn = 0, tn = 0;
        for (int i = 0; i < actual.length; i++) {
            if (actual[i] == 1 && predicted[i] == 1) tp++;
            else if (actual[i] == 0 && predicted[i] == 1) fp++;
            else if (actual[i] == 1 && predicted[i] == 0) fn++;
            else tn++;
        }
        return new int[]{tp, fp, fn, tn};
    }

    public static double accuracy(int tp, int fp, int fn, int tn) {
        return (double) (tp + tn) / (tp + fp + fn + tn);
    }

    public static double precision(int tp, int fp) {
        return tp + fp == 0 ? 0 : (double) tp / (tp + fp);
    }

    public static double recall(int tp, int fn) {
        return tp + fn == 0 ? 0 : (double) tp / (tp + fn);
    }

    public static double f1(double prec, double rec) {
        return prec + rec == 0 ? 0 : 2 * prec * rec / (prec + rec);
    }

    public static double auc(double[] scores, int[] actual) {
        int n = scores.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> Double.compare(scores[b], scores[a]));

        int pos = 0, neg = 0;
        for (int v : actual) { if (v == 1) pos++; else neg++; }

        double tpr = 0, fpr = 0, prevFpr = 0, prevTpr = 0, area = 0;
        for (int i = 0; i < n; i++) {
            int label = actual[idx[i]];
            if (label == 1) tpr += 1.0 / pos;
            else fpr += 1.0 / neg;
            if (i == n - 1 || scores[idx[i]] != scores[idx[i + 1]]) {
                area += (fpr - prevFpr) * (tpr + prevTpr) / 2;
                prevFpr = fpr;
                prevTpr = tpr;
            }
        }
        return area;
    }

    public static double[] crossVal(double[][] X, int[] y, int k, Random rng) {
        int n = X.length;
        List<Integer> shuf = new ArrayList<>();
        for (int i = 0; i < n; i++) shuf.add(i);
        Collections.shuffle(shuf, rng);

        double[] accs = new double[k];
        int foldSize = n / k;
        for (int fold = 0; fold < k; fold++) {
            int start = fold * foldSize;
            int end = (fold == k - 1) ? n : start + foldSize;
            Set<Integer> testIdx = new HashSet<>(shuf.subList(start, end));

            List<double[]> trainXl = new ArrayList<>();
            List<Integer> trainYl = new ArrayList<>();
            List<double[]> testXl = new ArrayList<>();
            List<Integer> testYl = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                if (testIdx.contains(i)) {
                    testXl.add(X[i]);
                    testYl.add(y[i]);
                } else {
                    trainXl.add(X[i]);
                    trainYl.add(y[i]);
                }
            }

            double[][] trainX = trainXl.toArray(new double[0][]);
            int[] trainY = trainYl.stream().mapToInt(v -> v).toArray();
            double[][] testX = testXl.toArray(new double[0][]);
            int[] testY = testYl.stream().mapToInt(v -> v).toArray();

            // Dummy classifier: predict majority class from training
            int sum = 0;
            for (int v : trainY) sum += v;
            int majority = sum > trainY.length / 2 ? 1 : 0;

            int ok = 0;
            for (int i = 0; i < testY.length; i++) {
                if (testY[i] == majority) ok++;
            }
            accs[fold] = (double) ok / testY.length;
        }
        return accs;
    }

    public static void main(String[] args) {
        System.out.println("=== Amazon-style Fraud Evaluation ===");

        // Seller-fraud predictions: actual vs predicted and model scores
        int[] actual    = {1, 1, 0, 1, 0, 0, 1, 0, 1, 0};
        int[] predicted = {1, 0, 0, 1, 1, 0, 1, 0, 0, 0};
        double[] scores = {0.9, 0.7, 0.2, 0.8, 0.6, 0.1, 0.95, 0.3, 0.4, 0.05};

        int[] cm = confusionMatrix(actual, predicted);
        System.out.printf("Confusion: TP=%d FP=%d FN=%d TN=%d%n", cm[0], cm[1], cm[2], cm[3]);

        double acc = accuracy(cm[0], cm[1], cm[2], cm[3]);
        double prec = precision(cm[0], cm[1]);
        double rec = recall(cm[0], cm[2]);
        double f = f1(prec, rec);
        System.out.printf("Acc=%.2f Prec=%.2f Rec=%.2f F1=%.2f%n", acc, prec, rec, f);

        double area = auc(scores, actual);
        System.out.printf("AUC = %.4f%n", area);

        Random rng = new Random(42);
        double[][] X = new double[20][2];
        int[] y = new int[20];
        for (int i = 0; i < 20; i++) {
            X[i][0] = rng.nextDouble();
            X[i][1] = rng.nextDouble();
            y[i] = rng.nextDouble() > 0.5 ? 1 : 0;
        }
        double[] cvAcc = crossVal(X, y, 5, rng);
        double mean = 0;
        for (double v : cvAcc) mean += v;
        mean /= cvAcc.length;
        double std = 0;
        for (double v : cvAcc) std += (v - mean) * (v - mean);
        std = Math.sqrt(std / cvAcc.length);
        System.out.printf("5-Fold CV: %.2f ± %.3f%n", mean, std);
    }
}
```

### Expected Output
```
=== Amazon-style Fraud Evaluation ===
Confusion: TP=3 FP=1 FN=2 TN=4
Acc=0.70 Prec=0.75 Rec=0.60 F1=0.67
AUC = 0.9600
5-Fold CV: 0.35 ± 0.255
```

---

## Problem 2: Google Ad CTR Ranking Check — Company: Google

### Interview Scenario
"You're at Google Ads. A new click model shows a perfect AUC on the pilot data,
but its accuracy at the 0.5 threshold is only 0.50. Investigate whether the
model is good — the ranking — and where the failure actually lives: the
operating threshold."

### The Problem
Evaluate the CTR model and: (1) Compute the confusion matrix at the 0.5 cut,
(2) Report precision, recall, F1, and accuracy, (3) Compute AUC, (4) Explain
how perfect ranking and mediocre threshold decisions coexist.

### Solution Walkthrough
- Step 1: 8 impressions with `actual` clicks, `predicted` at 0.5, and raw
  `scores`.
- Step 2: `confusionMatrix` → TP=2 FP=2 FN=2 TN=2 — every cell equal, all
  metrics 0.50.
- Step 3: `auc` = 1.0000 — the score ordering is perfect: every clicked ad
  outranks every non-clicked one.
- Step 4: The diagnosis: ranking quality is excellent; the 0.5 cut was just
  misplaced. Lower the threshold and precision/recall rebalance without any
  retraining.

### Code
```java
int[] actual    = {1, 1, 0, 0, 1, 0, 0, 1};
int[] predicted = {1, 0, 0, 1, 1, 0, 1, 0};

int[] cm = FraudEvaluation.confusionMatrix(actual, predicted);
System.out.printf("Confusion: TP=%d FP=%d FN=%d TN=%d%n", cm[0], cm[1], cm[2], cm[3]);

double prec = FraudEvaluation.precision(cm[0], cm[1]);
double rec = FraudEvaluation.recall(cm[0], cm[2]);
System.out.printf("Acc=%.2f Prec=%.2f Rec=%.2f F1=%.2f%n",
        FraudEvaluation.accuracy(cm[0], cm[1], cm[2], cm[3]), prec, rec,
        FraudEvaluation.f1(prec, rec));

double[] scores = {0.9, 0.6, 0.3, 0.4, 0.8, 0.2, 0.1, 0.7};
System.out.printf("AUC = %.4f%n", FraudEvaluation.auc(scores, actual));
```

### Expected Output
```
Confusion: TP=2 FP=2 FN=2 TN=2
Acc=0.50 Prec=0.50 Rec=0.50 F1=0.50
AUC = 1.0000
```

---

## Problem 3: Spotify Churn Threshold Tradeoff — Company: Spotify

### Interview Scenario
"You're at Spotify. The churn model's scores are ready, but the campaign team
asks the classic question: how many churners can we catch if we accept N false
alerts? Sweep the decision threshold and show the precision-recall tradeoff."

### The Problem
Sweep thresholds and: (1) Classify at 0.3, 0.5, and 0.7, (2) Recompute the
confusion matrix and P/R/F1 at each cut, (3) Show the tradeoff — lower
thresholds catch more churn (recall) at the cost of precision, (4) Pick the
operating point from business costs.

### Solution Walkthrough
- Step 1: 10 churn scores and actuals — the demo's data.
- Step 2: At each threshold, `scores[i] >= th ? 1 : 0` rebuilds the
  predictions; `confusionMatrix` + metrics per cut.
- Step 3: Read the curve: th=0.3 → P=0.57 R=0.80; th=0.5 → P=0.60 R=0.60;
  th=0.7 → P=0.67 R=0.40 — every 0.1 of recall costs precision, and vice versa.
- Step 4: Choose: if a saved subscriber is worth 3x an alert, the 0.3 cut
  (recall-first) wins; the demo's 0.5 default is just a convention.

### Code
```java
int[] actual = {1, 1, 0, 1, 0, 0, 1, 0, 1, 0};
double[] scores = {0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1, 0.05};

for (double th : new double[]{0.3, 0.5, 0.7}) {
    int[] pred = new int[scores.length];
    for (int i = 0; i < scores.length; i++) pred[i] = scores[i] >= th ? 1 : 0;
    int[] cm = FraudEvaluation.confusionMatrix(actual, pred);
    double p = FraudEvaluation.precision(cm[0], cm[1]);
    double r = FraudEvaluation.recall(cm[0], cm[2]);
    System.out.printf("th=%.1f -> Prec=%.2f Rec=%.2f F1=%.2f%n",
            th, p, r, FraudEvaluation.f1(p, r));
}
```

### Expected Output
```
th=0.3 -> Prec=0.57 Rec=0.80 F1=0.67
th=0.5 -> Prec=0.60 Rec=0.60 F1=0.60
th=0.7 -> Prec=0.67 Rec=0.40 F1=0.50
```
