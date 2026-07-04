# Debugging — AWS Networking

## VPC Debugging

### Cannot Reach Internet from Private Subnet
```
Symptom: EC2 in private subnet can't ping 8.8.8.8
Checklist:
  1. NAT Gateway running? (aws ec2 describe-nat-gateways)
  2. Elastic IP attached to NAT?
  3. Private route table: 0.0.0.0/0 → nat-xxx?
  4. NACL allows outbound port 80/443 and ephemeral ports?
  5. Security group allows outbound traffic?

Test: curl -I https://aws.amazon.com
```

### Latency Between AZs
```
Symptom: Traffic between AZs is slow
Check:
  - aws ec2 describe-network-insights-analyses
  - VPC Flow Logs: check ACCEPT vs REJECT
  - Inter-AZ traffic: ~1ms vs same-AZ ~0.3ms
  - Consider placement groups for low latency

Fix: Keep related services in same AZ (data locality)
Cost: Inter-AZ data transfer = $0.01/GB each way
```

## Route 53 Debugging

### DNS Resolution Failure
```
Symptom: nslookup example.com returns NXDOMAIN
Check:
  - Hosted zone exists? aws route53 list-hosted-zones
  - NS records delegated correctly?
  - SOA record present?
  - Record exists for query?

Test:
  dig @ns-xxx.awsdns-xx.com example.com A
  nslookup -type=SOA example.com
```

## CloudFront Debugging

### Cache Not Invalidating
```
Symptom: Old content served after update
Check:
  - Cache-Control headers: max-age=3600 (1 hour)?
  - Did you create invalidation?
  - Invalidation status: Completed?

Fix:
  aws cloudfront create-invalidation --distribution-id xxx --paths "/*"
  Or: change object version in URL (image.jpg?v=2)
```

### 502/503 from CloudFront
```
Symptom: 502 Bad Gateway or 503 Service Unavailable
Check:
  - Origin (ALB/EC2): is it healthy?
  - Origin timeout: default 30s, sufficient?
  - Origin response too large? (max 30GB)
  - Origin SSL certificate valid?

Fix:
  - Increase origin response timeout (up to 120s for large files)
  - Check origin load — add auto scaling
  - Verify SSL certificate (not expired)
```

## ALB Debugging

### 503 from ALB
```
Symptom: ALB returns 503
Check:
  - Target group health: aws elbv2 describe-target-health
  - Healthy targets count > 0?
  - Security group allows traffic from ALB?

Fix:
  - Check target response on /health endpoint
  - Verify target can reach ALB (return traffic)
  - Check deregistration delay (inflight requests)
```

### Slow Start
```
Symptom: New targets get overwhelmed immediately
Check:
  - Slow start duration: 0 (disabled)?
  
Fix:
  - Enable slow start: 60-300 seconds
  - Targets warm up gradually (linear ramp)
```
