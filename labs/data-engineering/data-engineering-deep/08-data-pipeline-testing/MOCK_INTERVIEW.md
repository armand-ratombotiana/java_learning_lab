# Lab 08: Mock Interview — Test Infrastructure Engineer

**Interviewer**: "Design a data contract testing framework for a company with 200 microservices that produce Kafka events."

**Candidate**: "I'd build a Contract Registry service that stores schemas, SLAs, and consumer expectations. Each producer publishes their Avro schema to the registry. Each consumer registers a contract: 'I expect these fields with these types and these min/max frequencies'. The framework runs in CI: when a producer changes a schema, the CI runs all consumer contracts against the new schema."

**Interviewer**: "How do you test a pipeline without actually moving data?"

**Candidate**: "We use record-and-replay: capture a small representative dataset from production, anonymize it, and use it as a fixture. For stream processing, we use TopologyTestDriver (Kafka Streams) which simulates the topology in memory without needing a Kafka cluster. For Spark, we use a local SparkSession with a small in-memory dataset."

**Interviewer**: "How do you handle false positives in contract tests?"

**Candidate**: "We have a contract review process: when a consumer contract fails, the producer and consumer teams must triage together. If the change is intentional and the consumer agrees, the contract is updated. The registry versions contracts, so we can roll back a contract change if needed. We also allow contracts to have a 'warning' mode that doesn't block deployment but sends a notification."

**Interviewer**: "How do you test exactly-once semantics?"

**Candidate**: "We have a dedicated 'durability test' that sends the same batch of records twice and verifies the output has no duplicates. For idempotency, we test that running the same pipeline twice produces identical results. We also do failure injection: kill the pipeline mid-way, restart it, and verify the output is correct (not duplicated, not missing any records)."
