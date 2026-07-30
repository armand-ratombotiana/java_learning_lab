# Lab 08: Experimental Design

## Overview
Experimental design plans experiments to ensure valid, objective conclusions. Key concepts include randomization, blocking, factorial designs, and sample size calculation.

## Learning Objectives
- Understand randomization and blocking
- Design 2^k factorial experiments
- Calculate required sample size
- Compute main effects and interactions

## Key Formulas

| Concept | Formula |
|---------|---------|
| Sample Size (mean) | n = (Z_α/2 + Z_β)² · 2σ² / Δ² |
| Sample Size (proportion) | n = (Z_α/2 + Z_β)² · (p₁(1-p₁) + p₂(1-p₂)) / (p₁-p₂)² |
| Factorial Effect | Effect = ȳ₊ - ȳ₋ |
| Interaction | Interaction = (ȳ₁₁ - ȳ₁₋) - (ȳ₂₁ - ȳ₂₋) |

## Running the Code

```bash
javac -d out src/ExperimentalDesign.java
java -cp out com.statistics.lab08.ExperimentalDesign
```
