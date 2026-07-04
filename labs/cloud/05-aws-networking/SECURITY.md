# Security — AWS Networking

## VPC Security

### VPC Flow Logs for Threat Detection
```powershell
# Capture all traffic metadata
aws ec2 create-flow-logs --resource-type VPC --resource-id vpc-xxx `
    --traffic-type ALL --log-group-name vpc-flow-logs `
    --deliver-logs-permission-arn arn:aws:iam::xxx:role/FlowLogsRole

# Query for suspicious traffic
aws logs filter-log-events --log-group-name vpc-flow-logs `
    --filter-pattern "[version, account, eni, src, dst, srcport, dstport, protocol, packets, bytes, start, end, action]"
    # Find REJECT records for unauthorized ports
```

### Security Group Best Practices
```
SG: web-sg
  Inbound: HTTP (80) from 0.0.0.0/0
  Inbound: HTTPS (443) from 0.0.0.0/0
  Inbound: SSH (22) from 10.0.0.0/8 (internal only)

SG: app-sg
  Inbound: HTTP (8080) from web-sg (only ALB can reach app)
  Outbound: HTTPS (443) to 0.0.0.0/0 (for API calls)

SG: db-sg
  Inbound: MySQL (3306) from app-sg (only app can reach DB)
  Outbound: None (no outbound needed)
```

### Network ACL Hardening
```
Inbound NACL (public subnet):
  Rule 100: HTTP (80)  from 0.0.0.0/0  ALLOW
  Rule 110: HTTPS (443) from 0.0.0.0/0  ALLOW
  Rule 200: Ephemeral (1024-65535) from 0.0.0.0/0 ALLOW
  Rule *:   All Traffic DENY

Outbound NACL (public subnet):
  Rule 100: HTTP (80) to 0.0.0.0/0 ALLOW
  Rule 110: HTTPS (443) to 0.0.0.0/0 ALLOW
  Rule 200: Ephemeral (1024-65535) to 0.0.0.0/0 ALLOW
  Rule *:   All Traffic DENY
```

## CloudFront Security

### Origin Access Control (OAC)
```powershell
# Restrict S3 bucket to CloudFront only
aws cloudfront create-origin-access-control --origin-access-control-config '{
  "Name": "s3-oac",
  "Description": "Restrict S3 to CloudFront",
  "SigningProtocol": "sigv4",
  "SigningBehavior": "always",
  "OriginAccessControlOriginType": "s3"
}'
```

### WAF Web ACL
```json
{
  "Name": "app-waf",
  "DefaultAction": {"Allow": {}},
  "Rules": [
    {"Name": "AWS-AWSManagedRulesCommonRuleSet", "Priority": 0,
     "Statement": {"ManagedRuleGroupStatement": {
       "VendorName": "AWS",
       "Name": "AWSManagedRulesCommonRuleSet"
     }}},
    {"Name": "rate-limit", "Priority": 1,
     "Statement": {"RateBasedStatement": {
       "Limit": 2000,
       "AggregateKeyType": "IP"
     }}}
  ]
}
```
WAF blocks: SQL injection, XSS, known bad IPs, rate limiting, bad bots.

## Route 53 Security

### DNSSEC Signing
```powershell
aws route53 enable-dnssec --hosted-zone-id ZONE123
```
Prevents DNS spoofing/cache poisoning. Verifies DNS responses are authentic.

### Domain Registration Lock
```
Transfer lock: prevent unauthorized domain transfers
Registrant verification: email verification for changes
WHOIS privacy: protect personal information (GDPR compliance)
```
