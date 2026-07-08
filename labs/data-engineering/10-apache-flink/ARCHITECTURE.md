# Apache Flink Architecture

```
[Kafka Source] -> [Flink Job: Operators] -> [Sink: Kafka/JDBC/ES]
                      |
             [State Backend]
             (RocksDB/Heap)
                      |
        [JobManager <-> TaskManagers]
                      |
            [HA: ZooKeeper/K8s]

Typical Pipeline:
Source -> Map -> KeyBy -> Window -> Aggregate -> Sink
```
