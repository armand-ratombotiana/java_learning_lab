# Real-World Project: Neural Network Training Framework

## Objective
Build a minimal neural network library with multiple optimization algorithms.

## Architecture
1. **Layer**: Dense, activation (sigmoid, ReLU, tanh, softmax)
2. **Network**: Sequential model with forward/backward pass
3. **Loss**: MSE, cross-entropy
4. **Optimizer**: SGD, Momentum, Adam (pluggable interface)
5. **Training**: mini-batch, epoch, learning rate scheduling

## Components
- Layer (weights, biases, forward, backward)
- Activation functions and their derivatives
- Loss functions
- Optimizer interface (SGD, Momentum, Adam implementations)
- Network (add layers, compile, fit, predict)
- Data utilities (mini-batch generation)

## Training
Train on MNIST handwritten digit classification:
- Achieve > 90% accuracy on test set
- Compare convergence speed across optimizers
- Plot learning curves
- Experiment with architecture (hidden layers, neurons)

## Evaluation Criteria
- Correct backpropagation implementation
- Convergence on MNIST
- Optimizer comparison analysis
- Extensible design
