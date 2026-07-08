# Data Governance Architecture

```
[Data Platform: Sources, Lake, Warehouse, Stream]
                    |
[Governance Layer]
    |         |          |           |
[RBAC]   [PII Detect] [Masking]  [Audit]
    |         |          |           |
[GDPR/Compliance Engine]
                    |
[Data Catalog + Metadata]
                    |
[Governance Dashboard]
```
