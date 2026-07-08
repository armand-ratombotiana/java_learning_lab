# Math Foundation for Streaming Analytics

## Latency
```
EndToEnd = Ingestion + Processing + Storage + Query + Render
ProcessingLatency = watermark - event_time
RefreshLag = dashboard_freshness - realtime
```

## Throughput
```
EventsPerSecond = input_partitions * partition_throughput
AggregationThroughput = EventsPerSecond / aggregation_cardinality
DashboardQueries = users * query_rate
```

## Window Semantics
```
Tumbling: num_windows = time_range / window_size
Sliding: num_windows = time_range / slide + window_size / slide - 1
Session: session_window_end = last_event_time + gap_duration
```

## Materialized View Sizing
```
ViewRows = distinct_keys * distinct_windows
ViewSize = ViewRows * avg_row_size
RefreshThroughput = aggregate_changes_per_second
```
