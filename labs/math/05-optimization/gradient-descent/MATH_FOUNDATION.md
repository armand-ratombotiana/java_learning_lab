# Mathematical Foundation of Gradient Descent

## 📐 The Objective
Let $J(\theta)$ be a cost function, where $\theta$ represents the parameters of our model (e.g., weights and biases).
Our goal is to find the optimal parameters $\theta^*$ that minimize the cost function:
$$ \theta^* = \arg\min_{\theta} J(\theta) $$

## ∇ The Gradient Vector
In single-variable calculus, the derivative $f'(x)$ gives the slope of the tangent line at a point.
In multivariable calculus, the **Gradient** (denoted by $\nabla$) is a vector containing all the partial derivatives of the function.

For a cost function $J(\theta_0, \theta_1, \dots, \theta_n)$, the gradient is:
$$ \nabla J(\theta) = \begin{bmatrix} \frac{\partial J}{\partial \theta_0} \\ \frac{\partial J}{\partial \theta_1} \\ \vdots \\ \frac{\partial J}{\partial \theta_n} \end{bmatrix} $$

**Crucial Property**: The gradient vector always points in the direction of the *steepest ascent* (uphill). Therefore, the negative gradient $-\nabla J(\theta)$ points in the direction of the *steepest descent* (downhill).

## 🔄 The Update Rule
To move our parameters downhill towards the minimum, we subtract a fraction of the gradient from our current parameters.

Let $\alpha$ be the learning rate. The update rule for a single parameter $\theta_j$ is:
$$ \theta_j := \theta_j - \alpha \frac{\partial J}{\partial \theta_j} $$

This update is applied simultaneously to all parameters in the model.

## 🧮 Example: Linear Regression (MSE)
Let's derive the specific gradient for a simple Linear Regression model with one variable: $h_\theta(x) = \theta_0 + \theta_1 x$.

The Cost Function is Mean Squared Error (MSE):
$$ J(\theta_0, \theta_1) = \frac{1}{2m} \sum_{i=1}^{m} (h_\theta(x^{(i)}) - y^{(i)})^2 $$

To find the update rule, we need the partial derivatives of $J$ with respect to $\theta_0$ and $\theta_1$.

1. **Partial derivative w.r.t $\theta_0$** (applying the chain rule):
$$ \frac{\partial J}{\partial \theta_0} = \frac{1}{m} \sum_{i=1}^{m} (h_\theta(x^{(i)}) - y^{(i)}) $$

2. **Partial derivative w.r.t $\theta_1$**:
$$ \frac{\partial J}{\partial \theta_1} = \frac{1}{m} \sum_{i=1}^{m} (h_\theta(x^{(i)}) - y^{(i)}) \cdot x^{(i)} $$

These are the exact formulas we will implement in code to train a linear regression model.