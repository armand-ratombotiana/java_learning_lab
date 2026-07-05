# Math Foundation: Vector Database

## Distance Metrics
- Cosine distance: 1 - cos(θ) = 1 - (A · B) / (||A|| * ||B||)
- L2 (Euclidean) distance: sqrt(∑(a_i - b_i)^2)
- Dot product (inner) similarity: A · B = ∑(a_i * b_i)
- L2 squared: ∑(a_i - b_i)^2 (avoids sqrt, faster)

## HNSW Construction
- Probability of layer l: P(l) = e^(-l/mL) where mL = 1/ln(M)
- Expected connections per node: M * (1 + 1/ln(M))
- Search complexity: O(log n) for navigation, O(M * efSearch) for neighbor collection

## Product Quantization
- Codebook size: k * 2^(bits_per_sub_vector)
- Distortion: E[||x - q(x)||^2]
- SDC (Symmetric Distance Computation) vs ADC (Asymmetric)
