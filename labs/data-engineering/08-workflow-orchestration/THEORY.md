# Orchestration Theory

## DAG Fundamentals
- **Nodes**: Tasks (operators)
- **Edges**: Dependencies
- **Topological Order**: Execution sequence
- **No Cycles**: Prevents infinite loops

## Execution Models
- Sequential: One task at a time
- Parallel: Independent tasks run simultaneously
- Conditional: Branch based on outcome

## Strategy Comparison
| Tool | Language | Scalability |
|------|----------|-------------|
| Airflow | Python | High |
| Prefect | Python | High |
| Dagster | Python | High |
| Luigi | Python | Medium |
