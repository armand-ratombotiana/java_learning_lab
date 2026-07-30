# Lab 09: AI Security — Interview Q&A

## FAANG-Level Questions

### Q1: How would you architect a defense system against prompt injection?

**A:** Multi-layer defense: (1) input classifier — ML model trained to detect injection attempts; (2) pattern matching — known injection signatures; (3) structural separation — use special delimiters between system prompt and user input; (4) output classifier — check model responses for policy violations; (5) least privilege — the model's tools have minimal necessary permissions.

### Q2: Design an access control system for a multi-tenant AI platform.

**A:** Use RBAC with three tiers: tenant admin (manage users, models, billing), model developer (train, deploy, monitor), inference consumer (call models for predictions). Each tenant's data and models are isolated. API keys scoped to specific roles. All access decisions logged to an immutable audit trail.

### Q3: How do you prevent sensitive data leakage through LLM outputs?

**A:** (1) Pre-scan inputs for PII and redact before sending to the model; (2) post-scan outputs for leaked sensitive data; (3) train/fine-tune models to refuse generating PII; (4) implement contextual recall detection — if the model reproduces a verbatim string from training data, flag it; (5) use differential privacy during training.

### Q4: What security considerations are unique to multi-agent systems?

**A:** Agents can be compromised through indirect prompt injection (malicious content in retrieved documents). Mitigations: (1) sandbox agent execution environments; (2) limit inter-agent communication to structured schemas only; (3) validate all tool outputs before they enter the reasoning loop; (4) implement human-in-the-loop for high-privilege actions.

### Q5: How would you build a tamper-proof audit log for AI decisions?

**A:** Use a hash chain where each entry includes the hash of the previous entry. Store the chain root hash in a trusted location (hardware security module or blockchain). Periodically verify integrity by recomputing hashes. For compliance, export the log in a format that can be cryptographically verified by external auditors.