# Common Mistakes with Delta Lake

1. No VACUUM Retention: Setting too short retention (<7 days) breaks time travel queries; default 7 days is safe
2. Too Many Small Files: Without OPTIMIZE, thousands of small files cause metadata overhead; run OPTIMIZE regularly
3. Wrong Z-order Columns: Z-ordering on low-cardinality columns (booleans, status codes) provides no benefit
4. Concurrent Merge Conflicts: High contention on same partition causes frequent retries; partition by high-cardinality keys
5. Expecting Auto Schema Evolution: mergeSchema=true must be explicitly set; default behavior rejects schema changes
