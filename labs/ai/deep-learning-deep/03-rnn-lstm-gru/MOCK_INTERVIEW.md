# Mock Interview: Implement LSTM from Scratch and Explain Gradient Flow

## Scenario
You are interviewing for a research engineer role at a NLP lab. The interviewer wants to test your understanding of recurrent networks and vanishing gradients.

## Interviewer Opening Question
"Implement an LSTM cell from scratch using NumPy, then explain how its gating mechanism solves the vanishing gradient problem."

## Candidate Response
"I'll implement the full LSTM forward pass with input, forget, cell, and output gates. The key insight is that the cell state acts as a gradient highway — the forget gate controls gradient flow multiplicatively and the additive cell updates prevent gradients from vanishing."

## Interviewer Probing Questions

**Q: How does LSTM compare to GRU?**
"LSTM has three gates (input, forget, output) and a separate cell state. GRU has two gates (reset, update) and merges cell/hidden state. GRU is computationally cheaper, LSTM is more expressive with explicit memory control."

**Q: What causes vanishing gradients in vanilla RNNs?**
"The recurrent weight matrix is multiplied at every timestep. During backprop, the gradient contains a product of Jacobians over T steps. If the largest eigenvalue is < 1, the gradient vanishes exponentially with T."

**Q: How do LSTMs solve this?**
"The cell state derivative involves a sum of products through the forget gate, not a chain. The additive gradient path means the gradient can flow unchanged through the cell state when the forget gate is near 1."

## Candidate Solution (Python)

```python
import numpy as np

class LSTMCell:
    def __init__(self, input_size, hidden_size):
        self.input_size = input_size
        self.hidden_size = hidden_size

        # Combined weight matrix for all gates
        self.W = np.random.randn(hidden_size, input_size + hidden_size) * 0.01
        self.b = np.zeros((hidden_size, 1))

    def sigmoid(self, x):
        return 1.0 / (1.0 + np.exp(-np.clip(x, -500, 500)))

    def tanh(self, x):
        return np.tanh(x)

    def forward(self, x, h_prev, c_prev):
        """
        x: (input_size, 1)
        h_prev: (hidden_size, 1)
        c_prev: (hidden_size, 1)
        """
        # Concatenate input and previous hidden
        concat = np.vstack([x, h_prev])  # (input_size + hidden_size, 1)

        # Linear transformation
        gates = self.W @ concat + self.b  # (hidden_size, 1)

        # Split into 4 gates
        i = self.sigmoid(gates[0*self.hidden_size : 1*self.hidden_size])
        f = self.sigmoid(gates[1*self.hidden_size : 2*self.hidden_size])
        g = self.tanh(gates[2*self.hidden_size : 3*self.hidden_size])
        o = self.sigmoid(gates[3*self.hidden_size : 4*self.hidden_size])

        # Cell state update
        c = f * c_prev + i * g

        # Hidden state
        h = o * self.tanh(c)

        cache = (x, h_prev, c_prev, concat, i, f, g, o, c, h)
        return h, c, cache

    def backward(self, dh, dc_next, cache):
        x, h_prev, c_prev, concat, i, f, g, o, c, h = cache

        # Backprop through tanh and output gate
        do = dh * self.tanh(c)
        do = do * o * (1 - o)  # sigmoid derivative

        dc = dh * o * (1 - self.tanh(c) ** 2) + dc_next

        # Backprop through cell state gates
        df = dc * c_prev
        df = df * f * (1 - f)

        di = dc * g
        di = di * i * (1 - i)

        dg = dc * i
        dg = dg * (1 - g ** 2)

        # Concatenate gate gradients
        d_hidden = np.vstack([di, df, dg, do])

        # Backprop through linear layer
        dW = d_hidden @ concat.T
        db = d_hidden
        dconcat = self.W.T @ d_hidden

        # Split input and hidden gradients
        dx = dconcat[:self.input_size]
        dh_prev = dconcat[self.input_size:]
        dc_prev = f * dc

        return dx, dh_prev, dc_prev, dW, db

class LSTM:
    def __init__(self, input_size, hidden_size, output_size):
        self.hidden_size = hidden_size
        self.cell = LSTMCell(input_size, hidden_size)
        self.W_output = np.random.randn(output_size, hidden_size) * 0.01
        self.b_output = np.zeros((output_size, 1))

    def forward(self, inputs):
        # inputs: list of (input_size, 1) vectors
        h = np.zeros((self.hidden_size, 1))
        c = np.zeros((self.hidden_size, 1))
        caches = []
        hiddens = []
        for x in inputs:
            h, c, cache = self.cell.forward(x, h, c)
            caches.append(cache)
            hiddens.append(h)
        output = self.W_output @ h + self.b_output
        return output, hiddens, caches

    def backward(self, dh, dc_next, caches):
        dW = np.zeros_like(self.cell.W)
        db = np.zeros_like(self.cell.b)
        for cache in reversed(caches):
            dx, dh_prev, dc_next, dW_i, db_i = self.cell.backward(dh, dc_next, cache)
            dW += dW_i
            db += db_i
            dh = dh_prev
            dc_next = dc_next
        return dW, db

# Gradient check: The cell state gradient path
# dL/dc_t = dL/dh_t * o_t * (1 - tanh(c_t)^2) + f_{t+1} * dL/dc_{t+1}
# The forget gate f controls how much gradient flows back through time.
# When f is close to 1, gradients can flow unchanged across many timesteps.
```

## Interviewer Feedback
"Excellent implementation with proper forward/backward passes. Your explanation of how the forget gate creates a gradient highway is exactly right. The code is clean and well-structured."

## Key Takeaways
- LSTM uses three gates (input, forget, output) to control information flow
- Cell state provides an additive gradient path that prevents vanishing gradients
- Forget gate controls how much gradient flows through time
- GRU is a simplified alternative with two gates
- LSTM outperforms vanilla RNNs on long-range dependency tasks
