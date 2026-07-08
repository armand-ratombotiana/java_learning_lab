# Visual Guide to Data Governance

```
RBAC Model:
[Admin] -> [Data Engineer] -> [Data Analyst] -> [Data Consumer]
    |             |                  |                |
[Full Access] [Read/Write]     [Read Only]      [Limited]
                                    |
                              [Column Masking]
                              email: ***@***.com
                              ssn: ***-**-1234

PII Detection Pipeline:
[Column Names] -> [Pattern Matching] -> [Value Sampling] -> [Classification]
                                                              |
                                                    [Confidence Score]
                                                    [Sensitivity Tag]

GDPR Workflow:
Subject Request -> Identity Verification -> Search Data Stores
                    -> Process Request (access/erase/port)
                    -> Confirm Completion -> Log for Audit
```
