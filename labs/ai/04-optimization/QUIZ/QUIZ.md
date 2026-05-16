# Optimization - Quiz

## Assessment Questions

### Question 1
In gradient descent, what happens if the learning rate is set too high?

A) The algorithm converges faster
B) The algorithm may diverge or oscillate
C) The algorithm always finds the global minimum
D) The algorithm stops immediately

**Answer: B** - A high learning rate can cause the algorithm to overshoot the minimum and potentially diverge or oscillate indefinitely.

---

### Question 2
Which optimizer combines momentum with adaptive learning rates?

A) SGD only
B) Adagrad only
C) RMSprop only
D) Adam

**Answer: D** - Adam (Adaptive Moment Estimation) combines the benefits of momentum (first moment) with adaptive learning rates (second moment).

---

### Question 3
What is the purpose of momentum in optimization?

A) To increase the learning rate
B) To accelerate convergence in directions of consistent gradient
C) To reduce the model parameters
D) To add regularization

**Answer: B** - Momentum helps accelerate convergence by accumulating a velocity vector in directions that persist across iterations.

---

### Question 4
Which regularization adds a penalty proportional to the sum of absolute values of weights?

A) L2 (Ridge)
B) L1 (Lasso)
C) Elastic Net
D) Dropout

**Answer: B** - L1 (Lasso) regularization adds a penalty λ||w||₁, promoting sparsity by driving some weights to exactly zero.

---

### Question 5
In Adam optimizer, what do β₁ and β₂ represent?

A) Learning rate parameters
B) Momentum decay rates
C) Gradient clipping thresholds
D) L1 and L2 regularization strengths

**Answer: B** - β₁ is the decay rate for the first moment (momentum), and β₂ is the decay rate for the second moment (variance).

---

### Question 6
What does learning rate scheduling help achieve?

A) Fixed convergence speed
B) Better convergence through adaptive step sizes over time
C) Removing the need for gradients
D) Eliminating the need for validation data

**Answer: B** - Learning rate scheduling gradually reduces the learning rate, helping the optimizer converge more smoothly and avoid overshooting near the end of training.

---

### Question 7
What is the purpose of gradient clipping?

A) To increase gradient magnitude
B) To prevent exploding gradients in deep networks
C) To speed up computation
D) To add more parameters

**Answer: B** - Gradient clipping limits the maximum gradient magnitude, preventing numerical instability when gradients become too large.

---

### Question 8
Which method uses second-order derivative information?

A) Vanilla gradient descent
B) SGD with momentum
C) Newton's method
D) Adagrad

**Answer: C** - Newton's method uses the Hessian (second-order derivatives) to determine step direction and size, achieving quadratic convergence near optima.

---

### Question 9
What is the key advantage of mini-batch gradient descent over batch gradient descent?

A) Always achieves better accuracy
B) Faster iterations with noisy gradients
C) Uses the entire dataset
D) Requires no learning rate

**Answer: B** - Mini-batch gradient descent processes small batches of data, providing a balance between computation speed and gradient quality.

---

### Question 10
What problem does weight decay (L2 regularization) help mitigate?

A) Exploding gradients
B) Underfitting
C) Overfitting
D) Vanishing gradients

**Answer: C** - Weight decay penalizes large weights, encouraging simpler models and reducing overfitting.

---

### Question 11 (Bonus)
Why is warmup important in learning rate schedules?

A) It speeds up training immediately
B) It helps the model adapt gradually to increasing learning rates
C) It eliminates the need for validation
D) It always improves final accuracy

**Answer: B** - Warmup gradually increases the learning rate from small to larger values, helping stabilize early training and prevent large parameter updates before the model has adapted.

---

### Question 12 (Bonus)
Which optimizer is best for sparse data with many features?

A) SGD with constant learning rate
B) Adam
C) Adagrad
D) Standard momentum

**Answer: C** - Adagrad adapts the learning rate for each parameter based on historical gradients, making it particularly effective for sparse data.