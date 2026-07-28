# Lab 14: AutoML Pipelines — Guide

## Step 1: Understand Hyperparameter Optimization

| Method | Description | Pros | Cons |
|--------|-------------|------|------|
| Grid Search | Exhaustive over defined grid | Simple, deterministic | Curse of dimensionality |
| Random Search | Random samples from distribution | Better coverage, efficient | No guarantee of optimal |
| Bayesian Opt. | Probabilistic model of objective | Sample-efficient | Complex, sequential |

## Step 2: Implement HyperparameterTuner

The `HyperparameterTuner` supports:
- **GridSearch**: Iterates over all combinations
- **RandomSearch**: Random sampling with configurable iterations
- **BayesianOptimization**: Gaussian Process-based acquisition (simplified)

## Step 3: Implement NAS (Neural Architecture Search) concept

The `NeuralArchitectureSearch` generates random architectures and evaluates them.

## Step 4: Compile and Run

```bash
cd lab14/src
javac com/mlops/lab14/*.java
java com.mlops.lab14.AutoMLLab
```

## Key Parameters to Tune

| Parameter | Search Space | Scale |
|-----------|-------------|-------|
| Learning Rate | [1e-5, 1e-1] | Log |
| Batch Size | [16, 32, 64, 128, 256] | Linear |
| Hidden Units | [32, 64, 128, 256, 512] | Linear |
| Dropout | [0.0, 0.5] | Linear |
| L2 Regularization | [1e-6, 1e-2] | Log |
| Optimizer | [SGD, Adam, RMSprop] | Categorical |

## Best Practices
- Use random search over grid search for high-dimensional spaces
- Run early stopping to prune poor trials
- Use learning rate warmup and scheduling
- Leverage parallel trial execution
- Log all trials to MLflow for analysis
