# How AWS Networking Works

## Route 53 DNS Resolution

1. Client queries `www.example.com`
2. Recursive resolver asks root DNS → TLD → Route 53 nameserver
3. Route 53 returns ALB IP address (based on routing policy)
4. Client connects to ALB IP

```java
// Route 53 SDK — create A record pointing to ALB
ChangeResourceRecordSetsRequest request = ChangeResourceRecordSetsRequest.builder()
    .hostedZoneId("ZONE123")
    .changeBatch(ChangeBatch.builder()
        .changes(Change.builder()
            .action(ChangeAction.UPSERT)
            .resourceRecordSet(ResourceRecordSet.builder()
                .name("api.example.com")
                .type(RRType.A)
                .aliasTarget(AliasTarget.builder()
                    .dnsName("my-alb-xxx.elb.amazonaws.com")
                    .evaluateTargetHealth(true)
                    .hostedZoneId("ALBZONE123")
                    .build())
                .build())
            .build())
        .build())
    .build();
route53Client.changeResourceRecordSets(request);
```

## CloudFront Content Delivery

1. User requests `d123.cloudfront.net/image.jpg`
2. DNS resolves to nearest CloudFront edge location
3. Edge checks cache. HIT → return immediately. MISS → forward to origin.
4. Origin fetch: CloudFront requests `s3://my-bucket/image.jpg`
5. Response cached at edge (according to TTL/Cache-Control)
6. Edge returns response to user

## VPC Traffic Flow

```
Instance ──► ENI ──► VPC Switch ──► SG (stateful) ──► Route Table
  │                                                   │
  └── Local (10.0.0.0/16) → direct delivery           │
  └── Internet (0.0.0.0/0) → IGW / NAT GW            │
  └── Peered VPC → VPC Peering connection             │
  └── VPN/Direct Connect → VGW / TGW                 │
                                                      ▼
                                              NACL (stateless)
```

## Load Balancer Flow (ALB)

1. Listener receives request on port 80/443
2. Listener checks rules (host-based, path-based)
3. Forward to target group
4. ALB performs health check on target
5. Request forwarded to healthy target using least outstanding requests algorithm
6. X-Forwarded-For header preserves client IP

```java
// ALB target group registration
RegisterTargetsRequest request = RegisterTargetsRequest.builder()
    .targetGroupArn("arn:aws:elasticloadbalancing:us-east-1:xxx:targetgroup/my-tg/abc")
    .targets(
        TargetDescription.builder()
            .id("i-xxx").port(8080).build(),
        TargetDescription.builder()
            .id("i-yyy").port(8080).build()
    )
    .build();
elbv2Client.registerTargets(request);
```
