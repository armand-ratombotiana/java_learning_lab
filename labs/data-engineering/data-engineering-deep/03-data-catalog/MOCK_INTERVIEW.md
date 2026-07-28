# Lab 03: Mock Interview — Data Platform Engineer

**Interviewer**: "Design a data catalog that serves 10,000 internal users across 50 business units."

**Candidate**: "The core challenge is balancing discoverability with governance. I'd build the catalog around three layers: a metadata ingestion layer, a storage/indexing layer, and an API/UI layer."

**Interviewer**: "How do you ingest metadata?"

**Candidate**: "We'd use a push-and-pull model. Push: data producers emit metadata events to a Kafka topic on dataset creation/schema change. Pull: a background crawler scans data lake storage (AWS Glue catalog, Hive Metastore) nightly for changes. Each source has a connector plugin that normalizes metadata into a common model."

**Interviewer**: "How do you handle search at scale?"

**Candidate**: "I'd use Elasticsearch for the primary search index with faceted filtering by tag, owner, data source, and schema field. The index schema includes a 'popularity' score based on query frequency. For synonyms and typos, I'd use a custom synonym filter and a fuzzy query with edit distance 2."

**Interviewer**: "How do you ensure metadata freshness?"

**Candidate**: "Each metadata record has a 'lastSeen' timestamp. A freshness checker runs every hour and flags datasets not seen in 7 days as 'stale'. After 30 days, the catalog marks them as 'inactive' with a warning. Dataset owners receive weekly emails about their stale assets."

**Interviewer**: "How would you integrate lineage into the catalog?"

**Candidate**: "Each dataset in the catalog has a 'lineage' field linking to the lineage service. The catalog UI shows a 'View Lineage' button that opens the lineage graph for that dataset. We cache the key lineage relationships in the catalog DB to answer queries like 'what downstream datasets are affected if I change this table?' without calling the lineage service."
