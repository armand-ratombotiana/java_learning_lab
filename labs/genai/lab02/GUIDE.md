# Lab 02: GPT Architecture Guide

## Step 1: Causal Masking
Modify attention to prevent tokens from attending to future positions using an upper-triangular mask.

## Step 2: Autoregressive Decoder
Stack transformer decoder blocks with causal masking.

## Step 3: Tokenization (BPE)
Build a simplified BPE tokenizer that merges frequent byte pairs.

## Step 4: Generation Loop
Predict next token, append to sequence, repeat — with and without KV cache.

## Step 5: KV Cache Optimization
Store K and V from previous steps to avoid recomputation.

## Compile & Run
```bash
cd lab02/src
javac com/genai/lab02/Main.java
java com.genai.lab02.Main
```
