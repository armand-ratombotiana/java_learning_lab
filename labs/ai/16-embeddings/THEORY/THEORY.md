# Embeddings - Complete Theory

## 1. Word Embeddings

### 1.1 Why Embeddings?
- One-hot encoding is high-dimensional and sparse
- Similar words should have similar vectors
- Capture semantic meaning

### 1.2 Distributed Representation
Each word represented as dense vector
Meanings distributed across dimensions

## 2. Word2Vec

### 2.1 Skip-gram
Predict context words from target word
Maximize: sum_{(w,c)} log P(c|w)

### 2.2 CBOW (Continuous Bag of Words)
Predict target word from context
Average context embeddings

### 2.3 Negative Sampling
Binary classification: real vs fake pairs
Sigmoid on dot product

## 3. GloVe (Global Vectors)

### 3.1 Co-occurrence Matrix
P_ij = count(i,j) / sum_j count(i,j)

### 3.2 Objective
Minimize weighted squared difference between log co-occurrences and dot products

## 4. Contextual Embeddings

### 4.1 ELMo
Bi-directional LSTM
Layer combination for final representation

### 4.2 BERT
Transformer encoder
Masked language model pre-training

### 4.3 GPT
Transformer decoder
Next token prediction

## 5. Sentence Embeddings

### 5.1 Average Word Embeddings
Simple averaging

### 5.2 Sentence-BERT
Siamese network with BERT

### 5.3 Universal Sentence Encoder
Transformer or DAN architecture

## Java Implementation

```java
public class SkipGram {
    private Matrix wordVectors;  // vocabSize x embeddingDim
    private Matrix contextVectors;
    private int vocabSize;
    private int embeddingDim;
    
    public SkipGram(int vocabSize, int embeddingDim) {
        this.vocabSize = vocabSize;
        this.embeddingDim = embeddingDim;
        
        wordVectors = Matrix.random(vocabSize, embeddingDim, 42);
        contextVectors = Matrix.random(vocabSize, embeddingDim, 42);
    }
    
    public double train(int target, int[] context, double lr) {
        double loss = 0;
        Vector targetVec = wordVectors.getRow(target);
        
        for (int ctx : context) {
            // Score: dot product of word and context vectors
            double score = VectorOperations.dot(targetVec, 
                contextVectors.getRow(ctx));
            
            // Sigmoid
            double prob = 1.0 / (1.0 + Math.exp(-score));
            
            // Loss (binary cross-entropy)
            loss += -Math.log(prob);
            
            // Gradients
            double error = prob - 1;  // for positive context
            
            // Update vectors
            Vector gradW = VectorOperations.scale(contextVectors.getRow(ctx), error * lr);
            Vector gradC = VectorOperations.scale(targetVec, error * lr);
            
            wordVectors.data[target] = VectorOperations.subtract(
                wordVectors.getRow(target), gradW).toArray();
            contextVectors.data[ctx] = VectorOperations.subtract(
                contextVectors.getRow(ctx), gradC).toArray();
        }
        
        return loss;
    }
    
    public double similarity(int w1, int w2) {
        Vector v1 = wordVectors.getRow(w1);
        Vector v2 = wordVectors.getRow(w2);
        return cosineSimilarity(v1, v2);
    }
}
```