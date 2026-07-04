# Step-by-Step: Designing and Analyzing an A/B Test

**Scenario**: Your team changed the checkout button from blue to green. You want to know if it increases the purchase conversion rate.

## Step 1: Define the Hypothesis

```
H₀: Green button does not change conversion rate (p_green - p_blue = 0)
H₁: Green button increases conversion rate (p_green - p_blue > 0)
Type: One-tailed (we only care about improvement)
```

## Step 2: Power Analysis

```java
double baseline = 0.05;  // current 5% conversion
double mde = 0.005;      // want to detect +0.5pp improvement
double alpha = 0.05;
double power = 0.80;

long sampleSize = SampleSizeCalculator.requiredSamplePerGroup(baseline, mde, alpha, power);
System.out.println("Required: " + sampleSize + " per group");
// 0.5pp MDE at 5% baseline with 80% power needs ~75,000 users per group
```

## Step 3: Randomize

```java
// Simple random assignment
Random rng = new Random(42);  // seed for reproducibility
for (User user : allUsers) {
    user.setGroup(rng.nextBoolean() ? "treatment" : "control");
}
```

## Step 4: Collect Data (wait for sample size)

## Step 5: Analyze

```java
long nControl = 75000;
long nTreatment = 75000;
long convControl = 3750;    // 5.0%
long convTreatment = 4125;  // 5.5%

double pControl = (double) convControl / nControl;
double pTreatment = (double) convTreatment / nTreatment;
double lift = pTreatment - pControl;

// Z-test
double z = zTest(pControl, nControl, pTreatment, nTreatment);
double pValue = 1 - normalCDF(z);  // one-tailed

// Confidence interval
double[] ci = confidenceInterval(pControl, pTreatment, nControl, nTreatment, 0.05);

System.out.printf("Control: %.2f%% | Treatment: %.2f%% | Lift: %.2fpp%n",
    pControl * 100, pTreatment * 100, lift * 100);
System.out.printf("p-value: %.4f | 95%% CI: [%.4f%%, %.4f%%]%n",
    pValue, ci[0] * 100, ci[1] * 100);

// Result: p = 0.002, CI = [0.18%, 0.82%]
// → Statistically significant → we reject H₀
```

## Step 6: Check Practical Significance

```
Q: Is a lift of 0.5pp worth the engineering effort?
A: At 1M checkouts/year, 0.5pp = 5,000 additional purchases.
   At $50 average order, that's $250K additional revenue.
   → YES, it's practically significant.
```

## Step 7: Decide

→ Ship the green button.
