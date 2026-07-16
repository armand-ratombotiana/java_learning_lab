# Statistical Distributions Theory & Intuition

## 💡 What is a Distribution?
In data science, a distribution is a mathematical function that describes the relationship between the possible outcomes of a variable and the frequency with which those outcomes occur. It tells you how the "mass" of the data is spread out.

## 📐 The Normal (Gaussian) Distribution
The most important distribution in all of science. It is the famous "Bell Curve".
- **Intuition**: Most values cluster around a central mean, and extreme values become increasingly rare as you move away from that mean.
- **The 68-95-99.7 Rule**:
  - 68% of data falls within 1 standard deviation ($\sigma$).
  - 95% falls within 2 standard deviations.
  - 99.7% falls within 3 standard deviations.

### The Central Limit Theorem (CLT)
The CLT states that if you take enough random samples from *any* population (regardless of its original distribution), the distribution of the *sample means* will eventually become Normal. This is why the Normal distribution appears everywhere in nature and social science.

## 🎲 Bernoulli and Binomial Distributions
- **Bernoulli**: A single trial with exactly two outcomes (Success/Failure, 1/0). e.g., A single coin flip.
- **Binomial**: The number of successes in $n$ independent Bernoulli trials. e.g., The number of "Heads" in 10 coin flips.

## ⏱️ Poisson Distribution
Models the number of times an event occurs in a fixed interval of time or space.
- **Intuition**: Use this for "rare" events that happen at a constant average rate.
- **Examples**: Number of customers arriving at a shop per hour, number of mutations in a DNA strand, or (as seen in the HashMap lab) the number of collisions in a hash bucket.