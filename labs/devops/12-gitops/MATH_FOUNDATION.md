# Math Foundation for GitOps

N/A — GitOps is an operational methodology with no significant mathematical foundation.

## State Comparison
- **Diff algorithm**: Three-way merge of desired (Git) vs actual (cluster) vs previous (last sync).
- **Reconciliation loop**: Continuous comparison and correction cycle.

## Sync Ordering
- **Dependency graph**: Resources may depend on other resources (CRD → CR → App).
- **Sync waves**: Ordered execution groups (0, -1, 1, 2, ...) for resource creation ordering.

## Progressive Delivery Math
- **Canary weight**: Percentage-based traffic shifting (10% → 50% → 100%).
- **Promotion criteria**: Health checks, metric thresholds, manual approval.
- **Rollback**: Revert to previous version; Git revert and sync.

## ApplicationSet Generators
- **List generator**: Iterate over list of clusters/environments.
- **Git generator**: Generate applications from Git directory structure.
- **Matrix generator**: Combine outputs from multiple generators.
- **Cluster generator**: Iterate over registered clusters.

No advanced mathematics — basic graph theory for dependencies and percentage calculations.
