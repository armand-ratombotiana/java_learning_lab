# Mock Interview: How Do You Evaluate RAG Systems?

## Scenario
You are interviewing for a ML evaluation lead role. The team needs a comprehensive RAG evaluation framework covering retrieval and generation quality.

## Interviewer Opening Question
"Build an evaluation framework for a RAG system. What metrics matter and how do you implement them?"

## Candidate Response
"I'd evaluate along two axes: retrieval quality and generation quality. For retrieval: recall@k, MRR, and NDCG. For generation: faithfulness, answer relevance, and context precision. I'd use both automated metrics and a small annotation set for calibration."

## Interviewer Probing Questions

**Q: How do you measure faithfulness without ground-truth?**
"I'd use NLI-based metrics: AlignScore or TrueTeacher. Also, token-level attribution via perplexity on the context vs. on the answer alone."

**Q: How do you handle cases where the correct answer isn't in any retrieved chunk?**
"That's a retrieval failure. I'd track the 'no-answer recall' rate and log a separate category for unanswerable queries."

**Q: What about end-to-end evaluation?**
"Use RAGAS — a framework with context relevancy, answer relevancy, and faithfulness. I'd also do human evaluation on a 500-example sample."

## Candidate Solution (Python)

```python
import numpy as np
from typing import List, Dict
from dataclasses import dataclass

@dataclass
class RAGSample:
    question: str
    contexts: List[str]
    answer: str
    ground_truth: str = None
    ground_truth_chunks: List[str] = None

class RetrievalMetrics:
    @staticmethod
    def recall_at_k(retrieved: List[str], relevant: List[str], k: int) -> float:
        retrieved_k = set(retrieved[:k])
        relevant_set = set(relevant)
        if not relevant_set:
            return 1.0
        return len(retrieved_k & relevant_set) / len(relevant_set)

    @staticmethod
    def mrr(retrieved: List[str], relevant: List[str]) -> float:
        for i, r in enumerate(retrieved):
            if r in relevant:
                return 1.0 / (i + 1)
        return 0.0

class GenerationMetrics:
    def __init__(self, nli_model):
        self.nli_model = nli_model

    def faithfulness(self, answer: str, contexts: List[str]) -> float:
        scores = []
        for sentence in self._split_sentences(answer):
            max_score = max(
                self.nli_model.predict(sentence, ctx) for ctx in contexts
            )
            scores.append(max_score)
        return np.mean(scores)

    def _split_sentences(self, text: str) -> List[str]:
        return text.replace("! ", ".").replace("? ", ".").split(". ")

class RAGEvaluator:
    def __init__(self, retrieval_metrics, generation_metrics):
        self.retrieval_metrics = retrieval_metrics
        self.generation_metrics = generation_metrics

    def evaluate(self, samples: List[RAGSample], k_values=[1, 3, 5, 10]):
        results = {"retrieval": {}, "generation": {}}
        for k in k_values:
            recalls = []
            for s in samples:
                if s.ground_truth_chunks:
                    recalls.append(
                        self.retrieval_metrics.recall_at_k(
                            s.contexts, s.ground_truth_chunks, k
                        )
                    )
            results["retrieval"][f"recall@{k}"] = np.mean(recalls) if recalls else 0.0
        faithfulness_scores = [
            self.generation_metrics.faithfulness(s.answer, s.contexts)
            for s in samples
        ]
        results["generation"]["faithfulness"] = np.mean(faithfulness_scores)
        return results

    def report_card(self, samples: List[RAGSample]):
        scores = self.evaluate(samples)
        print("=== RAG Evaluation Report ===")
        for category in scores:
            print(f"\n{category}:")
            for metric, value in scores[category].items():
                print(f"  {metric}: {value:.4f}")
        return scores
```

## Interviewer Feedback
"Comprehensive framework covering both retrieval and generation quality. The NLI-based faithfulness metric and the report card format are production-ready. Consider adding latency and cost metrics."

## Key Takeaways
- RAG evaluation must separate retrieval and generation quality
- Recall@k and MRR are standard retrieval metrics
- Faithfulness requires NLI-based verification
- Track edge cases: no-answer queries and context gaps
- Calibrate automated metrics with human annotation samples
