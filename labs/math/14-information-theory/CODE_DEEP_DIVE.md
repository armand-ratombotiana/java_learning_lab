# Code Deep Dive: Information Theory in Java

## 1. Entropy Calculation

### 1.1 From Probabilities
```java
public static double entropy(double[] probabilities) {
    double h = 0;
    for (double p : probabilities) {
        if (p > 0) {
            h -= p * log2(p);
        }
    }
    return h;
}
```

**Key insights:**
- Entropy is measured in bits (base-2 logarithms)
- 0 log 0 = 0 by convention (limit as p → 0)
- Maximum entropy for uniform distribution: log₂(n)
- Minimum entropy (0) for deterministic distribution

### 1.2 From Text
```java
public static double entropy(String text) {
    Map<Character, Long> freq = text.chars()
        .mapToObj(c -> (char) c)
        .collect(Collectors.groupingBy(c -> c, Collectors.counting()));
    double total = text.length();
    double h = 0;
    for (long count : freq.values()) {
        double p = count / total;
        h -= p * log2(p);
    }
    return h;
}
```

This computes the empirical entropy of character frequencies in a text. For English text, typical entropy is about 4.5 bits per character (much less than log₂(26) ≈ 4.7 due to structure).

## 2. Mutual Information

```java
public static double mutualInformation(double[][] jointProbs) {
    double mi = 0;
    int rows = jointProbs.length, cols = jointProbs[0].length;
    double[] marginalsX = new double[rows];
    double[] marginalsY = new double[cols];
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            marginalsX[i] += jointProbs[i][j];
            marginalsY[j] += jointProbs[i][j];
        }
    }
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            double p = jointProbs[i][j];
            if (p > 0 && marginalsX[i] > 0 && marginalsY[j] > 0) {
                mi += p * log2(p / (marginalsX[i] * marginalsY[j]));
            }
        }
    }
    return mi;
}
```

**Interpretation:**
- MI = 0 for independent variables: p(x,y) = p(x)p(y)
- MI = H(X) for deterministic: Y = f(X)
- MI = H(Y) for deterministic: X = g(Y)
- MI = H(X) = H(Y) for bijective mapping

## 3. KL Divergence

```java
public static double klDivergence(double[] p, double[] q) {
    if (p.length != q.length) throw new IllegalArgumentException("Distributions must have same length");
    double kl = 0;
    for (int i = 0; i < p.length; i++) {
        if (p[i] > 0) {
            if (q[i] == 0) throw new IllegalArgumentException("KL divergence undefined: q[i] = 0 with p[i] > 0");
            kl += p[i] * log2(p[i] / q[i]);
        }
    }
    return kl;
}
```

**Important:** KL divergence is finite only when q's support contains p's support (absolute continuity). This is why we throw an error when p[i] > 0 and q[i] = 0.

## 4. Huffman Coding

### 4.1 Tree Construction
```java
public static Map<String, String> huffmanEncoding(Map<String, Double> symbols) {
    PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(
        Comparator.comparingDouble(n -> n.probability));
    for (var entry : symbols.entrySet()) {
        pq.add(new HuffmanNode(entry.getValue(), entry.getKey(), null, null));
    }
    while (pq.size() > 1) {
        HuffmanNode left = pq.poll();
        HuffmanNode right = pq.poll();
        pq.add(new HuffmanNode(left.probability + right.probability(), null, left, right));
    }
    // ... build codes from tree
}
```

**Algorithm:**
1. Create leaf node for each symbol with its probability
2. Repeatedly merge the two lowest-probability nodes
3. Assign '0' to left branches, '1' to right branches
4. Code for each symbol is the path from root to leaf

### 4.2 Optimality
Huffman coding is optimal for symbol-by-symbol coding with known probabilities. The average code length L satisfies:
H(X) ≤ L < H(X) + 1

## 5. Channel Capacity

```java
public static double channelCapacity(double[][] channelMatrix, int iterations) {
    // Blahut-Arimoto algorithm
    int inputSize = channelMatrix.length;
    int outputSize = channelMatrix[0].length;
    double[] inputDist = new double[inputSize];
    Arrays.fill(inputDist, 1.0 / inputSize);
    
    for (int iter = 0; iter < iterations; iter++) {
        // Update output distribution
        double[] outputDist = new double[outputSize];
        for (int j = 0; j < outputSize; j++)
            for (int i = 0; i < inputSize; i++)
                outputDist[j] += inputDist[i] * channelMatrix[i][j];
        
        // Update input distribution via exponential weighting
        double[] phi = new double[inputSize];
        for (int i = 0; i < inputSize; i++)
            for (int j = 0; j < outputSize; j++)
                if (channelMatrix[i][j] > 0 && outputDist[j] > 0)
                    phi[i] += channelMatrix[i][j] * log2(channelMatrix[i][j] / outputDist[j]);
        
        double sum = 0;
        for (int i = 0; i < inputSize; i++) {
            inputDist[i] = Math.exp(phi[i]);
            sum += inputDist[i];
        }
        for (int i = 0; i < inputSize; i++) inputDist[i] /= sum;
    }
    // Compute final capacity
    return capacity;
}
```

The Blahut-Arimoto algorithm iteratively maximizes mutual information. It alternates between estimating the output distribution and updating the input distribution.

## 6. Practical Considerations

### 6.1 Numerical Stability
- Add small epsilon to avoid log(0)
- Use Math.log(x) / Math.log(2) for base-2 logarithms
- Handle degenerate cases (zero entropy, perfect correlation)

### 6.2 Performance
- Entropy calculation is O(n) for n symbols
- Mutual information for discrete variables is O(m·n)
- Huffman coding with a priority queue is O(n log n)
- Channel capacity via Blahut-Arimoto typically converges in O(iterations·m·n)

### 6.3 Extensions
- Joint entropy: O(m·n) for m×n joint distribution
- Conditional entropy: derived from joint and marginal
- Cross entropy: widely used in ML classification loss
- Information bottleneck: trade-off between compression and prediction
