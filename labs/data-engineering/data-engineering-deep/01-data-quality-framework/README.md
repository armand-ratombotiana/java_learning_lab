# Lab 01: Data Quality Framework

## Overview

Build a data quality rules engine that validates, profiles, and enforces constraints on streaming and batch datasets.

## Learning Objectives

- Design a composable rules engine for data quality checks
- Implement schema validation and anomaly detection
- Profile data distributions (null rates, cardinality, min/max)
- Integrate quality checks into a pipeline with alerting

## Key Concepts

- **Rule Engine**: Chain of responsibility for quality checks
- **Schema Validation**: AVRO/Parquet schema compatibility
- **Data Profiling**: Statistical summaries of column distributions
- **Anomaly Detection**: Threshold-based and Z-score outlier detection
- **Quality Score**: Composite health score across dimensions

## Prerequisites

- Java 21+
- Basic knowledge of functional programming patterns
- Familiarity with CSV/Parquet data formats
