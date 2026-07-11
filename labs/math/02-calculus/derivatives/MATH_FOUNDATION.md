# Mathematical Foundation of Derivatives

## 📐 The Formal Definition (Limits)
The derivative of a function $f(x)$ with respect to $x$ is denoted as $f'(x)$ or $\frac{df}{dx}$. 
It is formally defined using the concept of a limit:

$$ f'(x) = \lim_{h \to 0} \frac{f(x + h) - f(x)}{h} $$

This formula calculates the slope between two points $(x, f(x))$ and $(x+h, f(x+h))$ as the distance $h$ between them becomes infinitely small.

## 📏 Common Rules of Differentiation
Calculating limits from scratch is tedious. Mathematicians have derived shortcuts (rules) for common functions.

1. **Power Rule**: If $f(x) = x^n$, then $f'(x) = n x^{n-1}$
   - Example: $f(x) = x^2 \Rightarrow f'(x) = 2x$
2. **Constant Rule**: The derivative of a constant is 0.
   - Example: $f(x) = 5 \Rightarrow f'(x) = 0$
3. **Sum Rule**: $\frac{d}{dx}[f(x) + g(x)] = f'(x) + g'(x)$

## 🔗 The Chain Rule
This is the most important rule for Deep Learning (Backpropagation). It tells us how to differentiate composite functions (functions inside other functions).

If $y = f(g(x))$, then:
$$ \frac{dy}{dx} = f'(g(x)) \cdot g'(x) $$

Alternatively, using Leibniz notation:
$$ \frac{dy}{dx} = \frac{dy}{du} \cdot \frac{du}{dx} $$
Where $u = g(x)$.

## 🔀 Partial Derivatives
In Machine Learning, cost functions have millions of variables (weights), not just one $x$.
A partial derivative measures how a function changes as *one* specific variable changes, while holding all other variables constant.

Denoted by $\partial$ (del):
If $f(x, y) = x^2 y + y^3$, then:
- Partial w.r.t $x$: $\frac{\partial f}{\partial x} = 2xy$ (treat $y$ as a constant)
- Partial w.r.t $y$: $\frac{\partial f}{\partial y} = x^2 + 3y^2$ (treat $x$ as a constant)