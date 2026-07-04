# Secrets Management Exercises

## Exercise 1: Deploy Vault
Run Vault in dev mode. Access the UI. Write and read a secret.

## Exercise 2: KV v2 Engine
Enable KV v2 secrets engine. Write multiple versions of a secret. Read specific versions.

## Exercise 3: Policies
Create a policy that allows read-only access to `secret/data/team/*`. Create a token with this policy. Verify access.

## Exercise 4: Dynamic DB Credentials (with PostgreSQL)
Enable database secrets engine. Configure PostgreSQL connection. Create a role. Request dynamic credentials.

## Exercise 5: Dynamic AWS Credentials (with AWS)
Enable AWS secrets engine. Configure AWS root credentials. Create a role with IAM policy. Generate dynamic AWS credentials.

## Exercise 6: PKI Engine
Enable PKI secrets engine. Generate root CA certificate. Create a role. Issue a certificate for a web service.

## Exercise 7: Kubernetes Auth
Enable Kubernetes auth method. Configure with service account. Create role and policy. Authenticate from a pod.

## Exercise 8: Vault Agent
Create a Vault Agent configuration for auto-auth and secret rendering. Run in Docker.

## Exercise 9: Audit Logging
Enable file audit device. Perform operations. Inspect the audit log.
