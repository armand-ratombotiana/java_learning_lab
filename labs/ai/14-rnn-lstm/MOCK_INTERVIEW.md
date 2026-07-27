# Mock Interview: RNN & LSTM

## Question 1: RNN Forward Pass
**Q**: Implement an RNN cell forward pass.

**A**:
```python
class RNNCell:
    def __init__(self, input_size, hidden_size):
        self.W_xh = np.random.randn(input_size, hidden_size) * 0.01
        self.W_hh = np.random.randn(hidden_size, hidden_size) * 0.01
        self.b_h = np.zeros(hidden_size)

    def forward(self, x, h_prev):
        # x: (batch, input_size), h_prev: (batch, hidden_size)
        h = np.tanh(x @ self.W_xh + h_prev @ self.W_hh + self.b_h)
        return h
```

**Follow-up**: What happens during backpropagation through time (BPTT)?
Gradients multiply through W_hh at each timestep. If ||W_hh|| < 1: vanishing. If > 1: exploding.

## Question 2: LSTM Architecture
**Q**: Explain the LSTM gating mechanism and how it mitigates vanishing gradients.

**A**: LSTM introduces a cell state C that acts as a memory highway.

```
Forget gate: f = sigmoid(x*W_xf + h*W_hf + bf)
Input gate: i = sigmoid(x*W_xi + h*W_hi + bi)
Candidate: g = tanh(x*W_xg + h*W_hg + bg)
Output gate: o = sigmoid(x*W_xo + h*W_ho + bo)

Cell state: C = f * C_prev + i * g
Hidden state: h = o * tanh(C)
```

The additive update C = f * C_prev + i * g allows gradients to flow through C unchanged (when f=1, i=0). This is the key to mitigating vanishing gradients.

## Question 3: LSTM vs GRU
**Q**: Compare LSTM and GRU. When would you use each?

**A**: 
| Feature | LSTM | GRU |
|---------|------|-----|
| Gates | 3 (forget, input, output) | 2 (reset, update) |
| Cell state | Yes (C) | No |
| Parameters | More | Fewer |
| Computation | Slower | Faster |
| Performance | Higher capacity | Often similar or better |

Use GRU for: smaller datasets, faster training, simpler models.
Use LSTM for: larger datasets, complex long-range dependencies, when you need explicit memory control.

## Question 4: Bidirectional RNNs
**Q**: Explain bidirectional RNNs. When would you use them vs unidirectional?

**A**: Bidirectional RNN processes sequence left-to-right AND right-to-left, concatenating hidden states.

Use cases:
- Text classification (whole document available)
- Named entity recognition (context from both sides)
- Machine translation (encoder, full sentence available)

Not suitable for: real-time recognition (need future context), online generation (autoregressive).

## Question 5: Sequence Modeling
**Q**: Compare RNNs, CNNs, and Transformers for sequence modeling.

**A**:
| Aspect | RNN | CNN | Transformer |
|--------|-----|-----|-------------|
| Parallelization | Sequential (bad) | Parallel (good) | Parallel (best) |
| Long-range | Poor (gradient issues) | Good with dilation | Best (direct attention) |
| Memory | O(1) per step | O(k) kernel | O(n^2) attention |
| Position | Implicit (order=step) | Implicit (padding) | Explicit (positional encoding) |
| Best for | Streaming, short seq | Medium seq, signal | Long seq, SOTA |

Modern NLP uses Transformers almost exclusively. RNNs still used for: speech recognition, real-time applications, small models on-device.
