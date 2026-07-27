# Mock Interview: AI Safety & Alignment

**Topic:** Design alignment evaluation for a production LLM

## Core Questions

### Q1: What is AI alignment?

**Answer:**
Alignment ensures AI systems reliably pursue the goals and values intended by humans.

**Key concerns:**
- Capability vs. alignment gap
- Specification gaming (model finds loopholes)
- Goal misgeneralization (proxy objective diverges)
- Deceptive alignment (appears aligned during training)

### Q2: Design alignment evaluation framework.

**Answer:**
Dimensions:
1. Harmlessness: Toxicity, bias, dangerous capabilities, jailbreak resistance
2. Helpfulness: Truthfulness, instruction following, relevance, refusal quality
3. Honesty: Calibration, uncertainty expression, hallucination rate
4. Robustness: Adversarial, OOD, multi-turn consistency, sycophancy
5. Safety: Prompt injection, memorization, privacy leakage, tool-use safety

### Q3: Evaluate jailbreak resistance.

**Answer:**
Types: Role-playing ("DAN"), hypotheticals, encoding (Base64), context manipulation, many-shot.

Evaluation: Automated (AdvBench, HEx-PHI), human red-teaming, RLHF adversarial training.

### Q4: Explain RLHF.

**Answer:**
Three stages:
1. SFT on demonstration data
2. Reward model from human preferences (chosen vs rejected)
3. PPO optimization: max E[r(x,y)] - beta * KL(pi_theta || pi_SFT)

Alternatives: DPO (direct preference optimization), SLiC, KTO.

### Q5: Detect and mitigate misalignment.

**Answer:**
Detection: Sensitivity analysis, activation probes, consistency checks, interpretability.

Mitigation: Constitutional AI, red-teaming + adversarial training, gradual deployment, human oversight.

### Q6: Key safety benchmarks.

**Answer:**
- MMLU (knowledge/reasoning)
- TruthfulQA (truthfulness)
- BBQ (bias)
- HaluEval (hallucination)
- RealToxicityPrompts (toxicity)
- SafetyBench (general safety)
- AdvBench (jailbreak resistance)
- WMDP (dangerous capabilities)
- Chatbot Arena (human preference)

### Q7: Production safety monitoring.

**Answer:**
Pre-deployment: Automated eval, red-teaming, model card, safety review board.

Runtime: Input guard, output guard, rate limiting, anomaly detection.

Post-deployment: Incident logging, continuous improvement, periodic re-evaluation.
