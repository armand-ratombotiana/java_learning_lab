# Backpropagation - FLASHCARDS

## Chain Rule

### Card 1
**Q:** Chain rule formula for composite function?
**A:** dL/dx = dL/dy * dy/dx

### Card 2
**Q:** Backpropagation order?
**A:** From output layer to input layer

### Card 3
**Q:** Delta definition?
**A:** δ = ∂L/∂z (gradient w.r.t. pre-activation)

### Card 4
**Q:** How to compute upstream gradient?
**A:** Sum over weights from next layer: δ_prev = W^T * δ_current

## Gradient Computation

### Card 5
**Q:** Weight gradient formula?
**A:** ∂L/∂W = δ * a_prev^T

### Card 6
**Q:** Bias gradient formula?
**A:** ∂L/∂b = δ

### Card 7
**Q:** Activation gradient formula?
**A:** ∂L/∂a_prev = W^T * δ

### Card 8
**Q:** Sigmoid derivative?
**A:** σ'(x) = σ(x)(1 - σ(x))

### Card 9
**Q:** Tanh derivative?
**A:** tanh'(x) = 1 - tanh²(x)

### Card 10
**Q:** ReLU derivative?
**A:** ReLU'(x) = 1 if x > 0, else 0

## Training

### Card 11
**Q:** Gradient descent weight update?
**A:** W = W - η * ∂L/∂W

### Card 12
**Q:** Momentum update formula?
**A:** v = γv - η∇W; W = W + v

### Card 13
**Q:** Weight decay (L2) formula?
**A:** W = W - η(∂L/∂W + λW)

### Card 14
**Q:** Vanishing gradient cause?
**A:** Sigmoid/Tanh derivatives < 1 multiply through layers

### Card 15
**Q:** Exploding gradient occurs when?
**A:** Gradients grow exponentially through layers

### Card 16
**Q:** Gradient clipping formula?
**A:** clip(grad, -threshold, threshold)

### Card 17
**Q:** Learning rate too small leads to?
**A:** Slow convergence

### Card 18
**Q:** Learning rate too large leads to?
**A:** Oscillation or divergence

## Implementation

### Card 19
**Q:** Gradient checking formula?
**A:** (f(x+h) - f(x-h)) / 2h

### Card 20
**Q:** Numerical gradient should be close to?
**A:** Analytical gradient (error < 1e-5)

### Card 21
**Q:** Xavier init helps with?
**A:** Maintaining gradient variance across layers

### Card 22
**Q:** Batch norm reduces?
**A:** Internal covariate shift, allows higher LR

### Card 23
**Q:** Softmax gradient complexity?
**A:** Requires Jacobian matrix due to sum constraint

### Card 24
**Q:** Mini-batch advantage?
**A:** Reduces variance, better generalization than SGD

---

**Total: 24 flashcards**