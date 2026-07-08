# Snowflake Data Cloud Architecture

## Production Architecture
```
[BI Tools] -> [Reporting_WH]    [ETL_WH]    [DS_WH]
                |                    |           |
           [Cloud Services: Auth, Metadata, Optimizer]
                         |
                    [Storage Layer]
              [Raw -> Conformed -> Aggregated]
              [Micro-partitions, Columnar, Encrypted]
```

## Pipeline Pattern
Sources -> Snowpipe -> Raw Schema -> dbt/SQL -> Mart Schema -> BI
