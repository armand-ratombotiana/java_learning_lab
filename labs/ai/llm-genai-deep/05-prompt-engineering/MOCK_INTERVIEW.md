# Mock Interview: Design a Prompt Optimization System for Enterprise LLM Apps

## Scenario
You are interviewing for a prompt engineering lead role at a SaaS company. They need a system that automatically optimizes prompts across hundreds of enterprise use cases.

## Interviewer Opening Question
"Design a system that automatically optimizes prompts for accuracy, cost, and latency across diverse enterprise LLM applications."

## Candidate Response
"I'd build a prompt optimization loop with three components: a prompt template registry, an optimizer using Monte Carlo search over prompt variants, and an evaluation harness. The system would use DSPy-style prompt programming with automated few-shot selection and instruction tuning."

## Interviewer Probing Questions

**Q: How do you handle prompt versioning and A/B testing?**
"Every prompt template is versioned in a registry with metadata: model, temperature, cost, latency p50/p95, accuracy. A/B tests use statistical significance (p < 0.05) before promotion."

**Q: How does the optimizer explore variants?**
"Using a mixture of LLM-generated rewrites and discrete mutations: paraphrasing instructions, reordering examples, adding chain-of-thought triggers, and adjusting format (XML, Markdown, JSON)."

**Q: What metrics do you track?**
"Primary: task accuracy. Secondary: cost per call, latency p50/p95, token usage, refusal rate, and output format compliance."

## Candidate Solution (Python)

```python
import json
import random
from dataclasses import dataclass, field
from typing import List, Callable, Optional
from datetime import datetime
import hashlib

@dataclass
class PromptTemplate:
    id: str
    instruction: str
    examples: List[dict]
    output_format: str
    model: str
    temperature: float = 0.0
    version: int = 1
    parent_id: Optional[str] = None

@dataclass
class EvaluationResult:
    prompt_id: str
    accuracy: float
    latency_p50: float
    latency_p95: float
    cost: float
    token_count: int
    timestamp: datetime = None

class PromptRegistry:
    def __init__(self):
        self.templates = {}
        self.results = {}
        self.live_version = {}

    def register(self, template: PromptTemplate):
        template.id = hashlib.md5(json.dumps({
            "instruction": template.instruction,
            "examples": template.examples,
            "format": template.output_format,
            "model": template.model,
            "temp": template.temperature
        }, sort_keys=True).encode()).hexdigest()[:12]
        self.templates[template.id] = template

    def promote(self, prompt_id: str, use_case: str):
        self.live_version[use_case] = prompt_id
        template = self.templates[prompt_id]
        template.version += 1

class PromptOptimizer:
    def __init__(self, registry: PromptRegistry, llm_client, eval_fn: Callable):
        self.registry = registry
        self.llm = llm_client
        self.eval_fn = eval_fn

    def _mutate(self, template: PromptTemplate) -> PromptTemplate:
        mutations = [
            lambda t: self._paraphrase_instruction(t),
            lambda t: self._reorder_examples(t),
            lambda t: self._add_cot(t),
            lambda t: self._change_format(t),
            lambda t: self._adjust_temperature(t),
        ]
        mutation = random.choice(mutations)
        new_t = mutation(template)
        new_t.parent_id = template.id
        return new_t

    def _paraphrase_instruction(self, t: PromptTemplate) -> PromptTemplate:
        resp = self.llm.generate(
            f"Paraphrase this instruction differently: '{t.instruction}'. Return only the paraphrased text."
        )
        t.instruction = resp.strip()
        return t

    def _add_cot(self, t: PromptTemplate) -> PromptTemplate:
        if "step by step" not in t.instruction.lower():
            t.instruction += " Think step by step."
        return t

    def _change_format(self, t: PromptTemplate) -> PromptTemplate:
        formats = ["json", "xml", "markdown"]
        current = t.output_format
        choices = [f for f in formats if f != current]
        t.output_format = random.choice(choices)
        return t

    def _adjust_temperature(self, t: PromptTemplate) -> PromptTemplate:
        t.temperature = round(random.uniform(0.0, 0.7), 1)
        return t

    def optimize(self, use_case: str, base_template: PromptTemplate, budget: int = 50):
        self.registry.register(base_template)
        best_id = base_template.id
        best_score = 0.0
        for step in range(budget):
            parent = self.registry.templates[best_id]
            candidate = self._mutate(PromptTemplate(
                instruction=parent.instruction,
                examples=parent.examples,
                output_format=parent.output_format,
                model=parent.model,
                temperature=parent.temperature
            ))
            self.registry.register(candidate)
            result = self.eval_fn(candidate)
            self.registry.results[candidate.id] = result
            if result.accuracy > best_score:
                best_score = result.accuracy
                best_id = candidate.id
            print(f"Step {step+1}: score={result.accuracy:.3f} best={best_score:.3f}")
        self.registry.promote(best_id, use_case)
        return self.registry.live_version[use_case]
```

## Interviewer Feedback
"Excellent — this is a production-grade design. The mutation strategies are practical, the registry handles versioning, and the optimization loop is grounded in empirical evaluation. DSPy-inspired approach is appropriate."

## Key Takeaways
- Treat prompts as versioned artifacts with A/B testing
- Mutation-based optimization explores instruction, format, and temperature
- Track accuracy, cost, latency, and token count simultaneously
- Statistical significance before promoting prompt changes
- DSPy-style programming provides a structured foundation
