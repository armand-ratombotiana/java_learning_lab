# RNN and LSTM - Theory

## 1. Recurrent Neural Networks

### Sequential Processing
```
h_t = f(W * x_t + U * h_{t-1} + b)
```

### BPTT (Backpropagation Through Time)
- Unfold network through time
- Gradient computation over sequences
- Vanishing/exploding gradients

## 2. LSTM

### Gates
- **Forget**: what to discard
- **Input**: what to store
- **Output**: what to output

### Equations
```
f_t = σ(W_f · [h_{t-1}, x_t] + b_f)
i_t = σ(W_i · [h_{t-1}, x_t] + b_i)
C̃_t = tanh(W_C · [h_{t-1}, x_t] + b_C)
C_t = f_t * C_{t-1} + i_t * C̃_t
o_t = σ(W_o · [h_{t-1}, x_t] + b_o)
h_t = o_t * tanh(C_t)
```

## 3. GRU

### Simplified LSTM
- Update gate (combines forget/input)
- Reset gate
- Fewer parameters

## 4. Attention Mechanism

### Key Innovation
- Focus on relevant parts
- No compression to fixed vector

### Attention Weights
```
α_t = softmax(score(h_{t-1}, s_j))
context = Σ_t α_t * s_t
```

## 5. Sequence Models

### Applications
- Language modeling
- Machine translation
- Text generation
- Time series forecasting