# Transformers - EXERCISES

## Exercise 1: Scaled Dot-Product Attention
Compute attention for Q=[[1,0],[0,1]], K=[[1,0],[0,1]], V=[[1,2],[3,4]] with d_k=2.

```java
public double[][] scaledDotProductAttention() {
    double[][] Q = {{1, 0}, {0, 1}};
    double[][] K = {{1, 0}, {0, 1}};
    double[][] V = {{1, 2}, {3, 4}};
    
    double[][] QK = matmul(Q, transpose(K));
    double scale = Math.sqrt(2);
    for (int i = 0; i < 2; i++) {
        for (int j = 0; j < 2; j++) {
            QK[i][j] /= scale;
        }
    }
    
    double[][] attention = softmax(QK);
    double[][] output = matmul(attention, V);
    
    return output;
}

private double[][] matmul(double[][] A, double[][] B) {
    double[][] result = new double[A.length][B[0].length];
    for (int i = 0; i < A.length; i++) {
        for (int j = 0; j < B[0].length; j++) {
            for (int k = 0; k < A[0].length; k++) {
                result[i][j] += A[i][k] * B[k][j];
            }
        }
    }
    return result;
}

private double[][] softmax(double[][] X) {
    double[][] result = new double[X.length][X[0].length];
    for (int i = 0; i < X.length; i++) {
        double sum = 0;
        double max = X[i][0];
        for (int j = 1; j < X[0].length; j++) {
            if (X[i][j] > max) max = X[i][j];
        }
        for (int j = 0; j < X[0].length; j++) {
            sum += Math.exp(X[i][j] - max);
        }
        for (int j = 0; j < X[0].length; j++) {
            result[i][j] = Math.exp(X[i][j] - max) / sum;
        }
    }
    return result;
}

// Output: [[1, 2], [3, 4]] (identity attention)
```

## Exercise 2: Multi-Head Attention Split
For d_model=8, num_heads=4, show how to split Q into 4 heads.

```java
public double[][][] splitIntoHeads(double[][] Q) {
    int dK = 8 / 4; // 2
    int seqLen = Q.length;
    int numHeads = 4;
    
    double[][][] heads = new double[numHeads][seqLen][dK];
    
    for (int h = 0; h < numHeads; h++) {
        for (int i = 0; i < seqLen; i++) {
            System.arraycopy(Q[i], h * dK, heads[h][i], 0, dK);
        }
    }
    
    return heads;
}

// Each head gets 2 dimensions of the 8-dim query/key/value
```

## Exercise 3: Positional Encoding
Compute PE(2, 4) for position=2, dimension=4 in d_model=512.

```java
public double computePE() {
    int position = 2;
    int i = 4;
    int dModel = 512;
    
    double angle = position / Math.pow(10000, 2.0 * i / dModel);
    
    double result = (i % 2 == 0) ? Math.sin(angle) : Math.cos(angle);
    
    return result;
    // Result: sin(2 / 10000^(4/512)) ≈ sin(0.997) ≈ 0.84
}
```

## Exercise 4: Causal Mask Creation
Create causal mask for sequence length 3.

```java
public double[][] createCausalMask() {
    int seqLen = 3;
    double[][] mask = new double[seqLen][seqLen];
    
    for (int i = 0; i < seqLen; i++) {
        for (int j = 0; j < seqLen; j++) {
            if (j > i) {
                mask[i][j] = Double.NEGATIVE_INFINITY;
            } else {
                mask[i][j] = 0;
            }
        }
    }
    
    return mask;
    // Output:
    // [[0, -inf, -inf],
    //  [0, 0, -inf],
    //  [0, 0, 0]]
}
```

## Exercise 5: Layer Normalization
Apply layer norm to input with mean=2, var=1, d_model=4.

```java
public double[] layerNorm(double[] input) {
    double mean = 2.0;
    double variance = 1.0;
    double epsilon = 1e-6;
    double[] gamma = {1, 1, 1, 1};
    double[] beta = {0, 0, 0, 0};
    
    double[] normalized = new double[input.length];
    
    for (int i = 0; i < input.length; i++) {
        normalized[i] = (input[i] - mean) / Math.sqrt(variance + epsilon);
        normalized[i] = normalized[i] * gamma[i] + beta[i];
    }
    
    return normalized;
    // Normalized to zero mean, unit variance, then scaled/shifted
}
```

## Exercise 6: Transformer Inference
Trace through a single transformer block with input dim [2,4,512].

```java
public double[][] transformerBlock(double[][] input) {
    int seqLen = input.length;
    int dModel = input[0].length;
    
    // 1. Multi-head attention with residual
    double[][] attention = multiHeadAttention(input, null);
    double[][] attnResidual = add(input, attention);
    double[][] norm1 = layerNorm(attnResidual);
    
    // 2. Feed-forward with residual
    double[][] ffn = feedForward(norm1);
    double[][] ffnResidual = add(norm1, ffn);
    double[][] norm2 = layerNorm(ffnResidual);
    
    return norm2;
    // Output shape: [2, 4, 512]
}
```

---

## Solutions

### Exercise 1:
```java
// Attention scores: [[1, 0], [0, 1]]
// After softmax: [[1, 0], [0, 1]]
// Output: [[1, 2], [3, 4]] - preserves values exactly
```

### Exercise 2:
```java
// Q split into 4 heads, each with 2 dimensions
// heads[0] = Q[:, :2], heads[1] = Q[:, 2:4], etc.
```

### Exercise 3:
```java
// Result: approximately 0.84 (sin term for even dimension)
```

### Exercise 4:
```java
// Causal mask prevents attending to future positions
// Used in GPT-style autoregressive decoding
```

### Exercise 5:
```java
// Normalizes to zero mean, unit variance
// Applies learned gamma (scale) and beta (shift)
```

### Exercise 6:
```java
// Complete forward pass: attention → add & norm → FFN → add & norm
// Maintains sequence length and model dimension throughout
```