# Math Foundation for CI/CD

N/A — CI/CD pipelines are primarily process-oriented with no significant mathematical foundation. Key relevant concepts:

## Dependency Graphs
Pipeline stages form a directed acyclic graph (DAG) where stages depend on previous stages' outputs. Topological sorting determines execution order.

## Parallel Execution
- Fan-out: Multiple jobs run simultaneously after a dependency completes.
- Resource allocation: Number of concurrent jobs limited by runner pool.

## Test Selection (Advanced)
- **Test impact analysis**: Changed code → affected tests → selective test execution.
- **Flaky test detection**: Statistical analysis of test pass/fail history.

## Rollback Probability
- **Change failure rate**: % of deployments causing failures.
- **MTTR**: Mean Time To Recover — measured from incident to recovery.

No advanced mathematics beyond basic statistics and graph theory.
