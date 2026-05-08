# Edge Cases and Best Practices - OpenNLP

## Common Pitfalls

### Pitfall 1: Resource Leaks
Models must be properly closed to release resources.

### Pitfall 2: Out-of-Vocabulary Tokens
Tokenizer may fail on unknown words - use adaptive features.

### Pitfall 3: Memory Issues with Large Documents
Process documents in chunks rather than loading entirely.

### Pitfall 4: Model Compatibility
Ensure model version matches OpenNLP version.

### Pitfall 5: Null Pointer Exceptions
Always check for null returns from detection methods.

---

## Best Practices

1. Always close model resources
2. Reuse models across requests
3. Process in batches for large volumes
4. Cache frequently used models
5. Use appropriate tokenizer for use case
6. Handle empty input gracefully
7. Validate model files before loading

---

## Debugging Tips

- Enable DEBUG logging for OpenNLP
- Test with known inputs first
- Check model file integrity
- Verify input encoding (UTF-8)
- Validate token alignment with spans

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Null results | Check input is not empty |
| Low accuracy | Verify model compatibility |
| Memory issues | Process in chunks |
| Model load failure | Check file path and permissions |

---

*Complete exercises in EXERCISES.md for hands-on practice.*