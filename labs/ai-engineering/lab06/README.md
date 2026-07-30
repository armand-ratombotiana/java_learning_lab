# Lab 06: AI Pipeline Orchestration

## Learning Objectives
- Design modular AI pipelines with composable stages
- Implement data preprocessing, feature extraction, and inference
- Measure and optimize pipeline performance
- Handle errors and monitor stage-level metrics

## Concepts Covered
- **Pipeline Architecture**: Chaining processing stages
- **Preprocessing**: Cleaning and normalizing input data
- **Feature Extraction**: Transforming raw data into model features
- **Model Inference**: Running predictions through trained models
- **Post-processing**: Formatting and interpreting results
- **Observability**: Per-stage timing and metrics

## Setup
```bash
cd lab06
javac src/com/aiengineering/lab06/AiPipelineOrchestrationDemo.java
java com.aiengineering.lab06.AiPipelineOrchestrationDemo
```

## Key Takeaways
- Modular pipelines enable testing and swapping individual stages
- Instrumenting each stage reveals bottlenecks
- Feature extraction is often the most impactful stage
