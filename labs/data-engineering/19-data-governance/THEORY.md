# Data Governance Theory

## Governance Framework Components
Policies: data classification, access control, retention, quality, privacy. Processes: approval workflows, incident response, compliance reviews. Roles: data owner (accountable), data steward (day-to-day management), data custodian (technical implementation), data consumer (user with access). Technology: access control, masking, audit, catalog.

## RBAC Design Patterns
Flat RBAC: user -> roles -> permissions (simple). Hierarchical RBAC: roles inherit from parent roles (organization hierarchy). Constrained RBAC: separation of duties (can't be in conflicting roles). Row-level: filter rows based on user attributes. Column-level: mask columns based on role sensitivity.

## PII Detection Methods
Pattern-based: regex for emails, phones, SSN, credit cards. Column name: keywords in column names (email, ssn, password). Sampling: read data samples and classify values. ML: NLP models trained on labeled PII data. Third-party APIs: specialized PII detection services.

## Data Masking Types
Static masking: permanently modify data in staging environments. Dynamic masking: obfuscate on-the-fly during query (user's role determines view). Deterministic: same input produces same masked output (for joins). Format-preserving: masked values look like original format. Encryption: reversible masking for authorized re-identification.
