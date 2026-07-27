# PROBLEM WALKTHROUGH: Word2Vec Skip-Gram with Negative Sampling

## Problem Statement

**Difficulty: Hard | Category: NLP / Embeddings**

Implement the Skip-Gram training algorithm with Negative Sampling from scratch. Given a corpus of text, your task is to build a word embedding model that learns dense vector representations of words such that semantically similar words have similar vectors.

**Interview Context:** This is a classic problem asked at top AI/ML companies (Google, OpenAI, Meta). The interviewer expects you to understand not just the forward pass but the mathematical derivations for backpropagation, the sampling distribution for negative samples, and the efficiency trade-offs compared to full softmax.

### Requirements

1. Parse a text corpus into a vocabulary with frequency counts.
2. Build a Skip-Gram training pair generator (center word → context word within a window).
3. Implement the Negative Sampling loss function to avoid full softmax over the vocabulary.
4. Train embedding matrices (input → projection, projection → output) using stochastic gradient descent.
5. Provide a `cosineSimilarity(word1, word2)` method to measure semantic similarity.
6. Support vector arithmetic: `king - man + woman ≈ queen`.

### Input/Output Contract

```
Input:  Corpus file path, embedding dimension d=100, window size=5, 
        negative samples k=5, learning rate=0.01, epochs=5
Output: Embedding matrix of size |V| × d, trained weights, 
        similarity queries, vector arithmetic results
```

---

## Step-by-Step Solution Walkthrough

### 1. Understanding Skip-Gram

In the Skip-Gram model, given a center word `w_c`, we predict its surrounding context words `w_o` within a window of size `m`. The objective is to maximize:

```
P(w_o | w_c) = exp(u_o^T v_c) / Σ_{j=1}^{|V|} exp(u_j^T v_c)
```

where `v_c` is the input embedding of the center word and `u_o` is the output embedding of the context word.

**Problem:** The denominator sums over the entire vocabulary `|V|` (often 100k+ words), making full softmax computationally prohibitive.

### 2. Negative Sampling to the Rescue

Instead of full softmax, Negative Sampling converts this into a binary classification problem:

- **Positive sample:** `(center_word, context_word)` — we want `P(D=1 | w_c, w_o)` to be high.
- **Negative samples:** `(center_word, random_word)` — we want `P(D=0 | w_c, w_neg)` to be high.

The loss for one training example `(w_c, w_o)` with `k` negative samples becomes:

```
L = -log σ(u_o^T v_c) - Σ_{i=1}^{k} log σ(-u_{neg_i}^T v_c)
```

where `σ(x) = 1 / (1 + exp(-x))` is the sigmoid function.

**Gradient derivation for positive sample:**

Let `x = u_o^T v_c`. The contribution to loss is `L_pos = -log σ(x)`.

```
∂L_pos/∂x = -1/σ(x) * σ(x) * (1 - σ(x)) = σ(x) - 1
```

So `∂L_pos/∂u_o = (σ(u_o^T v_c) - 1) * v_c` and `∂L_pos/∂v_c = (σ(u_o^T v_c) - 1) * u_o`.

**Gradient derivation for negative sample:**

Let `x_neg = u_{neg}^T v_c`. The contribution is `L_neg = -log σ(-x_neg)`.

```
∂L_neg/∂x_neg = -1/σ(-x_neg) * σ(-x_neg) * (1 - σ(-x_neg)) * (-1) = σ(-x_neg) - 0
```

Wait — let me re-derive carefully.

`L_neg = -log(σ(-x))`. Let `y = -x`. Then `L_neg = -log(σ(y))`.

`dL_neg/dy = -(1/σ(y)) * σ(y) * (1 - σ(y)) = σ(y) - 1`.

`dy/dx = -1`. So `dL_neg/dx = (σ(y) - 1) * (-1) = 1 - σ(y) = 1 - σ(-x) = σ(x)`.

`∂L_neg/∂u_neg = σ(u_{neg}^T v_c) * v_c`
`∂L_neg/∂v_c = σ(u_{neg}^T v_c) * u_neg`

### 3. Noise Distribution for Negative Sampling

Words are sampled according to a noise distribution designed to balance frequent and rare words:

```
P_noise(w) = (count(w)^(3/4)) / Σ_{j} (count(j)^(3/4))
```

The `3/4` power dampens the probability of very frequent words (like "the", "a") and boosts rare words, making negative samples more informative.

### 4. Training Algorithm

```
for each epoch:
    for each center word w_c in corpus:
        for each context word w_o in window:
            // Positive update
            error = σ(u_o^T v_c) - 1
            u_o += learning_rate * error * v_c
            v_c += learning_rate * error * u_o
            
            // Negative updates
            for i = 1 to k:
                w_neg ~ P_noise(w)
                error = σ(u_neg^T v_c)
                u_neg += learning_rate * error * v_c
                v_c += learning_rate * error * u_neg
```

### 5. Cosine Similarity & Vector Arithmetic

**Cosine similarity** between two word vectors `v_a` and `v_b`:

```
cosine_sim(a, b) = (v_a · v_b) / (||v_a|| * ||v_b||)
```

**Vector arithmetic:** To find `king - man + woman`, we compute the resulting vector and find the nearest word by cosine similarity.

---

## Java Implementation

```java
package com.llm.genai.deep.embeddings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Implements the Word2Vec Skip-Gram model with Negative Sampling.
 * <p>
 * Trains dense word embeddings from a text corpus by predicting context
 * words from center words using binary classification with negative samples.
 */
public class SkipGram {

    private final int embeddingDimension;
    private final int windowSize;
    private final int negativeSamples;
    private final double learningRate;
    private final int epochs;
    private final Map<String, Integer> wordToIdx;
    private final List<String> idxToWord;
    private double[][] inputWeights;
    private double[][] outputWeights;
    private double[] unigramTable;
    private int tableSize;
    private static final int UNIGRAM_TABLE_SIZE = 100_000_000;
    private static final double NEGATIVE_POWER = 0.75;

    /**
     * Constructs a SkipGram trainer with specified hyperparameters.
     *
     * @param embeddingDimension size of the dense embedding vectors
     * @param windowSize         maximum context window radius
     * @param negativeSamples    number of negative samples per positive pair
     * @param learningRate       SGD learning rate
     * @param epochs             number of full training passes
     */
    public SkipGram(int embeddingDimension, int windowSize,
                    int negativeSamples, double learningRate, int epochs) {
        this.embeddingDimension = embeddingDimension;
        this.windowSize = windowSize;
        this.negativeSamples = negativeSamples;
        this.learningRate = learningRate;
        this.epochs = epochs;
        this.wordToIdx = new HashMap<>();
        this.idxToWord = new ArrayList<>();
    }

    /**
     * Tokenizes text into lowercase words, filtering non-alphabetic tokens.
     *
     * @param text raw input text
     * @return list of normalized tokens
     */
    private List<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase().split("\\s+"))
                .filter(w -> w.matches("[a-z]+"))
                .collect(Collectors.toList());
    }

    /**
     * Builds vocabulary from token list, keeping only words with frequency >= minCount.
     *
     * @param tokens   corpus tokens
     * @param minCount minimum word frequency to include
     */
    private void buildVocabulary(List<String> tokens, int minCount) {
        Map<String, Integer> freqMap = new HashMap<>();
        for (String token : tokens) {
            freqMap.merge(token, 1, Integer::sum);
        }
        wordToIdx.clear();
        idxToWord.clear();
        idxToWord.add(null); // index 0 reserved for unknown
        for (var entry : freqMap.entrySet()) {
            if (entry.getValue() >= minCount) {
                wordToIdx.put(entry.getKey(), idxToWord.size());
                idxToWord.add(entry.getKey());
            }
        }
    }

    /**
     * Builds the unigram sampling table for negative sampling.
     * Each word appears proportionally to count(w)^0.75 in the table.
     *
     * @param wordFrequencies map of word to raw frequency
     */
    private void buildUnigramTable(Map<String, Integer> wordFrequencies) {
        double total = 0.0;
        double[] weights = new double[idxToWord.size()];
        for (int i = 1; i < idxToWord.size(); i++) {
            String word = idxToWord.get(i);
            weights[i] = Math.pow(wordFrequencies.get(word), NEGATIVE_POWER);
            total += weights[i];
        }
        tableSize = UNIGRAM_TABLE_SIZE;
        unigramTable = new double[tableSize];
        double cumulative = 0.0;
        int idx = 1;
        for (int i = 0; i < tableSize; i++) {
            cumulative += weights[idx] / total;
            int needed = (int) Math.ceil(cumulative * tableSize - i);
            while (needed-- > 0 && i < tableSize) {
                unigramTable[i++] = idx;
            }
            idx++;
            cumulative = 0.0;
            i--;
        }
    }

    /**
     * Initializes weight matrices with small random values.
     */
    private void initializeWeights() {
        int vocabSize = idxToWord.size();
        inputWeights = new double[vocabSize][embeddingDimension];
        outputWeights = new double[vocabSize][embeddingDimension];
        double scale = 0.5 / embeddingDimension;
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        for (int i = 1; i < vocabSize; i++) {
            for (int j = 0; j < embeddingDimension; j++) {
                inputWeights[i][j] = rng.nextDouble(-scale, scale);
                outputWeights[i][j] = rng.nextDouble(-scale, scale);
            }
        }
    }

    /**
     * Trains the Skip-Gram model on the provided corpus.
     *
     * @param corpusPath path to the training text file
     * @param minCount   minimum word frequency for vocabulary inclusion
     */
    public void train(String corpusPath, int minCount) throws IOException {
        System.out.println("Reading corpus...");
        String text = readFile(corpusPath);
        List<String> tokens = tokenize(text);
        System.out.println("Tokens: " + tokens.size());

        System.out.println("Building vocabulary...");
        buildVocabulary(tokens, minCount);
        System.out.println("Vocabulary size: " + idxToWord.size());

        Map<String, Integer> freqMap = new HashMap<>();
        for (String t : tokens) {
            if (wordToIdx.containsKey(t)) {
                freqMap.merge(t, 1, Integer::sum);
            }
        }
        buildUnigramTable(freqMap);
        initializeWeights();

        // Convert tokens to indices for fast training
        int[] corpusIndices = tokens.stream()
                .filter(wordToIdx::containsKey)
                .mapToInt(wordToIdx::get)
                .toArray();

        System.out.println("Starting training for " + epochs + " epochs...");
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (int epoch = 0; epoch < epochs; epoch++) {
            long startTime = System.currentTimeMillis();
            double totalLoss = 0.0;
            int pairs = 0;

            for (int pos = 0; pos < corpusIndices.length; pos++) {
                int centerIdx = corpusIndices[pos];
                int dynamicWindow = rng.nextInt(windowSize) + 1;

                for (int offset = -dynamicWindow; offset <= dynamicWindow; offset++) {
                    if (offset == 0) continue;
                    int contextPos = pos + offset;
                    if (contextPos < 0 || contextPos >= corpusIndices.length) continue;
                    int contextIdx = corpusIndices[contextPos];

                    // Forward: compute sigmoid score for positive pair
                    double score = dotProduct(
                            outputWeights[contextIdx], inputWeights[centerIdx]);
                    double sigma = sigmoid(score);
                    totalLoss += Math.log(sigma);
                    pairs++;

                    // Gradient for positive: sigma - 1
                    double gradPos = sigma - 1.0;
                    for (int d = 0; d < embeddingDimension; d++) {
                        double inputDelta = learningRate * gradPos * outputWeights[contextIdx][d];
                        double outputDelta = learningRate * gradPos * inputWeights[centerIdx][d];
                        inputWeights[centerIdx][d] += inputDelta;
                        outputWeights[contextIdx][d] += outputDelta;
                    }

                    // Negative sampling
                    for (int neg = 0; neg < negativeSamples; neg++) {
                        int negIdx = sampleNegative(rng);
                        if (negIdx == 0 || negIdx == contextIdx) continue;

                        double negScore = dotProduct(
                                outputWeights[negIdx], inputWeights[centerIdx]);
                        double negSigma = sigmoid(negScore);
                        totalLoss += Math.log(1.0 - negSigma);

                        double gradNeg = negSigma;
                        for (int d = 0; d < embeddingDimension; d++) {
                            double inputDelta = learningRate * gradNeg * outputWeights[negIdx][d];
                            double outputDelta = learningRate * gradNeg * inputWeights[centerIdx][d];
                            inputWeights[centerIdx][d] += inputDelta;
                            outputWeights[negIdx][d] += outputDelta;
                        }
                    }
                }
            }

            double avgLoss = -totalLoss / pairs;
            long elapsed = System.currentTimeMillis() - startTime;
            System.out.printf("Epoch %d: avg loss = %.4f, time = %ds%n",
                    epoch + 1, avgLoss, elapsed / 1000);
        }
        System.out.println("Training complete.");
    }

    /**
     * Reads entire file content into a string.
     */
    private String readFile(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * Sigmoid activation function.
     */
    private double sigmoid(double x) {
        if (x > 20) return 1.0;
        if (x < -20) return 0.0;
        return 1.0 / (1.0 + Math.exp(-x));
    }

    /**
     * Dot product of two vectors.
     */
    private double dotProduct(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    /**
     * Samples a negative word index from the unigram table.
     */
    private int sampleNegative(ThreadLocalRandom rng) {
        int idx = rng.nextInt(tableSize);
        return (int) unigramTable[idx];
    }

    /**
     * Returns the trained input embedding vector for a word.
     *
     * @param word the query word
     * @return embedding vector, or null if word not in vocabulary
     */
    public double[] getEmbedding(String word) {
        Integer idx = wordToIdx.get(word);
        if (idx == null) return null;
        return inputWeights[idx].clone();
    }

    /**
     * Computes cosine similarity between two words.
     *
     * @param wordA first word
     * @param wordB second word
     * @return cosine similarity in [-1, 1]
     */
    public double cosineSimilarity(String wordA, String wordB) {
        double[] vecA = getEmbedding(wordA);
        double[] vecB = getEmbedding(wordB);
        if (vecA == null || vecB == null) {
            throw new IllegalArgumentException("Word not in vocabulary");
        }
        return cosineSimilarity(vecA, vecB);
    }

    /**
     * Computes cosine similarity between two embedding vectors.
     *
     * @param vecA first vector
     * @param vecB second vector
     * @return cosine similarity
     */
    public double cosineSimilarity(double[] vecA, double[] vecB) {
        double dot = dotProduct(vecA, vecB);
        double normA = Math.sqrt(dotProduct(vecA, vecA));
        double normB = Math.sqrt(dotProduct(vecB, vecB));
        if (normA == 0 || normB == 0) return 0.0;
        return dot / (normA * normB);
    }

    /**
     * Performs vector arithmetic: returns top-N most similar words to the
     * resulting vector from (positiveWords - negativeWords).
     *
     * @param positiveWords words to add
     * @param negativeWords words to subtract
     * @param topN          number of results to return
     * @return list of (word, similarity) pairs, excluding input words
     */
    public List<Map.Entry<String, Double>> vectorArithmetic(
            List<String> positiveWords, List<String> negativeWords, int topN) {
        double[] result = new double[embeddingDimension];
        for (String w : positiveWords) {
            double[] vec = getEmbedding(w);
            if (vec != null) {
                for (int i = 0; i < embeddingDimension; i++) {
                    result[i] += vec[i];
                }
            }
        }
        for (String w : negativeWords) {
            double[] vec = getEmbedding(w);
            if (vec != null) {
                for (int i = 0; i < embeddingDimension; i++) {
                    result[i] -= vec[i];
                }
            }
        }

        Set<String> exclude = new HashSet<>(positiveWords);
        exclude.addAll(negativeWords);

        Map<String, Double> similarities = new HashMap<>();
        for (int i = 1; i < idxToWord.size(); i++) {
            String word = idxToWord.get(i);
            if (exclude.contains(word)) continue;
            double sim = cosineSimilarity(result, inputWeights[i]);
            similarities.put(word, sim);
        }

        return similarities.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toList());
    }

    /**
     * Returns the vocabulary size.
     */
    public int getVocabSize() {
        return idxToWord.size();
    }

    /**
     * Main method demonstrating training and evaluation on a sample corpus.
     */
    public static void main(String[] args) throws IOException {
        SkipGram model = new SkipGram(100, 5, 5, 0.025, 5);
        model.train("corpus.txt", 5);

        System.out.println("\n=== Cosine Similarities ===");
        System.out.println("king - queen: " + model.cosineSimilarity("king", "queen"));
        System.out.println("good - bad: " + model.cosineSimilarity("good", "bad"));
        System.out.println("dog - cat: " + model.cosineSimilarity("dog", "cat"));

        System.out.println("\n=== Vector Arithmetic: king - man + woman ===");
        var result = model.vectorArithmetic(
                List.of("king", "woman"), List.of("man"), 5);
        for (var entry : result) {
            System.out.printf("  %s: %.4f%n", entry.getKey(), entry.getValue());
        }
    }
}
```

---

## Complexity Analysis

### Time Complexity

- **Vocabulary building:** O(V + N) where V is vocab size and N is token count.
- **Unigram table construction:** O(V + T) where T = 100M (table size).
- **Training per epoch:** O(N × 2m × (1 + k) × d)
  - `N` = corpus tokens, `m` = window size, `k` = negative samples, `d` = embedding dimension.
  - Each token pair requires 1 positive + k negative updates.
  - Each update does O(d) work for forward + backward.
  - Total: O(N × m × k × d) per epoch.

**Typical numbers:** N=10^9, m=5, k=5, d=100 → ~10^12 operations per epoch. Parallelization via HogWild! or negative sampling sub-sampling is essential.

### Space Complexity

- **Vocabulary:** O(V) for indexing.
- **Input embeddings:** O(V × d) — typically the dominant cost.
- **Output embeddings:** O(V × d) — can be discarded after training if using only input vectors.
- **Unigram table:** O(T) = 100M integers ≈ 400 MB (can be reduced).

**Optimization:** For production, store only input embeddings after training and discard the output matrix, halving memory.

---

## Follow-Up Questions

### Q1: How does Negative Sampling compare to Hierarchical Softmax?

**Answer:** Both avoid the O(V) normalization cost of full softmax.

| Aspect | Negative Sampling | Hierarchical Softmax |
|--------|------------------|---------------------|
| Speed | Faster for small k (5-20) | Faster for very large V |
| Training signal | Noisy, binary classification | Exact tree-based probability |
| Rare words | Better (more negative exposure) | Worse (long paths in Huffman tree) |
| Implementation | Simple | Requires Huffman tree |
| Memory | O(V×d) for both matrices | O(V×d) for one matrix |

Hierarchical Softmax uses a binary tree (typically Huffman) where each leaf is a word. Training is O(log V) per example but the gradient signal for rare words is diluted. Negative Sampling gives stronger gradients to rare words because they appear as negatives and force discrimination.

### Q2: Explain sub-sampling of frequent words. Why is it needed?

**Answer:** Words like "the", "a", "is" appear so frequently that they dominate training. Sub-sampling discards each word with probability:

```
P_discard(w) = 1 - sqrt(t / f(w))
```

where `t` is a threshold (typically 10^-5) and `f(w)` is the word frequency. This:
- Reduces total training pairs by 2-10x with minimal accuracy loss.
- Forces the model to focus on meaningful co-occurrences.
- Prevents frequent words from dominating the embedding updates.

### Q3: How would you extend this to the CBOW architecture?

**Answer:** CBOW (Continuous Bag of Words) predicts the center word from the average of context word embeddings. Key changes:
- Input: average of context word input embeddings → `h = (1/2m) * Σ v_context`.
- Output: predict center word using output embedding `u_c`.
- Loss: same negative sampling formulation.
- CBOW is faster and works better for frequent words; Skip-Gram is better for rare words.

### Q4: What is the role of the output embedding matrix? Why have two matrices?

**Answer:** The two matrices create an asymmetric factorization of the word-context co-occurrence matrix. At convergence, the product `W_input × W_output^T` approximates the PMI (Pointwise Mutual Information) matrix:

```
w_i · w_j ≈ PMI(w_i, w_j) = log(P(w_i, w_j) / (P(w_i) P(w_j)))
```

Using only input vectors tends to give better semantic similarity. Using averaged input + output vectors can sometimes improve quality.

### Q5: How do you evaluate embedding quality beyond analogies?

**Answer:** Four standard evaluations:
1. **Word similarity** (WordSim-353, SimLex-999): compare cosine similarities to human ratings.
2. **Analogy completion** (Google Analogy, MSR): "a is to b as c is to d".
3. **Downstream tasks**: use embeddings as features for NER, sentiment, or classification.
4. **Intrinsic clustering**: semantic categories should cluster in embedding space.

---

## Test Cases

### Test Case 1: Small Vocabulary Training

```
Corpus: "the cat sat on the mat the dog sat on the log"
Expected: Embeddings for {the, cat, sat, on, mat, dog, log}
Training pairs: (cat, the), (cat, sat), (dog, the), (dog, sat), etc.
```

### Test Case 2: Cosine Similarity Symmetry

```
Input: cosineSimilarity("cat", "dog") and cosineSimilarity("dog", "cat")
Expected: Both values identical (within floating point tolerance)
```

### Test Case 3: Self-Similarity

```
Input: cosineSimilarity("king", "king")
Expected: 1.0 (or very close to 1.0)
```

### Test Case 4: Negative Sampling Distribution

```
Input: Unigram table for vocab {"the": 100, "cat": 10, "dog": 5}
Compute: weight = count^0.75
  the: 100^0.75 = 31.62, cat: 10^0.75 = 5.62, dog: 5^0.75 = 3.34
  total = 40.59
  P(the) = 31.62/40.59 = 0.779
  P(cat) = 5.62/40.59 = 0.138
  P(dog) = 3.34/40.59 = 0.082
Expected: Frequent word "the" dominates, but less than raw frequency (100/115 = 0.870)
```

### Test Case 5: Vector Arithmetic Property

```
Input: vectorArithmetic(["king"], ["man"], 1) should be close to "king"
       vectorArithmetic(["king", "woman"], ["man"], 5) should return "queen" as top result
Expected: After sufficient training, semantic analogies emerge.
```

### Test Case 6: Gradient Check (Numerical)

```
For a single pair (center_idx=1, context_idx=2) with d=3:
  Input weights [1]: [0.1, 0.2, -0.1]
  Output weights [2]: [0.3, -0.2, 0.1]
  Score: 0.1*0.3 + 0.2*(-0.2) + (-0.1)*0.1 = 0.03 - 0.04 - 0.01 = -0.02
  sigma(-0.02) = 0.495
  grad_pos = 0.495 - 1 = -0.505
  Input update[1]: += 0.025 * (-0.505) * [0.3, -0.2, 0.1] = [-0.00379, 0.00253, -0.00126]
  Output update[2]: += 0.025 * (-0.505) * [0.1, 0.2, -0.1] = [-0.00126, -0.00253, 0.00126]
Expected: Gradients correctly scaled and applied
```

---

## Summary

This walkthrough covered the complete Skip-Gram with Negative Sampling implementation. The key mathematical insight is converting the computationally expensive multi-class softmax into efficient binary classification using noise-contrastive estimation. The Java implementation demonstrates production-quality code with proper encapsulation, vocabulary management, and training loops. Understanding these fundamentals is critical for working with modern LLM embeddings and retrieval systems.