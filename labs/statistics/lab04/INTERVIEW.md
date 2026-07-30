# Lab 04 Interview Questions

## Q1: What is the F-statistic?
The F-statistic is the ratio of between-group variance to within-group variance. A large F indicates group means differ more than expected by chance.

## Q2: When should you use ANOVA instead of multiple t-tests?
ANOVA controls the family-wise error rate. Running many t-tests inflates Type I error. Use ANOVA when comparing 3+ groups.

## Q3: What are post-hoc tests and why are they needed?
Post-hoc tests adjust for multiple comparisons after ANOVA finds a significant effect. Tukey's HSD, Bonferroni, and Scheffé are common methods.

## Q4: What does a significant interaction in two-way ANOVA mean?
A significant interaction means the effect of one factor depends on the level of another factor. Main effects cannot be interpreted independently.

## Q5: What happens if ANOVA assumptions are violated?
If normality is violated, use non-parametric alternative (Kruskal-Wallis). If variances are unequal, use Welch's ANOVA.
