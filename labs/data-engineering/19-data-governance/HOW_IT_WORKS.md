# How Data Governance Works

1. Data classification policy defines sensitivity levels (Public, Internal, Confidential, Restricted)
2. RBAC maps user roles to permission levels with row and column granularity
3. PII scanner continuously scans for sensitive data using patterns and ML
4. Sensitive columns tagged in metadata catalog with classification
5. Dynamic masking engine intercepts queries and masks based on user role
6. Retention policies automatically expire data beyond lifecycle
7. Audit pipeline logs all data access and governance operations
8. GDPR workflows handle subject rights requests (access, erasure, portability)
9. Governance dashboard tracks compliance metrics and risk scores
