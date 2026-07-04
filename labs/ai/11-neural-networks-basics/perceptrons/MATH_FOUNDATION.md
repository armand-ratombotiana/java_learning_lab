# Mathematical Foundation of the Perceptron

## 📐 The Forward Pass
The output $y$ of a Perceptron is determined by a Step Function applied to the weighted sum of inputs plus the bias.

Let:
- $\mathbf{x} = [x_1, x_2, \dots, x_n]$ be the input vector.
- $\mathbf{w} = [w_1, w_2, \dots, w_n]$ be the weight vector.
- $b$ be the bias.

The weighted sum $z$ is the dot product of the weights and inputs, plus the bias:
$$ z = \mathbf{w} \cdot \mathbf{x} + b = \sum_{i=1}^{n} w_i x_i + b $$

The activation function $f(z)$ (Heaviside Step Function) is defined as:
$$
y = f(z) = \begin{cases} 
1 & \text{if } z \geq 0 \\
0 & \text{if } z < 0 
\end{cases}
$$

## 🔄 The Perceptron Learning Rule
How does the Perceptron learn? It starts with random weights and bias. It makes a prediction, compares it to the true label, and updates the weights if it made a mistake.

Let:
- $\alpha$ be the learning rate (a small positive number, e.g., 0.01).
- $y_{\text{true}}$ be the actual correct label (0 or 1).
- $y_{\text{pred}}$ be the predicted label from the Perceptron.
- The error $E = y_{\text{true}} - y_{\text{pred}}$.

If the prediction is correct, $E = 0$, and no weights change.
If the prediction is wrong, the weights and bias are updated according to the Perceptron Learning Rule:

$$ \Delta w_i = \alpha \times E \times x_i $$
$$ w_i = w_i + \Delta w_i $$

$$ b = b + (\alpha \times E) $$

### Why this works:
- If $y_{\text{true}} = 1$ but $y_{\text{pred}} = 0$, the error is $+1$. The weights are *increased* proportionally to the input, pushing the weighted sum higher for the next time.
- If $y_{\text{true}} = 0$ but $y_{\text{pred}} = 1$, the error is $-1$. The weights are *decreased*, pushing the weighted sum lower.