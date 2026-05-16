# Transformers - CODE DEEP DIVE

## Java Implementations

### 1. Multi-Head Attention

```java
public class MultiHeadAttention {
    private int numHeads;
    private int dModel;
    private int dK;
    private double[][] Wq, Wk, Wv, Wo;
    private double[] bq, bk, bv, bo;
    
    public MultiHeadAttention(int dModel, int numHeads) {
        this.dModel = dModel;
        this.numHeads = numHeads;
        this.dK = dModel / numHeads;
        
        initializeWeights();
    }
    
    private void initializeWeights() {
        Random rand = new Random(42);
        double scale = Math.sqrt(2.0 / dK);
        
        Wq = createMatrix(dModel, dModel, rand, scale);
        Wk = createMatrix(dModel, dModel, rand, scale);
        Wv = createMatrix(dModel, dModel, rand, scale);
        Wo = createMatrix(dModel, dModel, rand, scale);
        
        bq = new double[dModel];
        bk = new double[dModel];
        bv = new double[dModel];
        bo = new double[dModel];
    }
    
    private double[][] createMatrix(int rows, int cols, Random rand, double scale) {
        double[][] matrix = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = (rand.nextDouble() - 0.5) * 2 * scale;
            }
        }
        
        return matrix;
    }
    
    public double[][] forward(double[][] X, double[][] mask) {
        int seqLen = X.length;
        
        double[][] Q = multiplyMatrixVector(Wq, X);
        double[][] K = multiplyMatrixVector(Wk, X);
        double[][] V = multiplyMatrixVector(Wv, X);
        
        Q = splitIntoHeads(Q);
        K = splitIntoHeads(K);
        V = splitIntoHeads(V);
        
        double[][][] attentionOutputs = new double[numHeads][][];
        
        for (int h = 0; h < numHeads; h++) {
            double[][] scores = matmul(Q[h], transpose(K[h]));
            
            double scale = Math.sqrt(dK);
            
            for (int i = 0; i < seqLen; i++) {
                for (int j = 0; j < seqLen; j++) {
                    scores[i][j] /= scale;
                }
            }
            
            if (mask != null) {
                scores = applyMask(scores, mask);
            }
            
            double[][] attentionWeights = softmax(scores);
            attentionOutputs[h] = matmul(attentionWeights, V[h]);
        }
        
        double[][] concatenated = concatHeads(attentionOutputs);
        
        double[][] output = new double[seqLen][dModel];
        
        for (int i = 0; i < seqLen; i++) {
            output[i] = multiplyMatrixVector(Wo, concatenated[i]);
            
            for (int j = 0; j < dModel; j++) {
                output[i][j] += bo[j];
            }
        }
        
        return output;
    }
    
    private double[][] multiplyMatrixVector(double[][] W, double[][] X) {
        int rows = W.length;
        int cols = X[0].length;
        double[][] result = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for (int k = 0; k < X.length; k++) {
                    result[i][j] += W[i][k] * X[k][j];
                }
            }
        }
        
        return result;
    }
    
    private double[][][] splitIntoHeads(double[][] X) {
        int seqLen = X[0].length;
        double[][][] heads = new double[numHeads][seqLen][dK];
        
        for (int h = 0; h < numHeads; h++) {
            for (int i = 0; i < seqLen; i++) {
                System.arraycopy(X, h * dK, heads[h][i], 0, dK);
            }
        }
        
        return heads;
    }
    
    private double[][] matmul(double[][] A, double[][] B) {
        int rows = A.length;
        int cols = B[0].length;
        int inner = A[0].length;
        
        double[][] result = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for (int k = 0; k < inner; k++) {
                    result[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        
        return result;
    }
    
    private double[][] transpose(double[][] A) {
        int rows = A.length;
        int cols = A[0].length;
        
        double[][] result = new double[cols][rows];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j][i] = A[i][j];
            }
        }
        
        return result;
    }
    
    private double[][] softmax(double[][] X) {
        int rows = X.length;
        int cols = X[0].length;
        
        double[][] result = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            double max = X[i][0];
            
            for (int j = 1; j < cols; j++) {
                if (X[i][j] > max) max = X[i][j];
            }
            
            double sum = 0;
            
            for (int j = 0; j < cols; j++) {
                sum += Math.exp(X[i][j] - max);
            }
            
            for (int j = 0; j < cols; j++) {
                result[i][j] = Math.exp(X[i][j] - max) / sum;
            }
        }
        
        return result;
    }
    
    private double[][] applyMask(double[][] scores, double[][] mask) {
        double[][] masked = new double[scores.length][scores[0].length];
        
        for (int i = 0; i < scores.length; i++) {
            for (int j = 0; j < scores[0].length; j++) {
                masked[i][j] = scores[i][j] + mask[i][j];
            }
        }
        
        return masked;
    }
    
    private double[][] concatHeads(double[][][] heads) {
        int seqLen = heads[0].length;
        double[][] result = new double[seqLen][dModel];
        
        for (int i = 0; i < seqLen; i++) {
            for (int h = 0; h < numHeads; h++) {
                System.arraycopy(heads[h][i], 0, result[i], h * dK, dK);
            }
        }
        
        return result;
    }
}
```

### 2. Positional Encoding

```java
public class PositionalEncoding {
    private int dModel;
    private int maxSeqLength;
    
    public PositionalEncoding(int dModel, int maxSeqLength) {
        this.dModel = dModel;
        this.maxSeqLength = maxSeqLength;
    }
    
    public double[][] encode(double[][] input) {
        int seqLen = input.length;
        double[][] output = new double[seqLen][dModel];
        
        for (int i = 0; i < seqLen; i++) {
            for (int j = 0; j < dModel; j++) {
                output[i][j] = input[i][j];
            }
        }
        
        for (int pos = 0; pos < seqLen; pos++) {
            for (int i = 0; i < dModel / 2; i++) {
                double angle = pos / Math.pow(10000, 2.0 * i / dModel);
                
                output[pos][2 * i] += Math.sin(angle);
                output[pos][2 * i + 1] += Math.cos(angle);
            }
        }
        
        return output;
    }
    
    public double[][] encodeWithDropout(double[][] input, double dropoutRate) {
        double[][] encoded = encode(input);
        
        Random rand = new Random(42);
        
        for (int i = 0; i < encoded.length; i++) {
            for (int j = 0; j < encoded[0].length; j++) {
                if (rand.nextDouble() < dropoutRate) {
                    encoded[i][j] = 0;
                }
            }
        }
        
        return encoded;
    }
    
    public static double[][] getPositionalEncoding(int seqLen, int dModel) {
        double[][] pe = new double[seqLen][dModel];
        
        for (int pos = 0; pos < seqLen; pos++) {
            for (int i = 0; i < dModel / 2; i++) {
                double angle = pos / Math.pow(10000, 2.0 * i / dModel);
                
                pe[pos][2 * i] = Math.sin(angle);
                pe[pos][2 * i + 1] = Math.cos(angle);
            }
        }
        
        return pe;
    }
}
```

### 3. Transformer Encoder Block

```java
public class TransformerEncoderBlock {
    private MultiHeadAttention attention;
    private FeedForwardLayer ffn;
    private LayerNorm layerNorm1;
    private LayerNorm layerNorm2;
    
    public TransformerEncoderBlock(int dModel, int numHeads, int dFF) {
        this.attention = new MultiHeadAttention(dModel, numHeads);
        this.ffn = new FeedForwardLayer(dModel, dFF);
        this.layerNorm1 = new LayerNorm(dModel);
        this.layerNorm2 = new LayerNorm(dModel);
    }
    
    public double[][] forward(double[][] x, double[][] mask) {
        double[][] attentionOutput = attention.forward(x, mask);
        
        double[][] addNorm1 = layerNorm1.forward(add(x, attentionOutput));
        
        double[][] ffnOutput = ffn.forward(addNorm1);
        
        double[][] output = layerNorm2.forward(add(addNorm1, ffnOutput));
        
        return output;
    }
    
    private double[][] add(double[][] a, double[][] b) {
        double[][] result = new double[a.length][a[0].length];
        
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        
        return result;
    }
}

class LayerNorm {
    private double[] gamma;
    private double[] beta;
    private double epsilon = 1e-6;
    
    public LayerNorm(int dModel) {
        this.gamma = new double[dModel];
        this.beta = new double[dModel];
        
        Arrays.fill(gamma, 1.0);
    }
    
    public double[][] forward(double[][] x) {
        int seqLen = x.length;
        int dModel = x[0].length;
        
        double[][] normalized = new double[seqLen][dModel];
        
        for (int i = 0; i < seqLen; i++) {
            double mean = computeMean(x[i]);
            double variance = computeVariance(x[i], mean);
            
            for (int j = 0; j < dModel; j++) {
                normalized[i][j] = (x[i][j] - mean) / Math.sqrt(variance + epsilon);
                normalized[i][j] = normalized[i][j] * gamma[j] + beta[j];
            }
        }
        
        return normalized;
    }
    
    private double computeMean(double[] x) {
        return Arrays.stream(x).average().orElse(0);
    }
    
    private double computeVariance(double[] x, double mean) {
        return Arrays.stream(x)
            .map(v -> (v - mean) * (v - mean))
            .average()
            .orElse(0);
    }
}

class FeedForwardLayer {
    private double[][] w1, w2;
    private double[] b1, b2;
    
    public FeedForwardLayer(int dModel, int dFF) {
        this.w1 = createMatrix(dFF, dModel, new Random(42), 0.1);
        this.w2 = createMatrix(dModel, dFF, new Random(42), 0.1);
        this.b1 = new double[dFF];
        this.b2 = new double[dModel];
    }
    
    private double[][] createMatrix(int rows, int cols, Random rand, double scale) {
        double[][] matrix = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = (rand.nextDouble() - 0.5) * 2 * scale;
            }
        }
        
        return matrix;
    }
    
    public double[][] forward(double[][] x) {
        int seqLen = x.length;
        int dModel = x[0].length;
        
        double[][] hidden = new double[seqLen][w1.length];
        
        for (int i = 0; i < seqLen; i++) {
            for (int j = 0; j < w1.length; j++) {
                hidden[i][j] = b1[j];
                
                for (int k = 0; k < dModel; k++) {
                    hidden[i][j] += w1[j][k] * x[i][k];
                }
                
                hidden[i][j] = relu(hidden[i][j]);
            }
        }
        
        double[][] output = new double[seqLen][dModel];
        
        for (int i = 0; i < seqLen; i++) {
            for (int j = 0; j < dModel; j++) {
                output[i][j] = b2[j];
                
                for (int k = 0; k < w1.length; k++) {
                    output[i][j] += w2[j][k] * hidden[i][k];
                }
            }
        }
        
        return output;
    }
    
    private double relu(double x) {
        return Math.max(0, x);
    }
}
```

### 4. Transformer Decoder Block

```java
public class TransformerDecoderBlock {
    private MultiHeadAttention maskedAttention;
    private MultiHeadAttention crossAttention;
    private FeedForwardLayer ffn;
    private LayerNorm layerNorm1, layerNorm2, layerNorm3;
    
    public TransformerDecoderBlock(int dModel, int numHeads, int dFF) {
        this.maskedAttention = new MultiHeadAttention(dModel, numHeads);
        this.crossAttention = new MultiHeadAttention(dModel, numHeads);
        this.ffn = new FeedForwardLayer(dModel, dFF);
        this.layerNorm1 = new LayerNorm(dModel);
        this.layerNorm2 = new LayerNorm(dModel);
        this.layerNorm3 = new LayerNorm(dModel);
    }
    
    public double[][] forward(double[][] x, double[][] encoderOutput, double[][] mask) {
        double[][] maskedAtt = maskedAttention.forward(x, createCausalMask(x.length));
        
        double[][] addNorm1 = layerNorm1.forward(add(x, maskedAtt));
        
        double[][] crossAtt = crossAttention.forward(addNorm1, null);
        
        double[][] addNorm2 = layerNorm2.forward(add(addNorm1, crossAtt));
        
        double[][] ffnOutput = ffn.forward(addNorm2);
        
        double[][] output = layerNorm3.forward(add(addNorm2, ffnOutput));
        
        return output;
    }
    
    private double[][] createCausalMask(int seqLen) {
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
    }
    
    private double[][] add(double[][] a, double[][] b) {
        double[][] result = new double[a.length][a[0].length];
        
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        
        return result;
    }
}
```

### 5. BERT-Style Model

```java
public class BERTModel {
    private TransformerEncoderBlock[] encoderBlocks;
    private int vocabSize;
    private int dModel;
    private TokenEmbedding tokenEmbedding;
    private PositionalEncoding positionalEncoding;
    private double[][] embeddingMatrix;
    
    public BERTModel(int vocabSize, int dModel, int numHeads, int numLayers) {
        this.vocabSize = vocabSize;
        this.dModel = dModel;
        
        this.tokenEmbedding = new TokenEmbedding(vocabSize, dModel);
        this.positionalEncoding = new PositionalEncoding(dModel, 512);
        
        this.encoderBlocks = new TransformerEncoderBlock[numLayers];
        
        for (int i = 0; i < numLayers; i++) {
            encoderBlocks[i] = new TransformerEncoderBlock(dModel, numHeads, dModel * 4);
        }
    }
    
    public BERTOutput forward(int[] inputIds, int[] segmentIds) {
        double[][] embeddings = getEmbeddings(inputIds);
        
        double[][] encoderOutput = embeddings;
        
        for (TransformerEncoderBlock block : encoderBlocks) {
            encoderOutput = block.forward(encoderOutput, null);
        }
        
        double[][][] predictions = compute MLM and NSP predictions
        
        return new BERTOutput(encoderOutput, predictions);
    }
    
    private double[][] getEmbeddings(int[] inputIds) {
        double[][] tokenEmbeds = new double[inputIds.length][dModel];
        
        for (int i = 0; i < inputIds.length; i++) {
            tokenEmbeds[i] = getEmbedding(inputIds[i]);
        }
        
        double[][] positionalEmbeds = positionalEncoding.encode(tokenEmbeds);
        
        return positionalEmbeds;
    }
    
    private double[] getEmbedding(int tokenId) {
        if (tokenId >= embeddingMatrix.length) {
            tokenId = 0;
        }
        
        return embeddingMatrix[tokenId];
    }
    
    public double[][] getLastHiddenState() {
        return encoderOutput;
    }
}

class BERTOutput {
    double[][] lastHiddenState;
    double[][][] predictions;
    double[] pooledOutput;
    
    public BERTOutput(double[][] lastHiddenState, double[][][] predictions) {
        this.lastHiddenState = lastHiddenState;
        this.predictions = predictions;
        
        this.pooledOutput = computePooledOutput(lastHiddenState);
    }
    
    private double[] computePooledOutput(double[][] hiddenState) {
        double[] pooled = new double[hiddenState[0].length];
        
        for (int i = 0; i < hiddenState.length; i++) {
            for (int j = 0; j < hiddenState[0].length; j++) {
                pooled[j] += hiddenState[i][j];
            }
        }
        
        for (int j = 0; j < pooled.length; j++) {
            pooled[j] /= hiddenState.length;
        }
        
        return pooled;
    }
}
```

### 6. GPT-Style Autoregressive Generation

```java
public class GPTModel {
    private TransformerDecoderBlock[] decoderBlocks;
    private int vocabSize;
    private int dModel;
    private int maxSeqLength;
    private double[][] lmHead;
    
    public GPTModel(int vocabSize, int dModel, int numHeads, int numLayers, int maxSeqLength) {
        this.vocabSize = vocabSize;
        this.dModel = dModel;
        this.maxSeqLength = maxSeqLength;
        
        this.decoderBlocks = new TransformerDecoderBlock[numLayers];
        
        for (int i = 0; i < numLayers; i++) {
            decoderBlocks[i] = new TransformerDecoderBlock(dModel, numHeads, dModel * 4);
        }
        
        this.lmHead = createMatrix(vocabSize, dModel, new Random(42), 0.1);
    }
    
    private double[][] createMatrix(int rows, int cols, Random rand, double scale) {
        double[][] matrix = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = (rand.nextDouble() - 0.5) * 2 * scale;
            }
        }
        
        return matrix;
    }
    
    public int[] generate(int[] prompt, int maxNewTokens, double temperature) {
        int[] generated = new int[prompt.length + maxNewTokens];
        
        System.arraycopy(prompt, 0, generated, 0, prompt.length);
        
        int currentLength = prompt.length;
        
        for (int i = 0; i < maxNewTokens; i++) {
            double[][] inputSeq = new double[currentLength][];
            
            for (int j = 0; j < currentLength; j++) {
                inputSeq[j] = getTokenEmbedding(generated[j]);
            }
            
            double[][] decoderOutput = inputSeq;
            
            for (TransformerDecoderBlock block : decoderBlocks) {
                decoderOutput = block.forward(decoderOutput, encoderOutput, null);
            }
            
            double[] logits = computeLogits(decoderOutput[decoderOutput.length - 1]);
            
            double[] probs = applyTemperature(logits, temperature);
            
            int nextToken = sample(probs);
            
            generated[currentLength] = nextToken;
            currentLength++;
            
            if (nextToken == 2) {
                break;
            }
        }
        
        return generated;
    }
    
    private double[] getTokenEmbedding(int tokenId) {
        double[] embed = new double[dModel];
        
        for (int i = 0; i < dModel; i++) {
            embed[i] = Math.sin(tokenId * Math.pow(10000, -i / (double) dModel));
        }
        
        return embed;
    }
    
    private double[] computeLogits(double[] hiddenState) {
        double[] logits = new double[vocabSize];
        
        for (int i = 0; i < vocabSize; i++) {
            for (int j = 0; j < dModel; j++) {
                logits[i] += lmHead[i][j] * hiddenState[j];
            }
        }
        
        return logits;
    }
    
    private double[] applyTemperature(double[] logits, double temperature) {
        double[] scaled = new double[logits.length];
        
        for (int i = 0; i < logits.length; i++) {
            scaled[i] = logits[i] / temperature;
        }
        
        double max = scaled[0];
        
        for (int i = 1; i < scaled.length; i++) {
            if (scaled[i] > max) max = scaled[i];
        }
        
        double sum = 0;
        
        for (int i = 0; i < scaled.length; i++) {
            scaled[i] = Math.exp(scaled[i] - max);
            sum += scaled[i];
        }
        
        for (int i = 0; i < scaled.length; i++) {
            scaled[i] /= sum;
        }
        
        return scaled;
    }
    
    private int sample(double[] probs) {
        Random rand = new Random();
        double cumulative = 0;
        double r = rand.nextDouble();
        
        for (int i = 0; i < probs.length; i++) {
            cumulative += probs[i];
            
            if (r <= cumulative) {
                return i;
            }
        }
        
        return probs.length - 1;
    }
}
```