# Why Statistics Matters

Statistics is the science of learning from data — increasingly critical in the age of big data and AI.

## Applications

| Domain | Use |
|--------|-----|
| Data Science | Exploratory analysis, feature engineering, model evaluation |
| A/B Testing | Website design, marketing campaigns |
| Epidemiology | Vaccine efficacy, risk factors, causal inference |
| Finance | Portfolio risk, market analysis |
| Sports Analytics | Player performance, game strategy |
| Machine Learning | Model evaluation, overfitting detection, feature selection |

## In Java

```java
// Java provides DescriptiveStatistics in Commons Math
DescriptiveStatistics stats = new DescriptiveStatistics();
stats.addValue(1.2);
stats.addValue(3.4);
stats.addValue(5.6);
System.out.println("Mean: " + stats.getMean());
System.out.println("StdDev: " + stats.getStandardDeviation());
```
