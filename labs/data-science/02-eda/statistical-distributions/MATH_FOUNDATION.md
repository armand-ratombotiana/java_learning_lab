# Statistical Distributions Mathematical Foundation

## 📐 Mean and Variance
For any distribution, the two most fundamental parameters are the **Mean** ($\mu$) and **Variance** ($\sigma^2$).

- **Mean**: The expected value or average.
- **Variance**: The spread of the data around the mean.
- **Standard Deviation ($\sigma$)**: The square root of the variance.

## 📉 Probability Density Function (PDF)
For a continuous distribution, the PDF $f(x)$ gives the probability that a variable takes a value in a tiny interval around $x$.

### Normal Distribution PDF
$$ f(x | \mu, \sigma) = \frac{1}{\sigma\sqrt{2\pi}} e^{-\frac{1}{2}\left(\frac{x-\mu}{\sigma}\right)^2} $$

## 📊 Cumulative Distribution Function (CDF)
The CDF $F(x)$ is the probability that the variable is **less than or equal to** $x$.
$$ F(x) = P(X \le x) = \int_{-\infty}^{x} f(t) dt $$

## 🎲 Discrete Formulas

### Bernoulli Distribution
- $P(X=1) = p$
- $P(X=0) = 1-p$
- Mean = $p$, Variance = $p(1-p)$

### Binomial Distribution
Probability of $k$ successes in $n$ trials:
$$ P(X=k) = \binom{n}{k} p^k (1-p)^{n-k} $$

### Poisson Distribution
Probability of $k$ events in an interval:
$$ P(X=k) = \frac{e^{-\lambda} \lambda^k}{k!} $$
Where $\lambda$ is the average rate.