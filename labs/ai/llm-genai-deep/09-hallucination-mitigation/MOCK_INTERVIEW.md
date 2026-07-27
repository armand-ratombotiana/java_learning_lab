# Mock Interview: Design a Hallucination Detection System for Production LLMs

## Scenario
You are interviewing for a ML engineer role at a healthcare AI company. They need to detect and prevent hallucinations in their LLM-powered clinical decision support system.

## Interviewer Opening Question
"Design a production-grade hallucination detection system. How do you catch factual errors before they reach users?"

## Candidate Response
"I'd build a multi-layered system: (1) Pre-generation: constrain output to retrieved context. (2) During generation: token-level uncertainty estimation. (3) Post-generation: NLI-based verification and fact-checking against a knowledge base. The system would score each claim and block responses below a confidence threshold."

## Interviewer Probing Questions

**Q: How do you measure token-level uncertainty?**
"Use the model's log probabilities. If the max logit is below a threshold or the entropy is high, flag the token. I also use semantic entropy — sample multiple responses and check for divergence."

**Q: How do you handle domain-specific facts (medical knowledge)?**
"Build a medical knowledge graph from trusted sources (UMLS, DrugBank). After generation, extract entities and verify relationships against the graph using a lightweight classifier."

**Q: What threshold strategy do you use?**
"Calibrate on a held-out set with known hallucinations. Choose a threshold that maximizes F1. Use separate thresholds for different severity levels: warning (low confidence), block (high confidence hallucination)."

## Candidate Solution (Python)

```python
import torch
import numpy as np
from typing import List, Dict, Optional
from dataclasses import dataclass
from collections import Counter

@dataclass
class HallucinationReport:
    claim: str
    score: float
    risk: str  # "low", "medium", "high"
    evidence: Optional[str] = None
    flagged_tokens: List[str] = None

class TokenLevelDetector:
    def __init__(self, threshold=0.4, entropy_threshold=2.0):
        self.threshold = threshold
        self.entropy_threshold = entropy_threshold

    def analyze(self, log_probs: torch.Tensor, tokens: List[str]) -> Dict:
        probs = torch.exp(log_probs)
        max_probs = probs.max(dim=-1).values
        entropy = -(probs * torch.log(probs + 1e-10)).sum(dim=-1)
        flagged_indices = (max_probs < self.threshold) | (entropy > self.entropy_threshold)
        return {
            "avg_confidence": max_probs.mean().item(),
            "max_entropy": entropy.max().item(),
            "flagged_ratio": flagged_indices.float().mean().item(),
            "flagged_tokens": [t for i, t in enumerate(tokens) if flagged_indices[i]]
        }

class SemanticEntropyDetector:
    def __init__(self, num_samples=5, temperature=0.5):
        self.num_samples = num_samples
        self.temperature = temperature

    def detect(self, model, tokenizer, prompt: str) -> float:
        samples = []
        for _ in range(self.num_samples):
            inputs = tokenizer(prompt, return_tensors="pt")
            with torch.no_grad():
                outputs = model.generate(**inputs, max_new_tokens=128,
                                         do_sample=True, temperature=self.temperature)
            samples.append(tokenizer.decode(outputs[0], skip_special_tokens=True))
        # Cluster by semantic similarity
        clusters = self._cluster_by_meaning(samples)
        entropy = sum(-p * np.log(p) for p in clusters.values())
        return entropy

    def _cluster_by_meaning(self, samples: List[str]) -> Dict[str, float]:
        # Simplified: group by normalized sentence
        cluster = Counter(samples)
        total = sum(cluster.values())
        return {k: v / total for k, v in cluster.items()}

class FactVerifier:
    def __init__(self, knowledge_graph, nli_model):
        self.kg = knowledge_graph
        self.nli_model = nli_model

    def verify_claim(self, claim: str) -> float:
        entities = self._extract_entities(claim)
        for entity, relation, obj in self._extract_triples(claim, entities):
            if not self.kg.query(entity, relation, obj):
                return 0.0
        return 1.0

    def _extract_entities(self, text: str) -> List[str]:
        # Simplified: use spaCy or similar NER
        return []

    def _extract_triples(self, text: str, entities: List[str]) -> List[tuple]:
        # Simplified triple extraction
        return []

class HallucinationDetectionPipeline:
    def __init__(self, token_detector, semantic_detector, fact_verifier,
                 risk_thresholds={"low": 0.3, "medium": 0.6, "high": 0.8}):
        self.token_detector = token_detector
        self.semantic_detector = semantic_detector
        self.fact_verifier = fact_verifier
        self.thresholds = risk_thresholds

    def evaluate(self, prompt: str, response: str,
                 log_probs: torch.Tensor = None, tokens: List[str] = None) -> HallucinationReport:
        token_risk = 0.0
        semantic_risk = 0.0
        fact_risk = 0.0

        if log_probs is not None and tokens is not None:
            token_result = self.token_detector.analyze(log_probs, tokens)
            token_risk = token_result["flagged_ratio"]

        semantic_entropy = self.semantic_detector.detect(None, None, prompt + response)
        semantic_risk = min(semantic_entropy / 5.0, 1.0)

        fact_score = self.fact_verifier.verify_claim(response)
        fact_risk = 1.0 - fact_score

        final_score = (token_risk * 0.3 + semantic_risk * 0.3 + fact_risk * 0.4)
        risk_level = "low"
        for level, threshold in self.thresholds.items():
            if final_score > threshold:
                risk_level = level
        return HallucinationReport(claim=response, score=final_score, risk=risk_level)
```

## Interviewer Feedback
"Excellent multi-layered approach. Combining token-level, semantic, and knowledge-based verification is the right architecture. The calibration to risk thresholds makes it production-ready."

## Key Takeaways
- Use multiple detection layers: token, semantic, and factual
- Semantic entropy from multiple samples detects uncertainty
- Domain-specific knowledge graphs ground factual claims
- Calibrate thresholds on domain data for precision-recall trade-off
- Block high-risk responses, flag medium-risk for human review
