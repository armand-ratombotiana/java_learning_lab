# Lab 02: Mock Interview — Data Engineer, L6

**Interviewer**: "Design a data lineage system that can handle 10,000 pipelines and provides sub-second impact analysis."

**Candidate**: "I'd model lineage as a property graph with two node types: Dataset and Job. Edges are 'produces' (Job->Dataset) and 'consumes' (Dataset->Job). For sub-second impact analysis, I'd use an adjacency list in memory with a read-optimized structure."

**Interviewer**: "How do you populate the graph?"

**Candidate**: "Two approaches. For Spark jobs, I'd hook into the QueryExecutionListener to capture input/output datasets and column-level expressions. For SQL-based ETL, I'd use sqlparser-js or Calcite to parse queries and extract table/column references. Each job emits lineage events to Kafka; a consumer updates the graph."

**Interviewer**: "How do you handle schema changes — like a column rename?"

**Candidate**: "We version each dataset. When a column is renamed, the lineage graph shows the old column as deprecated with an alias to the new name. Impact analysis can traverse both old and new names. The data catalog stores column history so we can trace lineage across schema versions."

**Interviewer**: "What about performance at scale?"

**Candidate**: "The live query path uses a read-optimized in-memory graph (guava Cache with TTL). For deep analysis queries, I'd offload to a graph DB like Neo4j or Dgraph. The in-memory graph is sharded by domain/team — most impact queries only touch one domain."

**Interviewer**: "How would you test lineage correctness?"

**Candidate**: "We'd use a gold-standard dataset of known transformations: e.g., read CSV, join, aggregate, write Parquet. For each, we verify that the captured lineage matches the expected DAG. We'd also fuzz-test with randomly generated SQL queries."
