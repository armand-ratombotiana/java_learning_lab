# Lab 01: Transformer Architecture Guide

## Step 1: Scaled Dot-Product Attention
The core operation: `Attention(Q,K,V) = softmax(QK^T / sqrt(d_k)) V`

## Step 2: Multi-Head Attention
Split Q, K, V into `h` heads, apply attention in parallel, concatenate.

## Step 3: Positional Encoding
Add sinusoidal position information to input embeddings.

## Step 4: Encoder Block
Multi-head attention → Add & Norm → FFN → Add & Norm.

## Step 5: Decoder Block
Masked multi-head attention → cross-attention → FFN with residuals.

## Compile & Run
```bash
cd lab01/src
javac com/genai/lab01/Main.java
java com.genai.lab01.Main
```
