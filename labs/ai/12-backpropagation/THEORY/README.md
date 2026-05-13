# Backpropagation - Theory

## 1. Forward Pass

### Computation Graph
- Each node: operation
- Edges: data flow

### Example
```
x → [Linear] → z → [ReLU] → a → [Linear] → ŷ → [Loss] → L
```

### Store Activations
- Save inputs and outputs for each layer
- Required for backward pass

## 2. Backward Pass

### Chain Rule
```
∂L/∂w = ∂L/∂ŷ * ∂ŷ/∂z * ∂z/∂w
```

### Gradient Flow
```
∂L/∂a = Σᵢ ∂L/∂zᵢ * ∂zᵢ/∂a
```

### Process
1. Compute output gradient
2. Propagate backward through layers
3. Compute parameter gradients

## 3. Algorithm

### For each sample:
1. Forward pass, store activations
2. Compute output error
3. Backward pass
4. Update weights

### Mini-batch: Average gradients

## 4. Practical Issues

### Vanishing Gradients
- Deep networks
- Sigmoid/tanh activation
- Solution: ReLU, skip connections

### Exploding Gradients
- Large weights
- Solution: Gradient clipping

### Numerical Stability
- Log-sum-trick for softmax
- Gradient checking

## 5. Computational Graph

### Auto Differentiation
- Forward mode: for single input
- Reverse mode (backprop): for single output