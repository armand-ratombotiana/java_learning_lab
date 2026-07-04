# Data Lake Internals

## Delta Lake Transaction Log
JSON files in _delta_log/ tracking:
- Add/remove file actions
- Schema changes
- Commit info
- Checkpoint files

## File Layout
```
s3://lake/
  bronze/events/event_date=YYYY-MM-DD/
    part-00001.snappy.parquet
    _delta_log/00000000000000000001.json
  silver/events/...
  gold/aggregates/...
```
