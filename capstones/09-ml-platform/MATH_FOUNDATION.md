# Math Foundation: ML Platform

## Evaluation Metrics
- Accuracy = (TP + TN) / (TP + TN + FP + FN)
- Precision = TP / (TP + FP)
- Recall = TP / (TP + FN)
- F1 = 2 * P * R / (P + R)
- AUC-ROC: Area under the ROC curve (true positive rate vs false positive rate)

## Feature Scaling
- Standardization: z = (x - mean) / std
- Min-Max: x' = (x - min) / (max - min)

## Population Stability Index (PSI)
PSI = sum((P_i - Q_i) * ln(P_i / Q_i))
Measures shift in feature distribution between training and serving.

## Drift Detection
- KS test: max difference between cumulative distributions
- Jensen-Shannon divergence: symmetric KL divergence
- Threshold alert when p-value < 0.05 or PSI > 0.2
