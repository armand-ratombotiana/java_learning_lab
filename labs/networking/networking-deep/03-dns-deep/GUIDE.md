# GUIDE — DNS Deep

## Step 1: DNS Message Model
```java
public record DnsMessage(int id, DnsHeader header, List<DnsQuestion> questions, List<DnsRecord> answers) {}
public record DnsQuestion(String name, short type, short qclass) {}
public record DnsRecord(String name, short type, short qclass, long ttl, String rdata) {}
```

## Step 2: Recursive Resolver
- Check cache, return if found
- Start at root servers, follow delegations
- Collect and cache NS records and glue records

## Step 3: DNSSEC Validation
```java
DnssecValidator validator = new DnssecValidator(trustAnchor);
boolean valid = validator.validate(record, rrsig, dnskey);
```

## Step 4: DNS over HTTPS Client
- Marshal DNS query to HTTP POST
- Send to DoH endpoint (e.g., Cloudflare 1.1.1.1)
- Parse response

## Step 5: Zone Transfer
- Primary server sends full zone via AXFR
- Incremental updates via IXFR
- NOTIFY triggers secondary check

## Step 6: Exercises
1. Implement an anycast-aware DNS resolver
2. Build a DNSSEC chain of trust validator
3. Create a zone transfer parser for SOA and NS records
