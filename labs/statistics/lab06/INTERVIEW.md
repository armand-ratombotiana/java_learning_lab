# Lab 06 Interview Questions

## Q1: What is the difference between frequentist and Bayesian statistics?
Frequentists treat parameters as fixed unknowns; Bayesians treat parameters as random variables with probability distributions representing uncertainty.

## Q2: What is a conjugate prior?
A prior that, when combined with a given likelihood, yields a posterior of the same family. Example: Beta is conjugate to Binomial.

## Q3: How do you interpret a 95% credible interval?
There is a 95% probability the parameter lies in this interval given the data. This contrasts with frequentist confidence intervals.

## Q4: What is the role of the prior in Bayesian analysis?
The prior encodes beliefs before seeing data. As sample size grows, the posterior is dominated by the likelihood, diminishing the prior's influence.

## Q5: How would you compare two conversion rates using Bayesian methods?
Model each rate with a Beta posterior, then use Monte Carlo to estimate P(rate_A > rate_B). This directly answers the business question.
