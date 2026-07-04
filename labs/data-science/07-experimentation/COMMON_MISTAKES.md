# Common Mistakes in Experimentation

## 1. Peeking at Results

**Problem**: Checking results every day and stopping when p < 0.05 inflates false positives to ~30%.

**Fix**: Pre-register the sample size. Use sequential testing (always-valid p-values) if you must monitor.

## 2. Multiple Metrics Without Correction

**Problem**: Testing 20 metrics — some are "significant" by chance.

**Fix**: Choose one primary metric. For secondary metrics, use Bonferroni or FDR correction.

## 3. Interpreting Non-Significance as "No Effect"

**Problem**: p > 0.05 → "the feature doesn't work."

**Fix**: p > 0.05 means "we couldn't detect an effect." The effect could be real but small, or the sample size might be insufficient. Report the confidence interval.

## 4. Interaction Between Experiments

**Problem**: Running two experiments simultaneously that affect the same metric → interference.

**Fix**: Use holdouts, or run experiments in non-overlapping user segments.

## 5. Network Effects (Social Contagion)

**Problem**: In social networks, treatment affects control users through interactions → SUTVA violation.

**Fix**: Use cluster randomization (randomize by network cluster) or switchback experiments.

## 6. Novelty Effect

**Problem**: Users click more on the new button only because it's new, not because it's better.

**Fix**: Run experiments long enough for novelty to wear off (2-4 weeks).

## 7. Survivorship Bias in Long-Running Tests

**Problem**: Power analysis assumed all users stay. If users drop out differentially, results are biased.

**Fix**: Track and report attrition by group. Use intention-to-treat analysis.

## 8. Using p-values as Effect Sizes

**Problem**: "p = 0.0001 → the effect must be huge."

**Fix**: p-value depends on sample size. With n=1M, even a 0.01% effect is significant. Always report the effect size and confidence interval.
