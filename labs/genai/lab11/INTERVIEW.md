# Lab 11: Interview Questions

## Q1: What is the difference between symmetric and asymmetric quantization?
**A:** Symmetric: range = [-max|W|, max|W|], zero-point = 0. Asymmetric: range = [min(W), max(W)], zero-point shifts the range. Asymmetric utilizes the full range but adds zero-point computation overhead.

## Q2: How much memory savings do INT8 and FP16 provide vs FP32?
**A:** FP32 = 4 bytes, FP16 = 2 bytes (50% savings), INT8 = 1 byte (75% savings). Actual speedup depends on hardware support.

## Q3: What is quantization-aware training (QAT) vs post-training quantization (PTQ)?
**A:** PTQ quantizes a pretrained model without retraining (fast, may lose accuracy). QAT simulates quantization during training (slower but better accuracy, especially at INT4).

## Q4: What does TensorRT do for model optimization?
**A:** TensorRT: 1) Layer fusion (e.g., Conv+Bias+ReLU), 2) Precision calibration (FP16/INT8), 3) Kernel auto-tuning, 4) Memory optimization, 5) Dynamic shape support.

## Q5: What are the trade-offs of ONNX as an intermediate format?
**A:** Pros: framework interoperability, graph optimizations, hardware targeting. Cons: operator coverage gaps, debugging difficulty, overhead in conversion pipeline.
