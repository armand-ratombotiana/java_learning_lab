# Interview Questions: Snowflake Data Cloud

### Architecture
**Q**: Explain Snowflake's three-layer architecture.
**A**: Storage (compressed columnar in cloud blob storage), Compute (virtual warehouses as elastic clusters), Services (authentication, metadata management, query optimization)

### Performance
**Q**: How would you optimize a slow query in Snowflake?
**A**: Check pruning efficiency via Query Profile, analyze clustering depth, verify warehouse sizing, review joins and filters

### Cost
**Q**: How do you optimize Snowflake costs?
**A**: Set AUTO_SUSPEND on warehouses, match warehouse size to workload, use multi-cluster only when needed, separate ETL from reporting warehouses

### Advanced
**Q**: Difference between transient and permanent tables?
**A**: Transient: no Fail-safe, optional Time Travel; Permanent: full Time Travel + Fail-safe; both support zero-copy cloning
