# Hypothesis Testing — Interview Questions

### Q1: t-test vs Mann-Whitney
**Q**: When would you use a Mann-Whitney U instead of a t-test?

**A**: Mann-Whitney is preferred when (1) data is ordinal, (2) normality assumption is violated (especially with outliers), (3) sample sizes are very small. It tests whether one group tends to have larger values than the other (stochastic dominance), not necessarily a difference in means. If both assumptions hold, t-test has slightly higher power. For large samples, the CLT makes the t-test robust to non-normality, and the difference between the two diminishes.

### Q2: Multiple Testing in Practice
**Q**: You're testing 20 metrics in an experiment. How do you handle multiple testing?

**A**: Pre-register primary (1-3) and secondary metrics. For primary: no correction (they're the main hypotheses). For secondary: Benjamini-Hochberg (FDR control). For exploratory: report all p-values with a note about multiplicity. Never cherry-pick significant results. If the metrics are correlated (typical in practice), Bonferroni is too conservative — use Holm-Bonferroni or BH instead.

### Q3: p-value Controversy
**Q**: What's wrong with p-values? What alternatives exist?

**A**: Issues: (1) p = 0.051 vs p = 0.049 treated qualitatively different. (2) p-values conflate effect size and precision. (3) p-hacking / forking paths. (4) p > 0.05 doesn't mean "no effect." Alternatives: report effect sizes with confidence intervals, use Bayesian posterior probabilities, use second-generation p-values (interval null), or use equivalence testing. Always: visualize data, report CIs, discuss effect sizes.

### Q4: ANOVA Assumptions
**Q**: What are the assumptions of one-way ANOVA? What if they're violated?

**A**: Assumptions: (1) independence of observations, (2) normality of residuals, (3) homogeneity of variances. If normality violated: Kruskal-Wallis. If heteroscedasticity: Welch's ANOVA or Brown-Forsythe. If both: non-parametric or bootstrap. Independence is the most critical — correlated errors inflate Type I error dramatically.

### Q5: Effect Size Reporting
**Q**: How do you measure effect size for different tests?

**A**: t-test: Cohen's d = (m₁ - m₂) / s_pooled. ANOVA: η² = SS_between / SS_total, ω² = (SS_between - df_between * MS_within) / (SS_total + MS_within). Chi-square: Cramér's V = √(χ² / (n * min(r-1, c-1))). Mann-Whitney: rank-biserial correlation = 1 - 2U/(n₁n₂). Always report the effect size alongside the p-value.

## Coding

### Q6: Paired t-test
```java
public record PairedTTest(double tStat, double pValue, double ciLower, double ciUpper) {
    public static PairedTTest test(double[] before, double[] after, double alpha) {
        double[] diffs = new double[before.length];
        for (int i = 0; i < before.length; i++) diffs[i] = after[i] - before[i];
        return OneSampleTTest.test(diffs, 0.0, alpha);
    }
}
```
