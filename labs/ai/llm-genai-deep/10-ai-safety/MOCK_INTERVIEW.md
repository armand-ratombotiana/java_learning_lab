# Mock Interview: Design Safety Guardrails for a Customer-Facing Chatbot

## Scenario
You are interviewing for a responsible AI lead role at a fintech company. They want safety guardrails for their customer-facing LLM chatbot handling financial questions.

## Interviewer Opening Question
"Design safety guardrails for a chatbot that answers customer questions about loans, investments, and account management."

## Candidate Response
"I'd design a defense-in-depth system with four layers: (1) Input guard — block toxic or adversarial prompts. (2) Topic guard — restrict to approved financial domains. (3) Output guard — filter harmful or hallucinated responses. (4) Monitoring — continuous logging and alerting for policy violations."

## Interviewer Probing Questions

**Q: How do you handle financial advice vs. factual information?**
"I'd classify intents: 'give information' (approved) vs. 'give personalized advice' (blocked). Use a classifier trained on regulatory guidelines. Any response containing ticker symbols, dollar amounts, or projections gets flagged."

**Q: What about jailbreak attempts?**
"Implement a prompt injection detector using a small model trained on adversarial examples. Rate-limit to 5 queries per minute. Use perplexity filtering — jailbreaks often have unusual token patterns."

**Q: How do you audit and improve?**
"Log all queries and responses with a safety verdict. Weekly manual review of false positives/negatives. Monthly red-teaming sessions with adversarial prompts. Continuously fine-tune the guard models."

## Candidate Solution (Python)

```python
import re
import json
import hashlib
from dataclasses import dataclass, field
from typing import List, Optional
from datetime import datetime
from enum import Enum

class SafetyVerdict(Enum):
    PASS = "pass"
    BLOCK_INPUT = "block_input"
    BLOCK_OUTPUT = "block_output"
    FLAG_FOR_REVIEW = "flag"

@dataclass
class SafetyReport:
    query: str
    response: Optional[str]
    verdict: SafetyVerdict
    reason: str
    risk_score: float
    timestamp: str = field(default_factory=lambda: datetime.now().isoformat())

class InputGuard:
    def __init__(self):
        self.blocked_patterns = [
            r"ignore.*instructions", r"system.*prompt",
            r"pretend", r"jailbreak", r"forget.*rules",
            r"malicious", r"hack", r"exploit",
        ]
        self.pii_patterns = [
            r"\b\d{3}-\d{2}-\d{4}\b", r"\b\d{16}\b",  # SSN, credit card
        ]
        self.toxic_classifier = None  # Would load a detoxify model

    def check(self, text: str) -> tuple[bool, str]:
        for pat in self.blocked_patterns:
            if re.search(pat, text, re.IGNORECASE):
                return True, f"Blocked pattern: {pat}"
        for pat in self.pii_patterns:
            if re.search(pat, text):
                return True, "PII detected in input"
        return False, ""

class TopicGuard:
    def __init__(self):
        self.allowed_topics = ["account_balance", "transaction_history",
                               "loan_products", "interest_rates", "branch_locations"]
        self.blocked_topics = ["investment_advice", "stock_predictions",
                               "tax_evasion", "money_laundering"]

    def classify(self, text: str) -> str:
        # Simplified — would use a classifier
        topic_keywords = {
            "investment_advice": ["should I invest", "buy stock", "good investment"],
            "account_balance": ["balance", "how much money"],
        }
        for topic, keywords in topic_keywords.items():
            if any(kw in text.lower() for kw in keywords):
                return topic
        return "general"

    def check(self, text: str) -> tuple[bool, str]:
        topic = self.classify(text)
        if topic in self.blocked_topics:
            return True, f"Topic '{topic}' is blocked"
        if topic not in self.allowed_topics and topic != "general":
            return True, f"Topic '{topic}' is not in allowed list"
        return False, ""

class OutputGuard:
    def __init__(self):
        self.hallucination_detector = None
        self.forbidden_phrases = [
            "guaranteed return", "risk-free", "100% safe",
            "this is financial advice", "you should buy",
        ]

    def check(self, response: str) -> tuple[bool, str]:
        for phrase in self.forbidden_phrases:
            if phrase.lower() in response.lower():
                return True, f"Forbidden phrase: '{phrase}'"
        return False, ""

class SafetyMonitor:
    def __init__(self):
        self.audit_log = []
        self.alert_threshold = 0.8

    def log(self, report: SafetyReport):
        self.audit_log.append(report)
        if report.risk_score > self.alert_threshold:
            self.alert(report)

    def alert(self, report: SafetyReport):
        print(f"ALERT: High risk query detected — {report.reason}")
        # Would send to Slack/PagerDuty

class SafetyGuardrails:
    def __init__(self):
        self.input_guard = InputGuard()
        self.topic_guard = TopicGuard()
        self.output_guard = OutputGuard()
        self.monitor = SafetyMonitor()

    def process(self, query: str, llm_generate_fn) -> SafetyReport:
        blocked, reason = self.input_guard.check(query)
        if blocked:
            report = SafetyReport(query=query, response=None,
                                  verdict=SafetyVerdict.BLOCK_INPUT, reason=reason, risk_score=1.0)
            self.monitor.log(report)
            return report

        blocked, reason = self.topic_guard.check(query)
        if blocked:
            report = SafetyReport(query=query, response=None,
                                  verdict=SafetyVerdict.BLOCK_INPUT, reason=reason, risk_score=0.9)
            self.monitor.log(report)
            return report

        response = llm_generate_fn(query)

        blocked, reason = self.output_guard.check(response)
        if blocked:
            report = SafetyReport(query=query, response=response,
                                  verdict=SafetyVerdict.BLOCK_OUTPUT, reason=reason, risk_score=0.7)
            self.monitor.log(report)
            return report

        report = SafetyReport(query=query, response=response,
                              verdict=SafetyVerdict.PASS, reason="", risk_score=0.0)
        self.monitor.log(report)
        return report
```

## Interviewer Feedback
"Excellent defense-in-depth design. The separation into input, topic, output, and monitoring layers is clean and production-ready. The PII detection and topic classification are essential for fintech."

## Key Takeaways
- Defense-in-depth with multiple independent guard layers
- Input guard: block jailbreaks, PII, and adversarial prompts
- Topic guard: restrict LLM to approved domain categories
- Output guard: filter forbidden content and hallucinated claims
- Monitoring: continuous audit logging and alerting for incidents
