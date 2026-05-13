# Exploratory Analysis Flashcards

## Descriptive Statistics

### Card 1: Mean
**Q:** What is mean and when is it appropriate?
**A:** Average value; best for symmetric, continuous data without outliers.

### Card 2: Median
**Q:** When is median preferred over mean?
**A:** For skewed data or data with outliers - median is resistant to extreme values.

### Card 3: Mode
**Q:** What is mode and when is it useful?
**A:** Most frequent value; useful for categorical data or identifying peaks.

### Card 4: Variance
**Q:** What does variance measure?
**A:** Average squared deviation from the mean; indicates spread of data.

### Card 5: Standard Deviation
**Q:** How is standard deviation related to variance?
**A:** Std = sqrt(variance); in same units as original data.

### Card 6: Range
**Q:** What are the limitations of range?
**A:** Only considers min/max, ignores middle values, sensitive to outliers.

### Card 7: IQR
**Q:** What does IQR represent?
**A:** Interquartile Range (Q3-Q1); middle 50% of data, resistant to outliers.

### Card 8: Skewness
**Q:** What does skewness measure?
**A:** Asymmetry of distribution; negative = left-skewed, positive = right-skewed, zero = symmetric.

### Card 9: Kurtosis
**Q:** What does kurtosis indicate?
**A:** "Peakedness" of distribution; leptokurtic (tall), platykurtic (flat), mesokurtic (normal).

### Card 10: CV
**Q:** When is coefficient of variation useful?
**A:** For comparing variability of datasets with different units or scales.

---

## Distributions

### Card 11: Normal Distribution
**Q:** Properties of normal distribution?
**A:** Bell-shaped, symmetric, mean=median=mode, 68/95/99.7% within 1/2/3 std.

### Card 12: Z-Score
**Q:** How do you calculate and interpret z-score?
**A:** z = (x - mean) / std; number of standard deviations from mean.

### Card 13: Central Limit Theorem
**Q:** What does CLT state?
**A:** Sample means approach normal distribution as sample size increases, regardless of population distribution.

### Card 14: Percentile
**Q:** What is 90th percentile?
**A:** Value below which 90% of observations fall.

### Card 15: PDF vs CDF
**Q:** Difference between PDF and CDF?
**A:** PDF shows probability density; CDF shows cumulative probability P(X <= x).

---

## Correlation

### Card 16: Pearson r
**Q:** What does Pearson correlation measure?
**A:** Linear relationship strength and direction between two continuous variables.

### Card 17: Interpretation
**Q:** How do you interpret r = 0.7?
**A:** Moderate to strong positive linear relationship; 49% of variance shared.

### Card 18: Spearman vs Pearson
**Q:** When use Spearman instead of Pearson?
**A:** Ordinal data, non-normal distributions, or monotonic but not linear relationships.

### Card 19: Correlation Matrix
**Q:** What is a correlation matrix?
**A:** Table showing pairwise correlations between all variables.

### Card 20: Spurious Correlation
**Q:** Why correlation doesn't imply causation?
**A:** Third variables, coincidence, or direction problems can create misleading correlations.

---

## Hypothesis Testing

### Card 21: H0 and H1
**Q:** What is null vs alternative hypothesis?
**A:** H0: No effect/difference assumed; H1: Effect/difference exists.

### Card 22: P-value
**Q:** What does p-value < 0.05 mean?
**A:** Probability of observing data if H0 is true is < 5%; reject H0.

### Card 23: Type I Error
**Q:** What is Type I error?
**A:** False positive; rejecting H0 when it's actually true.

### Card 24: Type II Error
**Q:** What is Type II error?
**A:** False negative; failing to reject H0 when it's actually false.

### Card 25: Alpha Level
**Q:** What is significance level alpha?
**A:** Threshold for rejecting H0; commonly 0.05 (5% false positive rate).

### Card 26: One vs Two-tailed
**Q:** When use one-tailed vs two-tailed test?
**A:** Two-tailed: any difference; one-tailed: specific direction of effect.

### Card 27: T-Test
**Q:** When do you use t-test?
**A:** Comparing means when population std is unknown or sample is small.

### Card 28: ANOVA
**Q:** When do you use ANOVA instead of t-test?
**A:** Comparing means across 3 or more groups.

### Card 29: Chi-Square
**Q:** When do you use chi-square test?
**A:** Testing association between categorical variables or goodness-of-fit.

### Card 30: Effect Size
**Q:** Why is effect size important?
**A:** Measures practical significance, not just statistical significance.

---

## Statistical Tests

### Card 31: Normality Tests
**Q:** How do you test for normality?
**A:** Shapiro-Wilk, Kolmogorov-Smirnov, Anderson-Darling tests.

### Card 32: Outlier Detection (IQR)
**Q:** IQR rule for outliers?
**A:** Values < Q1 - 1.5*IQR or > Q3 + 1.5*IQR are outliers.

### Card 33: Z-Score Outliers
**Q:** Z-score threshold for outliers?
**A:** |z| > 3 typically considered outliers (99.7% coverage in normal data).

### Card 34: Confidence Interval
**Q:** What does 95% CI mean?
**A:** 95% of intervals constructed would contain true population parameter.

### Card 35: Sample Size
**Q:** What affects required sample size?
**A:** Desired power, effect size, alpha level, expected variability.

---

## Advanced Concepts

### Card 36: PCA
**Q:** What is Principal Component Analysis?
**A:** Technique to reduce dimensionality by transforming to uncorrelated components.

### Card 37: Eigenvalue
**Q:** What does eigenvalue represent in PCA?
**A:** Amount of variance explained by each principal component.

### Card 38: Mahalanobis Distance
**Q:** When is Mahalanobis distance useful?
**A:** Measuring distance in multivariate space accounting for correlations.

### Card 39: Non-parametric Tests
**Q:** When use non-parametric tests?
**A:** When data violates normality assumptions or is ordinal.

### Card 40: Bootstrap
**Q:** What is bootstrap?
**A:** Resampling technique to estimate confidence intervals without parametric assumptions.

---

## Common Calculations

### Card 41: Mean Formula
**Q:** Formula for sample mean?
**A:** x̄ = Σx / n

### Card 42: Variance Formula
**Q:** Formula for sample variance?
**A:** s² = Σ(x - x̄)² / (n - 1)

### Card 43: Standard Error
**Q:** Formula for standard error of mean?
**A:** SE = s / √n

### Card 44: Z-Score Formula
**Q:** Formula for z-score?
**A:** z = (x - μ) / σ

### Card 45: Correlation Formula
**Q:** Formula for Pearson correlation?
**A:** r = Σ(x - x̄)(y - ȳ) / √[Σ(x - x̄)² × Σ(y - ȳ)²]

---

## Visualizations

### Card 46: Histogram
**Q:** What does histogram show?
**A:** Distribution of continuous data showing frequency in bins.

### Card 47: Box Plot
**Q:** What does box plot show?
**A:** Median, quartiles, range, and outliers at a glance.

### Card 48: Scatter Plot
**Q:** What does scatter plot show?
**A:** Relationship between two continuous variables.

### Card 49: Q-Q Plot
**Q:** What does Q-Q plot check?
**A:** Whether data follows theoretical distribution (e.g., normal).

### Card 50: Violin Plot
**Q:** What does violin plot combine?
**A:** Box plot with density distribution showing shape.

---

## Power Analysis

### Card 51: Statistical Power
**Q:** What is power of a test?
**A:** Probability of correctly rejecting false H0 (1 - β).

### Card 52: Effect Size
**Q:** What is Cohen's d?
**A:** Standardized measure of effect size; small=0.2, medium=0.5, large=0.8.

### Card 53: Sample Size Formula
**Q:** What determines required sample size?
**A:** Larger effect, higher power, lower alpha, less variability → smaller n needed.

### Card 54: Beta Error
**Q:** What is beta error?
**A:** Probability of failing to reject false H0 (false negative rate).

### Card 55: Power Calculation
**Q:** How to increase power?
**A:** Increase sample size, effect size, alpha level; decrease variability.

---

## Assumptions

### Card 56: T-Test Assumptions
**Q:** What are t-test assumptions?
**A:** Independence, normality (or large n), equal variances (or Welch's correction).

### Card 57: ANOVA Assumptions
**Q:** ANOVA assumptions?
**A:** Independence, normality, homoscedasticity (equal variances).

### Card 58: Correlation Assumptions
**Q:** Pearson correlation assumptions?
**A:** Linear relationship, bivariate normality, no outliers, continuous data.

### Card 59: Independence
**Q:** Why is independence important?
**A:** Violations lead to biased estimates and incorrect conclusions.

### Card 60: Homoscedasticity
**Q:** What is homoscedasticity?
**A:** Equal variance across groups or levels of predictor variable.

---

## Error Analysis

### Card 61: RMSE
**Q:** What is RMSE?
**A:** Root Mean Squared Error; square root of average squared errors.

### Card 62: MAE
**Q:** What is MAE?
**A:** Mean Absolute Error; average absolute difference between predicted and actual.

### Card 63: MAPE
**Q:** What is MAPE?
**A:** Mean Absolute Percentage Error; average percentage error.

### Card 64: R-squared
**Q:** What does R² represent?
**A:** Proportion of variance explained by model; 0 to 1.

### Card 65: Adjusted R²
**Q:** Why use adjusted R²?
**A:** Accounts for number of predictors; penalizes overfitting.

---

## Time Series

### Card 66: Stationarity
**Q:** What is stationarity?
**A:** Constant mean, variance, and autocorrelation over time.

### Card 67: Autocorrelation
**Q:** What does autocorrelation measure?
**A:** Correlation of series with itself at different lags.

### Card 68: Trend
**Q:** How do you detect trend?
**A:** Visual inspection, moving averages, or regression on time.

### Card 69: Seasonality
**Q:** How do you detect seasonality?
**A:** Time series plot, seasonal decomposition, or autocorrelation peaks.

### Card 70: Differencing
**Q:** What does differencing do?
**A:** Transforms non-stationary series to stationary by computing differences.

---

## Reporting

### Card 71: Confidence Interval Reporting
**Q:** How to report CI?
**A:** "95% CI [lower, upper]" or "CI: x ± margin"

### Card 72: P-value Reporting
**Q:** How to report p-value?
**A:** "p = 0.03" or "p < 0.05" (avoid "p = 0.000")

### Card 73: Effect Size Reporting
**Q:** Why report effect size?
**A:** To convey practical significance beyond statistical significance.

### Card 74: Sample Description
**Q:** What to include in sample description?
**A:** n, mean, std, range, any transformations or exclusions.

### Card 75: Multiple Testing
**Q:** What is multiple testing problem?
**A:** Increased false positive rate when testing multiple hypotheses.

---

## Advanced Tests

### Card 76: Shapiro-Wilk
**Q:** When is Shapiro-Wilk used?
**A:** Testing normality of data; works well for small to medium samples.

### Card 77: Levene's Test
**Q:** What does Levene's test assess?
**A:** Equality of variances across groups.

### Card 78: Grubbs Test
**Q:** What does Grubbs test detect?
**A:** Single outlier in univariate data.

### Card 79: Durbin-Watson
**Q:** What does Durbin-Watson statistic indicate?
**A:** Presence of autocorrelation in residuals; range 0-4, 2 = no autocorrelation.

### Card 80: Kolmogorov-Smirnov
**Q:** What does K-S test compare?
**A:** Empirical distribution to theoretical distribution or two samples.

---

## Relationships

### Card 81: Covariance
**Q:** What does covariance indicate?
**A:** Direction of linear relationship (positive/negative), not standardized.

### Card 82: Partial Correlation
**Q:** What does partial correlation measure?
**A:** Correlation between two variables controlling for third variable.

### Card 83: Multiple Regression
**Q:** What does R² adjusted measure?
**A:** Variance explained by model accounting for number of predictors.

### Card 84: Confounding
**Q:** What is confounding?
**A:** Third variable affects both dependent and independent variables.

### Card 85: Interaction
**Q:** What is interaction effect?
**A:** Effect of one variable depends on level of another variable.

---

## Distribution Functions

### Card 86: Normal PDF
**Q:** Formula for normal distribution?
**A:** f(x) = (1/σ√2π) × e^(-(x-μ)²/2σ²)

### Card 87: Z to Percentile
**Q:** How to convert z-score to percentile?
**A:** Use standard normal table or cumulative distribution function.

### Card 88: Binomial Distribution
**Q:** When is binomial distribution used?
**A:** Fixed number of trials, each with success/failure outcome.

### Card 89: Poisson Distribution
**Q:** When is Poisson distribution used?
**A:** Counting events in fixed interval when events are independent.

### Card 90: Exponential Distribution
**Q:** When is exponential distribution used?
**A:** Time between events in Poisson process; memoryless property.

---

## Practical Tips

### Card 91: Check Assumptions First
**Q:** Why check assumptions before tests?
**A:** Violated assumptions lead to invalid conclusions.

### Card 92: Plot Your Data
**Q:** What visualizations to create first?
**A:** Histogram, box plot, scatter plot, Q-Q plot.

### Card 93: Report Centrality and Spread
**Q:** Always report what with central tendency?
**A:** Mean with std or median with IQR; never just mean for skewed data.

### Card 94: Consider Sample Size
**Q:** How does sample size affect tests?
**A:** Large n detects smaller effects as significant; practical vs statistical significance.

### Card 95: Pre-registration
**Q:** Why pre-register hypotheses?
**A:** Prevents p-hacking and ensures transparent analysis.

---

## Common Mistakes

### Card 96: Ignoring Outliers
**Q:** What to do with outliers?
**A:** Investigate, report, consider robust methods; don't automatically remove.

### Card 97: Confusing Correlation with Causation
**Q:** What to consider when seeing correlation?
**A:** Could be due to third variable, reverse causation, or coincidence.

### Card 98: Multiple Testing Without Correction
**Q:** What happens without multiple testing correction?
**A:** Increased probability of false positives; p-values become unreliable.

### Card 99: Assuming Normality
**Q:** When can you assume normality?
**A:** Large sample (n > 30) by CLT, or verified with normality test.

### Card 100: Overinterpreting Small Effects
**Q:** Why be cautious with small p-values?
**A:** Statistically significant doesn't mean practically important.