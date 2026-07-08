# Apache Beam Architecture

```
[Pipeline] -> [Runner API] -> [Runner: Flink/Spark/Dataflow]
    |                            |
[SDK Harness]            [Execution Engine]
[PCollections]           [Workers/Containers]
[PTransforms]
    |
[User Code]

Portability: SDK Harness <-> Fn API <-> Runner
Write once in Java/Python/Go, run on any runner
```
