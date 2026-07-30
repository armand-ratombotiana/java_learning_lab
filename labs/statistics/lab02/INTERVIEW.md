# Lab 02 Interview Questions

## Q1: What is the Central Limit Theorem?
The CLT states that the sampling distribution of the sample mean approaches a normal distribution as sample size increases, regardless of the population's distribution, provided the population has finite variance.

## Q2: When do you use Poisson vs Binomial?
Use Binomial when you have a fixed number of trials with success/failure outcomes. Use Poisson when counting rare events over a continuous interval where n is large and p is small.

## Q3: What is the memoryless property of the exponential distribution?
The exponential distribution has no memory: P(X > s+t | X > s) = P(X > t). This means the remaining lifetime is independent of age.

## Q4: How do you sample from a normal distribution in Java?
Using the Box-Muller transform: generate two uniform U1, U2 and compute Z = √(-2 ln U1) * cos(2π U2).

## Q5: What does the PDF integrate to?
The integral of a PDF over its entire support equals 1. The CDF at x is the integral from -∞ to x.
