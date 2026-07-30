# Lab 06 Guide: Naive Bayes Classifier

## Step 1 — Compute Priors
P(y = c) = count(c) / total samples

## Step 2 — Compute Likelihoods
For Gaussian NB: for each class c, compute mean and variance of each feature.

## Step 3 — Posterior
P(y=c|x) ∝ P(y=c) · Π P(xᵢ | y=c)

## Step 4 — Prediction
Choose class with highest posterior probability.

## Step 5 — Laplace Smoothing
In multinomial/Bernoulli: P(xᵢ=v | y=c) = (count + 1) / (N_c + |V|)

## Step 6 — Run Tests
Test on the Iris dataset (sepal length/width as features).
