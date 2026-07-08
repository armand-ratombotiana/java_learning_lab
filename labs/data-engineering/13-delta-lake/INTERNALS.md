# Delta Lake Internals

## Commit Protocol
1. Read latest table version from _delta_log. 2. Check for conflicts (file overlap). 3. Create new commit JSON with AddFile/RemoveFile actions. 4. Write atomically to _delta_log/. 5. On conflict, retry with latest version. 6. Periodic checkpoints reduce log replay overhead.

## File Layout
Table stored as Parquet files in directory. _delta_log/ contains commit JSONs (00000001.json, 00000002.json) and checkpoint Parquet files. Each commit is complete snapshot of files for that version. Time travel reads historical commit's file list.

## Optimize Mechanics
1. Scan all small files in table. 2. Read and repartition data into target file size. 3. Write new larger files. 4. Commit AddFile for new files + RemoveFile for old files. 5. Optionally Z-order data within files by specified columns. 6. All happen atomically in single commit.

## Change Data Feed
Enabled per table. Records insert, update, delete events with before/after images. Reads as version table with _change_type column. Supports incremental processing without full table scans. Output compatible with streaming consumers.
