# 09 — AWS Security — Code Deep Dive

## 1. IAM Policy Management

### Creating a Managed Policy
```java
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.*;

IamClient iam = IamClient.create();

String policyDocument = """
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Action": ["s3:GetObject", "s3:ListBucket"],
    "Resource": [
      "arn:aws:s3:::my-secure-bucket",
      "arn:aws:s3:::my-secure-bucket/*"
    ],
    "Condition": {
      "IpAddress": {"aws:SourceIp": "10.0.0.0/8"}
    }
  }]
}
""";

CreatePolicyResponse response = iam.createPolicy(CreatePolicyRequest.builder()
    .policyName("S3SecureAccess")
    .policyDocument(policyDocument)
    .description("Restricted S3 access with IP condition")
    .build());

System.out.println("Policy ARN: " + response.policy().arn());
```

### Assuming a Cross-Account Role
```java
StsClient sts = StsClient.create();

AssumeRoleResponse roleResponse = sts.assumeRole(AssumeRoleRequest.builder()
    .roleArn("arn:aws:iam::123456789012:role/CrossAccountRole")
    .roleSessionName("JavaSession")
    .durationSeconds(3600)
    .build());

Credentials creds = roleResponse.credentials();
System.out.println("Access Key: " + creds.accessKeyId());
System.out.println("Expires: " + creds.expiration());
```

## 2. KMS Encryption Operations

### Encrypt and Decrypt with KMS
```java
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.*;
import java.util.Base64;

KmsClient kms = KmsClient.create();

String plaintext = "This is a sensitive secret!";
byte[] plaintextBytes = plaintext.getBytes();

// Encrypt
EncryptResponse encryptResponse = kms.encrypt(EncryptRequest.builder()
    .keyId("alias/my-key")
    .plaintext(SdkBytes.fromByteArray(plaintextBytes))
    .encryptionAlgorithm(EncryptionAlgorithmSpec.SYMMETRIC_DEFAULT)
    .build());
byte[] ciphertext = encryptResponse.ciphertextBlob().asByteArray();
System.out.println("Ciphertext (base64): " + Base64.getEncoder().encodeToString(ciphertext));

// Decrypt
DecryptResponse decryptResponse = kms.decrypt(DecryptRequest.builder()
    .ciphertextBlob(SdkBytes.fromByteArray(ciphertext))
    .keyId("alias/my-key")
    .encryptionAlgorithm(EncryptionAlgorithmSpec.SYMMETRIC_DEFAULT)
    .build());
String decrypted = new String(decryptResponse.plaintext().asByteArray());
System.out.println("Decrypted: " + decrypted);
```

### Generate Data Key (Envelope Encryption)
```java
GenerateDataKeyResponse dataKeyResponse = kms.generateDataKey(GenerateDataKeyRequest.builder()
    .keyId("alias/my-key")
    .keySpec(DataKeySpec.AES_256)
    .build());
byte[] dataKey = dataKeyResponse.plaintext().asByteArray();
byte[] encryptedDataKey = dataKeyResponse.ciphertextBlob().asByteArray();
// Use dataKey for client-side encryption, store encryptedDataKey with data
```

## 3. Security Group Simulation

### In-Memory Security Group Engine
```java
public record SecurityRule(String protocol, int fromPort, int toPort, String cidr) {}

public class SecurityGroup {
    private final String groupId;
    private final List<SecurityRule> ingressRules = new ArrayList<>();

    public SecurityGroup(String groupId) { this.groupId = groupId; }

    public void addIngressRule(String protocol, int fromPort, int toPort, String cidr) {
        ingressRules.add(new SecurityRule(protocol, fromPort, toPort, cidr));
    }

    public boolean isAllowed(String protocol, int port, String sourceIp) {
        return ingressRules.stream().anyMatch(rule ->
            rule.protocol().equalsIgnoreCase(protocol) &&
            port >= rule.fromPort() && port <= rule.toPort() &&
            ipInCidr(sourceIp, rule.cidr()));
    }

    private boolean ipInCidr(String ip, String cidr) {
        String[] parts = cidr.split("/");
        int prefix = Integer.parseInt(parts[1]);
        int mask = prefix == 0 ? 0 : 0xFFFFFFFF << (32 - prefix);
        long ipLong = ipToLong(ip);
        long cidrLong = ipToLong(parts[0]);
        return (ipLong & mask) == (cidrLong & mask);
    }

    private long ipToLong(String ip) {
        String[] octets = ip.split("\\.");
        return (Long.parseLong(octets[0]) << 24) |
               (Long.parseLong(octets[1]) << 16) |
               (Long.parseLong(octets[2]) << 8)  |
               Long.parseLong(octets[3]);
    }
}
```

## 4. WAF Rule Implementation

### Simulating WAF Rule Evaluation
```java
public record WafRule(String name, String condition, String action) {}

public class WafEngine {
    private final List<WafRule> rules = new ArrayList<>();

    public void addRule(WafRule rule) { rules.add(rule); }

    public String evaluate(HttpRequest request) {
        for (WafRule rule : rules) {
            if (matchesCondition(rule.condition(), request)) {
                return rule.action(); // ALLOW, BLOCK, COUNT
            }
        }
        return "ALLOW"; // default
    }

    private boolean matchesCondition(String condition, HttpRequest request) {
        return switch (condition) {
            case "XSS" -> detectXss(request.body());
            case "SQLi" -> detectSqli(request.body());
            case "RATE_LIMIT" -> isRateLimited(request.sourceIp());
            case "IP_BLOCK" -> isBlockedIp(request.sourceIp());
            default -> false;
        };
    }

    private boolean detectXss(String body) {
        return body != null && (
            body.contains("<script>") ||
            body.contains("onerror=") ||
            body.contains("javascript:"));
    }

    private boolean detectSqli(String body) {
        return body != null && (
            body.toLowerCase().contains("' or '1'='1") ||
            body.contains("-- ") ||
            body.contains("union select"));
    }
}
```

## 5. GuardDuty Finding Simulator

### Analyzing Threat Findings
```java
public record GuardDutyFinding(String findingId, String severity, String type, String resourceArn) {}

public class GuardDutySimulator {
    private final List<GuardDutyFinding> findings = new ArrayList<>();

    public void simulateFinding(String type, String resourceArn) {
        String severity = switch (type) {
            case "CryptoCurrency" -> "HIGH";
            case "PortProbe" -> "MEDIUM";
            case "Discovery" -> "LOW";
            default -> "INFO";
        };
        GuardDutyFinding finding = new GuardDutyFinding(
            UUID.randomUUID().toString(), severity, type, resourceArn);
        findings.add(finding);
        System.out.println("Finding: " + finding);
    }

    public List<GuardDutyFinding> getHighSeverityFindings() {
        return findings.stream()
            .filter(f -> f.severity().equals("HIGH"))
            .toList();
    }

    public void autoRemediate(GuardDutyFinding finding) {
        System.out.println("Auto-remediating: " + finding.findingId());
        // In production: invoke Lambda, update SG, isolate instance
    }
}
```
