# Visual Guide to Delta Lake

```
Transaction Log Timeline:
v1: +file1.parquet (INSERT 1000 rows)
v2: +file2.parquet (INSERT 500 rows)
v3: -file1.parquet +file3.parquet (UPDATE 200 rows)
v4: -file2.parquet (DELETE file2 rows)

Read at v2: file1 + file2
Read at v4: file3 (latest)

Delta Architecture:
[Parquet Files] + [Delta Log: _delta_log/]
                      |
             [Transaction Log]
             [JSON Commits] [Checkpoints]

Merge Flow:
MERGE INTO target USING source ON key
-> Read latest version
-> Compute matches
-> Generate AddFile/RemoveFile
-> Write commit atomically
```
