# Why Probability Matters

Probability is essential for making decisions under uncertainty.

## Applications

| Domain | Use |
|--------|-----|
| Machine Learning | Model uncertainty, Bayesian inference, entropy |
| Finance | Option pricing, risk management, portfolio optimization |
| Medicine | Diagnostic test accuracy, drug trial analysis |
| Engineering | Reliability analysis, quality control, fault tolerance |
| Games | AI decision-making under uncertainty (poker, Go) |
| Physics | Statistical mechanics, quantum mechanics |

## In Java

```java
// Monte Carlo simulation
Random rng = new Random();
int hits = 0;
for (int i = 0; i < 1_000_000; i++) {
    double x = rng.nextDouble();
    double y = rng.nextDouble();
    if (x * x + y * y <= 1) hits++;
}
double pi = 4.0 * hits / 1_000_000; // ≈ 3.1416
```
