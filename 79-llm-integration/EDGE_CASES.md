# Edge Cases and Best Practices - LLM Integration

## Pitfalls

1. **Hardcoded API keys**: Never hardcode, use environment variables
2. **No error handling**: Always handle API failures
3. **Missing timeouts**: Configure appropriate timeouts
4. **Not handling rate limits**: Implement backoff

## Best Practices

1. Use environment variables for API keys
2. Implement retry with exponential backoff
3. Set appropriate timeouts (30-60 seconds)
4. Handle streaming properly
5. Log all API calls for debugging
6. Use connection pooling

---

*Complete exercises in EXERCISES.md.*