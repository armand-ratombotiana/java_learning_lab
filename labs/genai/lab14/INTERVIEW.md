# Lab 14: Interview Questions

## Q1: What metrics should you monitor for an LLM serving system?
**A:** Latency (p50/p95/p99), throughput (tokens/sec, requests/sec), error rate (4xx/5xx), GPU utilization, KV cache hit rate, queue depth, cost per request.

## Q2: How do you detect model drift in production?
**A:** Compare response distributions over time (embedding drift), track user feedback (thumbs up/down), monitor task-specific metrics (accuracy on eval samples), statistical tests on output distributions.

## Q3: What is a canary deployment for LLMs?
**A:** Route a small percentage of traffic (e.g., 5%) to a new model version while monitoring metrics. If no regressions, gradually increase traffic. If issues, rollback immediately.

## Q4: How do you handle an LLM incident (e.g., toxic outputs)?
**A:** 1) Detect via monitoring/feedback, 2) Kill switch (block output), 3) Fallback to safe mode or cached responses, 4) Analyze root cause (prompt injection, model regression), 5) Patch guardrails or rollback model.

## Q5: What is the cost breakdown of serving an LLM?
**A:** GPU compute (80%), memory (KV cache — 10%), networking (5%), storage (model weights — 3%), monitoring (2%). Optimization focus: GPU utilization and KV cache efficiency.
