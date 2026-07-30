# Lab 10 Guide: Model Evaluation

## Step 1 — Confusion Matrix
Build 2×2 matrix from actual vs predicted labels: TP, FP, FN, TN.

## Step 2 — Metrics
- Accuracy = (TP+TN) / (TP+FP+FN+TN)
- Precision = TP / (TP+FP)
- Recall = TP / (TP+FN)
- F1 = 2·P·R / (P+R)

## Step 3 — ROC Curve
- Sort predictions by score
- Sweep threshold, compute TPR and FPR
- Plot TPR vs FPR

## Step 4 — AUC
Area under the ROC curve (trapezoidal rule).

## Step 5 — K-Fold Cross-Validation
- Split data into K folds
- For each fold: train on K−1, evaluate on held-out fold
- Report mean ± std of metrics

## Step 6 — Run Tests
Evaluate a simple classifier with 5-fold CV.
