# Lab 03: Decision Trees & Random Forests

## Topics Covered
- ID3 Algorithm
- CART (Classification and Regression Trees)
- Gini Impurity
- Entropy / Information Gain
- Bagging (Bootstrap Aggregating)
- Feature Importance

## Objective
Implement a decision tree (ID3) from scratch and understand bagging for random forests.

## Key Concepts
| Concept | Description |
|---|---|
| Entropy | Σ −pᵢ log₂(pᵢ) — measure of impurity |
| Information Gain | Entropy(parent) − Σ weighted entropy(child) |
| Gini Impurity | 1 − Σ pᵢ² |
| Bagging | Train trees on bootstrap samples, average predictions |
| Feature Importance | Mean decrease in impurity across splits |

## Files
- `GUIDE.md` — Step-by-step lab walkthrough
- `INTERVIEW.md` — Interview Q&A on Decision Trees & Random Forests
- `src/com/ml/lab03/Main.java` — Compilable Java source with test cases
