# Visual Guide: Fraud Detection

```
[Transaction Source] --> [Kafka: raw.transactions]
                                |
                          [Transaction Ingestor]
                                |
                     +----------+----------+
                     |                     |
              [Feature Store]        [Feature Extractor]
                     |                     |
                     +----------+----------+
                                |
                       [Rule Engine (Stage 1)]
                                |
                    +-----------+-----------+
                    |                       |
               [APPROVE]               [SCORE > 90]
                    |                       |
              [ML Scorer (Stage 2)]    [REJECT]
                    |
              [Decision Maker]
                    |
          +---------+---------+
          |         |         |
     [APPROVE] [REVIEW] [REJECT]
          |         |         |
          +----+----+---------+
               |
         [Kafka: fraud.decisions]
               |
          [Feedback Loop]
```

## Architecture Diagram
```
+-------------------+     +------------------+
| Transaction Source| --> | Kafka            |
+-------------------+     +--------+---------+
                                   |
                            +------+------+
                            | Feature     |
                            | Extraction  |
                            +------+------+
                                   |
                     +-------------+-------------+
                     |             |             |
               +-----+---+  +-----+---+  +------+------+
               | Rule    |  | ML       |  | Feature    |
               | Engine  |  | Scorer   |  | Store      |
               +---------+  +---------+  +------+------+
                     |             |             |
                     +------+------+-------------+
                            |
                      +-----+------+
                      | Decision   |
                      | Maker      |
                      +-----+------+
                            |
                     +------+------+
                     | Fraud       |
                     | Decisions   |
                     +------+------+
```
