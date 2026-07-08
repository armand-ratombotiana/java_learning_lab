# Why Apache Iceberg Exists

Traditional Hive-style partitioning is rigid — changing partition structure requires rewriting entire table. File listing on object storage is slow (API calls are expensive). Iceberg's layered metadata enables fast planning, partition evolution without rewrite, and engine-agnostic table management. It solves the problems of Hive tables at petabyte scale.
