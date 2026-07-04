# Reflection — AWS Storage

## Self-Assessment

| Skill | Know | Explain | Teach |
|-------|:----:|:-------:|:-----:|
| S3 bucket creation and configuration | ☐ | ☐ | ☐ |
| S3 lifecycle policies | ☐ | ☐ | ☐ |
| EBS volume types and performance | ☐ | ☐ | ☐ |
| EBS snapshots and encryption | ☐ | ☐ | ☐ |
| EFS mount and configuration | ☐ | ☐ | ☐ |
| S3 presigned URLs | ☐ | ☐ | ☐ |
| S3 security best practices | ☐ | ☐ | ☐ |
| Cross-region replication | ☐ | ☐ | ☐ |
| Storage cost optimization | ☐ | ☐ | ☐ |

## Journal Prompts

1. How would you choose between EBS, EFS, and S3 for a new Java application?

2. What surprised you about S3's internal architecture (3+ AZ replication, partition routing)?

3. How does understanding storage classes change your approach to data lifecycle management?

4. If cost were no object, would you still use lifecycle policies? Why?

5. How would you design a disaster recovery plan using AWS storage services?

## Key Takeaways
- Three storage types serve three different needs: block (EBS), file (EFS), object (S3)
- S3's 11 9s durability is achieved through 3-AZ replication + background verification
- Lifecycle policies can reduce storage costs by 70-90%
- EBS is AZ-specific; snapshot to cross AZs
- EFS scales throughput with filesystem size (bursting) or provisioned capacity
- Always encrypt data at rest and in transit
