# Mathematical Foundation of Positional Encoding

## 📐 The Problem
We need a way to encode the position $pos$ of a token into a vector of the same dimension $d_{model}$ as the word embeddings, so they can be summed together.

Requirements for a good positional encoding:
1. It should output a unique encoding for each time-step (position).
2. The distance between any two time-steps should be consistent across sentences of different lengths.
3. The model should generalize to longer sentences than those seen in training.
4. It must be deterministic.

## 🌊 Sinusoidal Positional Encoding
The creators of the Transformer chose to use sine and cosine functions of different frequencies.

Let:
- $pos$ be the position of the word in the sequence (0, 1, 2, ...).
- $i$ be the dimension index of the embedding (0, 1, ..., $d_{model}-1$).
- $d_{model}$ be the size of the embedding vector.

The positional encoding $PE$ is defined as:

For even dimensions ($2i$):
$$ PE(pos, 2i) = \sin\left(\frac{pos}{10000^{2i / d_{model}}}\right) $$

For odd dimensions ($2i + 1$):
$$ PE(pos, 2i+1) = \cos\left(\frac{pos}{10000^{2i / d_{model}}}\right) $$

### Why this specific math?
1. **Bounded Values**: Sine and Cosine always output values between -1 and +1. This ensures the positional encodings don't overpower the actual word embeddings when added together.
2. **Relative Positions**: By using a combination of sines and cosines, the authors proved that for any fixed offset $k$, $PE(pos + k)$ can be represented as a linear function of $PE(pos)$. This makes it easy for the model to learn to attend by *relative* positions, not just absolute positions.
3. **Wavelengths**: The wavelengths form a geometric progression from $2\pi$ to $10000 \cdot 2\pi$. The early dimensions (low $i$) oscillate very quickly, while the later dimensions (high $i$) oscillate very slowly. This creates a unique "binary-like" continuous signature for every position.