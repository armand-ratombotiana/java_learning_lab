# Debugging Data Catalog

## Ingestion Failures
Check connector configuration, network access, credentials, and source query timeouts

## Missing Lineage
Verify SQL parser captures the transformation pattern; some dialects need custom parsing

## Search Not Reflecting Changes
Check Elasticsearch indexing; force reindex if needed

## Performance Issues
Batch ingestion in smaller chunks; schedule during off-peak hours
