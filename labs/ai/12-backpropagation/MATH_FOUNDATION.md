# Mathematical Foundation of Backpropagation

## 📐 The Chain Rule
The core of Backpropagation is the **Chain Rule** from calculus. If a variable $z$ depends on $y$, and $y$ depends on $x$, then the rate of change of $z$ with respect to $x$ is:

$$ \frac{\partial z}{\partial x} = \frac{\partial z}{\partial y} \cdot \frac{\partial y}{\partial x} $$

In a neural network, the Loss $L$ depends on the network output $\hat{y}$, which depends on the pre-activation $z$, which depends on the weights $w$. We want to find $\frac{\partial L}{\partial w}$ (the gradient of the loss with respect to the weight) so we can update the weight using Gradient Descent.

## 🧮 Step-by-Step Derivation

Let's look at a single neuron in the output layer.
1. **Linear Combination**: $z = w \cdot a_{in} + b$
2. **Activation**: $a_{out} = \sigma(z)$ (where $\sigma$ is the Sigmoid function)
3. **Loss Function (MSE)**: $L = \frac{1}{2}(a_{out} - y_{true})^2$

We want to find $\frac{\partial L}{\partial w}$. Using the chain rule:
$$ \frac{\partial L}{\partial w} = \frac{\partial L}{\partial a_{out}} \cdot \frac{\partial a_{out}}{\partial z} \cdot \frac{\partial z}{\partial w} $$

Let's calculate each part (the local gradients):
1. **Derivative of Loss w.r.t Activation**: 
   $$ \frac{\partial L}{\partial a_{out}} = (a_{out} - y_{true}) $$
2. **Derivative of Activation w.r.t $z$** (Derivative of Sigmoid):
   $$ \frac{\partial a_{out}}{\partial z} = \sigma(z) \cdot (1 - \sigma(z)) = a_{out} \cdot (1 - a_{out}) $$
3. **Derivative of $z$ w.r.t weight**:
   $$ \frac{\partial z}{\partial w} = a_{in} $$

Multiplying them together gives the final gradient for that specific weight:
$$ \frac{\partial L}{\partial w} = (a_{out} - y_{true}) \cdot [a_{out} \cdot (1 - a_{out})] \cdot a_{in} $$

## 📉 Gradient Descent Update
Once we have the gradient $\frac{\partial L}{\partial w}$, we update the weight by moving in the *opposite* direction of the gradient (down the hill) by a small step size called the learning rate ($\alpha$):

$$ w_{new} = w_{old} - \alpha \frac{\partial L}{\partial w} $$

## ⚠️ The Vanishing Gradient Problem
Notice the derivative of the Sigmoid function: $\sigma(z)(1 - \sigma(z))$. The maximum value this can ever be is $0.25$ (when $z=0$). 
As we backpropagate through many layers, we multiply these gradients together (e.g., $0.25 \times 0.25 \times 0.25 \dots$). The gradient shrinks exponentially, eventually becoming so close to zero that the early layers of the network stop learning completely. This is the **Vanishing Gradient Problem**, which led to the adoption of the ReLU activation function in modern deep learning.