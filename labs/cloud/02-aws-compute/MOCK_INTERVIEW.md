# Mock Interview — AWS Compute

## Format
- **Duration**: 45 minutes
- **Type**: Technical + System Design
- **Difficulty**: Associate/Professional

## Warm-Up (5 min)

Q1: Compare EC2, ECS, EKS, and Lambda. When would you choose each?

Q2: What is a Fargate launch type and how does it differ from EC2 launch type for ECS?

## Technical Questions (20 min)

### Question 1: ECS vs EKS Decision (10 min)
Your company is containerizing a Java microservices application (12 services). The team has Docker experience but no Kubernetes expertise. You expect moderate traffic (1000 req/s) and need to deploy 3x/day.

**Question**: Should you use ECS or EKS? Justify your decision with trade-offs. Design the deployment pipeline.

### Question 2: Lambda Optimization (10 min)
A real-time image processing Lambda function takes 8 seconds to process a 5MB image. It runs 100K times/day. The current timeout is 15 seconds and memory is 1024 MB.

**Question**: How would you optimize this function for cost and performance? Consider Lambda Power Tuning, provisioned concurrency, and whether Lambda is the right compute choice.

## Behavioral Question (10 min)

**Question**: Tell me about a time you containerized a monolithic application or migrated from EC2 to containers. What challenges did you face?

## System Design Whiteboard (10 min)

**Problem**: Design a video transcoding pipeline that:
- Accepts uploads (up to 1GB) via HTTP
- Transcodes to 3 resolutions (360p, 720p, 1080p)
- Stores results in S3
- Notifies user upon completion
- Costs less than $100/month at current volume (1000 videos/day)

**Choose the right compute**: Lambda, Batch, ECS, or Step Functions?

## Evaluation Criteria

| Area | Excellent | Good | Needs Improvement |
|------|-----------|------|-------------------|
| ECS/EKS | Deep comparison, operational experience | Basic differences | Can't articulate trade-offs |
| Lambda | Concurrency, throttling, cold starts, tuning | Basic understanding | No optimization awareness |
| Design | Considers cost, performance, operations | Reasonable architecture | Missing key constraints |
| Trade-offs | Articulates pros/cons clearly | Mentions some trade-offs | Single-minded approach |

## Sample Solution Outline

### ECS vs EKS Decision
- Go with ECS if: No K8s expertise, want simpler operations, tight AWS integration
- Go with EKS if: Need multi-cloud portability, complex networking, future-proofing
- For this case: ECS on Fargate (no cluster management, simpler deployment)

### Lambda Optimization
- Use AWS Lambda Power Tuning tool to find optimal memory (likely 2048-3072 MB)
- Consider: For 5MB images, transform might be better on ECS Fargate (no 15-min limit)
- Implement reserving concurrency to avoid throttling
- Consider S3 batch operations for large volumes

### Video Transcoding Pipeline
- S3 upload triggers Lambda (or S3 Event Notification)
- Lambda submits job to AWS Elemental MediaConvert or Elastic Transcoder
- Use SQS for job queue to handle spikes
- SNS / SES for user notification upon completion
- Step Functions for workflow orchestration if complex logic needed
