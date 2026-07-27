# Mock Interview: Information Theory

**Topic:** Derive KL divergence, cross-entropy — explain mutual information

## Core Questions

### Q1: What is entropy and how is it interpreted?

**Answer:**
$H(X) = -\sum_{x} p(x) \log p(x)$ (discrete) or $H(X) = -\int p(x) \log p(x) dx$ (continuous)

**Interpretations:**
- Average information content (surprise) of a random variable
- Minimum expected number of bits to encode $X$ (source coding theorem)
- Measure of uncertainty/disorder

**Properties:**
- $H(X) \ge 0$ with equality iff $X$ is deterministic
- $H(X) \le \log |\mathcal{X}|$ with equality iff uniform distribution
- Joint entropy: $H(X, Y) = -\sum\sum p(x,y) \log p(x,y)$

### Q2: Derive KL divergence and cross-entropy.

**Answer:**
**KL Divergence (relative entropy):**
$D_{KL}(P \parallel Q) = \sum_x p(x) \log \frac{p(x)}{q(x)}$

**Properties:**
- $D_{KL}(P \parallel Q) \ge 0$ (Gibbs' inequality), equality iff $P = Q$
- Not symmetric: $D_{KL}(P \parallel Q) \ne D_{KL}(Q \parallel P)$
- Not a metric (no symmetry, no triangle inequality)

**Cross-entropy:**
$H(P, Q) = -\sum_x p(x) \log q(x)$

**Relationship:**
$H(P, Q) = H(P) + D_{KL}(P \parallel Q)$

**In ML:** Minimizing cross-entropy between data distribution $P$ and model $Q$ is equivalent to:
- Minimizing KL divergence (since $H(P)$ is constant)
- Maximizing log-likelihood (MLE)
- Making model distribution match data distribution

### Q3: Explain mutual information.

**Answer:**
$I(X; Y) = D_{KL}(P_{XY} \parallel P_X \otimes P_Y) = \sum\sum p(x,y) \log \frac{p(x,y)}{p(x)p(y)}$

**Properties:**
- $I(X; Y) \ge 0$, equality iff $X \perp Y$
- $I(X; Y) = H(X) - H(X \mid Y) = H(Y) - H(Y \mid X)$
- $I(X; Y) = H(X) + H(Y) - H(X, Y)$

**Interpretation:** How much information $Y$ reveals about $X$. Reduction in uncertainty of $X$ given knowledge of $Y$.

### Q4: Applications in ML.

**Answer:**
1. **Cross-entropy loss:** Most common classification loss — equivalent to minimizing $D_{KL}(p_{\text{data}} \parallel p_{\text{model}})$
2. **InfoGAN:** Maximize $I(c; G(z,c))$ between latent codes and generated images for disentangled representations
3. **Mutual Information Neural Estimation (MINE):** Train a neural network to estimate $I(X; Y)$ using Donsker-Varadhan representation
4. **Decision trees:** Information gain = $H(Y) - H(Y \mid X) = I(X; Y)$ for split evaluation
5. **Feature selection:** Select features with highest mutual information with target
6. **VAE:** ELBO = $\mathbb{E}_{q(z|x)}[\log p(x|z)] - D_{KL}(q(z|x) \parallel p(z))$ — KL regularizes latent distribution
7. **Contrastive learning:** InfoNCE loss maximizes mutual information between augmentations of same sample

### Q5: What is differential entropy (for continuous variables)?

**Answer:**
$h(X) = -\int p(x) \log p(x) dx$

**Key differences from discrete entropy:**
- Can be negative
- Not invariant to transformations (scaling changes entropy)
- Gaussian maximizes entropy for given variance: $h(N(\mu, \sigma^2)) = \frac{1}{2}\log(2\pi e \sigma^2)$

## Advanced

- **Chain rule:** $H(X,Y) = H(X) + H(Y \mid X)$, $I(X,Y; Z) = I(X; Z) + I(Y; Z \mid X)$
- **Data processing inequality:** $X \to Y \to Z \Rightarrow I(X; Z) \le I(X; Y)$. Features can't increase information about the target.
- **Fano's inequality:** $H(\text{error}) \ge H(X \mid \hat{X})$ — lower bound on Bayes error rate
- **Entropy of Gaussian:** $H(N(\mu, \Sigma)) = \frac{d}{2}\log(2\pi e) + \frac{1}{2}\log|\Sigma|$ — log determinant of covariance
