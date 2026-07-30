# Lab 05: Prompt Engineering at Scale — Interview Q&A

## FAANG-Level Questions

### Q1: How do you manage prompts across multiple models and use cases at scale?

**A:** Use a centralized prompt registry with versioning, metadata tags (model, use case, author), and automated rendering. Prompts are stored as templates with typed variables. A/B testing infrastructure compares variants statistically. Changes go through code review and are deployed via the same CI/CD pipeline as application code.

### Q2: Design an A/B testing framework for prompt optimization.

**A:** Each prompt variant is rendered with identical inputs and sent to the LLM in randomized order. Metrics collected: response quality (human evaluation or LLM-as-judge), latency, token usage, cost. Statistical significance is computed using a chi-squared test or t-test. Winning variants are automatically promoted to production after sufficient sample size.

### Q3: How do you detect and handle prompt drift?

**A:** Prompt drift occurs when LLM behavior changes due to model updates or deployment changes. Mitigations: (1) pin prompts to specific model versions; (2) run regression test suites after every model update; (3) monitor output distributions (sentiment, length, topic) for shifts; (4) maintain golden test sets with expected outputs.

### Q4: What is your strategy for prompt rollback?

**A:** Every prompt change creates a new version entry. If quality metrics drop after deployment, an automated rollback restores the previous version. Rollback is instantaneous because old versions remain in the registry. Alert thresholds trigger automatic rollback when accuracy drops below a configurable threshold.

### Q5: How do you measure prompt quality objectively?

**A:** Combine automated metrics (ROUGE/BLEU for summarization, exact match for classification, cosine similarity for semantic tasks) with human evaluation and LLM-as-judge ratings. Track longitudinal trends of each metric. Use pairwise comparisons (A/B) rather than absolute scoring for more reliable results.