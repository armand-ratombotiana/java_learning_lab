# Lab 01: Mock Interview — Senior Data Engineer

**Interviewer**: "Design a data quality framework for a real-time event streaming platform processing 1M events/second."

**Candidate**: "I'd start by categorizing quality into four dimensions: completeness, timeliness, consistency, and accuracy. For 1M events/sec, each check must be O(1) per event—no full-batch scans in the hot path."

**Interviewer**: "Walk me through the architecture."

**Candidate**: "Layer 1 is an in-stream lightweight validator using a configurable rules chain—null checks, schema conformance, range checks. These run in the Kafka Streams topology as a transform step. Layer 2 is a windowed profiler that computes 5-minute tumbling window aggregates—null rates, cardinality estimates via HyperLogLog, latency percentiles. Layer 3 is an offline batch validator that runs hourly on the data lake S3/Parquet using something like Apache Spark or Deequ for deeper checks—referential integrity, statistical distribution comparisons."

**Interviewer**: "How do you handle schema evolution?"

**Candidate**: "We'd use Avro with a Schema Registry enforcing backward/forward compatibility. The quality engine has access to the schema and can detect field additions, removals, and type changes. On schema change, we pause the strict rules, compute the diff, and apply a new ruleset from a versioned rules store."

**Interviewer**: "How do you surface quality issues?"

**Candidate**: "Each quality metric emits to a `quality-events` Kafka topic. A streaming consumer computes a composite quality score per data product and materializes it into a time-series DB (e.g., VictoriaMetrics). Alerts fire when the score drops below threshold—using a sliding window to avoid pager fatigue. We also publish quality SLOs in a data catalog so consumers can see freshness and completeness before querying."

**Interviewer**: "How would you test the quality framework itself?"

**Candidate**: "We'd produce synthetic 'known-bad' datasets—nulls, duplicates, out-of-range values, late arrivals—and assert that the engine flags every known violation. We'd also do chaos testing: inject random corruptions into a production shadow pipeline and verify detection without affecting real consumers."
