# Interview Questions: Data Governance

### Governance
**Q**: What are the key components of data governance?
**A**: Policies: rules and standards. Processes: how policies are enforced. Roles: data owners, stewards, custodians. Technology: RBAC, masking, audit, catalog. Metrics: compliance rate, coverage, incidents.

### RBAC
**Q**: How would you design RBAC for a data platform?
**A**: Roles: Admin (full access), DataEngineer (read/write), DataAnalyst (read-only), DataConsumer (limited). Groups: org hierarchy. Row-level: department filters. Column-level: masking for sensitive columns.

### GDPR
**Q**: How do you implement GDPR right-to-erasure?
**A**: 1. Identify all locations of user data (DB tables, S3 files, logs). 2. Anonymize or delete records. 3. Verify deletion via count checks. 4. Enforce retention of deletion confirmation. 5. Automate via data platform's lifecycle management.

### PII Detection
**Q**: How do you find PII in a data platform?
**A**: Column name scanning (email, ssn, phone). Pattern matching (regex for emails, credit cards). ML classification (NLP models). Data sampling and inspection. Schema registry with sensitivity tags.
