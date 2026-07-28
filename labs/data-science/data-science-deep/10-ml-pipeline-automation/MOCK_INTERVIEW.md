# Mock Interview: ML Pipeline Automation

**Interviewer**: Design an automated ML system that can handle 1000s of datasets with minimal human intervention.

**Candidate**: The system has 4 layers: (1) Data profiling — automatically detect column types, missing rates, cardinality, distributions. (2) Cleaning — imputation strategy depends on missing rate (<5%: drop rows, 5-50%: median/mode/simple model, >50%: create is_missing feature). (3) Feature engineering — generate numeric transformations (log, sqrt, polynomial) only for features with high mutual information. (4) Modeling — try 5 model families (linear, tree, RF, GBM, neural net) with default params first, then tune the top-3 using Bayesian optimization. Select via nested CV. The system should detect when simpler models (linear) perform comparably to complex ones and prefer the simpler model for deployment.

**Interviewer**: How do you prevent the pipeline from overfitting during automated model selection?

**Candidate**: Three safeguards: (1) Nested cross-validation — outer CV estimates generalization error, inner CV selects hyperparameters. (2) Simpler model penalty — if the top model's CV score is within 0.01 of the second-best, choose the simpler one. (3) Early stopping — in Boosting, monitor validation error and stop if it doesn't improve for n rounds. (4) Regularization — always use L1/L2 penalties. (5) Feature limits — cap the number of features at some fraction of sample size (e.g., max p = n/10).

**Interviewer**: A new dataset arrives where the best model after tuning is a simple linear regression with performance close to the best XGBoost. How does your pipeline handle this?

**Candidate**: The nested CV will reveal that the linear model's generalization error is comparable. The pipeline should apply a model complexity penalty (e.g., if XGBoost's score is within 1% of linear, prefer linear). It should also check if the feature-target relationships are approximately linear (e.g., by checking if higher-order polynomial features don't improve R² much). The advantage: linear models are more interpretable, faster at inference, easier to deploy, and require less monitoring.

**Interviewer**: How do you handle model drift in an automated pipeline?

**Candidate**: After deployment, I'd compute a data drift score (PSI/KL divergence) on the feature distribution. If drift exceeds a threshold, trigger re-training. I'd also track prediction distribution drift. The re-training pipeline should be the same automated pipeline (feature selection, tuning, etc.), but I'd compare the new model's performance on a holdout set from the new data against the current model's performance. Only deploy if the new model is clearly better. A/B test the new model in production with a shadow deployment.

**Interviewer**: Let's code. Implement k-fold cross-validation indices.

**Candidate**:
```java
public class CrossValidationSplitter {
    private final int k;
    
    public int[][] getSplitIndices(int n) {
        int[] shuffled = IntStream.range(0, n).toArray();
        // shuffle in-place
        Random rng = new Random(42L);
        for (int i = n - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int tmp = shuffled[i]; shuffled[i] = shuffled[j]; shuffled[j] = tmp;
        }
        
        int[][] folds = new int[k][];
        int foldSize = n / k;
        for (int fold = 0; fold < k; fold++) {
            int start = fold * foldSize;
            int end = (fold == k - 1) ? n : start + foldSize;
            folds[fold] = Arrays.copyOfRange(shuffled, start, end);
        }
        return folds;
    }
    
    public double[][] getTrain(double[][] X, int fold, int[][] folds) {
        List<Integer> trainIdx = new ArrayList<>();
        for (int f = 0; f < folds.length; f++) {
            if (f == fold) continue;
            for (int idx : folds[f]) trainIdx.add(idx);
        }
        return selectRows(X, trainIdx);
    }
}
```
