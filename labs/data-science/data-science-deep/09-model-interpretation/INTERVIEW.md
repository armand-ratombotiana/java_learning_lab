# Model Interpretation — Interview Questions

### Q1: SHAP vs LIME
**Q**: Compare SHAP and LIME for model interpretability.

**A**: SHAP is grounded in Shapley values from game theory — it provides a unique, additive, and consistent attribution of each feature to the prediction. It's computationally expensive (exponential for exact, O(2^n) features). Kernel SHAP is model-agnostic; TreeSHAP is efficient for trees. LIME fits a local linear model around a prediction — it's faster but less stable (different explanations for nearby perturbations). SHAP is generally preferred for scientific rigor; LIME is better for quick, interactive explanations. Both are local (per-prediction). For global understanding, use permutation importance or partial dependence.

### Q2: When Features Are Correlated
**Q**: SHAP values can be misleading when features are highly correlated. Why?

**A**: SHAP assumes features are independent when averaging over feature subsets (the "missing" feature is replaced by drawing from the marginal distribution). With correlated features, this creates unrealistic combinations (e.g., age=5 and salary=$200k). Conditional SHAP (using the conditional distribution) addresses this but is expensive and changes the interpretation. For correlated features, consider grouping them or using a different method like SAGE (Shapley Additive Global importancE).

### Q3: Interpreting Black-Box Models
**Q**: A gradient boosting model outperforms logistic regression by 5% in AUC. Your stakeholder distrusts black-box models. How do you build trust?

**A**: (1) Show that the top-10 important features from SHAG overlap with logistic regression coefficients (same features, same direction). (2) Show partial dependence plots for key features — monotonic relationships are intuitive. (3) Use a glass-box model (EBM — Explainable Boosting Machine by Nori et al.) that achieves comparable performance. (4) Show that predictions are consistent with domain knowledge on edge cases. (5) Quantify the business value of the 5% AUC improvement.

### Q4: Permutation Importance Pitfalls
**Q**: What are the issues with permutation importance?

**A**: (1) Correlated features: permuting one feature creates unrealistic data points, and the drop in performance reflects both the loss of that feature AND the loss of correlation structure. (2) Importance can be negative if the model relies on the feature's correlation with noise. (3) Sampling variability: need multiple permutations to estimate standard error. (4) Only measures prediction drop, not the direction or nature of a feature's effect.

### Q5: Global vs Local Explanations
**Q**: When do you need global vs local explanations?

**A**: Global: understanding the model overall (auditing, regulatory compliance, feature selection, scientific discovery). Methods: permutation importance, partial dependence, SHAP summary plots. Local: explaining a specific prediction (why was this loan denied, why did the model flag this transaction as fraud). Methods: SHAP values, LIME. For debugging, both are needed: global tells you what the model learned, local tells you how it applies that knowledge to specific cases.

## Coding

### Q6: Permutation importance loop
```java
public double[] permutationImportance(double[][] X, double[] y, int feature, int repeats) {
    double baseline = evaluate(X, y);
    double[] scores = new double[repeats];
    for (int r = 0; r < repeats; r++) {
        double[][] Xp = permuteCol(X, feature);
        scores[r] = baseline - evaluate(Xp, y);
    }
    return new double[]{mean(scores), std(scores)};
}
```
