# Guide: ANOVA in Java

## One-Way ANOVA
1. Compute grand mean
2. Compute group means
3. Compute Sum of Squares Between (SSB)
4. Compute Sum of Squares Within (SSW)
5. Compute Mean Squares: MSB = SSB/(k-1), MSW = SSW/(N-k)
6. F = MSB / MSW
7. Compute p-value from F-distribution CDF

## Two-Way ANOVA
Partition variance into:
- Factor A
- Factor B
- Interaction A×B
- Error (residual)

## Post-Hoc Tests (Tukey's HSD)
After significant ANOVA, determine which groups differ:
HSD = q_α * √(MSW / n)
Where q_α is the studentized range statistic.

## Assumptions
1. **Normality**: residuals should be normally distributed
2. **Homogeneity of variance**: equal variances across groups (Levene's test)
3. **Independence**: observations should be independent
