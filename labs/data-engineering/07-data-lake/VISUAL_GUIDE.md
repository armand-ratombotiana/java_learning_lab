# Visual Guide

## Medallion Architecture
```
Source Data -> [BRONZE] raw -> [SILVER] clean -> [GOLD] aggregated -> BI
```

## Delta Lake Timeline
```
v1: INSERT file1 -> v2: INSERT file2 -> v3: UPDATE (remove file1, add file3)
Read at v2: file1 + file2
Read at v4: file3 (after update+delete)
```
