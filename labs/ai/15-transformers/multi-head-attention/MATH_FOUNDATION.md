# Multi-Head Attention Mathematical Foundation

## 📐 The Multi-Head Projection
Given an input matrix $X$ of shape $(seq\_length \times d_{model})$, we want to project it into $h$ different heads.

For each head $i \in \{1, \dots, h\}$, we have learned weight matrices:
- $W_i^Q \in \mathbb{R}^{d_{model} \times d_k}$
- $W_i^K \in \mathbb{R}^{d_{model} \times d_k}$
- $W_i^V \in \mathbb{R}^{d_{model} \times d_v}$

The queries, keys, and values for each head are calculated as:
$$ Q_i = X W_i^Q, \quad K_i = X W_i^K, \quad V_i = X W_i^V $$

## 🎯 Scaled Dot-Product per Head
Each head computes its own attention independent of the others:
$$ \text{head}_i = \text{Attention}(Q_i, K_i, V_i) = \text{softmax}\left(\frac{Q_i K_i^T}{\sqrt{d_k}}\right) V_i $$

## 🔗 Concatenation and Final Projection
The outputs of all $h$ heads are concatenated along the feature dimension:
$$ \text{MultiHead}(Q, K, V) = \text{Concat}(\text{head}_1, \dots, \text{head}_h) W^O $$

Where $W^O \in \mathbb{R}^{hd_v \times d_{model}}$ is a learned parameter matrix that projects the concatenated vector back to the original model dimension.

### Dimensionality Constraint
In most implementations (like BERT or GPT), we set $d_k = d_v = d_{model} / h$. 
This ensures that the total computational cost is similar to that of single-head attention with full dimensionality.