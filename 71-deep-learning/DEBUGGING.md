# Debugging Deep Learning Models

## Common Failure Scenarios

### Training Instability

Neural network training often exhibits instability that manifests as loss not decreasing, loss exploding to infinity (NaN values), or erratic loss curves that oscillate wildly. These problems typically stem from incorrect learning rates, improper data preprocessing, or gradient issues.

A learning rate too high causes the optimizer to overshoot minima, causing loss to increase or oscillate. Learning rates too low result in painfully slow training where loss decreases glacially. The learning rate appropriate for one model architecture may be completely wrong for another, especially with different batch sizes due to the effective learning rate scaling.

NaN values during training usually indicate numerical instability. Division by zero, logarithm of zero or negative numbers, or overflow from operations on large numbers produce NaN. These often trace to unnormalized input data, incorrect loss functions, or gradient explosion that overflows floating-point representations.

### Overfitting and Underfitting

Overfitting occurs when the model learns training data too well, including noise, and performs poorly on validation data. The training loss continues decreasing while validation loss plateaus or increases. This typically happens with insufficient training data relative to model complexity, or training for too many epochs without regularization.

Underfitting shows as both training and validation loss remaining high. The model fails to learn the underlying patterns in the data. This results from a model too simple for the task, insufficient training time, or poor feature engineering.

### Stack Trace Examples

**NaN in gradient computation:**
```
RuntimeWarning: overflow encountered in exp
RuntimeWarning: invalid value encountered in divide
Loss: nan
    at torch.nn.functional.cross_entropy (functional.py:2947)
    at model.forward(model.py:45)
```

**GPU out of memory:**
```
RuntimeError: CUDA out of memory. Tried to allocate 2.00 GiB
    at torch.nn.functional.conv2d (functional.py:2426)
    at model.forward(model.py:89)
```

**Data dimension mismatch:**
```
RuntimeError: size mismatch, m1: [32, 512], m2: [256, 10]
    at torch.nn.functional.linear (functional.py:1060)
    at model.forward(model.py:67)
```

## Debugging Techniques

### Diagnosing Training Problems

When loss fails to decrease, first verify data flow. Add assertions or logging to check input tensor shapes, value ranges, and labels at each batch. Ensure data preprocessing matches what the model expects, especially normalization parameters.

Implement gradient checking by monitoring gradient magnitudes. Print the mean and max gradient values after each backward pass. Extremely large gradients indicate exploding gradients, while near-zero gradients suggest vanishing gradients or a saturated model.

Track both training and validation metrics per epoch. Plot these over time to visualize the learning curve. If training loss decreases but validation loss plateaus, you have overfitting. If both remain high, the model is underfitting or there's a data problem.

### Memory Optimization

GPU out-of-memory errors require either reducing batch size, reducing model size, or freeing unused tensors. Use `torch.cuda.empty_cache()` between training steps to release cached memory from deleted tensors.

Profile memory usage with `torch.cuda.memory_allocated()` and `torch.cuda.max_memory_allocated()`. Identify layers or operations consuming excessive memory. Sometimes gradients stored for optimizer updates consume more memory than forward activations.

Gradient accumulation provides an effective way to handle large effective batch sizes within limited memory. Process smaller batches, accumulate gradients, and update weights only after reaching the target effective batch size.

## Best Practices

Always start with a simple baseline before adding complexity. Train a minimal model on a subset of data to verify the pipeline works. Only after achieving reasonable results should you scale up data, increase model capacity, or add regularization.

Normalize inputs consistently using precomputed statistics from training data. Apply the same normalization to validation and test data. Incorrect normalization is one of the most common causes of training failures.

Use learning rate schedulers that start with a warmup period. Linear warmup gradually increases learning rate from zero to the target, which helps avoid initial instability. After warmup, decay the learning rate to help the optimizer converge to a better minimum.

Implement early stopping to automatically stop training when validation loss stops improving. This prevents overfitting and saves computation. Track validation loss and implement patience—the number of epochs to wait before stopping if no improvement occurs.

Save model checkpoints regularly and track the best model based on validation loss. This provides recovery from interrupted training and ensures you have access to the best-performing version rather than just the final one.