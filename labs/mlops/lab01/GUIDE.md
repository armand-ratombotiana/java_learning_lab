# Lab 01: ML Pipeline Orchestration — Guide

## Step 1: Understand DAG Concepts

A DAG (Directed Acyclic Graph) defines task dependencies. Each node is a task; edges represent dependencies.

```
     ┌──────────┐
     │ Validate │
     │   Data   │
     └────┬─────┘
          │
     ┌────▼─────┐
     │ Feature  │
     │  Eng     │
     └────┬─────┘
          │
     ┌────▼─────┐
     │  Train   │
     │  Model   │
     └────┬─────┘
          │
     ┌────▼─────┐
     │Evaluate  │
     │ & Deploy │
     └──────────┘
```

## Step 2: Implement PipelineTask

Each task has a name, dependencies, and execution logic.

## Step 3: Implement PipelineDAG

A DAG manages tasks, resolves dependency order via topological sort, and executes them.

## Step 4: Compile and Run

```bash
cd lab01/src
javac com/mlops/lab01/*.java
java com.mlops.lab01.MLOpsPipelineOrchestrationLab
```

## Airflow Mapping

| Java Class        | Airflow Equivalent |
|-------------------|--------------------|
| PipelineTask      | BaseOperator       |
| PipelineDAG       | DAG                |
| execute()         | execute() method   |
| topologicalSort() | scheduler          |

## Key Takeaways
- Orchestration ensures deterministic, repeatable pipelines
- Topological sort guarantees correct execution order
- Retry and error handling make pipelines production-ready
- Pipeline-as-code enables version control, testing, and collaboration
