# Lab 04 Guide: Support Vector Machines

## Step 1 — Hard Margin SVM
Maximize margin = 2/‖w‖ subject to yⁱ(w·xⁱ + b) ≥ 1.

## Step 2 — Soft Margin SVM
Introduce slack ξᵢ ≥ 0; minimize ½‖w‖² + C Σ ξᵢ.

## Step 3 — Dual Formulation
max Σ αᵢ − ½ Σ Σ αᵢαⱼyᵢyⱼ(xᵢ·xⱼ)
subject to 0 ≤ αᵢ ≤ C, Σ αᵢyᵢ = 0

## Step 4 — Kernel Trick
Replace xᵢ·xⱼ with K(xᵢ,xⱼ):
- Linear: xᵢ·xⱼ
- Polynomial: (γ xᵢ·xⱼ + r)ᵈ
- RBF: exp(−γ‖xᵢ−xⱼ‖²)

## Step 5 — SMO
Select two αᵢ to optimize at each step; update analytically.

## Step 6 — Run Tests
Run linear SVM on linearly separable 2D data.
