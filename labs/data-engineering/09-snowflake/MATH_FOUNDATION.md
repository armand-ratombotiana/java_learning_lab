# Math Foundation for Snowflake Data Cloud

## Storage Estimation
```
RawSize = SUM(row_count * avg_column_size)
CompressedSize = RawSize / CompressionRatio (2-10x)
TotalStorage = SUM(all micro-partition compressed sizes)
```

## Time Travel Cost
```
TT_Storage = DailyChangeRate * RetentionDays * CompressedSize
Example: 10% daily change on 1TB table with 90-day retention = 9TB additional storage
```

## Clustering Depth
```
Depth = AVG(overlapping partitions for a given range)
Well-clustered: 1-4
Unclustered: 10+
Lower depth = better pruning = faster queries
```

## Credit Usage
```
TotalCredits = SUM(warehouse_credits) + MAX(0, cloud_services - 0.1*warehouse_credits)
Cloud services credits are free up to 10% of warehouse credits
```
