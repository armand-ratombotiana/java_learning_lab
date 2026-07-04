# Distributed Transactions: Security

## Security Concerns
- Transaction tampering
- Replay attacks on compensation actions
- Unauthorized transaction initiation
- Information leakage through transaction payloads

## Best Practices
1. **Authenticate all participants**: Mutual TLS between coordinator and participants
2. **Sign transaction payloads**: Prevent tampering
3. **Audit logging**: Log all transaction phases
4. **Authorization**: Validate permissions for each step
5. **Encrypt sensitive data**: Payment info, PII in transaction payloads

```java
public class SecureTransaction {
    public boolean prepare(Transaction tx, byte[] signature) {
        if (!verifySignature(tx.getBytes(), signature)) {
            throw new SecurityException("Invalid transaction signature");
        }
        return actualPrepare(tx);
    }
}
```
