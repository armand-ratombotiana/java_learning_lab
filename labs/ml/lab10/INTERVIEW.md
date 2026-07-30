# Lab 10 Interview: Model Evaluation

## Q1: Why use cross-validation instead of a single train-test split?
CV gives a more stable estimate of model performance and uses all data for both training and validation.

## Q2: When is AUC a poor metric?
When the dataset is highly imbalanced; AUC can be misleadingly high. Use precision-recall instead.

## Q3: What is the bias-variance tradeoff?
High bias = underfitting (simple model); high variance = overfitting (complex model). Goal is to balance both.

## Q4: Explain the difference between precision and recall.
Precision: of predicted positives, how many are correct. Recall: of actual positives, how many were found.

## Q5: When should you use macro vs micro F1?
Macro: average F1 per class (treats classes equally). Micro: aggregate TP/FP/FN across classes (favors large classes).
