# Common Mistakes with Data Governance

1. Over-Classification: Classifying all data as 'Confidential' dilutes focus; be specific about what needs protection
2. RBAC Sprawl: Creating too many roles creates management overhead; keep role hierarchy simple
3. Masking Without Testing: Masking breaks joins if not deterministic; test masking in downstream consumers
4. One-Time Compliance: Treating GDPR as one-time project; it requires ongoing process and monitoring
5. No Enforcement: Having policies without technical enforcement; policies must be implemented in code
