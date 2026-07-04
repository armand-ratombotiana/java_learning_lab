# Why AWS Networking Matters

## Business Impact
- **Global reach**: Route 53 serves DNS from 200+ edge locations
- **Performance**: CloudFront reduces latency by 60%+ for global users
- **Security**: Shield + WAF at edge stops DDoS before hitting origin
- **Hybrid connectivity**: Direct Connect provides consistent 10-100 Gbps

## Technical Impact
- **Private connectivity**: VPC Peering + Transit Gateway for service mesh
- **Traffic management**: Route 53 routing policies (latency, geolocation, weighted)
- **Load balancing**: ALB (HTTP/HTTPS) and NLB (TCP/UDP) for different Java app patterns
- **Network isolation**: Subnets, SGs, NACLs for defense-in-depth

## For Java Developers
- CloudFront + S3 = fast static content delivery
- ALB + ECS/Fargate = seamless container load balancing
- Route 53 + health checks = zero-downtime deployments
- VPC endpoints = private access to DynamoDB/S3 (no internet)
