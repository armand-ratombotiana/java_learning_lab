# Lab 09 Guide: Gradient Boosting

## Step 1 — Initialize
Start with F₀(x) = mean of targets (regression) or log-odds (classification).

## Step 2 — Compute Residuals
rⁱ = yⁱ − F(xⁱ) (regression) or pseudo-residuals (classification).

## Step 3 — Fit Weak Learner
Train a shallow decision tree (max_depth=1–3) to predict residuals.

## Step 4 — Update Model
Fₘ(x) = Fₘ₋₁(x) + η · hₘ(x) where η is the learning rate.

## Step 5 — Repeat
Steps 2–4 for M iterations.

## Step 6 — AdaBoost Twist
Increase weights of misclassified samples; weighted training of weak learners.

## Step 7 — Run Tests
Test gradient boosting on a synthetic binary classification problem.
