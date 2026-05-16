# AWS Networking - Theory

## Overview
AWS provides comprehensive networking services for building secure, scalable cloud architectures.

## 1. Amazon Route 53

### What is Route 53?
- Scalable DNS web service
- Domain registration
- Health checking

### Record Types
- **A**: IPv4 address
- **AAAA**: IPv6 address
- **CNAME**: Canonical name (subdomain)
- **ALIAS**: AWS resource alias
- **MX**: Mail server
- **TXT**: Text records

### Routing Policies
- **Simple**: Single record
- **Weighted**: Traffic percentage
- **Latency**: Lowest latency
- **Geolocation**: Geographic location
- **Failover**: Primary/secondary
- **Multivalue**: Multiple healthy answers

### Health Checks
- Endpoint monitoring
- CloudWatch alarm integration
- Automatic failover

## 2. Amazon CloudFront

### What is CloudFront?
- Content Delivery Network (CDN)
- Global edge locations
- Low latency delivery

### Key Concepts
- **Distribution**: Content delivery configuration
- **Origin**: Source of content (S3, ALB, custom)
- **Cache Behavior**: Path-based routing rules
- **Signed URL**: Time-limited access
- **Signed Cookie**: Group-based access
- **Origin Shield**: Reduced origin load

### Features
- **Edge Functions**: Lambda@Edge for customization
- **Field-level Encryption**: Sensitive data protection
- **Real-time Metrics**: CloudWatch integration
- **Invalidation**: Remove cached content

## 3. Application Load Balancer (ALB)

### What is ALB?
- Layer 7 (HTTP/HTTPS) load balancer
- Content-based routing
- Container/microservices support

### Key Features
- **Listener**: Port/protocol configuration
- **Target Group**: Backend instance group
- **Target**: Individual instance/container
- **Rules**: Path/host-based routing
- **Sticky Sessions**: Session affinity

### Use Cases
- Microservices
- Container-based apps
- HTTP(S) applications

### Components
- **Listener Rules**: Request routing logic
- **Target Registration**: Instance health checks
- **SSL Termination**: HTTPS offload
- **Authentication**: OIDC/OAuth integration