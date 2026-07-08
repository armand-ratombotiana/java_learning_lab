# Mathematical Foundations of Multi-Task Learning

## 1. Introduction
This document provides a comprehensive treatment of the mathematical concepts underlying Multi-Task Learning. Understanding these foundations is essential for developing intuition, implementing algorithms correctly, and advancing to more complex topics.

## 2. Probability Theory

### 2.1 Fundamentals
Probability theory quantifies uncertainty. A probability space consists of a sample space Î© (all possible outcomes), an event space F (set of events), and a probability measure P that assigns probabilities to events satisfying P(Î©)=1 and countable additivity.

### 2.2 Random Variables
A random variable X is a function from the sample space to real numbers. For discrete variables, the probability mass function p(x)=P(X=x). For continuous variables, the probability density function f(x) such that P(aâ‰¤Xâ‰¤b)=âˆ«â‚áµ‡ f(x)dx.

### 2.3 Important Distributions
- **Normal (Gaussian)**: N(Î¼,ÏƒÂ²) with pdf f(x)=1/âˆš(2Ï€ÏƒÂ²)Â·exp(-(x-Î¼)Â²/(2ÏƒÂ²)). Central by the Central Limit Theorem.
- **Bernoulli**: Bern(p) for binary outcomes, P(X=1)=p, P(X=0)=1-p.
- **Categorical**: Generalization to K categories with probabilities pâ‚,...,p_K summing to 1.
- **Uniform**: U(a,b) where all values in [a,b] are equally likely.

### 2.4 Expectation and Moments
E[X]=âˆ«xÂ·f(x)dx or Î£xÂ·p(x). Variance ÏƒÂ²=E[(X-Î¼)Â²]=E[XÂ²]-(E[X])Â². Covariance Cov(X,Y)=E[(X-Î¼_X)(Y-Î¼_Y)] measures linear dependence.

### 2.5 Bayes' Theorem
P(A|B)=P(B|A)P(A)/P(B). This is the foundation of Bayesian inference where we update prior beliefs P(Î¸) with data likelihood P(D|Î¸) to obtain posterior P(Î¸|D).

## 3. Linear Algebra

### 3.1 Vectors
A vector vâˆˆâ„â¿ is an ordered n-tuple. Operations include addition (u+v)áµ¢=uáµ¢+váµ¢, scalar multiplication (Î±v)áµ¢=Î±váµ¢, and dot product uÂ·v=Î£uáµ¢váµ¢=||u||Â·||v||Â·cosÎ¸.

### 3.2 Matrices
A matrix Aâˆˆâ„^(mÃ—n) maps vectors from â„â¿ to â„áµ. Matrix multiplication (AB)áµ¢â±¼=Î£â‚–Aáµ¢â‚–Bâ‚–â±¼ is associative but not commutative. The transpose Aáµ€ has entries (Aáµ€)áµ¢â±¼=Aâ±¼áµ¢.

### 3.3 Eigenvalues and Eigenvectors
For square A, if Av=Î»v with vâ‰ 0, then v is an eigenvector with eigenvalue Î». The characteristic equation det(A-Î»I)=0 yields eigenvalues. Eigendecomposition A=QÎ›Qâ»Â¹ diagonalizes A when eigenvectors are linearly independent.

### 3.4 Matrix Decompositions
- **SVD**: A=UÎ£Váµ€ for any matrix, with orthogonal U,V and diagonal Î£ of singular values
- **QR**: A=QR with orthogonal Q and upper triangular R
- **Cholesky**: A=LLáµ€ for symmetric positive definite A

## 4. Calculus

### 4.1 Derivatives
The derivative f'(x)=lim_(hâ†’0)(f(x+h)-f(x))/h measures instantaneous rate of change. Key rules include the power rule, product rule, quotient rule, and chain rule.

### 4.2 Multivariable Calculus
For f:â„â¿â†’â„, the gradient âˆ‡f=(âˆ‚f/âˆ‚xâ‚,...,âˆ‚f/âˆ‚xâ‚™)áµ€ points in the direction of steepest increase. The Hessian Háµ¢â±¼=âˆ‚Â²f/âˆ‚xáµ¢âˆ‚xâ±¼ contains second derivatives.

### 4.3 Gradient Descent
Î¸(t+1)=Î¸(t)-Î·Â·âˆ‡L(Î¸(t)) iteratively minimizes loss L. The learning rate Î· controls step size. Convergence is guaranteed for convex functions with appropriate Î·.

## 5. Information Theory

### 5.1 Entropy
H(X)=-Î£p(x)logâ‚‚p(x) measures average information content. Higher entropy means more uncertainty.

### 5.2 Cross-Entropy and KL Divergence
H(p,q)=-Î£p(x)log q(x) measures coding efficiency. KL divergence D_KL(p||q)=Î£p(x)log(p(x)/q(x))â‰¥0 measures distribution difference, zero only when p=q.

### 5.3 Mutual Information
I(X;Y)=D_KL(p(x,y)||p(x)p(y))=H(X)-H(X|Y) measures dependence between variables.

## 6. Optimization Theory

### 6.1 Convex Optimization
A function f is convex if f(Î»x+(1-Î»)y)â‰¤Î»f(x)+(1-Î»)f(y) for Î»âˆˆ[0,1]. Convex functions have no local minima that are not global, making optimization tractable.

### 6.2 Stochastic Optimization
SGD uses mini-batches: Î¸(t+1)=Î¸(t)-Î·Â·(1/B)Â·Î£_{iâˆˆbatch}âˆ‡L_i(Î¸(t)). This adds noise that can help escape sharp minima.

### 6.3 Regularization Theory
Adding penalty Î©(Î¸) to loss: L_reg=L_data+Î»Î©(Î¸). L1 regularization Î©(Î¸)=||Î¸||â‚ promotes sparsity. L2 regularization Î©(Î¸)=||Î¸||â‚‚Â² promotes small weights.

## 7. Statistical Learning Theory

### 7.1 Empirical Risk Minimization
True risk R(f)=E[L(f(X),Y)] is approximated by empirical risk RÌ‚(f)=(1/n)Î£L(f(xáµ¢),yáµ¢). The gap between them is bounded by VC dimension or Rademacher complexity.

### 7.2 Bias-Variance Decomposition
E[(Å·-y)Â²]=Bias[Å·]Â²+Var[Å·]+ÏƒÂ² where ÏƒÂ² is irreducible error. This decomposition guides model selection.

## 8. Conclusion
The mathematical foundations presented here are essential for understanding and implementing Multi-Task Learning. Mastery enables better algorithm design, debugging, and innovation.
