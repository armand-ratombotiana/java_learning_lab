# Lab 12: Interview Questions

## Q1: What are the biggest contributors to LLM inference cost?
**A:** 1) GPU compute (attention is O(n^2) in sequence length), 2) Memory bandwidth (model weights must be loaded for each token), 3) KV cache memory (grows with batch size × sequence length).

## Q2: How does semantic caching reduce cost?
**A:** Instead of running inference for semantically similar queries, cache returns the previous response. Saves compute for repeated patterns (FAQ, common queries). Requires embedding model + similarity search.

## Q3: What is dynamic batching and when should you use it?
**A:** Dynamic batching groups requests arriving within a small window (e.g., 50ms) into a single batch. Increases throughput (GPU utilization) at the cost of added latency. Best for throughput-sensitive, latency-tolerant workloads.

## Q4: Explain speculative decoding and its trade-offs.
**A:** A small draft model generates K tokens quickly; the large target model verifies them in parallel. Can achieve 2-3x speedup. Trade-off: overhead if draft is often wrong, requires compatible model pair.

## Q5: How does prompt compression work?
**A:** Methods: 1) Stop-word removal, 2) Extractive summarization, 3) Learned compression (LLMLingua), 4) Semantic token pruning. Reduces context length, saving both compute and KV cache memory.
