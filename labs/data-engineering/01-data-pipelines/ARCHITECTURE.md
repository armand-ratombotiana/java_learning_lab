# Architecture

## High-Level
```
+----------------------------------------------------+
|                Data Pipeline Platform               |
+------------+-----------+-----------+---------------+
| Ingestion  | Processing | Storage   | Serving       |
| Kafka      | Spark      | Data Lake | APIs          |
| JDBC       | Flink      | Warehouse | BI Tools      |
+------------+-----------+-----------+---------------+
```
