# Debugging

## Common Failures
- **OOM**: Collecting large datasets - use foreachPartition
- **Serialization**: Task not serializable - use mapPartitions
- **Data Skew**: Few tasks run longer - salt keys

## Commands
```bash
open http://localhost:4040  # Spark UI
kafka-consumer-groups --describe --group pipeline-group
kubectl logs -l app=pipeline -n data-platform --tail=100 -f
```
