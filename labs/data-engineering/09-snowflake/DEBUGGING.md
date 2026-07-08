# Debugging Snowflake Data Cloud

## Warehouse Congestion
Check TABLE(INFORMATION_SCHEMA.WAREHOUSE_LOAD_HISTORY) to see queue depth; add multi-cluster or resize

## Query Performance
Use Snowsight Query Profile to view operator-level times, bytes scanned vs returned, pruning efficiency

## Pruning Inefficiency
Calculate 1 - (PARTITIONS_SCANNED / PARTITIONS_TOTAL); low values indicate poor clustering or missing filters

## Permission Errors
Verify role grants with SHOW GRANTS TO ROLE; check network policies and IP restrictions
