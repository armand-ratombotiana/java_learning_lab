# Performance — AWS Networking

## CloudFront Performance

### Cache Hit Ratio Optimization
```
Current cache hit ratio: 65%
Optimizations:
  1. Set Cache-Control: max-age=86400 (1 day) for static assets
  2. Use versioned URLs: /static/app.abc123.js (never expires)
  3. Enable default TTL: 86400 seconds
  4. Enable Forwarded-Values only when needed (cookies, headers)

Target: 90%+ cache hit ratio
Result: 90% of requests served from edge (10-50ms vs 200ms+ origin)
```

### Compression
```java
// Enable compression in CloudFront
// Console: CloudFront → Distribution → Behavior → Compress Objects Automatically → Yes

// Ensure Java app sets proper content types
response.setContentType("text/css");
response.setHeader("Content-Encoding", "gzip");

// OR: Apache/Tomcat gzip configuration
// server.xml: <Connector compression="on" compressionMinSize="2048"/>
```

## ALB Performance

### Target Group Capacity Planning
```
Target group metrics:
  - RequestCountPerTarget: target < 5000 req/s (general rule)
  - TargetResponseTime: p99 < 500ms
  - HealthyHostCount: should equal desired capacity

ALB itself: 10M requests/sec (theoretical max)
Real limit: ~50K req/s per AZ (depends on rules complexity)
```

### NLB vs ALB Decision
```
Protocol         │  ALB  │  NLB
─────────────────┼───────┼──────
HTTP/HTTPS       │  ✓    │  TCP passthrough
gRPC             │  ✓    │  ✗
WebSocket        │  ✓    │  ✗
TCP/UDP          │  ✗    │  ✓
Static IP        │  ✗    │  ✓
PrivateLink      │  ✗    │  ✓
Latency          │ ~1ms  │  ~0.1ms
```

## Route 53 Performance

### Routing Policy for Latency
```powershell
aws route53 create-health-check --caller-reference ref1 `
    --health-check-config Type=HTTPS,ResourcePath=/health,`
    FullyQualifiedDomainName=api-us.example.com

aws route53 change-resource-record-sets --hosted-zone-id ZONE123 `
    --change-batch '{
      "Changes": [{
        "Action": "CREATE",
        "ResourceRecordSet": {
          "Name": "api.example.com",
          "Type": "A",
          "SetIdentifier": "US",
          "Region": "us-east-1",
          "LatencyRouting": true
        }
      }]
    }'
```
- Users automatically routed to closest healthy region
- Improves global latency by 40-60%

## Direct Connect Performance

### Bandwidth Planning
```
Application bandwidth needs:
  100 concurrent users × 1 Mbps = 100 Mbps baseline
  Peak: 3x baseline = 300 Mbps

  1 Gbps DX connection: comfortable for 300 users
  Latency: consistent 5-15ms (vs 20-50ms over VPN)
  Jitter: <1ms (vs 5-10ms over internet)
```
