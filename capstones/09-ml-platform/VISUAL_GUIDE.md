# Visual Guide: ML Platform

## Pipeline Architecture
```
[Data Sources] --> [DataIngestor] --> [Data Lake (S3)]
                                           |
                                   [FeaturePipeline]
                                           |
                          +----------------+----------------+
                          |                                 |
                   [Online Feature Store]           [Offline Feature Store]
                          (Redis)                       (Parquet on S3)
                          |                                 |
                    +-----+------+                   +------+------+
                    |            |                   |             |
            [Training Pipeline]            [Batch Feature Dataset]
                    |                                 |
              [Model Training]                [Evaluation]
                    |                                 |
              [Model Registry] <---------------------+
                    |
            [Promotion: Staging -> Production]
                    |
            [ServingService (Online)] / [BatchPredictor]
                    |
            [Prediction API] / [Scheduled Predictions]
                    |
            [MonitorService] -> [Alerts]
```

## Serving Architecture
```
[Client] --> [Load Balancer] --> [Serving Instance 1 (Model v3)]
                                  [Serving Instance 2 (Model v3)]
                                  [Serving Instance 3 (Model v4 AB Test)]
                                         |
                                     [Feature Store (Redis)]
```

## Model Registry Lifecycle
```
Created -> Staging -> Production -> Archived
                |            |
            Evaluation    A/B Test
               pass         wins
```
