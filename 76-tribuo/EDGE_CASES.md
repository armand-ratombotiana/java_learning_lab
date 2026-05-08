# Edge Cases and Best Practices - Tribuo

## Pitfalls

1. **Type mismatches**: Ensure Label, ClusterID types match
2. **Data splits**: Use proper TrainTestSplit
3. **Feature types**: Match feature type expectations
4. **Model loading**: Use correct ModelIO version

## Best Practices

1. Use TransformTrainer for preprocessing
2. Evaluate on held-out test set
3. Use appropriate trainers for data size
4. Log metrics during training

---

*Complete exercises in EXERCISES.md.*