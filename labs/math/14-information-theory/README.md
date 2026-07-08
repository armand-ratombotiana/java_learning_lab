# Information Theory

The mathematics of information, entropy, and communication — foundational for compression and machine learning.

## Scope

- Entropy: Shannon entropy, joint entropy, conditional entropy
- Mutual information and its properties
- KL divergence and cross entropy
- Channel capacity via Blahut-Arimoto algorithm
- Source coding: Huffman and Shannon-Fano encoding
- Data compression principles
- Rate-distortion theory
- Applications in machine learning and communications

## Java Implementation

```java
package com.math14;

public class InformationTheory {
    public static double entropy(double[] probabilities) { /* ... */ }
    public static double entropy(String text) { /* ... */ }
    public static double mutualInformation(double[][] jointProbs) { /* ... */ }
    public static double klDivergence(double[] p, double[] q) { /* ... */ }
    public static double channelCapacity(double[][] channelMatrix, int iterations) { /* ... */ }
    public static Map<String, String> huffmanEncoding(Map<String, Double> symbols) { /* ... */ }
}
```

## Key Topics

- Shannon entropy as a measure of uncertainty
- Information content of random variables
- Mutual information for measuring dependence
- KL divergence for comparing distributions
- Channel capacity and the Shannon limit
- Optimal source coding with Huffman codes
- Cross entropy as a loss function
- Applications: compression, communication, ML

## Prerequisites

- Probability theory (random variables, distributions)
- Basic calculus and logarithms

## How to Use This Lab

1. Read THEORY.md for comprehensive mathematical treatment
2. Review MATH_FOUNDATION.md for prerequisite review
3. Study CODE_DEEP_DIVE.md for implementation patterns
4. Complete EXERCISES.md problem sets
5. Build the MINI_PROJECT for hands-on application
6. Challenge yourself with the REAL_WORLD_PROJECT

## Time Estimate

- Theory study: 2.5 hours
- Code implementation: 2.5 hours
- Exercises: 2 hours
- Projects: 3 hours
- **Total: ~10 hours**

## Difficulty: ★★★★☆ (Advanced)
