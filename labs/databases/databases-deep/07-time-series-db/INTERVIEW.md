# Time-Series Database — Interview Questions

## Beginner
1. What makes time-series data different from OLTP data?
2. What is downsampling and why is it used?
3. How does time-based partitioning help query performance?

## Intermediate
4. Explain delta-of-delta timestamp compression (Gorilla paper).
5. What is a retention policy and how is it implemented efficiently?
6. How does TimescaleDB's hypertable differ from a regular partitioned table?

## Advanced
7. Design a compression scheme for float64 values that achieves < 1.5 bytes per point.
8. How would you handle out-of-order writes in a time-series DB?
9. Compare InfluxDB's TSM engine with Prometheus's TSDB storage format.

## System Design
10. Design a time-series platform that ingests 100M data points/second from IoT sensors with 5-year retention.