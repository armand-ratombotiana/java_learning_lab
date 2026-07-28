# Lab 06: Fine-Tuning with LoRA/QLoRA Guide

## Step 1: Weight Matrix
Create a pretrained weight matrix W (frozen).

## Step 2: LoRA Low-Rank Matrices
Decompose delta W into A (dxr) and B (rxd) where r << d.

## Step 3: Forward Pass with LoRA
Compute `y = xW + xAB` (scale by lora_alpha / r).

## Step 4: Training Simulation
"Train" only A and B while keeping W frozen.

## Step 5: QLoRA Concept
Simulate quantized base weights with LoRA adapters.

## Compile & Run
```bash
cd lab06/src
javac com/genai/lab06/Main.java
java com.genai.lab06.Main
```
