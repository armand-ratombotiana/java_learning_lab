# Flashcards: Delta Lake

## Card 1
**Front**: What is Delta Lake?
**Back**: Open-source storage layer adding ACID transactions to data lakes using Parquet + transaction log

## Card 2
**Front**: How does Delta handle concurrent writes?
**Back**: Optimistic concurrency control with conflict detection; writers retry on conflict

## Card 3
**Front**: What is OPTIMIZE?
**Back**: File compaction that bin-packs small files and optionally Z-orders data for better pruning

## Card 4
**Front**: What is the transaction log?
**Back**: JSON files in _delta_log/ recording every commit; enables time travel and ACID

## Card 5
**Front**: What is Z-ordering?
**Back**: Clustering related data within files for better file skipping during queries
