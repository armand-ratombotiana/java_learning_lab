# Lab 11: Model Quantization & Deployment Guide

## Step 1: FP16 Conversion
Convert float to half-precision and back.

## Step 2: INT8 Symmetric Quantization
Scale weights by absmax, round to int8.

## Step 3: INT8 Asymmetric Quantization
Use (min, max) range with zero-point offset.

## Step 4: Calibration Dataset
Use representative data to compute quantization ranges.

## Step 5: Graph Optimization (TensorRT concept)
Fuse operations, eliminate redundant layers, constant folding.

## Compile & Run
```bash
cd lab11/src
javac com/genai/lab11/Main.java
java com.genai.lab11.Main
```
