# Apache Beam

## Overview
Apache Beam is a unified programming model for batch and streaming data processing pipelines. It provides portable SDKs (Java, Python, Go) that run on multiple runners (Flink, Spark, Dataflow, Samza). This lab covers Beam pipelines, PCollections, windowing, triggers, and portable runners.

## Key Concepts
- **Pipeline**: End-to-end data processing workflow as a DAG of PTransforms
- **PCollection**: Distributed dataset — bounded (batch) or unbounded (streaming)
- **PTransform**: Processing operation that transforms one or more PCollections
- **Windowing**: Time-based grouping of unbounded data into finite windows
- **Trigger**: Policy for when to emit window results (early, on-time, late)
- **Runner**: Execution engine — DirectRunner, FlinkRunner, SparkRunner, DataflowRunner

## Learning Objectives
1. Construct Beam pipelines with ParDo, GroupByKey, Combine, and Composite transforms
2. Apply windowing (Fixed, Sliding, Sessions) to unbounded PCollections
3. Configure triggers for early and late firings
4. Run the same pipeline on multiple runners (Direct, Flink, Dataflow)
5. Use stateful processing with ValueState, ListState, MapState
6. Build custom I/O connectors with Splittable DoFn
