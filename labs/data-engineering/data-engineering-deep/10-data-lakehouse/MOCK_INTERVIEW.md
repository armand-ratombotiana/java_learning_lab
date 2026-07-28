# Lab 10: Mock Interview — Lakehouse Platform Engineer

**Interviewer**: "Design a lakehouse that combines the schema flexibility of a data lake with the ACID guarantees of a data warehouse."

**Candidate**: "The core is a transaction log on object storage. Every table operation (insert, update, delete, compaction) writes an entry to the log. To read the table, you replay the log to build the current snapshot — this is the Delta Lake approach. For Iceberg-style, you'd use manifest files that track the set of data files at each version. I'd recommend Iceberg for its partition evolution and hidden partitioning capabilities."

**Interviewer**: "How do you handle concurrent writers to the same lakehouse table?"

**Candidate**: "Optimistic concurrency control. Each writer reads the latest version from the transaction log, makes their changes, and tries to commit. The commit succeeds only if the version hasn't advanced since they read it — CAS (compare-and-swap) on the log file. If there's a conflict, the writer retries. This works well for most workloads; for high-contention tables, you'd use bucketing or partitioning to reduce conflicts."

**Interviewer**: "Your lakehouse has 10 million small files (under 64KB). What do you do?"

**Candidate**: "That's a classic small files problem. I'd run a compaction job that reads small files from the same partition, merges them into larger files (target 256MB-1GB), and writes a new commit to the transaction log. The old small files are marked for deletion in the log but kept for time travel. I'd also adjust the ingestion to write fewer, larger files by batching writes or using a higher file size threshold."

**Interviewer**: "How do you implement schema evolution in a lakehouse?"

**Candidate**: "In Iceberg, each schema change creates a new schema version in the metadata. The table metadata tracks all versions. Queries use the schema version that was active when the data was written — this is schema-on-read. Adding columns is a metadata-only operation. Removing or renaming columns marks them as deleted in the latest schema but old data files still have them. Iceberg's schema evolution supports adding, renaming, reordering, and dropping columns without rewriting data files."
