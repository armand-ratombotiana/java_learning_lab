# RNN & LSTM - CODE DEEP DIVE

## Java Implementations

### 1. Basic RNN Cell

```java
public class RNNCell {
    private double[][] wx;  // Input weights
    private double[][] wh;  // Hidden weights
    private double[] b;     // Bias
    
    private int inputSize;
    private int hiddenSize;
    
    public RNNCell(int inputSize, int hiddenSize) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        
        this.wx = new double[hiddenSize][inputSize];
        this.wh = new double[hiddenSize][hiddenSize];
        this.b = new double[hiddenSize];
        
        initializeWeights();
    }
    
    private void initializeWeights() {
        Random rand = new Random(42);
        double scale = Math.sqrt(2.0 / (inputSize + hiddenSize));
        
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                wx[i][j] = (rand.nextDouble() - 0.5) * 2 * scale;
            }
            for (int j = 0; j < hiddenSize; j++) {
                wh[i][j] = (rand.nextDouble() - 0.5) * 2 * scale;
            }
            b[i] = 0;
        }
    }
    
    public double[] forward(double[] x, double[] hPrev) {
        double[] h = new double[hiddenSize];
        
        for (int i = 0; i < hiddenSize; i++) {
            h[i] = b[i];
            
            for (int j = 0; j < inputSize; j++) {
                h[i] += wx[i][j] * x[j];
            }
            
            for (int j = 0; j < hiddenSize; j++) {
                h[i] += wh[i][j] * hPrev[j];
            }
            
            h[i] = Math.tanh(h[i]);
        }
        
        return h;
    }
    
    public RNNState backward(double[] x, double[] hPrev, double[] dhNext, 
                             double[] hCurrent) {
        double[] dhPrev = new double[hiddenSize];
        double[][] dwx = new double[hiddenSize][inputSize];
        double[][] dwh = new double[hiddenSize][hiddenSize];
        double[] db = new double[hiddenSize];
        
        double[] dtanh = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            dtanh[i] = dhNext[i] * (1 - hCurrent[i] * hCurrent[i]);
        }
        
        for (int i = 0; i < hiddenSize; i++) {
            db[i] = dtanh[i];
            
            for (int j = 0; j < inputSize; j++) {
                dwx[i][j] = dtanh[i] * x[j];
            }
            
            for (int j = 0; j < hiddenSize; j++) {
                dwh[i][j] = dtanh[i] * hPrev[j];
                dhPrev[j] += dtanh[i] * wh[i][j];
            }
        }
        
        return new RNNState(dwx, dwh, db, dhPrev);
    }
    
    private void initializeWeights() {
        Random rand = new Random(42);
        double scale = Math.sqrt(2.0 / (inputSize + hiddenSize));
        
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                wx[i][j] = (rand.nextDouble() - 0.5) * 2 * scale;
            }
            for (int j = 0; j < hiddenSize; j++) {
                wh[i][j] = (rand.nextDouble() - 0.5) * 2 * scale;
            }
            b[i] = 0;
        }
    }
}

class RNNState {
    double[][] dwx;
    double[][] dwh;
    double[] db;
    double[] dhPrev;
    
    public RNNState(double[][] dwx, double[][] dwh, double[] db, double[] dhPrev) {
        this.dwx = dwx;
        this.dwh = dwh;
        this.db = db;
        this.dhPrev = dhPrev;
    }
}
```

### 2. LSTM Cell Implementation

```java
public class LSTMCell {
    private double[][] wf, wi, wc, wo;  // Weight matrices for gates
    private double[][] uf, ui, uc, uo;  // Recurrent weights
    private double[] bf, bi, bc, bo;    // Biases
    
    private int inputSize;
    private int hiddenSize;
    
    public LSTMCell(int inputSize, int hiddenSize) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        
        initializeWeights();
    }
    
    private void initializeWeights() {
        Random rand = new Random(42);
        double scale = Math.sqrt(2.0 / (inputSize + hiddenSize));
        
        wf = createMatrix(hiddenSize, inputSize, rand, scale);
        wi = createMatrix(hiddenSize, inputSize, rand, scale);
        wc = createMatrix(hiddenSize, inputSize, rand, scale);
        wo = createMatrix(hiddenSize, inputSize, rand, scale);
        
        uf = createMatrix(hiddenSize, hiddenSize, rand, scale);
        ui = createMatrix(hiddenSize, hiddenSize, rand, scale);
        uc = createMatrix(hiddenSize, hiddenSize, rand, scale);
        uo = createMatrix(hiddenSize, hiddenSize, rand, scale);
        
        bf = new double[hiddenSize];
        bi = new double[hiddenSize];
        bc = new double[hiddenSize];
        bo = new double[hiddenSize];
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
    
    public LSTMState forward(double[] x, double[] hPrev, double[] cPrev) {
        double[] f = sigmoid(addVectors(multiplyMatrixVector(wf, x), 
                          multiplyMatrixVector(uf, hPrev), bf));
        
        double[] i = sigmoid(addVectors(multiplyMatrixVector(wi, x), 
                      multiplyMatrixVector(ui, hPrev), bi));
        
        double[] cTilde = tanh(addVectors(multiplyMatrixVector(wc, x), 
                            multiplyMatrixVector(uc, hPrev), bc));
        
        double[] c = new double[hiddenSize];
        for (int k = 0; k < hiddenSize; k++) {
            c[k] = f[k] * cPrev[k] + i[k] * cTilde[k];
        }
        
        double[] o = sigmoid(addVectors(multiplyMatrixVector(wo, x), 
                      multiplyMatrixVector(uo, hPrev), bo));
        
        double[] h = new double[hiddenSize];
        for (int k = 0; k < hiddenSize; k++) {
            h[k] = o[k] * Math.tanh(c[k]);
        }
        
        return new LSTMState(h, c, f, i, cTilde, o);
    }
    
    private double[] multiplyMatrixVector(double[][] matrix, double[] vector) {
        double[] result = new double[matrix.length];
        
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < vector.length; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
        }
        
        return result;
    }
    
    private double[] addVectors(double[] a, double[] b, double[] c) {
        double[] result = new double[a.length];
        
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] + b[i] + c[i];
        }
        
        return result;
    }
    
    private double[] sigmoid(double[] x) {
        double[] result = new double[x.length];
        
        for (int i = 0; i < x.length; i++) {
            result[i] = 1.0 / (1.0 + Math.exp(-x[i]));
        }
        
        return result;
    }
    
    private double[] tanh(double[] x) {
        double[] result = new double[x.length];
        
        for (int i = 0; i < x.length; i++) {
            result[i] = Math.tanh(x[i]);
        }
        
        return result;
    }
}

class LSTMState {
    double[] h;
    double[] c;
    double[] f;
    double[] i;
    double[] cTilde;
    double[] o;
    
    public LSTMState(double[] h, double[] c, double[] f, double[] i, 
                     double[] cTilde, double[] o) {
        this.h = h;
        this.c = c;
        this.f = f;
        this.i = i;
        this.cTilde = cTilde;
        this.o = o;
    }
}
```

### 3. Sequence to Sequence Model

```java
public class Seq2SeqLSTM {
    private LSTMCell encoder;
    private LSTMCell decoder;
    private int vocabularySize;
    private double learningRate = 0.001;
    
    public Seq2SeqLSTM(int inputSize, int hiddenSize, int vocabSize) {
        this.vocabularySize = vocabSize;
        this.encoder = new LSTMCell(inputSize, hiddenSize);
        this.decoder = new LSTMCell(inputSize, hiddenSize);
    }
    
    public double[][] encode(double[][] inputSequence) {
        int seqLength = inputSequence.length;
        double[] h = new double[256];
        double[] c = new double[256];
        
        for (int t = 0; t < seqLength; t++) {
            LSTMState state = encoder.forward(inputSequence[t], h, c);
            h = state.h;
            c = state.c;
        }
        
        return new double[][]{h, c};
    }
    
    public double[][] decode(double[][] contextVector, double[][] targetSequence) {
        double[][] outputs = new double[targetSequence.length][];
        double[] h = contextVector[0];
        double[] c = contextVector[1];
        
        for (int t = 0; t < targetSequence.length; t++) {
            LSTMState state = decoder.forward(targetSequence[t], h, c);
            h = state.h;
            c = state.c;
            outputs[t] = h;
        }
        
        return outputs;
    }
    
    public double[][] forward(double[][] inputSeq, double[][] targetSeq) {
        double[][] context = encode(inputSeq);
        return decode(context, targetSeq);
    }
    
    public void train(double[][] inputSeq, double[][] targetSeq, 
                      int[] targetLabels) {
        double[][] outputs = forward(inputSeq, targetSeq);
        
        double[][] outputLayer = new double[vocabularySize][outputs.length];
        
        for (int t = 0; t < outputs.length; t++) {
            double[] probs = softmax(outputs[t]);
            outputLayer[targetLabels[t]] = probs[t];
        }
        
        double loss = computeCrossEntropy(outputLayer, targetLabels);
        
        backpropagate(outputs, targetLabels);
    }
    
    private double[] softmax(double[] x) {
        double max = x[0];
        
        for (int i = 1; i < x.length; i++) {
            if (x[i] > max) max = x[i];
        }
        
        double sum = 0;
        double[] exp = new double[x.length];
        
        for (int i = 0; i < x.length; i++) {
            exp[i] = Math.exp(x[i] - max);
            sum += exp[i];
        }
        
        double[] result = new double[x.length];
        
        for (int i = 0; i < x.length; i++) {
            result[i] = exp[i] / sum;
        }
        
        return result;
    }
    
    private double computeCrossEntropy(double[][] predictions, int[] labels) {
        double loss = 0;
        
        for (int t = 0; t < predictions.length; t++) {
            loss -= Math.log(predictions[labels[t]] + 1e-10);
        }
        
        return loss / predictions.length;
    }
    
    private void backpropagate(double[][] outputs, int[] labels) {
        // Simplified BPTT - full implementation would compute gradients
        // through time for all timesteps
    }
}
```

### 4. Time Series Prediction

```java
public class TimeSeriesPredictor {
    private LSTMCell lstm;
    private double[][] outputWeights;
    private int lookback;
    private int hiddenSize = 128;
    
    public TimeSeriesPredictor(int featureSize, int lookback) {
        this.lookback = lookback;
        this.lstm = new LSTMCell(featureSize, hiddenSize);
        this.outputWeights = new double[1][hiddenSize];
        
        initializeOutputWeights();
    }
    
    private void initializeOutputWeights() {
        Random rand = new Random(42);
        
        for (int i = 0; i < outputWeights[0].length; i++) {
            outputWeights[0][i] = (rand.nextDouble() - 0.5) * 0.1;
        }
    }
    
    public double predict(double[][] sequence) {
        if (sequence.length != lookback) {
            throw new IllegalArgumentException("Sequence length must equal lookback");
        }
        
        double[] h = new double[hiddenSize];
        double[] c = new double[hiddenSize];
        
        for (int t = 0; t < lookback; t++) {
            LSTMState state = lstm.forward(sequence[t], h, c);
            h = state.h;
            c = state.c;
        }
        
        double prediction = 0;
        
        for (int i = 0; i < hiddenSize; i++) {
            prediction += outputWeights[0][i] * h[i];
        }
        
        return prediction;
    }
    
    public void train(double[][][] sequences, double[] targets) {
        for (int i = 0; i < sequences.length; i++) {
            double prediction = predict(sequences[i]);
            double error = targets[i] - prediction;
            
            updateWeights(error);
        }
    }
    
    private void updateWeights(double error) {
        for (int i = 0; i < outputWeights[0].length; i++) {
            outputWeights[0][i] += 0.01 * error;
        }
    }
    
    public double[] forecast(double[] lastSequence, int steps) {
        double[] forecasts = new double[steps];
        double[][] currentInput = new double[lookback][];
        
        for (int i = 0; i < lookback; i++) {
            currentInput[i] = new double[]{lastSequence[i]};
        }
        
        for (int step = 0; step < steps; step++) {
            double[] nextValue = new double[]{predict(currentInput)};
            forecasts[step] = nextValue[0];
            
            System.arraycopy(currentInput, 1, currentInput, 0, lookback - 1);
            currentInput[lookback - 1] = nextValue;
        }
        
        return forecasts;
    }
}
```

### 5. Attention Mechanism for Sequence Models

```java
public class AttentionLayer {
    private int querySize;
    private int keySize;
    private int valueSize;
    private double[][] Wq, Wk, Wv;
    private double[] b;
    
    public AttentionLayer(int querySize, int keySize, int valueSize) {
        this.querySize = querySize;
        this.keySize = keySize;
        this.valueSize = valueSize;
        
        initializeWeights();
    }
    
    private void initializeWeights() {
        Random rand = new Random(42);
        double scale = Math.sqrt(2.0 / querySize);
        
        Wq = createMatrix(querySize, querySize, rand, scale);
        Wk = createMatrix(keySize, querySize, rand, scale);
        Wv = createMatrix(valueSize, querySize, rand, scale);
        b = new double[valueSize];
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
    
    public double[][] forward(double[][] queries, double[][] keys, double[][] values) {
        int seqLen = queries.length;
        
        double[][] Q = new double[seqLen][querySize];
        double[][] K = new double[seqLen][keySize];
        double[][] V = new double[seqLen][valueSize];
        
        for (int i = 0; i < seqLen; i++) {
            Q[i] = multiplyMatrixVector(Wq, queries[i]);
            K[i] = multiplyMatrixVector(Wk, keys[i]);
            V[i] = multiplyMatrixVector(Wv, values[i]);
        }
        
        double[][] attentionScores = computeAttentionScores(Q, K);
        double[][] attentionWeights = softmax(attentionScores);
        double[][] context = computeContext(attentionWeights, V);
        
        return context;
    }
    
    private double[][] computeAttentionScores(double[][] Q, double[][] K) {
        int seqLen = Q.length;
        double[][] scores = new double[seqLen][seqLen];
        
        double scale = Math.sqrt(keySize);
        
        for (int i = 0; i < seqLen; i++) {
            for (int j = 0; j < seqLen; j++) {
                for (int k = 0; k < keySize; k++) {
                    scores[i][j] += Q[i][k] * K[j][k];
                }
                scores[i][j] /= scale;
            }
        }
        
        return scores;
    }
    
    private double[][] softmax(double[][] scores) {
        int rows = scores.length;
        int cols = scores[0].length;
        double[][] result = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            double max = scores[i][0];
            for (int j = 1; j < cols; j++) {
                if (scores[i][j] > max) max = scores[i][j];
            }
            
            double sum = 0;
            for (int j = 0; j < cols; j++) {
                sum += Math.exp(scores[i][j] - max);
            }
            
            for (int j = 0; j < cols; j++) {
                result[i][j] = Math.exp(scores[i][j] - max) / sum;
            }
        }
        
        return result;
    }
    
    private double[][] computeContext(double[][] attentionWeights, double[][] V) {
        int seqLen = attentionWeights.length;
        int valueSize = V[0].length;
        double[][] context = new double[seqLen][valueSize];
        
        for (int i = 0; i < seqLen; i++) {
            for (int j = 0; j < seqLen; j++) {
                for (int k = 0; k < valueSize; k++) {
                    context[i][k] += attentionWeights[i][j] * V[j][k];
                }
            }
        }
        
        return context;
    }
    
    private double[] multiplyMatrixVector(double[][] matrix, double[] vector) {
        double[] result = new double[matrix.length];
        
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < vector.length; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
        }
        
        return result;
    }
}
```

### 6. Bidirectional RNN

```java
public class BidirectionalRNN {
    private RNNCell forwardCell;
    private RNNCell backwardCell;
    private int hiddenSize;
    
    public BidirectionalRNN(int inputSize, int hiddenSize) {
        this.hiddenSize = hiddenSize;
        this.forwardCell = new RNNCell(inputSize, hiddenSize);
        this.backwardCell = new RNNCell(inputSize, hiddenSize);
    }
    
    public double[][] forward(double[][] sequence) {
        int seqLength = sequence.length;
        
        double[][] forwardHidden = new double[seqLength][hiddenSize];
        double[][] backwardHidden = new double[seqLength][hiddenSize];
        
        double[] hFwd = new double[hiddenSize];
        
        for (int t = 0; t < seqLength; t++) {
            hFwd = forwardCell.forward(sequence[t], hFwd);
            forwardHidden[t] = hFwd.clone();
        }
        
        double[] hBwd = new double[hiddenSize];
        
        for (int t = seqLength - 1; t >= 0; t--) {
            hBwd = backwardCell.forward(sequence[t], hBwd);
            backwardHidden[t] = hBwd.clone();
        }
        
        double[][] combined = new double[seqLength][hiddenSize * 2];
        
        for (int t = 0; t < seqLength; t++) {
            System.arraycopy(forwardHidden[t], 0, combined[t], 0, hiddenSize);
            System.arraycopy(backwardHidden[t], 0, combined[t], hiddenSize, hiddenSize);
        }
        
        return combined;
    }
    
    public double[][] predict(double[][] sequence) {
        double[][] hiddenStates = forward(sequence);
        
        double[][] outputs = new double[sequence.length][10];
        
        for (int t = 0; t < sequence.length; t++) {
            outputs[t] = softmaxClassifier(hiddenStates[t]);
        }
        
        return outputs;
    }
    
    private double[] softmaxClassifier(double[] input) {
        double[] output = new double[10];
        double sum = 0;
        
        for (int i = 0; i < 10; i++) {
            output[i] = Math.exp(input[i] * 0.1);
            sum += output[i];
        }
        
        for (int i = 0; i < 10; i++) {
            output[i] /= sum;
        }
        
        return output;
    }
}
```