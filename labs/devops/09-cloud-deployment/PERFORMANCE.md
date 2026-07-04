# Cloud Deployment Performance

## Cloud Provider Performance Factors
- **Region proximity**: Closest region to users reduces latency.
- **Instance types**: Compute-optimized (C series), memory-optimized (R series), GPU (P series).
- **Storage performance**: io2 (provisioned IOPS) > gp3 (baseline) > gp2 (burst) > st1 (throughput).
- **Network**: Placement groups (cluster, spread, partition) for low-latency inter-instance communication.

## K8s Performance in Cloud
- **CNI**: AWS VPC CNI (native IP) > Calico > Flannel.
- **Storage CSI**: EBS CSI (block), EFS CSI (shared NFS), EBS snapshots for backup.
- **Load balancer**: NLB (layer 4, TCP/UDP) > ALB (layer 7, HTTP/gRPC) > Classic LB.

## Cost-Performance Optimization
- **Spot instances**: 60-90% cheaper; use for stateless, batch, fault-tolerant workloads.
- **Compute savings plans**: 1yr/3yr commit for 40-60% savings vs on-demand.
- **Auto-scaling**: Match capacity to demand; scale down to zero for non-production.
- **Data transfer**: Keep services in same AZ to avoid cross-AZ charges.
- **Storage tiering**: S3 Standard → S3 Infrequent Access → Glacier for archiving.

## Cloud-Native Performance Patterns
- **CDN**: CloudFront/Cloud CDN/Azure CDN for static and dynamic content caching.
- **Connection pooling**: RDS Proxy (AWS) for efficient database connection management.
- **Read replicas**: Offload read traffic to replicas; cache with ElastiCache/Redis.
- **Async processing**: SQS/Cloud Pub-Sub/Service Bus for decoupling and buffering.
