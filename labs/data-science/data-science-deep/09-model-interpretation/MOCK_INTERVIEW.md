# Mock Interview: Model Interpretation

**Interviewer**: You trained a gradient boosting model for loan default prediction. The compliance team demands explanations for each declined applicant. How do you provide them?

**Candidate**: I'd use SHAP values locally for each applicant. For a declined application, I'd show: (1) the baseline default rate, (2) how each feature pushes the prediction up (toward default) or down (toward approval), (3) the top-3 features contributing to the decline decision. I'd also create a summary plot showing the most important features globally. For the compliance team, I'd produce a report showing that the model's key drivers are fair lending-compliant (income, debt-to-income, credit history — not protected attributes like race or gender).

**Interviewer**: How do you check for bias in your explanations?

**Candidate**: I'd compute SHAP values separately for protected groups and compare: (1) Does a given feature have systematically different SHAP values across groups? (e.g., is income weighted differently for different demographics?) (2) Are the same features driving decisions across groups? (3) I'd compute a fairness metric: for each feature, the correlation between the feature value and the SHAP value should be similar across groups. I'd also validate that the model's false positive/negative rates are balanced.

**Interviewer**: You notice that SHAP values for a feature flip sign depending on other feature values. What does this mean?

**Candidate**: This indicates an interaction effect. For example, SHAP for "credit score" might be positive (increases default probability) when "loan amount" is high but negative when "loan amount" is low — meaning the model interacts credit score with loan amount. I'd use interaction SHAP values (SHAP interaction values decompose the Shapley value into main and interaction effects) or Friedman's H-statistic to quantify this interaction.

**Interviewer**: A stakeholder asks "why did the model predict this customer as high-risk?" The model is a 500-tree XGBoost model. Walk me through the explanation.

**Candidate**: I'd run Kernel SHAP (or TreeSHAP if we implement it specifically for XGBoost). This gives additive feature contributions that sum to the prediction minus baseline. I'd present a waterfall chart: start at baseline (average prediction), then add contributions sorted by magnitude. "This customer was predicted as high-risk primarily because (1) debt-to-income ratio was 0.45 (contributing +15% to risk), (2) missed 2 payments in the last year (+12%), and (3) loan amount was $50k (+8%). These were partially offset by (4) employment length of 8 years (-5%)."

**Interviewer**: Let's code. Calculate permutation importance for a given feature.

**Candidate**:
```java
public double permutationImportance(Predictor model, double[][] X, double[] y, int feature, int repeats) {
    double baseline = rmse(model.predictBatch(X), y);
    double sumImportance = 0;
    for (int r = 0; r < repeats; r++) {
        double[][] XPermuted = deepCopy(X);
        shuffleColumn(XPermuted, feature);
        double permutedRmse = rmse(model.predictBatch(XPermuted), y);
        sumImportance += permutedRmse - baseline;
    }
    return sumImportance / repeats;
}
```
