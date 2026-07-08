# Refactoring Apache Iceberg Pipelines

## Hive to Iceberg Migration
Before: Hive table with manual partition management
After: Iceberg table with partition evolution and hidden partitioning

## Partition Strategy Change
Before: Partition by hour (too many small partitions)
After: Evolve to partition by day (no rewrite needed)

## Manual to Automated Maintenance
Before: Manual file cleanup and compaction scripts
After: Automated maintenance via CALL procedures
