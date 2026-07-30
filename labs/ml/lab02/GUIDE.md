# Lab 02 Guide: Logistic Regression

## Step 1 — Hypothesis
h(x) = σ(βᵀx) = 1 / (1 + e^(−βᵀx))

## Step 2 — Cost Function
J(β) = −(1/m) Σ [yⁱ log(h(xⁱ)) + (1−yⁱ) log(1−h(xⁱ))]

## Step 3 — Gradient Descent
βⱼ := βⱼ − α * (1/m) Σ (h(xⁱ) − yⁱ) * xⱼⁱ

## Step 4 — Decision Boundary
Predict class 1 if σ(βᵀx) ≥ 0.5, else class 0.

## Step 5 — Evaluation
Accuracy, Precision, Recall, F1-Score, Confusion Matrix.

## Step 6 — Run Tests
Compile and run Main.java with synthetic binary data.
