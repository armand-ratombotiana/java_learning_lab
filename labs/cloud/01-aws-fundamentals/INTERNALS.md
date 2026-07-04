# AWS Fundamentals — Internals

## EC2 Internals

### Nitro Hypervisor
- **Pre-2017**: Xen hypervisor (paravirtualization)
- **2017+**: AWS Nitro — custom-built hypervisor
  - Dedicated hardware for networking (ENA)
  - Dedicated hardware for storage (NVMe)
  - SR-IOV for near-bare-metal performance
  - No host-OS overhead — each function offloaded to hardware

### Instance Metadata Service (IMDS)
- Link-local address: `http://169.254.169.254/latest/meta-data/`
- Returns instance ID, AMI ID, region, security groups, IAM role credentials
- IMDSv2 requires session-based token (TOKEN header), mitigates SSRF attacks

### EBS Architecture
- Each EBS volume is replicated within an AZ (not across AZs)
- Snapshots go to S3 (incremental, compressed)
- gp3: 3000 IOPS baseline, 125 MB/s throughput, no provisioned IOPS needed
- io2 Block Express: 256K IOPS, 4000 MB/s, 99.999% durability

## S3 Internals

### Object Storage Hierarchy
```
AWS Account
  └── Bucket (global namespace, regional storage)
       └── Object (key, version ID, metadata, data)
            ├── Standard → 3+ AZs
            ├── Standard-IA → 3+ AZs (lower access cost)
            ├── One Zone-IA → 1 AZ
            └── Glacier → 3+ AZs (minutes/hours retrieval)
```

### PUT Path
1. Request reaches S3 regional endpoint
2. Front-end parses and authenticates request
3. Front-end assigns object to a partition based on key hash
4. Partition stores data across 3 devices (or 1 for One Zone-IA)
5. ACK returned to client after all replicas are written
6. Metadata stored in S3 metadata DB (internal NoSQL)

## IAM Internals

### Policy Engine Flow
1. Parse request into principal, action, resource, conditions
2. Load all applicable policies (max 20 per principal + resource)
3. Build policy context (ordered set of statements)
4. Process deny statements first (short-circuit)
5. Process allow statements (match action/resource/condition)
6. If no allow matched → implicit deny
7. Cache decision for TTL (typically 15 minutes via STS)

### Temporary Credentials
- STS generates access key + secret key + session token
- Token includes expiration (default 1h, max 36h for roles)
- Signed with AWS internal CA chain

## VPC Internals

### Packet Flow
```
EC2 → ENI → VPC Switch → Security Group (stateful) → Route Table → ...
```
- **ENA**: Elastic Network Adapter — up to 100 Gbps
- **VPC Switch**: Distributed virtual switch (AWS custom software)
- **Security Group**: Stateful — return traffic auto-allowed
- **NACL**: Stateless — must allow both directions explicitly
- **Route Table**: Longest prefix match routing

### Hyperplane (NAT Gateway / Load Balancer)
- AWS internally uses "Hyperplane" — a distributed data plane
- NAT Gateway, ALB, NLB, VPC endpoints all run on Hyperplane
- Horizontal scaling: more traffic = more Hyperplane workers
