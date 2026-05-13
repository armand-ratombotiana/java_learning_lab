# Recurrent Neural Networks and LSTM - Complete Theory and Implementation

## 1. Sequence Modeling

### 1.1 Why RNNs?
- Variable length sequences
- Time step dependencies
- Shared parameters across time

### 1.2 RNN Architecture
h_t = f(W_xh x_t + W_hh h_{t-1} + b)

### 1.3 Unfolding Through Time
Graph structure: x1 -> h1 -> x2 -> h2 -> ...

## 2. Backpropagation Through Time (BPTT)

### 2.1 Forward Through Time
Compute all hidden states and outputs.

### 2.2 Loss Computation
Sum losses at each time step.

### 2.3 Backward Through Time
Gradient flows backward through all time steps.

### 2.4 Vanishing/Exploding Gradients
- Long sequences cause gradient issues
- Solutions: LSTM, GRU, gradient clipping

## 3. Long Short-Term Memory (LSTM)

### 3.1 Motivation
Capture long-range dependencies without vanishing gradients.

### 3.2 LSTM Cell
- Cell state: c_t
- Hidden state: h_t
- Gates: input, forget, output

### 3.3 Equations

**Forget Gate**:
f_t = sigma(W_f x_t + U_f h_{t-1} + b_f)

**Input Gate**:
i_t = sigma(W_i x_t + U_i h_{t-1} + b_i)

**Candidate**:
tilde_c_t = tanh(W_c x_t + U_c h_{t-1} + b_c)

**Update Cell State**:
c_t = f_t * c_{t-1} + i_t * tilde_c_t

**Output Gate**:
o_t = sigma(W_o x_t + U_o h_{t-1} + b_o)

**Hidden State**:
h_t = o_t * tanh(c_t)

## 4. GRU (Gated Recurrent Unit)

### 4.1 Equations

**Update Gate**:
z_t = sigma(W_z x_t + U_z h_{t-1})

**Reset Gate**:
r_t = sigma(W_r x_t + U_r h_{t-1})

**Candidate Hidden**:
tilde_h_t = tanh(W x_t + r_t * (U h_{t-1}))

**Hidden State**:
h_t = (1 - z_t) * h_{t-1} + z_t * tilde_h_t

### 4.2 Comparison with LSTM
- Simpler (fewer gates)
- Often comparable performance
- Easier to implement

## 5. Java Implementation

### 5.1 LSTM Cell

```java
package com.ml.rnn;

import com.ml.la.vector.Vector;
import com.ml.la.matrix.Matrix;

public class LSTMCell {
    private int inputSize;
    private int hiddenSize;
    
    // Weight matrices
    private MatrixWf, W_i, W_c, W_o;
    private MatrixU_f, U_i, U_c, U_o;
    
    // Biases
    private Vector b_f, b_i, b_c, b_o;
    
    public LSTMCell(int inputSize, int hiddenSize) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        
        java.util.Random gen = new java.util.Random(42);
        double scale = Math.sqrt(2.0 / (inputSize + hiddenSize));
        
        W_f = randomMatrix(hiddenSize, inputSize, gen, scale);
        W_i = randomMatrix(hiddenSize, inputSize, gen, scale);
        W_c = randomMatrix(hiddenSize, inputSize, gen, scale);
        W_o = randomMatrix(hiddenSize, inputSize, gen, scale);
        
        U_f = randomMatrix(hiddenSize, hiddenSize, gen, scale);
        U_i = randomMatrix(hiddenSize, hiddenSize, gen, scale);
        U_c = randomMatrix(hiddenSize, hiddenSize, gen, scale);
        U_o = randomMatrix(hiddenSize, hiddenSize, gen, scale);
        
        b_f = Vector.random(hiddenSize, gen.nextLong());
        b_i = Vector.random(hiddenSize, gen.nextLong());
        b_c = Vector.random(hiddenSize, gen.nextLong());
        b_o = Vector.random(hiddenSize, gen.nextLong());
    }
    
    public LSTMState forward(Vector x, LSTMState prev) {
        Vector f = sigmoid(add(matVecMul(W_f, x), 
                              matVecMul(U_f, prev.h), b_f));
        Vector i = sigmoid(add(matVecMul(W_i, x),
                              matVecMul(U_i, prev.h), b_i));
        Vector c_tilde = tanh(add(matVecMul(W_c, x),
                                 matVecMul(U_c, prev.h), b_c));
        Vector c = add(mult(f, prev.c), mult(i, c_tilde));
        Vector o = sigmoid(add(matVecMul(W_o, x),
                               matVecMul(U_o, prev.h), b_o));
        Vector h = mult(o, tanh(c));
        
        return new LSTMState(h, c);
    }
    
    // Helper methods
    private Matrix randomMatrix(int rows, int cols, java.util.Random gen, double scale) {
        double[][] data = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[i][j] = gen.nextGaussian() * scale;
            }
        }
        return new Matrix(data);
    }
    
    private Vector matVecMul(Matrix W, Vector x) {
        return MatrixOperations.multiply(W, x);
    }
    
    private Vector add(Vector... vecs) {
        Vector result = vecs[0];
        for (int i = 1; i < vecs.length; i++) {
            result = VectorOperations.add(result, vecs[i]);
        }
        return result;
    }
    
    private Vector mult(Vector a, Vector b) {
        return VectorOperations.elementwiseMultiply(a, b);
    }
    
    private Vector sigmoid(Vector x) {
        return VectorOperations.applyFunction(x, v -> 1.0 / (1.0 + Math.exp(-v)));
    }
    
    private Vector tanh(Vector x) {
        return VectorOperations.applyFunction(x, v -> Math.tanh(v));
    }
    
    public static class LSTMState {
        Vector h;  // hidden state
        Vector c;  // cell state
        
        public LSTMState(Vector h, Vector c) {
            this.h = h;
            this.c = c;
        }
    }
}
```

### 5.2 LSTM Layer

```java
package com.ml.rnn;

import java.util.ArrayList;
import java.util.List;

public class LSTMLayer {
    private LSTMCell cell;
    private int sequenceLength;
    
    public LSTMLayer(int inputSize, int hiddenSize, int seqLength) {
        this.cell = new LSTMCell(inputSize, hiddenSize);
        this.sequenceLength = seqLength;
    }
    
    public List<LSTMCell.LSTMState> forward(Vector[] inputs, LSTMState initialState) {
        List<LSTMCell.LSTMState> states = new ArrayList<>();
        LSTMState prev = initialState;
        
        for (int t = 0; t < sequenceLength; t++) {
            LSTMState curr = cell.forward(inputs[t], prev);
            states.add(curr);
            prev = curr;
        }
        
        return states;
    }
    
    public List<Vector> backward(List<LSTMCell.LSTMState> states,
                                  Vector[] inputs,
                                  Vector outputGradient,
                                  double learningRate) {
        int seqLen = states.size();
        Vector dh_next = VectorOperations.zeros(cell.hiddenSize);
        Vector dc_next = VectorOperations.zeros(cell.hiddenSize);
        
        List<Vector> inputGradients = new ArrayList<>();
        
        for (int t = seqLen - 1; t >= 0; t--) {
            // For simplicity, assume output from final hidden state
            Vector dh = (t == seqLen - 1) ? outputGradient : dh_next;
            
            // Backprop through cell
            // (Simplified - full implementation would compute all gate gradients)
            
            // Update weights with gradients
            updateWeights(learningRate);
            
            // Compute input gradients
            inputGradients.add(0, computeInputGradient(states.get(t)));
            
            // Propagate gradients
            dh_next = computeHiddenGradient(states.get(t), dh);
            dc_next = computeCellGradient(states.get(t), dh);
        }
        
        return inputGradients;
    }
    
    private void updateWeights(double lr) {
        // Update weights using computed gradients
        // (Implementation would apply gradients and clip if needed)
    }
    
    private Vector computeInputGradient(LSTMCell.LSTMState state) {
        // Compute dL/dx from cell state
        return VectorOperations.zeros(cell.inputSize);
    }
    
    private Vector computeHiddenGradient(LSTMCell.LSTMState state, Vector dh) {
        // Compute dL/dh_{t-1}
        return VectorOperations.zeros(cell.hiddenSize);
    }
    
    private Vector computeCellGradient(LSTMCell.LSTMState state, Vector dh) {
        // Compute dL/dc_{t-1}
        return VectorOperations.zeros(cell.hiddenSize);
    }
}
```

### 5.3 Complete RNN Cell

```java
package com.ml.rnn;

import com.ml.la.vector.Vector;
import com.ml.la.matrix.Matrix;

public class RNNCell {
    private int inputSize;
    private int hiddenSize;
    
    private Matrix W_xh;
    private Matrix W_hh;
    private Vector b_h;
    
    public RNNCell(int inputSize, int hiddenSize) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        
        java.util.Random gen = new java.util.Random(42);
        double scale = Math.sqrt(2.0 / (inputSize + hiddenSize));
        
        W_xh = Matrix.random(hiddenSize, inputSize, gen.nextLong());
        W_hh = Matrix.random(hiddenSize, hiddenSize, gen.nextLong());
        b_h = Vector.random(hiddenSize, gen.nextLong());
        
        // Scale
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                W_xh.data[i][j] *= scale;
            }
            for (int j = 0; j < hiddenSize; j++) {
                W_hh.data[i][j] *= scale;
            }
        }
    }
    
    public RNNCellState forward(Vector x, Vector h_prev) {
        // h_t = tanh(W_xh * x + W_hh * h_{t-1} + b)
        Vector h_tilde = MatrixOperations.multiply(W_xh, x);
        h_tilde = VectorOperations.add(h_tilde, MatrixOperations.multiply(W_hh, h_prev));
        h_tilde = VectorOperations.add(h_tilde, b_h);
        Vector h = tanh(h_tilde);
        
        return new RNNCellState(h, x, h_prev);
    }
    
    public Vector backward(RNNCellState state, Vector grad_h) {
        // Apply tanh derivative
        Vector grad_tilde = VectorOperations.applyFunction(state.h, 
            v -> (1 - v * v) * grad_h.get(0));
        
        // Gradients for weights
        // dL/dW_xh = grad_tilde * x^T
        // dL/dW_hh = grad_tilde * h_prev^T
        // dL/db = grad_tilde
        
        // Update weights
        Matrix gradW_xh = MatrixOperations.outerProduct(grad_tilde, state.x);
        Matrix gradW_hh = MatrixOperations.outerProduct(grad_tilde, state.h_prev);
        Vector gradB = grad_tilde;
        
        W_xh = MatrixOperations.subtract(W_xh, 
            MatrixOperations.scale(gradW_xh, 0.01));
        W_hh = MatrixOperations.subtract(W_hh, 
            MatrixOperations.scale(gradW_hh, 0.01));
        b_h = VectorOperations.subtract(b_h, 
            VectorOperations.scale(gradB, 0.01));
        
        // Propagate to previous hidden state
        Vector grad_h_prev = MatrixOperations.multiply(
            MatrixOperations.transpose(W_hh), grad_tilde);
        
        // Propagate to input
        Vector grad_x = MatrixOperations.multiply(
            MatrixOperations.transpose(W_xh), grad_tilde);
        
        return grad_x;
    }
    
    private Vector tanh(Vector x) {
        return VectorOperations.applyFunction(x, v -> Math.tanh(v));
    }
    
    public static class RNNCellState {
        Vector h;
        Vector x;
        Vector h_prev;
        
        public RNNCellState(Vector h, Vector x, Vector h_prev) {
            this.h = h;
            this.x = x;
            this.h_prev = h_prev;
        }
    }
}
```

### 5.4 Sequence Classifier

```java
package com.ml.rnn;

import java.util.List;
import com.ml.la.vector.Vector;
import com.ml.la.matrix.Matrix;

public class SequenceClassifier {
    private LSTMLayer encoder;
    private Matrix W_yh;
    private Vector b_y;
    private int hiddenSize;
    private int outputSize;
    
    public SequenceClassifier(int inputSize, int hiddenSize, int outputSize) {
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;
        
        encoder = new LSTMLayer(inputSize, hiddenSize, 0);  // variable length
        
        java.util.Random gen = new java.util.Random(42);
        W_yh = Matrix.random(outputSize, hiddenSize, gen.nextLong());
        b_y = Vector.random(outputSize, gen.nextLong());
    }
    
    public Vector forward(Vector[] sequence) {
        // Initialize LSTM state
        LSTMState initialState = new LSTMState(
            VectorOperations.zeros(hiddenSize),
            VectorOperations.zeros(hiddenSize)
        );
        
        // Encode sequence
        List<LSTMCell.LSTMState> states = encoder.forward(sequence, initialState);
        
        // Use final hidden state for classification
        LSTMState finalState = states.get(states.size() - 1);
        
        // Output projection
        Vector h_last = finalState.h;
        Vector logits = MatrixOperations.multiply(W_yh, h_last);
        logits = VectorOperations.add(logits, b_y);
        
        return softmax(logits);
    }
    
    public double train(Vector[] sequence, Vector yTrue) {
        // Forward pass
        Vector prediction = forward(sequence);
        
        // Compute loss
        double loss = crossEntropy(yTrue, prediction);
        
        // Backward pass (BPTT)
        Vector gradOutput = VectorOperations.subtract(prediction, yTrue);
        
        // Backprop through output layer
        Vector gradH = MatrixOperations.multiply(
            MatrixOperations.transpose(W_yh), gradOutput);
        
        // Backprop through LSTM
        // (Simplified - full implementation would do BPTT)
        
        // Update output layer
        LSTMState finalState = getLastState();
        Matrix gradW_yh = MatrixOperations.outerProduct(gradOutput, finalState.h);
        W_yh = MatrixOperations.subtract(W_yh, 
            MatrixOperations.scale(gradW_yh, 0.01));
        b_y = VectorOperations.subtract(b_y, 
            VectorOperations.scale(gradOutput, 0.01));
        
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

### 5.5 Bidirectional LSTM

```java
package com.ml.rnn;

import java.util.List;
import com.ml.la.vector.Vector;

public class BiLSTM {
    private LSTMLayer forwardLayer;
    private LSTMLayer backwardLayer;
    
    public BiLSTM(int inputSize, int hiddenSize) {
        forwardLayer = new LSTMLayer(inputSize, hiddenSize, 0);
        backwardLayer = new LSTMLayer(inputSize, hiddenSize, 0);
    }
    
    public List<Vector> forward(Vector[] sequence) {
        int seqLen = sequence.length;
        
        // Forward pass
        LSTMState initForward = new LSTMState(
            VectorOperations.zeros(hiddenSize),
            VectorOperations.zeros(hiddenSize)
        );
        List<LSTMCell.LSTMState> forwardStates = forwardLayer.forward(sequence, initForward);
        
        // Backward pass (reverse sequence)
        Vector[] reversedSeq = reverse(sequence);
        LSTMState initBackward = new LSTMState(
            VectorOperations.zeros(hiddenSize),
            VectorOperations.zeros(hiddenSize)
        );
        List<LSTMCell.LSTMState> backwardStates = backwardLayer.forward(reversedSeq, initBackward);
        
        // Combine forward and backward hidden states
        List<Vector> output = new ArrayList<>();
        for (int t = 0; t < seqLen; t++) {
            Vector h_fwd = forwardStates.get(t).h;
            Vector h_bwd = backwardStates.get(seqLen - 1 - t).h;
            output.add(VectorOperations.add(h_fwd, h_bwd));
        }
        
        return output;
    }
    
    private Vector[] reverse(Vector[] seq) {
        Vector[] reversed = new Vector[seq.length];
        for (int i = 0; i < seq.length; i++) {
            reversed[i] = seq[seq.length - 1 - i];
        }
        return reversed;
    }
}
```

## 6. Applications

### 6.1 Text Classification
- Sentiment analysis
- Topic classification
- Spam detection

### 6.2 Sequence-to-Sequence
- Machine translation
- Text summarization
- Chatbots

### 6.3 Time Series
- Stock prediction
- Weather forecasting
- Anomaly detection