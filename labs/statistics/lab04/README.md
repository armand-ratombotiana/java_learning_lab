# Lab 04: ANOVA

## Overview
Analysis of Variance (ANOVA) partitions total variance into components attributable to different sources, testing whether group means differ significantly.

## Learning Objectives
- Understand one-way ANOVA
- Understand two-way ANOVA
- Compute and interpret the F-statistic
- Perform post-hoc tests (Tukey's HSD)
- Test ANOVA assumptions (normality, homogeneity of variance)

## Key Formulas

| Source | SS | df | MS | F |
|--------|-----|-----|-----|-----|
| Between | Σ nᵢ(x̄ᵢ - x̄)² | k-1 | SSB/dfB | MSB/MSW |
| Within | Σ Σ (xᵢⱼ - x̄ᵢ)² | N-k | SSW/dfW | |
| Total | Σ Σ (xᵢⱼ - x̄)² | N-1 | | |

## Running the Code

```bash
javac -d out src/Anova.java
java -cp out com.statistics.lab04.Anova
```
