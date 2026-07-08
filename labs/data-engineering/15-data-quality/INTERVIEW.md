# Interview Questions: Data Quality Engineering

### Quality Dimensions
**Q**: Explain the six dimensions of data quality.
**A**: Accuracy (matches reality), Completeness (no nulls where required), Consistency (no contradictions), Timeliness (current enough), Validity (conforms to format/rules), Uniqueness (no duplicates)

### Great Expectations
**Q**: How does Great Expectations work?
**A**: Define expectations (rules) in Expectation Suite. Run against dataset. Results include passing statistics, failures, and metadata. Use checkpoints to enforce quality gates.

### Schema Drift
**Q**: How do you handle schema drift?
**A**: Detect via schema comparison (incoming vs expected). Alert on drift. Classify severity (add column = evolve, remove column = critical). Auto-evolve for additive changes; block for breaking changes.

### Data Contracts
**Q**: What should a data contract include?
**A**: Schema definition with types, ownership, SLAs (freshness, completeness), change procedure, consumer list, quality guarantees, and notification channels
