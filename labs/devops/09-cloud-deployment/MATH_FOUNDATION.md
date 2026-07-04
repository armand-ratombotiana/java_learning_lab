# Math Foundation for Cloud Deployment

N/A — Cloud deployment is an operational and architectural topic with no significant mathematical foundation.

## Cost Optimization Math
- **Reserved instances**: 1yr (40% savings) vs 3yr (60% savings) vs On-demand.
- **Spot instances**: 60-90% discount; risk of interruption.
- **Compute pricing**: per vCPU-hour, per GB-hour, per request (serverless).

## Auto-Scaling Math
- **Target tracking**: Maintain metric at target value (similar to HPA).
- **Step scaling**: Add/remove capacity in steps based on alarm breach size.
- **Predictive scaling**: ML-based forecast of future traffic patterns.

## Multi-Region Distribution
- **Global load balancing**: Route to nearest region based on latency or geo.
- **Disaster recovery**: RPO (Recovery Point Objective) and RTO (Recovery Time Objective).
- **Active-active**: Traffic split across regions (50/50 or weighted).
- **Active-passive**: One region serves, other ready for failover.

## Network Math
- **CIDR calculation**: VPC and subnet IP range allocation.
- **NAT gateway pricing**: Per-hour + per-GB data processing fees.
- **Data transfer costs**: Ingress (free), egress (charged), cross-AZ (charged).
