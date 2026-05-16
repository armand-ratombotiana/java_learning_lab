# RNN & LSTM - EXERCISES

## Exercise 1: Vanilla RNN Forward Pass
Compute forward pass for a single RNN cell with:
- input = [1.0, 0.5]
- hidden size = 3
- Previous hidden state = [0.0, 0.0, 0.0]
- Weights: wx = [[0.1, 0.2], [0.3, 0.4], [0.5, 0.6]], wh = identity matrix, b = [0, 0, 0]

```java
public double[] rnnForward() {
    double[] x = {1.0, 0.5};
    double[] hPrev = {0.0, 0.0, 0.0};
    
    double[][] wx = {
        {0.1, 0.2},
        {0.3, 0.4},
        {0.5, 0.6}
    };
    
    double[][] wh = {
        {1, 0, 0},
        {0, 1, 0},
        {0, 0, 1}
    };
    
    double[] b = {0, 0, 0};
    double[] h = new double[3];
    
    for (int i = 0; i < 3; i++) {
        h[i] = b[i];
        for (int j = 0; j < 2; j++) {
            h[i] += wx[i][j] * x[j];
        }
        for (int j = 0; j < 3; j++) {
            h[i] += wh[i][j] * hPrev[j];
        }
        h[i] = Math.tanh(h[i]);
    }
    
    return h;
    // Output: approximately [0.548, 0.785, 0.915]
}
```

## Exercise 2: LSTM Gate Computation
Given input x = [0.5, 0.3], previous hidden h = [0.1, 0.2], previous cell c = [0.0, 0.0], compute:
- Forget gate: f = sigmoid(Wf·x + Uf·h + bf)
- Input gate: i = sigmoid(Wi·x + Ui·h + bi)
- Candidate: cTilde = tanh(Wc·x + Uc·h + bc)
- Cell update: c' = f ⊙ c + i ⊙ cTilde

```java
public double[] computeLSTMGates() {
    double[] x = {0.5, 0.3};
    double[] h = {0.1, 0.2};
    double[] c = {0.0, 0.0};
    
    double[][] Wf = {{0.5, 0.1}, {0.2, 0.3}};
    double[][] Wi = {{0.3, 0.2}, {0.1, 0.4}};
    double[][] Wc = {{0.4, 0.2}, {0.3, 0.1}};
    
    double[][] Uf = {{0.6, 0.1}, {0.2, 0.5}};
    double[][] Ui = {{0.4, 0.3}, {0.2, 0.6}};
    double[][] Uc = {{0.5, 0.2}, {0.1, 0.4}};
    
    double[] bf = {0.0, 0.0};
    double[] bi = {0.0, 0.0};
    double[] bc = {0.0, 0.0};
    
    double[] f = sigmoid(add(multiply(Wf, x), multiply(Uf, h), bf));
    double[] i = sigmoid(add(multiply(Wi, x), multiply(Ui, h), bi));
    double[] cTilde = tanh(add(multiply(Wc, x), multiply(Uc, h), bc));
    
    double[] cNew = new double[2];
    for (int k = 0; k < 2; k++) {
        cNew[k] = f[k] * c[k] + i[k] * cTilde[k];
    }
    
    return cNew;
    // New cell state computed
}
```

## Exercise 3: BPTT Gradient Flow
For sequence length 3, compute gradients through time manually. Explain vanishing gradient problem.

```java
public double[] computeBPTT() {
    double[][] sequences = {
        {1.0, 0.0},
        {0.0, 1.0},
        {1.0, 1.0}
    };
    
    double[][] weights = {{0.5}, {0.3}};
    
    double[] h = {0, 0, 0};
    double gradient = 1.0;
    
    for (int t = 2; t >= 0; t--) {
        double[] dh = computeGradient(sequences[t], h[t], gradient);
        
        for (int i = 0; i < dh.length; i++) {
            gradient *= Math.abs(dh[i]);
        }
        
        System.out.println("t=" + t + ", gradient magnitude: " + gradient);
    }
    
    return h;
    // Shows gradient vanishing over time
}

private double[] computeGradient(double[] x, double h, double grad) {
    double dtanh = 1 - h * h;
    return new double[]{grad * dtanh};
}
```

## Exercise 4: Sequence Classification
Build a simple sentiment classifier using RNN:
- Input: sequence of word embeddings (length 5, each dim 50)
- Hidden: 100 units
- Output: binary sentiment (positive/negative)

```java
public class SentimentRNN {
    private RNNCell rnn;
    private double[] outputWeight;
    
    public SentimentRNN() {
        this.rnn = new RNNCell(50, 100);
        this.outputWeight = new double[100];
    }
    
    public boolean predict(double[][] sequence) {
        double[] h = new double[100];
        
        for (int t = 0; t < sequence.length; t++) {
            h = rnn.forward(sequence[t], h);
        }
        
        double score = 0;
        for (int i = 0; i < 100; i++) {
            score += outputWeight[i] * h[i];
        }
        
        return score > 0;
    }
    
    public void train(double[][][] sequences, boolean[] labels) {
        for (int i = 0; i < sequences.length; i++) {
            boolean pred = predict(sequences[i]);
            double error = (labels[i] ? 1 : 0) - (pred ? 1 : 0);
            
            if (error != 0) {
                for (int j = 0; j < outputWeight.length; j++) {
                    outputWeight[j] += 0.01 * error;
                }
            }
        }
    }
}
```

## Exercise 5: Time Series Forecasting
Predict next value in sequence using lookback=3:
- Sequence: [1.0, 2.0, 3.0, 4.0, 5.0]
- Predict: value at t=5

```java
public class TimeSeriesForecast {
    private int lookback = 3;
    private double[] weights = {0.3, 0.5, 0.2};
    
    public double predict(double[] sequence) {
        if (sequence.length != lookback) {
            throw new IllegalArgumentException("Need " + lookback + " values");
        }
        
        double prediction = 0;
        for (int i = 0; i < lookback; i++) {
            prediction += weights[i] * sequence[lookback - 1 - i];
        }
        
        return prediction;
    }
    
    public static void main(String[] args) {
        TimeSeriesForecast forecast = new TimeSeriesForecast();
        double[] seq = {3.0, 4.0, 5.0};
        
        double next = forecast.predict(seq);
        System.out.println("Predicted next value: " + next);
    }
}
```

## Exercise 6: Attention Mechanism
Implement scaled dot-product attention for sequence-to-sequence model:
- Q (query), K (key), V (value) matrices
- Scale factor: sqrt(d_k)

```java
public double[][] scaledDotProductAttention(double[][] Q, double[][] K, double[][] V) {
    int d_k = K[0].length;
    double scale = Math.sqrt(d_k);
    
    int seqLen = Q.length;
    double[][] scores = new double[seqLen][seqLen];
    
    for (int i = 0; i < seqLen; i++) {
        for (int j = 0; j < seqLen; j++) {
            for (int k = 0; k < d_k; k++) {
                scores[i][j] += Q[i][k] * K[j][k];
            }
            scores[i][j] /= scale;
        }
    }
    
    double[][] attention = softmax(scores);
    double[][] output = new double[seqLen][V[0].length];
    
    for (int i = 0; i < seqLen; i++) {
        for (int j = 0; j < seqLen; j++) {
            for (int k = 0; k < V[0].length; k++) {
                output[i][k] += attention[i][j] * V[j][k];
            }
        }
    }
    
    return output;
}
```

---

## Solutions

### Exercise 1:
```java
// Result: [0.548, 0.785, 0.915]
// tanh applied to linear combination of inputs and hidden state
```

### Exercise 2:
```java
// Sigmoid gates computed, cell state updated
// Output: new cell state c'
```

### Exercise 3:
```java
// BPTT shows gradient magnitude decreases over timesteps
// Vanishing gradient problem explains difficulty in learning long dependencies
```

### Exercise 4:
```java
// Sentiment classification using final hidden state
// Binary output based on weighted sum of hidden state
```

### Exercise 5:
```java
// Prediction: 0.3*3 + 0.5*4 + 0.2*5 = 4.1
// Linear combination with learned weights
```

### Exercise 6:
```java
// Scaled dot-product attention with softmax
// Context vector computed as weighted sum of values
```