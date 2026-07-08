# Reflection: Apache Iceberg

- Partition evolution is revolutionary — partition decisions are no longer permanent
- Hidden partitioning greatly simplifies user queries and reduces errors
- Catalog abstraction (Hive, Nessie, REST, Glue) provides deployment flexibility
- Iceberg's snapshot isolation enables consistent reads across engines
- Compaction is essential but can be disruptive if run during peak query times
