# Data Quality Engineering Architecture

```
[Data Sources] -> [Quality Check Engine]
     |                   |
[Schema Registry] <- [Expectation Suites]
     |                   |
     +-----> [Validation Results] <-----+
                    |
           [Quality Metric Store]
                    |
           [Dashboard] [Alerting]

[Data Contracts]: Producer -> Contract -> Consumer
                       |                       |
                 [Schema + SLAs]         [Quality Validation]
```
