# Flashcards: Snowflake Data Cloud

## Card 1
**Front**: What is a virtual warehouse?
**Back**: Elastic compute cluster that executes queries independently; can be suspended/resumed

## Card 2
**Front**: What are micro-partitions?
**Back**: Automatic 50-500 MB columnar storage units with embedded metadata statistics for pruning

## Card 3
**Front**: How does zero-copy cloning work?
**Back**: Creates metadata-only snapshot; copy-on-write for new data; instant regardless of table size

## Card 4
**Front**: What is the difference between Time Travel and Fail-safe?
**Back**: Time Travel: user-accessible (1-90 days); Fail-safe: Snowflake-managed recovery only (7 days)

## Card 5
**Front**: What is clustering depth?
**Back**: Average number of overlapping micro-partitions for a given range; lower = better pruning
