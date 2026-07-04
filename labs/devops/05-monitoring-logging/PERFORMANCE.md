# Monitoring & Logging Performance

## Prometheus Performance
- **Cardinality limits**: Time series with >10M series per instance may degrade performance.
- **High cardinality labels**: Avoid labels with unbounded values (user IDs, emails, IPs).
- **Retention tuning**: Balance storage cost vs. query history.
- **Downsampling**: Use recording rules for historical aggregations.
- **Remote write**: For long-term storage, write to Thanos or Cortex.

## Elasticsearch Performance
- **Shard sizing**: Aim for 20-50GB per shard; too many small shards wastes resources.
- **Index lifecycle**: Hot → Warm → Cold → Delete phases.
- **Bulk indexing**: Batch log writes (1000-5000 events per request).
- **Field mapping**: Disable `index` for fields not used in search.
- **Refresh interval**: Increase from 1s to 30s for log ingestion.

## Application Instrumentation
- Use client-side rate limiting for metrics exports.
- Use async logging libraries.
- Sample traces (head-based or tail-based) for high-throughput services.
- Use log levels correctly: DEBUG for dev, INFO for operations, ERROR for incidents.

## Monitoring System Itself
- Monitor Prometheus: storage, scrape failures, rule evaluation time.
- Monitor Elasticsearch: cluster health, JVM heap, query latency.
- Monitor Grafana: data source errors, dashboard load time.
