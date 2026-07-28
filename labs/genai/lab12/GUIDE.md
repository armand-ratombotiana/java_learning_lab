# Lab 12: Cost Optimization Guide

## Step 1: Exact Cache
Cache exact query-response pairs (LRU eviction).

## Step 2: Semantic Cache
Use embedding similarity to match semantically similar queries.

## Step 3: Dynamic Batching
Collect requests for a time window, batch them for inference.

## Step 4: Prompt Compression
Remove stop words, summarize verbose context.

## Step 5: Speculative Drafting
Draft tokens with a small model, verify with large model.

## Compile & Run
```bash
cd lab12/src
javac com/genai/lab12/Main.java
java com.genai.lab12.Main
```
