# Lab 02 Interview: Logistic Regression

## Q1: Why can't we use linear regression for classification?
Linear regression outputs unbounded values; logistic regression squashes to [0,1] via sigmoid.

## Q2: Explain the decision boundary for logistic regression.
βᵀx = 0 is the hyperplane where σ = 0.5; points on either side map to different classes.

## Q3: What is the advantage of cross-entropy over MSE for logistic regression?
Cross-entropy is convex in β; MSE with sigmoid is non-convex, making optimization harder.

## Q4: What is regularization in logistic regression?
L1 (Lasso) or L2 (Ridge) penalties added to the loss to prevent overfitting.
