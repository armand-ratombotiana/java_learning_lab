# Lab 02: Data Lineage

## Overview

Build a data lineage tracking system that captures provenance, enables impact analysis, and visualizes data dependencies across pipelines.

## Learning Objectives

- Model lineage as a directed acyclic graph (DAG)
- Implement column-level and dataset-level provenance
- Perform impact analysis (upstream/downstream traversal)
- Integrate lineage capture into ETL jobs

## Key Concepts

- **Provenance**: Origin and transformation history of data
- **Impact Analysis**: Determine affected downstream consumers
- **Column-Level Lineage**: Track individual field transformations
- **OpenLineage**: Standard lineage specification
- **Marquez**: Reference lineage server implementation
