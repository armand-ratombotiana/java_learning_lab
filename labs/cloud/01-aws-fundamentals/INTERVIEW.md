# Interview Questions — AWS Fundamentals

## Beginner

**Q1: What is the difference between a security group and a NACL?**

**Q2: Explain S3 storage classes and when to use each.**

**Q3: What is an IAM role and how is it different from an IAM user?**

**Q4: How does EC2 Auto Scaling work?**

## Intermediate

**Q5: Describe the AWS shared responsibility model.**

**Q6: What happens when you terminate an EC2 instance with an EBS root volume set to "Delete on Termination"?**

**Q7: How would you design a cost-effective architecture for a Java web app that needs to handle unpredictable traffic spikes?**

**Q8: Explain S3 consistency model. What guarantees does it provide for PUT, DELETE, and List operations?**

## Advanced

**Q9: How does AWS S3 achieve 99.999999999% durability?**

**Q10: Walk me through the IAM policy evaluation logic when a request arrives.**

**Q11: Your EC2 instance in a private subnet cannot reach the internet. Walk me through the troubleshooting steps.**

**Q12: How does the Nitro hypervisor differ from Xen, and what performance advantages does it provide?**

## System Design

**Q13: Design a multi-region active-active architecture for a Java application using AWS services.**

**Q14: How would you migrate a monolithic Java app running on a single EC2 instance to an auto-scaling architecture with zero downtime?**

## Sample Answers

**A1**: Security groups are stateful instance-level firewalls; NACLs are stateless subnet-level firewalls. Security groups evaluate all rules before allowing; NACLs process rules in order (first match wins).

**A2**: S3 Standard (frequent access), Standard-IA (infrequent, < once/month), One Zone-IA (recreatable data), Glacier (archival, minutes retrieval), Glacier Deep Archive (archival, hours retrieval), Intelligent-Tiering (auto-moves based on access patterns).

**A3**: A role is a temporary set of permissions assumed by an entity (user, service). Unlike a user (long-term credentials), roles use STS-generated temporary credentials with automatic rotation.

**A4**: Auto Scaling monitors CloudWatch metrics (CPU, memory, custom). When a threshold is breached, it launches/terminates instances from a launch template. It uses the ALB health check to determine instance health and replaces unhealthy instances.

**A5**: AWS secures the cloud (hardware, networking, hypervisor). Customers secure what's in the cloud (OS, applications, IAM, data encryption, network configuration). The split depends on the service — managed services (S3, RDS) shift more responsibility to AWS.

## Key Topics for AWS Practitioner Exam
- Shared responsibility model
- EC2 pricing models (On-Demand, Reserved, Spot, Savings Plans)
- S3 storage classes and lifecycle policies
- IAM policy structure (Effect, Action, Resource, Condition)
- VPC components (subnets, route tables, IGW, NAT, SG, NACL)
- AWS global infrastructure (regions, AZs, edge locations)
- Well-Architected Framework pillars
