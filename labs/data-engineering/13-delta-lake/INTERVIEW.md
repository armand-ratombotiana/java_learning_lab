# Interview Questions: Delta Lake

### Transaction Log
**Q**: Explain Delta Lake's transaction log protocol.
**A**: JSON files in _delta_log/, each commit creates new version; readers reconstruct file list from log; concurrent writes use optimistic locking with retry

### Time Travel
**Q**: How does Time Travel work in Delta Lake?
**A**: Each version tracks complete file set; read at version N reconstructs state from log; VACUUM removes old files beyond retention window

### Optimization
**Q**: How does OPTIMIZE improve performance?
**A**: Compacts small files into larger ones (256MB-1GB); Z-ordering colocates related data; reduces metadata overhead and improves file skipping

### Comparison
**Q**: Difference between Delta Lake and Apache Iceberg?
**A**: Delta: Spark-native, JSON transaction log. Iceberg: catalog abstraction, partition evolution, broader engine support. Both provide ACID on data lakes.
