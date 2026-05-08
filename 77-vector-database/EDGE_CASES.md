# Edge Cases and Best Practices - Vector Databases

## Pitfalls

1. **Dimension mismatch**: Ensure embedding models match for indexing and search
2. **Chunk size**: Too large loses precision, too small loses context
3. **Similarity threshold**: Set appropriate minScore to filter irrelevant results
4. **API rate limits**: Handle rate limiting from cloud providers
5. **Embedding drift**: Use same model version consistently

## Best Practices

1. Choose appropriate chunk size (500-1000 chars)
2. Use overlap between chunks (50-100 chars)
3. Set minScore threshold (0.7-0.8) for relevance
4. Implement caching for repeated queries
5. Monitor API costs and optimize embedding frequency

---

## Debugging Tips

- Check embedding dimensions match
- Verify chunk boundaries don't break semantic units
- Use minScore to filter noise
- Log retrieval results for debugging
- Test with known relevant documents

---

*Complete exercises in EXERCISES.md.*