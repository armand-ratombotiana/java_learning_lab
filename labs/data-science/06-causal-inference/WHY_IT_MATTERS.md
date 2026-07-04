# Why Causal Inference Matters

Without causal reasoning, data-driven decisions are at best random and at worst harmful. The history of data science is full of correlational mistakes.

## Costly Correlation-Equals-Causation Errors

| Claim | True Causal Story |
|---|---|
| "Ice cream causes drowning" | Summer heat causes both |
| "More police → less crime" | Cities with high crime hire more police (reverse causality) |
| "Early treatment → better outcomes" | People who seek early care are healthier (selection bias) |
| "Donation emails with sad stories get more clicks" | Sad stories sent to already-loyal donors (confounding) |

## Causal Inference in Java: What You Need

Java applications don't yet have mature causal inference libraries, but key techniques can be implemented:

- **Stratification** (group-by and compare within groups)
- **Propensity score matching** (logistic regression + nearest-neighbor matching)
- **Difference-in-differences** (pre/post + treatment/control)
- **Instrumental variables** (two-stage least squares)

```java
// Simple stratification to control for confounder
// "Is ad spend causal?" — compare within regions
for (String region : data.stringColumn("region").unique()) {
    Table regionData = data.where(data.stringColumn("region").equals(region));
    double rWithin = Smile.correlation(
        regionData.doubleColumn("ad_spend").asDoubleArray(),
        regionData.doubleColumn("sales").asDoubleArray()
    );
    System.out.println(region + ": r = " + rWithin);
}
// If r drops near zero within each region, the overall correlation was confounded
```
