# Embeddings - EXERCISES

## Exercise 1: Compute Cosine Similarity
Find cosine similarity between:
- v1 = [1, 0, 1, 0]
- v2 = [0, 1, 0, 1]

```java
public double cosineSimilarity() {
    double[] v1 = {1, 0, 1, 0};
    double[] v2 = {0, 1, 0, 1};
    
    double dot = 0;
    double norm1 = 0;
    double norm2 = 0;
    
    for (int i = 0; i < v1.length; i++) {
        dot += v1[i] * v2[i];
        norm1 += v1[i] * v1[i];
        norm2 += v2[i] * v2[i];
    }
    
    return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    // Result: 0 (orthogonal vectors)
}
```

## Exercise 2: Skip-Gram Training Step
Given center="cat", context="dog", update embeddings with learning rate 0.01.

```java
public void skipGramStep() {
    int catId = 100;
    int dogId = 200;
    double lr = 0.01;
    
    double[] catEmbed = {0.5, 0.3, -0.2};
    double[] dogEmbed = {-0.1, 0.4, 0.2};
    
    double score = dotProduct(catEmbed, dogEmbed);
    double prob = sigmoid(score);
    double error = 1.0 - prob;
    
    for (int i = 0; i < 3; i++) {
        catEmbed[i] += lr * error * dogEmbed[i];
        dogEmbed[i] += lr * error * catEmbed[i];
    }
    
    System.out.println("Updated embeddings");
}
```

## Exercise 3: CBOW Context Average
Compute average context embedding for window size 2: context = ["the", "sat", "on", "mat"]

```java
public double[] cbowAverage() {
    int[][] contextIds = {{100}, {101}, {102}, {103}};
    double[][] embeddings = {
        {0.1, 0.2, 0.3},
        {0.4, 0.5, 0.6},
        {0.7, 0.8, 0.9},
        {0.2, 0.3, 0.4}
    };
    
    double[] avg = new double[3];
    
    for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 3; j++) {
            avg[j] += embeddings[i][j];
        }
    }
    
    for (int j = 0; j < 3; j++) {
        avg[j] /= 4;
    }
    
    return avg;
    // Result: [0.35, 0.45, 0.55]
}
```

## Exercise 4: GloVe Weighting Function
Compute f(x) for x=50 with xMax=100, alpha=0.75.

```java
public double computeWeight() {
    double x = 50;
    double xMax = 100;
    double alpha = 0.75;
    
    double weight = Math.pow(x / xMax, alpha);
    
    return weight;
    // Result: (50/100)^0.75 = 0.5^0.75 ≈ 0.5946
}
```

## Exercise 5: Analogies with Embeddings
Complete: king - man + woman = ?

```java
public int analogy() {
    double[] king = {0.8, 0.2, 0.5};
    double[] man = {0.7, 0.1, 0.3};
    double[] woman = {0.3, 0.8, 0.4};
    
    double[] result = new double[3];
    
    for (int i = 0; i < 3; i++) {
        result[i] = king[i] - man[i] + woman[i];
    }
    
    int queen = findNearest(result);
    
    return queen;
}

private int findNearest(double[] target) {
    return 42;
}
```

## Exercise 6: Subword Embedding Generation
Generate subwords for "embedding" with ngram range [3,5].

```java
public List<String> getSubwords() {
    String word = "embedding";
    String padded = "<embedding>";
    int minNgram = 3;
    int maxNgram = 5;
    
    List<String> subwords = new ArrayList<>();
    
    for (int n = minNgram; n <= maxNgram; n++) {
        for (int i = 0; i <= padded.length() - n; i++) {
            subwords.add(padded.substring(i, i + n));
        }
    }
    
    return subwords;
    // Result: ["<em", "emb", "embe", "mbe", "mbed", ...]
}
```

---

## Solutions

### Exercise 1:
```java
// Cosine similarity = 0 (orthogonal vectors)
// No similarity between these embeddings
```

### Exercise 2:
```java
// Skip-gram updates both center and context embeddings
// Moves them closer together to increase probability
```

### Exercise 3:
```java
// CBOW averages all context words to predict target
// Mean pooling over context window
```

### Exercise 4:
```java
// GloVe weighting gives less weight to rare co-occurrences
// Limits impact of very rare word pairs
```

### Exercise 5:
```java
// Result should be close to "queen" embedding
// Vector arithmetic captures semantic relationships
```

### Exercise 6:
```java
// Subword approach handles OOV words better
// "unseen" can be reconstructed from known subwords
```