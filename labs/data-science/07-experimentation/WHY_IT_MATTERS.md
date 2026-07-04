# Why Experimentation Matters

Experimentation is the only reliable way to measure the causal impact of product changes, marketing campaigns, and policy interventions. Companies that experiment systematically (Amazon, Google, Netflix, Meta) grow faster because they can confidently separate effective changes from ineffective ones.

## Business Impact of Experimentation

| Company | Experiments Per Year | Impact |
|---|---|---|
| Amazon | 10,000+ | Every major UI decision tested |
| Google | 7,000+ | Search ranking, ad placement optimized |
| Microsoft | 1,000+ | Bing revenue increased by 12% via experiments |
| Netflix | 500+ | Artwork optimization drives engagement |

## Cost of Not Experimenting

- Launching a feature that decreases revenue (worse than doing nothing)
- Killing a feature that would have increased revenue (opportunity cost)
- Building the wrong thing based on HiPPO (Highest Paid Person's Opinion)

## Java in Experimentation

Java powers the backend systems that serve experiments and analyze results at scale. Streaming computation of p-values, real-time dashboard updates, and Bayesian update systems are built on Java.

```java
// Real-time p-value computation in Java
public class SequentialTest {
    private double zScore = 0;
    private long n = 0;
    
    public void addObservation(double controlValue, double treatmentValue) {
        n++;
        double diff = treatmentValue - controlValue;
        zScore = (zScore * (n - 1) + diff) / Math.sqrt(n);
        double pValue = 2 * (1 - normalCDF(Math.abs(zScore)));
        if (pValue < 0.01) {
            System.out.println("Early stopping: significant at n=" + n);
        }
    }
}
```
