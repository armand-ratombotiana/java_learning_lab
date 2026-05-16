# Probability for ML - EXERCISES

## Exercise 1: Basic Probability
Calculate P(A|B) given:
- P(A) = 0.3
- P(B) = 0.5
- P(A ∩ B) = 0.15

**Answer**: P(A|B) = P(A ∩ B) / P(B) = 0.15/0.5 = 0.3

## Exercise 2: Bayes' Theorem
Medical test: 99% sensitivity, 95% specificity.
Disease prevalence: 1%. Find P(Disease|+).

```java
public double bayesDisease(double sensitivity, double specificity, double prevalence) {
    // P(+|D) = sensitivity = 0.99
    // P(-|~D) = specificity = 0.95
    // P(D) = prevalence = 0.01
    // P(+|~D) = 1 - specificity = 0.05
    
    double pPositiveGivenDisease = sensitivity;
    double pPositiveGivenNoDisease = 1 - specificity;
    double pDisease = prevalence;
    double pNoDisease = 1 - prevalence;
    
    double pPositive = (pPositiveGivenDisease * pDisease) + 
                       (pPositiveGivenNoDisease * pNoDisease);
    
    return (pPositiveGivenDisease * pDisease) / pPositive;
}
// Result: ~0.166 or 16.6%
```

## Exercise 3: Gaussian PDF
Implement Gaussian PDF with mean=0, std=1 at x=0.

```java
public double gaussianPDF(double x, double mean, double std) {
    double coeff = 1.0 / (std * Math.sqrt(2 * Math.PI));
    double exponent = -Math.pow(x - mean, 2) / (2 * std * std);
    return coeff * Math.exp(exponent);
}
// At x=0, mean=0, std=1: result ≈ 0.3989
```

## Exercise 4: Entropy
Calculate entropy of fair coin: P(H)=0.5, P(T)=0.5

**Answer**: H = -0.5*log₂(0.5) - 0.5*log₂(0.5) = 1 bit

## Exercise 5: MLE for Normal Distribution
Find MLE for data: [2, 4, 4, 4, 5, 5, 7, 9]

```java
public double[] normalMLE(double[] data) {
    double mean = Arrays.stream(data).average().orElse(0);
    double variance = Arrays.stream(data)
        .map(x -> (x - mean) * (x - mean))
        .average().orElse(0);
    return new double[]{mean, Math.sqrt(variance)};
}
// mean ≈ 5, std ≈ 2
```

## Exercise 6: Naive Bayes Classification
Classify: X = [1.5, 2.5] with two classes

```java
public String naiveBayesClassify(double[] x, 
                                 Map<String, GaussianDistribution> dists,
                                 Map<String, Double> priors) {
    String bestClass = null;
    double maxProb = Double.NEGATIVE_INFINITY;
    
    for (String c : dists.keySet()) {
        double logProb = Math.log(priors.get(c));
        GaussianDistribution[] classDists = dists.get(c);
        
        for (int i = 0; i < x.length; i++) {
            logProb += Math.log(classDists[i].pdf(x[i]) + 1e-10);
        }
        
        if (logProb > maxProb) {
            maxProb = logProb;
            bestClass = c;
        }
    }
    return bestClass;
}
```

---

## Solutions

### Exercise 1:
```java
public double conditionalProb(double pAandB, double pB) {
    return pAandB / pB;  // 0.15/0.5 = 0.3
}
```

### Exercise 2:
```java
// See solution in exercise description
// P(Disease|+) ≈ 0.166
```

### Exercise 3:
```java
public double gaussianPDF(double x, double mean, double std) {
    double coeff = 1.0 / (std * Math.sqrt(2 * Math.PI));
    double exponent = -Math.pow(x - mean, 2) / (2 * std * std);
    return coeff * Math.exp(exponent);
}
// Answer: 0.3989
```

### Exercise 4:
```java
public double coinEntropy() {
    double pH = 0.5, pT = 0.5;
    return -pH * (Math.log(pH) / Math.log(2)) 
           - pT * (Math.log(pT) / Math.log(2));
}
// Answer: 1.0
```