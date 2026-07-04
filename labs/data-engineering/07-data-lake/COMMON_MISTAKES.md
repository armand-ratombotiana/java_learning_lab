# Common Mistakes

1. **Data Swamp**: No organization, no catalog
2. **No Partitioning**: Full table scans on all queries
3. **Too Many Small Files**: <64MB files cause metadata overhead
4. **No Lifecycle**: Keep everything forever (costs grow unbounded)
5. **Wrong Format**: Not using columnar formats (Parquet/Delta)
