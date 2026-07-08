# Streaming Analytics Internals

## Flink SQL Execution
SQL → Logical Plan → Optimized Plan → StreamGraph. The planner converts SQL operators to streaming operators. Window functions become window operators. Joins become CoProcessFunction or IntervalJoin. Aggregations become stateful operators with state backends.

## Materialized View Pipeline
1. Kafka stream events. 2. Flink SQL aggregates into window slots. 3. UPSERT output to JDBC sink (PostgreSQL upsert). 4. Database maintains aggregated table. 5. Dashboard queries the aggregated table. 6. CRON/trigger for periodic full refresh to reconcile accuracy.

## Kinesis Analytics Internals
SQL queries run on Flink under the hood. Kinesis Data Stream as source. In-application streams (similar to SQL tables). Pump operations create streaming pipelines. Group by + window creates sliding/tumbling aggregations. Sink to Firehose, Lambda, Kinesis Data Stream, or external.

## Lambda vs Kappa Trade-offs
Lambda: two independent pipelines (batch + stream). Reconciliation layer merges results. Complex but accurate and comprehensive. Kappa: single streaming pipeline. Simpler but limited to windowed processing. For most analytics use cases, Kappa with sufficient streaming resources covers requirements.
