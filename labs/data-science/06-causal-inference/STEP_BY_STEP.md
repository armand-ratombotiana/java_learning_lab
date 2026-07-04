# Step-by-Step: Estimating the Effect of a Training Program

**Scenario**: A company launched a job training program. Employees self-selected into training. We want to estimate the causal effect on salary.

**Challenge**: Confounding — employees who chose training may be more motivated and would have higher salaries regardless.

## Step 1: Build the Causal DAG

```
Motivation (confounder)
   / \
  ▼   ▼
Training ──→ Salary (?)
```

We need to control for Motivation. Since Motivation is unobserved, we use proxies: prior performance score, education, years at company.

## Step 2: Estimate Propensity Scores

```java
// Fit logistic regression: P(Training=1 | PriorScore, Education, Tenure)
LogisticRegression lr = new LogisticRegression();
double[][] X = extractCovariates(data, "prior_score", "education_years", "tenure");
double[] T = data.booleanColumn("training").asDoubleArray();
lr.fit(X, T);
double[] propensity = lr.predictProbabilities(X);
System.out.println("Propensity range: [" + min(propensity) + ", " + max(propensity) + "]");
```

## Step 3: Check Overlap (Common Support)

```java
double[] treatedProp = filter(propensity, T == 1);
double[] controlProp = filter(propensity, T == 0);
System.out.println("Treated: mean=" + mean(treatedProp) + ", min=" + min(treatedProp));
System.out.println("Control: mean=" + mean(controlProp) + ", min=" + min(controlProp));
// If regions don't overlap, we can't match reliably
```

## Step 4: Match

```java
NearestNeighborMatcher matcher = new NearestNeighborMatcher();
int[] matches = matcher.match(treatedProp, controlProp);
```

## Step 5: Estimate ATE

```java
double[] salaryTreated = filter(data.doubleColumn("salary"), T == 1);
double[] salaryControl = filter(data.doubleColumn("salary"), T == 0);
double[] matchedControlSalaries = select(salaryControl, matches);

double ate = mean(salaryTreated) - mean(matchedControlSalaries);
System.out.println("ATE: $" + ate);
// $3,200 — the training program increases salary by $3,200 on average
```

## Step 6: Sensitivity Analysis

```java
// How strong would an unobserved confounder need to be to nullify the effect?
// Use rbounds package (not in Java) or simple:
// If the confounder can shift the estimate by > $3,200, our result is not robust
```
