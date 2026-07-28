# Lab 08: Multimodal Models Guide

## Step 1: Image Patch Embedding
Divide image into patches, project to embedding dimension.

## Step 2: Text Embedding (Transformer)
Basic transformer encoder for text modality.

## Step 3: Dual Encoder Architecture
Separate encoders for image and text, shared embedding space.

## Step 4: Contrastive Learning
InfoNCE loss aligns matching image-text pairs.

## Step 5: Cross-Modal Attention
Fuse modalities for generation (image captioning).

## Compile & Run
```bash
cd lab08/src
javac com/genai/lab08/Main.java
java com.genai.lab08.Main
```
