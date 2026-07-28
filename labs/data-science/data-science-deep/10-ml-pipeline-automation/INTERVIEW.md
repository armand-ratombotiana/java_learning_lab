# ML Pipeline Automation — Interview Questions

### Q1: Pipeline Design
**Q**: Design an automated ML pipeline for a tabular classification problem with 200 features and 50k rows.

**A**: The pipeline: (1) Data validation — check missing rates, types, distributions. (2) Preprocessing — impute missing (median for numeric, mode for categorical), scale numeric (standard scaler), encode categorical (target encoding with CV). (3) Feature selection — variance threshold (drop near-zero variance), correlation filter (|r| > 0.95), then mutual information select top 50. (4) Model zoo — evaluate Logistic Regression, Random Forest, XGBoost, LightGBM with 5-fold CV. (5) Hyperparameter tuning — Bayesian optimization for the top-2 models. (6) Ensemble — weighted average of top-3 models (weights optimized via simple grid). (7) Calibration — Platt scaling if probabilities are needed. (8) Monitoring — track feature distributions and prediction drift.

### Q2: Data Leakage in Pipelines
**Q**: Where can data leakage occur in an automated ML pipeline?

**A**: Common sources: (1) Scaling before train/test split (leaks mean/variance). (2) Target encoding before CV split (leaks target information). (3) Feature selection using the full dataset (leaks signal). (4) Oversampling (SMOTE) before splitting (leaks validation samples into training). (5) Using future data to predict past events in time series. Solution: all preprocessing steps must be learned only from training folds and applied to validation/testing.

### Q3: Model Selection Strategy
**Q**: Compare grid search, random search, and Bayesian optimization for hyperparameter tuning.

**A**: Grid search: exhaustive but scales exponentially — useful for <= 4 low-cardinality params. Random search: samples uniformly, covers space faster — recommended by Bergstra & Bengio (2012) for high-dimensional spaces. Bayesian optimization: builds a surrogate model (GP or TPE), selects promising regions — most sample-efficient for expensive evaluations (e.g., deep learning). Rule of thumb: random search for quick experiments, Bayesian for production tuning, grid only when params are few and cheap.

### Q4: Ensembling Pitfalls
**Q**: What are the risks of stacking/ensembling?

**A**: (1) Overfitting — meta-learner learns noise from base model predictions. Mitigation: use out-of-fold predictions for training the meta-learner. (2) Model complexity — harder to deploy, explain, and debug. (3) Diminishing returns — ensembling models from the same family (e.g., 5 random forests) helps less than ensembling diverse models (tree + linear + neural net). (4) Calibration — ensembles of poorly calibrated models are even worse. (5) Interpretability — SHAP values for the ensemble require interpreting each base model, which is expensive.

### Q5: Pipeline Validation
**Q**: How do you validate that your automated pipeline generalizes?

**A**: Nested cross-validation. Outer loop (5-fold) estimates generalization error. Inner loop (5-fold) does model selection and tuning. This gives an unbiased estimate of the pipeline's performance. NEVER use the test data for any decisions — it's held out until the very end. I'd also use stratified CV for classification (preserving class proportions) and grouped CV when there are natural clusters (multiple rows per user).

## Coding

### Q6: K-fold cross-validation loop
```java
public double[] crossValidate(Predictor model, double[][] X, double[] y, int k) {
    int n = X.length;
    double[] scores = new double[k];
    int[] idx = shuffle(IntStream.range(0, n).toArray());
    int foldSize = n / k;
    for (int fold = 0; fold < k; fold++) {
        int start = fold * foldSize;
        int end = (fold == k - 1) ? n : start + foldSize;
        double[][] trainX = selectRows(X, idx, 0, start, end, n);
        double[] trainY = selectRows(y, idx, 0, start, end, n);
        double[][] testX = selectRows(X, idx, start, end);
        double[] testY = selectRows(y, idx, start, end);
        Predictor foldModel = ...; // clone and fit
        double[] preds = foldModel.predictBatch(testX);
        scores[fold] = evaluate(preds, testY);
    }
    return scores;
}
```
