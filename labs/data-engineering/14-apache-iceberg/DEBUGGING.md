# Debugging Apache Iceberg

## Metadata Issues
Check metadata JSON files for corruption; verify snapshot list integrity

## Performance Problems
Use Spark explain() to check partition pruning; verify manifest file count and size

## Concurrent Write Failures
Check commit conflicts; implement retry logic; consider partition design

## Migration Verification
Row count validation; checksum comparison; random sampling queries
