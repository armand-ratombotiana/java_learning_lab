# Common Mistakes

1. **Schema Evolution**: Hard-coding column names instead of schema-aware access
2. **Late Data**: Missing watermarks in streaming pipelines
3. **No Idempotency**: Duplicate data on retry (use MERGE)
4. **No Backpressure**: Unbounded memory with infinite queues
5. **Hardcoded Credentials**: Secrets in code instead of environment variables
