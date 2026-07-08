# Mental Models for Snowflake Data Cloud

## 1. The Elastic Workforce
Virtual warehouses are like hiring temporary workers. Scale up (hire more) for big jobs, suspend (release) when idle. You only pay for time worked.

## 2. The Library Archive
Micro-partitions are like books on shelves. The catalog (metadata) tells you which shelf has the books you need (pruning). No need to search the entire library.

## 3. The Git Analogy
Time Travel = git log (view history). Zero-Copy Clone = git branch (instant branch). Undrop = git reflog (recover deleted). Each version is a commit.
