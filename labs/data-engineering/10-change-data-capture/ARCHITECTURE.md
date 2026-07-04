# Architecture

## Production CDC
```
[MySQL] -> [Debezium] -> [Kafka Connect] -> [Kafka Cluster]
[Postgres] -> [Debezium] -> [Kafka Connect] -> [Kafka Cluster]
                                                 |
                            [Flink] [S3 Sink] [Elasticsearch]
```
