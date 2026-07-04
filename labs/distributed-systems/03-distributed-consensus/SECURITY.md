# Distributed Consensus: Security

## Threat Model
- **Byzantine faults**: Malicious nodes may send conflicting messages
- **Eavesdropping**: Network-level observation of consensus data
- **Tampering**: Modification of log entries in transit

## Security Mechanisms
1. **TLS/mTLS**: Encrypt consensus traffic, authenticate peers
2. **Signed log entries**: Cryptographically verify entry authenticity
3. **Audit trails**: Log all consensus decisions
4. **Access control**: Restrict access to consensus groups

## Byzantine Fault Tolerance
```java
public class PBFTNode {
    // Requires 3f+1 nodes to tolerate f Byzantine faults
    public boolean verifyAndCommit(List<byte[]> messages) {
        int validSigs = 0;
        for (byte[] msg : messages) {
            if (verifySignature(msg)) validSigs++;
        }
        return validSigs >= 2 * f + 1; // Commit with 2f+1 matching
    }
}
```
