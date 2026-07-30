# Interview Questions — Cloud Cost Optimization

## Beginner

Q: What is the difference between reserved and spot instances?
A: Reserved instances offer discounted rates for committing 1-3 years; spot instances use spare capacity at variable discount rates.

Q: What is rightsizing?
A: Rightsizing matches instance capacity to workload requirements to eliminate waste.

## Intermediate

Q: How would you design a cost allocation system for a multi-team organization?
A: Mandatory resource tagging strategy, tag propagation pipeline, cost and usage reports filtered by tags, chargeback/showback dashboards.

Q: What FinOps phases exist?
A: Inform (visibility), Optimize (efficiency), Operate (continuous improvement).

## Advanced

Q: Design a spot instance strategy for a fault-tolerant batch processing workload.
A: Spot instance diversification across instance types and availability zones, checkpointing for interruptions, fallback to on-demand, queue-based workload management.

Q: How do you model total cost of ownership (TCO) across multiple clouds?
A: Compute costs (instance hours), storage costs (GB-month, IOPS), data transfer (egress fees), managed service premiums, support plans.
