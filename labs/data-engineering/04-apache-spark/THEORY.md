# Apache Spark Theory

## Architecture
- **Driver**: Main program, creates SparkContext
- **Cluster Manager**: YARN, K8s, Standalone
- **Executors**: Worker processes
- **Tasks**: Units of work

## Core Abstractions
- **RDD**: Immutable partitioned collection
- **DataFrame**: RDD + Schema, optimized via Catalyst
- **Dataset**: Type-safe DataFrame

## Execution Model
DAG Scheduler -> Task Scheduler -> Executors
