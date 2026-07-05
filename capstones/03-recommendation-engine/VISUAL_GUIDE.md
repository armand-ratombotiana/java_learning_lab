# Visual Guide: Recommendation Engine

## Data Flow
```
[User Events] --> [Kafka] --> [Data Ingestion] --> [Feature Store]
                                                         |
                                                         v
[ALS Trainer] <-- [Training Data] <-- [Feature Engineering]
      |
      v
[User Factors] + [Item Factors] --> [Redis Cache]
                                         |
                                         v
[User Request] --> [Rec Service] --> [ANN Search] --> [Hybrid Ranker] --> [Top-K Results]
                                         |
                              [Content-Based Filter]
```

## Offline Training Pipeline
```
+-----------+     +----------+     +-----------+     +-----------+
| Historical| --> | Feature  | --> | ALS      | --> | Evaluation|
| Data      |     | Engineer |     | Trainer  |     | (Precision)|
+-----------+     +----------+     +-----+-----+     +-----------+
                                          |
                                    +-----+-----+
                                    | Model     |
                                    | Artifacts |
                                    +-----------+
```

## Online Serving Pipeline
```
+-----------+     +--------+     +----------+     +----------+
| User ID   | --> | Get   | --> | ANN      | --> | Hybrid   |
| Request   |     | Vector|     | Search   |     | Ranker   |
+-----------+     +--------+     +-----+----+     +-----+----+
                                          |              |
                                    +-----+----+   +----+-----+
                                    | Top-100  |   | Business |
                                    | Candidates|   | Filters  |
                                    +----------+   +----------+
```
