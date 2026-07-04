# Architecture

```
[Kafka Streams App]
  [Topology: Source -> Process -> Sink]
  [State Stores: RocksDB]
    |              |
[Kafka Topics]  [Changelog Topics]
```
