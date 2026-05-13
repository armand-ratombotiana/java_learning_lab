# Causal Inference Flashcards

**Card 1:** Potential Outcomes - Y(1) = outcome if treated, Y(0) = outcome if not treated

**Card 2:** ATE = E[Y(1)] - E[Y(0)] - average effect of treatment across population

**Card 3:** ATT = E[Y(1)-Y(0) | T=1] - effect for those who received treatment

**Card 4:** Propensity Score = P(T=1|X) - probability of treatment given covariates

**Card 5:** IPW Estimator = E[(T*Y)/e(X) - ((1-T)*Y)/(1-e(X))]

**Card 6:** Ignorability = (Y(0), Y(1) ⊥ T | X) - treatment assignment independent of potential outcomes given X

**Card 7:** Positivity = P(T=1|X) > 0 for all X - everyone has chance of treatment

**Card 8:** SUTVA - Stable Unit Treatment Value Assumption: no interference, consistent treatments

**Card 9:** Selection Bias - systematic difference between treated and control groups

**Card 10:** Matching - pair treated units with similar control units by propensity score

**Card 11:** Balance Check - verify covariates are similar across treatment groups (SMD < 0.1)

**Card 12:** DiD = (Post_T - Pre_T) - (Post_C - Pre_C) - difference in differences

**Card 13:** Parallel Trends - in DiD, treatment and control would have evolved similarly without treatment

**Card 14:** Instrumental Variable - variable affecting treatment but not outcome except through treatment

**Card 15:** LATE - Local Average Treatment Effect = effect for those who comply with assignment

**Card 16:** 2SLS - Two-stage least squares: (1) predict treatment from instrument, (2) predict outcome

**Card 17:** RD Design - exploit cutoff/threshold where treatment changes discontinuously

**Card 18:** Synthetic Control - weighted combination of units to create counterfactual

**Card 19:** Confounder - common cause of treatment and outcome; must control for

**Card 20:** Overlap/Common Support - propensity scores overlap between groups (0.1 < e(X) < 0.9)