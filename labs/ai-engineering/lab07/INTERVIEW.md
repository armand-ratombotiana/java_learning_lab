# Lab 07: AI Testing & Evaluation — Interview Q&A

## FAANG-Level Questions

### Q1: How do you test a model when there is no ground-truth label?

**A:** Use indirect evaluation: (1) LLM-as-judge — have a stronger model evaluate outputs against rubrics; (2) pairwise comparison — human raters compare outputs from old vs. new model; (3) proxy metrics — coherence, fluency, diversity scores; (4) behavioral testing — check specific capabilities (math, reasoning, safety) with curated probes.

### Q2: Design a regression testing strategy for a continuously deployed model.

**A:** Maintain three test suites: (1) golden test set — 500+ curated input/output pairs covering all major use cases; (2) adversarial tests — edge cases designed to break common failure modes; (3) performance benchmarks — latency and throughput thresholds. Run all three before every deployment. Automatically block deployment if any threshold is breached.

### Q3: How do you compare two model versions statistically?

**A:** Use paired evaluation on a held-out test set. Compute multiple metrics (accuracy, F1, latency, cost). Use McNemar's test for classification differences or a paired t-test for continuous metrics. Report confidence intervals. For LLMs, use pairwise preference ratings with statistical significance (binomial test).

### Q4: What metrics would you track for a conversational AI system?

**A:** Turn-level: response relevance, coherence, safety. Session-level: task completion rate, average turns to resolution, user satisfaction score. System-level: latency p50/p95/p99, cost per conversation, hallucination rate (via automated fact-checking), human escalation rate.

### Q5: How do you automate benchmark tests without flaky results?

**A:** Run benchmarks in a dedicated environment with pinned hardware/software. Warm up the system before measurements. Run multiple iterations and report median (not mean) to reduce outlier impact. Use statistical significance thresholds — differences below 2% are noise. Monitor and alert on benchmark environment changes.