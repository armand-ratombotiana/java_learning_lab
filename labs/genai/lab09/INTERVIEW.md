# Lab 09: Interview Questions

## Q1: What are the limitations of BLEU and ROUGE for LLM evaluation?
**A:** Both measure n-gram overlap, which correlates poorly with semantic quality. They penalize valid paraphrases and don't capture factual accuracy or coherence.

## Q2: How do you detect hallucinations in LLM outputs?
**A:** Methods: 1) NER overlap with source, 2) claim extraction + verification against knowledge base, 3) round-trip consistency (ask model to verify its own output), 4) dedicated factuality classifiers.

## Q3: What metrics should you use for evaluating instruction following?
**A:** Human evaluation (Likert scale), LLM-as-judge (GPT-4 evaluation), rubric-based scoring, pairwise preference comparisons.

## Q4: How do you measure bias in LLM outputs?
**A:** 1) Counterfactual evaluation (swap demographic terms), 2) Stereotype benchmarks (BBQ, StereoSet), 3) Demographic parity in downstream task outcomes, 4) Toxicity rate across demographic groups.

## Q5: What is the difference between held-out and held-in evaluation?
**A:** Held-out tests on unseen datasets; held-in tests on training data (for memorization detection). Both are needed to assess generalization vs memorization.
