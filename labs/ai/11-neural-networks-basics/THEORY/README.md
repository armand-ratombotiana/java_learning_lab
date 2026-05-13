# Neural Networks Basics - Theory

## 1. Perceptron

### Single Neuron
```
y = σ(w·x + b)
```

### Decision Boundary
- Linear classifier
- AND, OR (linearly separable)
- XOR (not separable)

## 2. Multi-Layer Perceptron (MLP)

### Architecture
- Input layer
- Hidden layers
- Output layer

### Universal Approximation
- Can approximate any continuous function
- With enough hidden units

## 3. Activation Functions

### Sigmoid
```
σ(x) = 1 / (1 + e^(-x))
```
- (0, 1) range
- Gradient vanishing

### Tanh
```
tanh(x) = (e^x - e^(-x)) / (e^x + e^(-x))
```
- (-1, 1) range
- Zero-centered

### ReLU
```
max(0, x)
```
- Sparse activation
- Fast convergence

### Leaky ReLU, ELU, Swish

## 4. Architecture Design

### Number of Layers
- Shallow: 1-2 layers
- Deep: many layers

### Width
- Number of neurons per layer

### Regularization
- Dropout
- L2 weight decay
- Early stopping

## 5. Practical Considerations

### Initialization
- Xavier/Glorot (tanh)
- He (ReLU)

### Batch Normalization
- Normalize layer inputs
- Reduces internal covariate shift