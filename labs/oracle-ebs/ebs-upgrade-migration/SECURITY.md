# Security: EBS Upgrade and Migration

## 1. Responsibility Security

Oracle EBS uses role-based security. Responsibilities control which functions, forms, and data a user can access.

## 2. Function Security

Functions registered in FND_FORM_FUNCTIONS. Access via FND_COMPILED_FUNC menu assignments.

## 3. Data Security

GRANT/REVOKE on tables. FND_MOBS provides VPD for multi-org isolation.

## 4. Audit Controls

FND_AUD_ tables store audit data. Audit groups track schema changes.

## 5. Password Policies

APPS password via FNDCPASS. Password complexity via profile options. ICX sessions manage login.

## 6. Network Security

SSL/TLS for web traffic. Oracle Wallet for credentials. TDE for data at rest.

## 7. Secure Configuration

Profile options like FND_ENCRYPTED_COLUMNS. Separation of duties. Least privilege principle.
