# Lab 09 Interview: Gradient Boosting

## Q1: How is boosting different from bagging?
Bagging trains models independently in parallel; boosting trains sequentially, each model correcting the previous.

## Q2: What is the role of the learning rate in gradient boosting?
Controls how much each tree contributes; smaller η requires more trees but reduces overfitting.

## Q3: What makes XGBoost faster than standard gradient boosting?
Approximate greedy split finding, sparsity-aware learning, cache-aware access, parallelized tree building.

## Q4: What are the key hyperparameters in gradient boosting?
n_estimators, learning_rate, max_depth, subsample, colsample_bytree, min_child_weight.

## Q5: How does AdaBoost update sample weights?
Increases weight of misclassified samples by a factor of exp(α) where α is the learner's weight.
