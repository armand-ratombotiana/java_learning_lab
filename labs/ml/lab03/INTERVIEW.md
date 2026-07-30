# Lab 03 Interview: Decision Trees & Random Forests

## Q1: Difference between ID3 and CART?
ID3 uses entropy/information gain; CART uses Gini impurity and produces binary splits.

## Q2: How does a random forest reduce overfitting?
Bagging reduces variance by averaging many high-variance, low-bias trees.

## Q3: What is feature importance in random forest?
Mean decrease in impurity (or increase in error when permuting feature values).

## Q4: When would you use entropy vs Gini?
Both are similar; Gini is faster to compute (no log). Entropy is slightly more sensitive to distribution changes.

## Q5: How deep should a decision tree be?
Use cross-validation to find optimal depth; prune if overfitting.
