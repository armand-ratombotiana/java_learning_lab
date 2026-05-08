# Apache Flink - Stream Processing

## Overview
Apache Flink is a distributed streaming processing framework for stateful computations with exactly-once guarantees.

## Key Features
- Event-time processing
- Window functions (tumbling, sliding, session)
- Exactly-once processing
- Checkpointing and state
- SQL support

## Project Structure
```
63-streaming/
  apache-flink/
    src/main/java/com/learning/streaming/flink/FlinkLab.java
```

## Running
```bash
cd 63-streaming/apache-flink
mvn compile exec:java
```

## Concepts Covered
- DataStream API
- Window types
- Time concepts (event, processing)
- Checkpointing

## Dependencies
- Flink Stream API