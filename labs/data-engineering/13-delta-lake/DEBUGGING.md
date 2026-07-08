# Debugging Delta Lake

## Conflict Resolution
Check isolation level; analyze overlapping file modifications; implement retry with backoff in application code

## Transaction Log Analysis
ls _delta_log/ for version history; cat JSON files to see actions; use DESCRIBE DETAIL for table metadata

## Version Operations
DESCRIBE HISTORY delta.`/path` shows full history; DESCRIBE DETAIL shows current state; FSCK REPAIR TABLE for orphan files

## Performance Issues
Check file count and sizes; verify partition pruning; analyze Z-order effectiveness with EXPLAIN
