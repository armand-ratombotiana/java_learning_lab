# Common Mistakes with Streaming Analytics

1. No Watermark Strategy: Default processing time gives wrong results for out-of-order events
2. Overloading Dashboards: Raw event streams go directly to dashboard; always pre-aggregate
3. No Punctuation: Streaming queries without window limits grow unboundedly
4. Wrong Parallelism: Too few parallelism for high-throughput streams causes backpressure
5. Ignoring Late Data: Not configuring allowed lateness causes data loss without warning
