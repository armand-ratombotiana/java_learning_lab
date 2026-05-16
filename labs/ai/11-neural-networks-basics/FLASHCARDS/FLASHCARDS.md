# Neural Networks Basics - FLASHCARDS

## Perceptron

### Card 1
**Q:** Perceptron formula?
**A:** y = activation(w^T x + b)

### Card 2
**Q:** Perceptron learning rule?
**A:** w = w + lr * (y - pred) * x

### Card 3
**Q:** Decision boundary of single perceptron?
**A:** Linear hyperplane

### Card 4
**Q:** XOR problem with single perceptron?
**A:** Not solvable - not linearly separable

### Card 5
**Q:** Bias purpose in perceptron?
**A:** Shifts decision boundary from origin

## MLP Architecture

### Card 6
**Q:** Universal approximation theorem?
**A:** Single hidden layer with enough units can approximate any continuous function

### Card 7
**Q:** What does hidden layer learn?
**A:** Hierarchical feature representations

### Card 8
**Q:** Fully connected layer?
**A:** Every neuron connects to every neuron in next layer

### Card 9
**Q:** Width vs depth trade-off?
**A:** Wide = more parameters per layer; Deep = hierarchical features

### Card 10
**Q:** When to add more layers?
**A:** When underfitting, model needs more capacity

## Activation Functions

### Card 11
**Q:** Sigmoid formula and range?
**A:** σ(x) = 1/(1+e^-x), range (0,1)

### Card 12
**Q:** Tanh formula and range?
**A:** tanh(x), range (-1,1), zero-centered

### Card 13
**Q:** ReLU formula and advantage?
**A:** max(0,x), sparse activation, fast computation

### Card 14
**Q:** Dying ReLU problem?
**A:** ReLU neurons can get stuck outputting 0 forever

### Card 15
**Q:** Softmax formula?
**A:** softmax(x)_i = e^{x_i} / Σ_j e^{x_j}

### Card 16
**Q:** Vanishing gradient cause?
**A:** Sigmoid/tanh derivatives < 1, gradients shrink exponentially

## Loss Functions

### Card 17
**Q:** Cross-entropy formula?
**A:** L = -Σ y * log(ŷ)

### Card 18
**Q:** MSE formula?
**A:** L = (1/n) Σ (y - ŷ)²

### Card 19
**Q:** Why cross-entropy for classification?
**A:** Better gradient flow, probabilistic interpretation

### Card 20
**Q:** Hinge loss formula?
**A:** L = max(0, 1 - y * pred)

## Initialization

### Card 21
**Q:** Xavier initialization formula?
**A:** W ~ N(0, 2/(n_in + n_out))

### Card 22
**Q:** He initialization formula?
**A:** W ~ N(0, 2/n_in) - for ReLU

### Card 23
**Q:** Bias initialization typically?
**A:** Zeros or small constants (0.01 for ReLU)

### Card 24
**Q:** Too small init leads to?
**A:** Vanishing gradients, slow learning

---

**Total: 24 flashcards**