# Security in Statistics

## Statistical Disclosure

Aggregate statistics can leak individual information:

- **Differential privacy**: add calibrated noise to statistics
- **k-Anonymity**: ensure each combination of identifying attributes appears at least $k$ times

```java
// Laplace mechanism for differential privacy
public static double noisyCount(long trueCount, double epsilon) {
    double scale = 1.0 / epsilon;
    double noise = new Random().nextGaussian() * scale;
    return trueCount + noise;
}
```

## Adversarial Statistics

Attackers can craft data to manipulate statistical conclusions:

- **Data poisoning**: injecting malicious training data
- **Evasion attacks**: crafting inputs that fool classifiers
- **Membership inference**: determining if a record was in training data

## Safe Statistical Releases

```java
// Never release per-row statistics
// Always aggregate, potentially with noise
// Check for small cell counts (e.g., < 5)
if (cellCount < 5) suppressCell(cell);
```

## Model Security

- ML models memorize training data — potential for extraction attacks
- Use differential privacy during training (DP-SGD)
- Validate model outputs before release
