# Lab 09: Gradient Boosting

## Topics Covered
- AdaBoost
- XGBoost Concepts
- Residual Fitting
- Learning Rate

## Objective
Implement a gradient boosting classifier using decision stumps as weak learners.

## Key Concepts
| Concept | Description |
|---|---|
| Boosting | Sequentially train weak learners, each correcting previous errors |
| AdaBoost | Weight samples, train weak learner, update weights |
| Residual Fitting | Each tree fits the residuals of the previous ensemble |
| Learning Rate | Shrinkage factor applied to each tree's contribution |
| XGBoost | Regularized gradient boosting with approximate split finding |

## Files
- `GUIDE.md` — Step-by-step lab walkthrough
- `INTERVIEW.md` — Interview Q&A on Gradient Boosting
- `src/com/ml/lab09/Main.java` — Compilable Java source with test cases
