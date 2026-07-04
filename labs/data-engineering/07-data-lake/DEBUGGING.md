# Debugging

## Transaction Log Conflicts
Multiple writers need separate partition directories.

## Metadata Issues
Large Iceberg metadata needs regular compaction.

## Partition Pruning
Check if predicates are pushed down:
```java
dataset.explain("formatted");
```
