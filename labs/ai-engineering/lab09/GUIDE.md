# AI Security — Deep Dive Guide

## Threat Model for AI Systems

| Threat | Impact | Attack Vector |
|--------|--------|---------------|
| Prompt Injection | Unauthorized behavior | Crafted user prompts |
| Data Leakage | Sensitive data exposure | PII in prompts/outputs |
| Model Theft | IP loss | Extraction queries |
| Denial of Service | Cost spikes | High-volume requests |
| Supply Chain | Compromised model | Malicious fine-tuning |

## Code Walkthrough: PromptInjectionDetector

The detector identifies known jailbreak patterns:

- Pattern list includes: "ignore all previous instructions", "system prompt", "act as a", "override", etc.
- `isInjected(prompt)`: Returns true if any pattern matches (case-insensitive)
- `sanitize(prompt)`: Replaces dangerous phrases with `[BLOCKED]`

Defense layers:
1. **Input detection**: Block known injection patterns
2. **Input sanitization**: Neutralize dangerous content
3. **Output validation**: Check model responses for sensitive data

## Data Leakage Prevention

The `DataLeakageDetector` uses regex patterns to find:

- **SSN**: `\d{3}-\d{2}-\d{4}`
- **Credit cards**: 16 consecutive digits
- **Emails**: Standard email regex
- **Credentials**: `api_key:...`, `secret=...`, etc.

The `redact()` method replaces matches with placeholder text while preserving the rest of the content.

## Role-Based Access Control

The `AccessControl` system implements:

- Three roles: `ADMIN` (full access), `ENGINEER` (read/write), `READER` (read-only)
- `registerUser(username, role)`: Assigns roles to users
- `checkAccess()`: Returns boolean for authorization decisions
- `enforceAccess()`: Throws `SecurityException` on denial

## Audit Logging

The `AuditLogger` demonstrates tamper-evident logging:

- Each entry contains: user, action, resource, success/failure, details, timestamp
- A hash chain links entries sequentially (blockchain-like integrity)
- `verifyIntegrity()` recomputes hashes to detect tampering
- `printRecent()` displays the most recent entries

## Production Considerations

- Use a dedicated LLM guardrail service (e.g., NVIDIA NeMo Guardrails)
- Implement rate limiting per user/IP to prevent abuse
- Encrypt sensitive data at rest and in transit
- Regular security audits of model inputs and outputs
- Keep a deny list and an allow list for prompt patterns