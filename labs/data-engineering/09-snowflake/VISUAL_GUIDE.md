# Visual Guide to Snowflake Data Cloud

```
+------------------------------------------------+
|                 Services Layer                   |
|  [Auth] [Metadata] [Optimizer] [Security]        |
+------------------------+------------------------+
                         |
+------------------------v------------------------+
|               Compute Layer                      |
|  [WH_1: X-Large]    [WH_2: Medium]             |
|  [Reporting]         [ETL Jobs]                 |
|  [Multi-cluster: 3]  [Single-cluster]          |
+------------------------+------------------------+
                         |
+------------------------v------------------------+
|               Storage Layer                      |
|  [Micro-partitions] [Columnar Format]           |
|  [S3 / Azure Blob / GCS] [Encryption at rest]   |
+------------------------------------------------+

Time Travel Timeline:
Now-90d         Now-1d          NOW
[Fail-safe:7d]  [Time Travel]   [Active]
  |                  |              |
Only Snowflake    Query any       Current
can restore       point in past   data
