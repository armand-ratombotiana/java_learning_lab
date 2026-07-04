# Common Mistakes

1. **Collecting Large Datasets**: Use foreachPartition instead of collectAsList
2. **Not Broadcasting**: Small tables should use broadcast hint
3. **Too Many Tasks**: Match shuffle partitions to cluster size
4. **Not Caching**: Reused DataFrames should be cached
5. **Serialization Issues**: Lambdas capturing non-serializable objects
