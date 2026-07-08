# Apache Flink Theory

## Processing Models
Batch: bounded data, periodic jobs, minutes-to-hours latency. Stream: unbounded data, continuous processing, millisecond latency. Flink unifies both — batch is a special case of streaming.

## Event Time vs Processing Time
Processing Time: wall clock when event arrives at operator. Event Time: timestamp embedded in the event. Ingestion Time: when event enters Flink. Event time enables accurate results despite out-of-order or late-arriving data.

## Window Types
Tumbling: fixed-size, non-overlapping (e.g., 1-hour windows). Sliding: fixed-size, overlapping (e.g., 5-min window every 2 min). Session: windows based on activity gaps (e.g., 30-min inactivity). Global: single window for all records.

## Watermarks
Signal how far event time has progressed. Watermark(t) = max(event_time_seen) - max_out_of_orderness. Late elements: those arriving after watermark. Allowed lateness: configurable grace period triggering another window evaluation.
