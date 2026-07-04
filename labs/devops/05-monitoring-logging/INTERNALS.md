# Monitoring & Logging Internals

## Prometheus TSDB
- **Block-based storage**: 2-hour blocks of time-series data.
- **Compression**: Gorilla compression (reduces storage 10-40x).
- **Memory-mapped**: Recent data in memory, older data on disk.
- **Retention**: Configurable retention period; blocks compacted and deleted.

## PromQL Engine
- **Instant vector**: Single point per time series at query time.
- **Range vector**: Multiple points over a time window.
- **Operators**: Arithmetic, comparison, logical, aggregation.
- **Functions**: rate(), increase(), histogram_quantile(), topk(), etc.

## Elasticsearch Internals
- **Inverted index**: Term-to-document mapping for fast full-text search.
- **Shards**: Indexes split into primary/replica shards across nodes.
- **Cluster coordination**: Zen Discovery (or newer) for node discovery and election.
- **Translog**: Write-ahead log for durability.

## Grafana Architecture
- **Frontend**: React-based UI with panel plugins.
- **Backend**: Go server for authentication, data source proxy, alerting.
- **Data sources**: Plugin architecture supporting 50+ data sources.
- **Dashboard JSON**: Dashboards are JSON documents (exportable, versionable).
