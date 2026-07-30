# Guide: Hypothesis Testing in Java

## T-Test
1. Compute t-statistic: t = (x̄ - μ₀) / (s / √n)
2. Compute degrees of freedom: df = n - 1
3. Compute p-value from t-distribution CDF
4. Compare to significance level α (typically 0.05)

## Z-Test
1. Compute z-statistic: z = (x̄ - μ₀) / (σ / √n)
2. Compute p-value from normal CDF
3. Requires known population standard deviation

## Chi-Square Goodness-of-Fit
1. Compute χ² = Σ (Oᵢ - Eᵢ)² / Eᵢ
2. df = number of categories - 1
3. Compare to chi-square distribution

## Chi-Square Test of Independence
1. Create contingency table of observed counts
2. Compute expected counts: (row total × col total) / grand total
3. Compute χ² = Σ (O - E)² / E
4. df = (rows - 1) × (cols - 1)

## Java Implementation
- Use regularized incomplete beta function for t-distribution CDF
- Use error function for normal CDF
- Use regularized incomplete gamma function for chi-square CDF
