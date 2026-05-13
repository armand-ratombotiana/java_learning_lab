# Transformers - Complete Theory and Implementation

## 1. Attention Mechanism

### 1.1 Motivation
- RNNs have trouble with long-range dependencies
- Sequential computation prevents parallelization
- Need direct connections between all positions

### 1.2 Scaled Dot-Product Attention
Attention(Q, K, V) = softmax(QK^T / sqrt(d_k)) V

Where:
- Q: Query matrix (n x d_k)
- K: Key matrix (m x d_k)
- V: Value matrix (m x d_v)
- d_k: dimension of keys/queries

### 1.3 Multi-Head Attention
MultiHead(Q, K, V) = Concat(head_1, ..., head_h) W^O

Where each head_i = Attention(QW_i^Q, KW_i^K, VW_i^V)

### 1.4 Why Multi-Head?
- Different heads can attend to different aspects
- Learn multiple representations
- Stabilizes learning

## 2. Positional Encoding

### 2.1 Problem
RNNs have inherent sequence order
Transformers have no inherent order
Need to inject position information

### 2.2 Sinusoidal Encoding
PE(pos, 2i) = sin(pos / 10000^{2i/d})
PE(pos, 2i+1) = cos(pos / 10000^{2i/d})

### 2.3 Learnable Position Embeddings
Add learnable position embeddings

### 2.4 Relative Position Embeddings
- Learn relative distances
- Better for generalization to longer sequences

## 3. Transformer Architecture

### 3.1 Encoder
- Input embedding + positional encoding
- N layers of:
  - Multi-head self-attention
  - Feed-forward network
  - Layer normalization
  - Residual connections

### 3.2 Decoder
- Output embedding + positional encoding
- N layers of:
  - Masked multi-head self-attention
  - Encoder-decoder attention
  - Feed-forward network

### 3.3 Full Architecture
- Encoder-decoder for seq2seq
- Encoder-only for classification (BERT)
- Decoder-only for generation (GPT)

## 4. Java Implementation

### 4.1 Scaled Dot-Product Attention

```java
package com.ml.transformer;

import com.ml.la.vector.Vector;
import com.ml.la.matrix.Matrix;
import com.ml.la.matrix.MatrixOperations;

public class Attention {
    private int d_k;
    
    public Attention(int d_k) {
        this.d_k = d_k;
    }
    
    public AttentionResult forward(Matrix Q, Matrix K, Matrix V) {
        // Compute attention scores: Q K^T / sqrt(d_k)
        Matrix Kt = MatrixOperations.transpose(K);
        Matrix scores = MatrixOperations.multiply(Q, Kt);
        Matrix scaled = MatrixOperations.scale(scores, 1.0 / Math.sqrt(d_k));
        
        // Apply softmax
        Matrix attn = softmax(scaled);
        
        // Compute output: attention * V
        Matrix output = MatrixOperations.multiply(attn, V);
        
        return new AttentionResult(output, attn);
    }
    
    public AttentionResult forwardWithMask(Matrix Q, Matrix K, Matrix V, Matrix mask) {
        Matrix Kt = MatrixOperations.transpose(K);
        Matrix scores = MatrixOperations.multiply(Q, Kt);
        Matrix scaled = MatrixOperations.scale(scores, 1.0 / Math.sqrt(d_k));
        
        // Apply mask (add large negative value to masked positions)
        if (mask != null) {
            scaled = MatrixOperations.add(scaled, mask);
        }
        
        Matrix attn = softmax(scaled);
        Matrix output = MatrixOperations.multiply(attn, V);
        
        return new AttentionResult(output, attn);
    }
    
    private Matrix softmax(Matrix M) {
        int rows = M.rows();
        int cols = M.cols();
        Matrix result = new Matrix(rows, cols);
        
        for (int i = 0; i < rows; i++) {
            // Find max for numerical stability
            double maxVal = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < cols; j++) {
                maxVal = Math.max(maxVal, M.get(i, j));
            }
            
            // Compute exp and sum
            double sum = 0;
            for (int j = 0; j < cols; j++) {
                double exp = Math.exp(M.get(i, j) - maxVal);
                result.data[i][j] = exp;
                sum += exp;
            }
            
            // Normalize
            for (int j = 0; j < cols; j++) {
                result.data[i][j] /= sum;
            }
        }
        
        return result;
    }
    
    public static class AttentionResult {
        public Matrix output;
        public Matrix attention;
        
        public AttentionResult(Matrix output, Matrix attention) {
            this.output = output;
            this.attention = attention;
        }
    }
}
```

### 4.2 Multi-Head Attention

```java
package com.ml.transformer;

import com.ml.la.matrix.Matrix;
import java.util.Random;

public class MultiHeadAttention {
    private int d_model;
    private int num_heads;
    private int d_k;
    
    private Matrix[] W_Q;  // Query projections
    private Matrix[] W_K;  // Key projections
    private Matrix[] W_V;  // Value projections
    private Matrix W_O;     // Output projection
    
    private Attention attention;
    
    public MultiHeadAttention(int d_model, int num_heads) {
        this.d_model = d_model;
        this.num_heads = num_heads;
        this.d_k = d_model / num_heads;
        this.attention = new Attention(d_k);
        
        Random gen = new Random(42);
        double scale = Math.sqrt(2.0 / d_model);
        
        W_Q = new Matrix[num_heads];
        W_K = new Matrix[num_heads];
        W_V = new Matrix[num_heads];
        
        for (int h = 0; h < num_heads; h++) {
            W_Q[h] = randomMatrix(d_model, d_k, gen, scale);
            W_K[h] = randomMatrix(d_model, d_k, gen, scale);
            W_V[h] = randomMatrix(d_model, d_k, gen, scale);
        }
        
        W_O = randomMatrix(d_model, d_model, gen, scale);
    }
    
    public Matrix forward(Matrix Q, Matrix K, Matrix V) {
        return forward(Q, K, V, null);
    }
    
    public Matrix forward(Matrix Q, Matrix K, Matrix V, Matrix mask) {
        int batchSize = Q.rows();
        int seqLen = Q.cols();
        
        Matrix[] heads = new Matrix[num_heads];
        
        for (int h = 0; h < num_heads; h++) {
            // Project Q, K, V for this head
            Matrix Q_h = MatrixOperations.multiply(Q, W_Q[h]);
            Matrix K_h = MatrixOperations.multiply(K, W_K[h]);
            Matrix V_h = MatrixOperations.multiply(V, W_V[h]);
            
            // Compute attention
            AttentionResult result = attention.forwardWithMask(Q_h, K_h, V_h, mask);
            heads[h] = result.output;
        }
        
        // Concatenate heads
        Matrix concat = concatenate(heads);
        
        // Final projection
        Matrix output = MatrixOperations.multiply(concat, W_O);
        
        return output;
    }
    
    public Matrix forwardSelfAttention(Matrix X) {
        return forward(X, X, X, null);
    }
    
    public Matrix forwardSelfAttentionWithMask(Matrix X, Matrix mask) {
        return forward(X, X, X, mask);
    }
    
    private Matrix concatenate(Matrix[] heads) {
        int batchSize = heads[0].rows();
        int seqLen = heads[0].cols();
        int d_total = d_k * num_heads;
        
        Matrix result = new Matrix(batchSize, seqLen * num_heads);
        
        for (int b = 0; b < batchSize; b++) {
            for (int s = 0; s < seqLen; s++) {
                for (int h = 0; h < num_heads; h++) {
                    for (int d = 0; d < d_k; d++) {
                        int outCol = s * num_heads + h;
                        int inCol = d;
                        result.data[b][outCol] = heads[h].data[b][s * d_k + d];
                    }
                }
            }
        }
        
        return result;
    }
    
    public void backward(Matrix gradOutput, double lr) {
        // Compute gradients for all weights
        // (Simplified - full implementation would propagate through all heads)
        
        // Update weights using gradient descent
        // W_Q[h] = W_Q[h] - lr * gradW_Q[h]
        // etc.
    }
    
    private Matrix randomMatrix(int rows, int cols, Random gen, double scale) {
        double[][] data = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[i][j] = gen.nextGaussian() * scale;
            }
        }
        return new Matrix(data);
    }
}
```

### 4.3 Feed-Forward Network

```java
package com.ml.transformer;

import com.ml.la.matrix.Matrix;
import java.util.Random;

public class FeedForward {
    private Matrix W1, b1;
    private Matrix W2, b2;
    private int d_ff;
    
    public FeedForward(int d_model, int d_ff) {
        this.d_ff = d_ff;
        
        Random gen = new Random(42);
        double scale = Math.sqrt(2.0 / d_model);
        
        W1 = randomMatrix(d_ff, d_model, gen, scale);
        b1 = randomVector(d_ff, gen, scale);
        W2 = randomMatrix(d_model, d_ff, gen, scale);
        b2 = randomVector(d_model, gen, scale);
    }
    
    public Matrix forward(Matrix X) {
        // First layer: linear + ReLU
        Matrix hidden = MatrixOperations.multiply(X, MatrixOperations.transpose(W1));
        hidden = MatrixOperations.add(hidden, b1);
        hidden = relu(hidden);
        
        // Second layer: linear
        Matrix output = MatrixOperations.multiply(hidden, MatrixOperations.transpose(W2));
        output = MatrixOperations.add(output, b2);
        
        return output;
    }
    
    private Matrix relu(Matrix X) {
        return MatrixOperations.applyFunction(X, x -> Math.max(0, x));
    }
    
    private Matrix randomMatrix(int rows, int cols, Random gen, double scale) {
        double[][] data = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[i][j] = gen.nextGaussian() * scale;
            }
        }
        return new Matrix(data);
    }
    
    private Matrix randomVector(int size, Random gen, double scale) {
        double[] data = new double[size];
        for (int i = 0; i < size; i++) {
            data[i] = gen.nextGaussian() * scale;
        }
        return new Matrix(new double[][]{data});
    }
}
```

### 4.4 Transformer Layer

```java
package com.ml.transformer;

import com.ml.la.matrix.Matrix;

public class TransformerLayer {
    private MultiHeadAttention selfAttention;
    private FeedForward ff;
    private int d_model;
    
    public TransformerLayer(int d_model, int num_heads, int d_ff) {
        this.d_model = d_model;
        this.selfAttention = new MultiHeadAttention(d_model, num_heads);
        this.ff = new FeedForward(d_model, d_ff);
    }
    
    public TransformerOutput forward(Matrix X) {
        return forward(X, null, null);
    }
    
    public TransformerOutput forward(Matrix X, Matrix mask, Matrix encOutput) {
        // Self-attention with residual connection and layer norm
        Matrix attnOut = (encOutput == null) 
            ? selfAttention.forwardSelfAttention(X)
            : selfAttention.forward(X, X, X, mask);
        
        Matrix norm1 = layerNormAdd(X, attnOut);
        
        // Feed-forward with residual connection and layer norm
        Matrix ffOut = ff.forward(norm1);
        Matrix norm2 = layerNormAdd(norm1, ffOut);
        
        return new TransformerOutput(norm2, attnOut, ffOut);
    }
    
    private Matrix layerNormAdd(Matrix residual, Matrix sublayer) {
        // Layer normalization: (x - mean) / std * gamma + beta
        // Here simplified as: residual + sublayer (with layer norm applied to sublayer)
        
        int batch = sublayer.rows();
        int d = sublayer.cols();
        
        double[] mean = new double[batch];
        double[] std = new double[batch];
        
        for (int b = 0; b < batch; b++) {
            double sum = 0;
            for (int i = 0; i < d; i++) {
                sum += sublayer.data[b][i];
            }
            mean[b] = sum / d;
            
            double varSum = 0;
            for (int i = 0; i < d; i++) {
                double diff = sublayer.data[b][i] - mean[b];
                varSum += diff * diff;
            }
            std[b] = Math.sqrt(varSum / d + 1e-6);
        }
        
        Matrix norm = new Matrix(batch, d);
        for (int b = 0; b < batch; b++) {
            for (int i = 0; i < d; i++) {
                norm.data[b][i] = (sublayer.data[b][i] - mean[b]) / std[b];
            }
        }
        
        return MatrixOperations.add(residual, norm);
    }
    
    public static class TransformerOutput {
        public Matrix output;
        public Matrix attnWeights;
        public Matrix ffOutput;
        
        public TransformerOutput(Matrix output, Matrix attn, Matrix ff) {
            this.output = output;
            this.attnWeights = attn;
            this.ffOutput = ff;
        }
    }
}
```

### 4.5 Positional Encoding

```java
package com.ml.transformer;

import com.ml.la.matrix.Matrix;
import com.ml.la.vector.Vector;

public class PositionalEncoding {
    private int d_model;
    private int maxLen;
    
    public PositionalEncoding(int d_model, int maxLen) {
        this.d_model = d_model;
        this.maxLen = maxLen;
    }
    
    public Matrix getEncoding(int seqLen) {
        Matrix pe = new Matrix(1, seqLen * d_model);
        
        for (int pos = 0; pos < seqLen; pos++) {
            for (int i = 0; i < d_model; i++) {
                double angle = pos / Math.pow(10000, (2 * i) / (double) d_model);
                double value;
                if (i % 2 == 0) {
                    value = Math.sin(angle);
                } else {
                    value = Math.cos(angle);
                }
                pe.data[0][pos * d_model + i] = value;
            }
        }
        
        return pe;
    }
    
    public Matrix addPositionalEncoding(Matrix X) {
        Matrix pe = getEncoding(X.cols());
        return MatrixOperations.add(X, pe);
    }
}
```

### 4.6 Complete Transformer

```java
package com.ml.transformer;

import com.ml.la.matrix.Matrix;

public class Transformer {
    private TransformerLayer[] encoderLayers;
    private TransformerLayer[] decoderLayers;
    private PositionalEncoding posEncoder;
    private int d_model;
    
    public Transformer(int d_model, int num_heads, int d_ff, int n_layers) {
        this.d_model = d_model;
        this.posEncoder = new PositionalEncoding(d_model, 512);
        
        encoderLayers = new TransformerLayer[n_layers];
        decoderLayers = new TransformerLayer[n_layers];
        
        for (int i = 0; i < n_layers; i++) {
            encoderLayers[i] = new TransformerLayer(d_model, num_heads, d_ff);
            decoderLayers[i] = new TransformerLayer(d_model, num_heads, d_ff);
        }
    }
    
    public TransformerResult encode(Matrix input, Matrix mask) {
        // Add positional encoding
        Matrix X = posEncoder.addPositionalEncoding(input);
        
        // Pass through encoder layers
        Matrix output = X;
        for (TransformerLayer layer : encoderLayers) {
            TransformerOutput out = layer.forward(output, mask, null);
            output = out.output;
        }
        
        return new TransformerResult(output);
    }
    
    public TransformerResult decode(Matrix output, Matrix memory, Matrix tgtMask, Matrix memMask) {
        // Add positional encoding
        Matrix X = posEncoder.addPositionalEncoding(output);
        
        // Pass through decoder layers
        Matrix result = X;
        for (TransformerLayer layer : decoderLayers) {
            TransformerOutput out = layer.forward(result, tgtMask, memory);
            result = out.output;
        }
        
        return new TransformerResult(result);
    }
    
    public Matrix forward(Matrix src, Matrix tgt, Matrix srcMask, Matrix tgtMask) {
        // Encode source
        TransformerResult encResult = encode(src, srcMask);
        Matrix memory = encResult.output;
        
        // Decode target
        TransformerResult decResult = decode(tgt, memory, tgtMask, null);
        
        return decResult.output;
    }
    
    public static class TransformerResult {
        public Matrix output;
        
        public TransformerResult(Matrix output) {
            this.output = output;
        }
    }
}
```

### 4.7 Sequence Classification Example

```java
package com.ml.transformer;

import com.ml.la.matrix.Matrix;
import com.ml.la.vector.Vector;

public class TransformerClassifier {
    private Transformer transformer;
    private Matrix W_class;
    private Vector b_class;
    private int d_model;
    private int numClasses;
    
    public TransformerClassifier(int d_model, int num_heads, int d_ff, 
                                  int n_layers, int numClasses) {
        this.transformer = new Transformer(d_model, num_heads, d_ff, n_layers);
        this.d_model = d_model;
        this.numClasses = numClasses;
        
        java.util.Random gen = new java.util.Random(42);
        W_class = Matrix.random(numClasses, d_model, gen.nextLong());
        b_class = Vector.random(numClasses, gen.nextLong());
    }
    
    public Vector forward(Matrix input, Matrix mask) {
        // Encode input
        TransformerResult encResult = transformer.encode(input, mask);
        
        // Use [CLS] token (first position) for classification
        // Or average pooling over sequence
        Matrix pooled = encResult.output;  // Simplified: use full output
        
        // Classification head
        Vector logits = MatrixOperations.multiply(W_class, pooled.transpose());
        logits = VectorOperations.add(logits, b_class);
        
        return softmax(logits);
    }
    
    public double train(Matrix input, Vector yTrue, Matrix mask) {
        Vector prediction = forward(input, mask);
        
        // Cross-entropy loss
        double loss = crossEntropy(yTrue, prediction);
        
        // Backward pass (simplified)
        Vector gradOutput = VectorOperations.subtract(prediction, yTrue);
        
        return loss;
    }
    
    private Vector softmax(Vector z) {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < z.size(); i++) {
            max = Math.max(max, z.get(i));
        }
        
        double sum = 0;
        double[] exp = new double[z.size()];
        for (int i = 0; i < z.size(); i++) {
            exp[i] = Math.exp(z.get(i) - max);
            sum += exp[i];
        }
        
        for (int i = 0; i < z.size(); i++) {
            exp[i] /= sum;
        }
        return new Vector(exp);
    }
    
    private double crossEntropy(Vector yTrue, Vector yPred) {
        double sum = 0;
        for (int i = 0; i < yTrue.size(); i++) {
            sum += yTrue.get(i) * Math.log(Math.max(1e-15, yPred.get(i)));
        }
        return -sum;
    }
}
```

## 5. Key Insights

### 5.1 Why Attention Works
- Direct connections between all positions
- No sequential dependency
- Can capture long-range dependencies
- Parallel computation

### 5.2 Positional Encoding Choices
- Sinusoidal: fixed, may generalize better
- Learnable: more flexible
- Relative: better for variable lengths

### 5.3 Training Tips
- Warmup learning rate
- Label smoothing
- Gradient clipping
- Mixed precision training