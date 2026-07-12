# Mathematical Foundation of Word2Vec

## 📐 The Skip-Gram Objective
Let's formalize the Skip-Gram model. Given a sequence of training words $w_1, w_2, w_3, \dots, w_T$, the objective is to maximize the average log probability of predicting the surrounding context words given the center word.

The objective function $J(\theta)$ is:
$$ J(\theta) = \frac{1}{T} \sum_{t=1}^{T} \sum_{-c \le j \le c, j \neq 0} \log P(w_{t+j} | w_t) $$

Where:
- $c$ is the size of the context window.
- $w_t$ is the center word.
- $w_{t+j}$ is a context word.

The basic probability $P(w_O | w_I)$ (Output word given Input word) is calculated using the Softmax function over the dot product of their vectors:
$$ P(w_O | w_I) = \frac{\exp(v'_{w_O} \cdot v_{w_I})}{\sum_{w=1}^{V} \exp(v'_w \cdot v_{w_I})} $$

Where:
- $v_w$ is the "input" vector representation of word $w$.
- $v'_w$ is the "output" vector representation of word $w$.
- $V$ is the total vocabulary size.

## ⚠️ The Softmax Bottleneck & Negative Sampling
Look closely at the denominator of the Softmax equation: $\sum_{w=1}^{V} \dots$
To calculate the probability of a *single* context word, we must calculate the dot product of the center word against **every single word in the entire vocabulary** ($V$). If $V = 1,000,000$, this is computationally impossible to do for every training step.

### Solution: Negative Sampling
Instead of updating all 1,000,000 weights for a single correct context word, we convert it into a binary classification problem.
1. We take the true context word (Positive Sample) and try to maximize its dot product with the center word.
2. We randomly select $K$ words from the vocabulary that are *not* in the context window (Negative Samples). We try to minimize their dot products with the center word.

The new objective function for a specific center-context pair becomes:
$$ \log \sigma(v'_{w_O} \cdot v_{w_I}) + \sum_{i=1}^{K} \mathbb{E}_{w_i \sim P_n(w)} [\log \sigma(-v'_{w_i} \cdot v_{w_I})] $$

Where $\sigma(x) = \frac{1}{1 + e^{-x}}$ (the Sigmoid function).
Now, instead of updating 1,000,000 weights, we only update $1 + K$ weights (e.g., 6 weights if $K=5$). This mathematical trick is what made Word2Vec computationally feasible.