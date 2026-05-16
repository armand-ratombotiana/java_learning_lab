# Backpropagation - QUIZ

## Section 1: Chain Rule

**Q1: Chain rule for composite functions d/dx f(g(x)) =**
- A) f'(g(x))
- B) f'(g(x)) * g'(x)
- C) f(g'(x))
- D) f'(x) * g'(x)

**Q2: In backpropagation, we compute gradients in which order?**
- A) Input to output
- B) Output to input
- C) Random order
- D) Parallel

**Q3: The delta at layer l depends on:**
- A) Only current layer
- B) Upstream delta and weights
- C) Input only
- D) Loss only

## Section 2: Gradient Computation

**Q4: For sigmoid, derivative σ'(x) =**
- A) σ(x)
- B) 1 - σ(x)
- C) σ(x)(1 - σ(x))
- D) σ(x) + σ(-x)

**Q5: Weight gradient ∂L/∂w =**
- A) δ * x
- B) δ + x
- C) δ - x
- D) δ / x

**Q6: Bias gradient ∂L/∂b =**
- A) δ
- B) δ / x
- C) δ * x
- D) 0

**Q7: Gradient w.r.t. input to a layer is computed using:**
- A) Upstream delta only
- B) Weights and upstream delta
- C) Only activation
- D) Only loss

## Section 3: Activation Derivatives

**Q8: ReLU derivative is:**
- A) Always 1
- B) 0 for x < 0, 1 for x ≥ 0
- C) σ(x)
- D) x

**Q9: Tanh derivative is:**
- A) 1 - tanh²(x)
- B) tanh(x)
- C) 1 + tanh(x)
- D) sech²(x)

**Q10: For softmax, gradient computation uses:**
- A) Simple derivative
- B) Jacobian matrix
- C) No gradient
- D) Fixed value

## Section 4: Training

**Q11: Vanishing gradients occur most with:**
- A) ReLU
- B) Identity
- C) Sigmoid/Tanh
- D) Leaky ReLU

**Q12: Learning rate too high causes:**
- A) Slow convergence
- B) Oscillation/divergence
- C) No change
- D) Perfect convergence

**Q13: Momentum helps by:**
- A) Increasing learning rate
- B) Adding past gradient direction
- C) Reducing weights
- D) Changing activation

**Q14: Weight decay (L2) adds to gradient:**
- A) λ * w
- B) λ / w
- C) w / λ
- D) 0

**Q15: Mini-batch gradient descent uses:**
- A) Single sample
- B) Full dataset
- C) Subset of data
- D) Random sample

## Section 5: Implementation

**Q16: Gradient checking compares:**
- A) Two networks
- B) Analytical vs numerical gradients
- C) Training vs test
- D) Weights vs biases

**Q17: Numerical gradient formula (centered):**
- A) (f(x+h) - f(x)) / h
- B) (f(x+h) - f(x-h)) / 2h
- C) f(x+h) + f(x-h)
- D) (f(x+h) + f(x-h)) / 2

**Q18: Xavier initialization helps with:**
- A) Loss calculation
- B) Gradient magnitude
- C) Data loading
- D) Activation function choice

**Q19: Batch normalization helps training by:**
- A) Adding noise
- B) Normalizing layer inputs
- C) Removing biases
- D) Changing loss

**Q20: Learning rate scheduling:**
- A) Constant rate
- B) Decreases over time
- C) Increases over time
- D) Random

---

## Answers

1. B
2. B
3. B
4. C
5. A
6. A
7. B
8. B
9. A
10. B
11. C
12. B
13. B
14. A
15. C
16. B
17. B
18. B
19. B
20. B