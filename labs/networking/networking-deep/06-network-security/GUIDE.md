# GUIDE — Network Security

## Step 1: Firewall Rule Engine
```java
public record FirewallRule(String id, String srcCidr, String dstCidr, int srcPort, int dstPort, String protocol, String action) {}
public record Packet(String srcIp, String dstIp, int srcPort, int dstPort, String protocol) {}
```

## Step 2: WAF Detection Engine
```java
public record WafRule(String id, String pattern, String category, String action) {}
WafEngine waf = new WafEngine();
waf.addRule(new WafRule("SQLI-001", ".*' OR '1'='1.*", "SQL Injection", "BLOCK"));
```

## Step 3: DDoS Mitigation
- SYN flood detection: monitor SYN rate per source IP
- Rate limiting with token bucket algorithm
- Traffic scrubbing via BGP blackhole or CDN

## Step 4: IPS/IDS Signature Matching
```java
IdsEngine ids = new IdsEngine();
ids.addSignature("ET-POLICY", ".*/etc/passwd.*");
ids.analyze(packet);
```

## Step 5: Network Segmentation
- Define security zones (public, private, management, DMZ)
- Enforce inter-zone traffic policies
- Bastion host for administrative access

## Step 6: Exercises
1. Implement a token bucket rate limiter for DDoS mitigation
2. Build a stateful firewall with connection tracking
3. Create a zero trust access policy evaluator
