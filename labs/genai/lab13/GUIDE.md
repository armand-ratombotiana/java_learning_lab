# Lab 13: Context Window Management Guide

## Step 1: Sliding Window Attention
Each token attends only to the W nearest previous tokens.

## Step 2: RoPE (Rotary Position Embedding)
Apply rotation matrices to Q and K based on position.

## Step 3: ALiBi (Attention with Linear Biases)
Add position-proportional bias to attention scores.

## Step 4: Context Compression
Summarize/compress old context into a smaller representation.

## Step 5: Memory Bank
Store compressed summaries for long-term access.

## Compile & Run
```bash
cd lab13/src
javac com/genai/lab13/Main.java
java com.genai.lab13.Main
```
