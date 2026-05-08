# Edge Cases and Best Practices - Weka

## Pitfalls

1. **Class not set**: Always call data.setClassIndex()
2. **Missing values**: Handle or filter missing values
3. **Numeric class**: For classification, ensure nominal class
4. **Model serialization**: Close files after saving
5. **Memory**: Large datasets may require sampling

## Best Practices

1. Always preprocess data (normalize, discretize)
2. Use cross-validation for reliable evaluation
3. Try multiple algorithms
4. Use feature selection for high-dimensional data
5. Save models for production use

---

*Complete exercises in EXERCISES.md.*