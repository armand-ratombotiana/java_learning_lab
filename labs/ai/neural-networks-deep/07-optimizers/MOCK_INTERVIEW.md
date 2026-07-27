# Mock Interview: Implement Adam Optimizer from Scratch with Weight Decay

## Scenario
You are interviewing for a ML engineer role. They want to verify your understanding of optimization algorithms beyond just calling optimizer.step().

## Interviewer Opening Question
"Implement the Adam optimizer from scratch with decoupled weight decay (AdamW). Explain the motivation behind each component."

## Candidate Response
"Adam combines momentum (first moment) and RMSprop (second moment) with bias correction. The update rule: m = beta1*m + (1-beta1)*g, v = beta2*v + (1-beta2)*g^2, then m_hat = m/(1-beta1^t), v_hat = v/(1-beta2^t), and w -= lr * m_hat/(sqrt(v_hat) + eps). AdamW decouples weight decay from the gradient update."

## Interviewer Probing Questions

**Q: Why do we need bias correction?**
"At early timesteps, m and v are initialized to 0, so they're biased toward zero. Bias correction divides by (1 - beta^t) to compensate, giving unbiased estimates of the first and second moments."

**Q: How does AdamW differ from standard Adam with L2?**
"In Adam, L2 adds lambda*w to the gradient, then Adam adapts it. This couples weight decay with the learning rate and adaptive gradients. AdamW subtracts lambda*w directly from the weights after the gradient update, properly decoupling it."

**Q: What are typical beta values?**
"beta1=0.9 (momentum decay), beta2=0.999 (RMS decay), eps=1e-8. These are standard from the Adam paper and work well across most problems."

## Candidate Solution (Python)

```python
import torch
import numpy as np
from typing import Iterable, Optional

class AdamW:
    """Adam with decoupled weight decay."""
    def __init__(self, params: Iterable[torch.nn.Parameter], lr=1e-3,
                 betas=(0.9, 0.999), eps=1e-8, weight_decay=0.01):
        self.params = list(params)
        self.lr = lr
        self.beta1, self.beta2 = betas
        self.eps = eps
        self.weight_decay = weight_decay
        self.t = 0
        self.m = [torch.zeros_like(p) for p in self.params]
        self.v = [torch.zeros_like(p) for p in self.params]

    def zero_grad(self):
        for p in self.params:
            if p.grad is not None:
                p.grad.detach_()
                p.grad.zero_()

    def step(self):
        self.t += 1
        for i, p in enumerate(self.params):
            if p.grad is None:
                continue
            g = p.grad.data

            # Decoupled weight decay
            if self.weight_decay != 0:
                p.data.mul_(1 - self.lr * self.weight_decay)

            # Update biased first moment estimate
            self.m[i] = self.beta1 * self.m[i] + (1 - self.beta1) * g

            # Update biased second raw moment estimate
            self.v[i] = self.beta2 * self.v[i] + (1 - self.beta2) * g * g

            # Bias correction
            m_hat = self.m[i] / (1 - self.beta1 ** self.t)
            v_hat = self.v[i] / (1 - self.beta2 ** self.t)

            # Update parameters
            p.data -= self.lr * m_hat / (torch.sqrt(v_hat) + self.eps)

class SGD:
    """SGD with momentum."""
    def __init__(self, params, lr=0.01, momentum=0.9, weight_decay=0.0):
        self.params = list(params)
        self.lr = lr
        self.momentum = momentum
        self.weight_decay = weight_decay
        self.velocities = [torch.zeros_like(p) for p in self.params]

    def zero_grad(self):
        for p in self.params:
            if p.grad is not None:
                p.grad.detach_()
                p.grad.zero_()

    def step(self):
        for i, p in enumerate(self.params):
            if p.grad is None:
                continue
            g = p.grad.data + self.weight_decay * p.data
            self.velocities[i] = self.momentum * self.velocities[i] + g
            p.data -= self.lr * self.velocities[i]

class OptimizerComparison:
    """Compare optimization trajectories."""
    def __init__(self):
        self.criterion = torch.nn.MSELoss()

    def train_comparison(self, model_class, x, y, optimizers, epochs=100):
        results = {}
        for name, opt_class, kwargs in optimizers:
            model = model_class()
            opt = opt_class(model.parameters(), **kwargs)
            losses = []
            for epoch in range(epochs):
                opt.zero_grad()
                out = model(x)
                loss = self.criterion(out, y)
                loss.backward()
                opt.step()
                losses.append(loss.item())
            results[name] = losses
        return results

# Gradient visualization for 2D optimization
def visualize_optimization_path():
    """Simulate optimization on a 2D loss landscape."""
    def loss_fn(w):
        return w[0]**2 + 10 * w[1]**2  # Elbow-shaped valley

    def grad_fn(w):
        return np.array([2 * w[0], 20 * w[1]])

    # SGD trajectory
    w_sgd = np.array([5.0, 1.0])
    lr_sgd = 0.01
    sgd_path = [w_sgd.copy()]
    for _ in range(50):
        w_sgd -= lr_sgd * grad_fn(w_sgd)
        sgd_path.append(w_sgd.copy())

    # Adam-like trajectory
    w_adam = np.array([5.0, 1.0])
    lr_adam = 0.1
    m = np.zeros(2)
    v = np.zeros(2)
    beta1, beta2 = 0.9, 0.999
    adam_path = [w_adam.copy()]
    for t in range(1, 51):
        g = grad_fn(w_adam)
        m = beta1 * m + (1 - beta1) * g
        v = beta2 * v + (1 - beta2) * g * g
        m_hat = m / (1 - beta1 ** t)
        v_hat = v / (1 - beta2 ** t)
        w_adam -= lr_adam * m_hat / (np.sqrt(v_hat) + 1e-8)
        adam_path.append(w_adam.copy())

    sgd_converged = loss_fn(sgd_path[-1])
    adam_converged = loss_fn(adam_path[-1])
    print(f"SGD final loss: {sgd_converged:.6f}")
    print(f"Adam final loss: {adam_converged:.6f}")
    print(f"Adam converges faster on ill-conditioned problems due to per-parameter adaptive LR")
```

## Interviewer Feedback
"Excellent implementation with all components: momentum, RMS scaling, bias correction, and decoupled weight decay. The comparison with SGD shows you understand why adaptive methods are needed. AdamW is the correct modern variant."

## Key Takeaways
- Adam = momentum (first moment) + RMSprop (second moment) + bias correction
- Bias correction compensates for zero-initialized moment estimates
- AdamW decouples weight decay from the adaptive gradient update
- beta1=0.9 controls momentum, beta2=0.999 controls RMS decay rate
- Adaptive per-parameter learning rates handle ill-conditioned landscapes
