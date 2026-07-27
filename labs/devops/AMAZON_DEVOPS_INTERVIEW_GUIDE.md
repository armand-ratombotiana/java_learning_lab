# Amazon DevOps/SysDE Interview Guide

> Comprehensive prep guide for Amazon Systems Engineer (SysDE), DevOps Engineer, and SRE roles.

---

## 1. Role Overview

### Systems Engineer (SysDE)
- **Focus**: Infrastructure, automation, tooling.
- **Difference from SDE**: More ops-focused; less algorithms, more systems.
- **Levels**: L4 → L5 → L6 → L7 (Principal).
- **Expectation**: You build and maintain infrastructure at Amazon scale.

### DevOps Engineer
- **Focus**: CI/CD, IaC, containerization, monitoring.
- **Skills**: AWS, Terraform/CloudFormation, Docker/Kubernetes, Jenkins.
- **Expectation**: You own the deployment pipeline and infrastructure automation.

### SRE (AWS)
- **Focus**: Reliability of AWS services (DynamoDB, S3, EC2, Lambda).
- **Difference**: Deeper focus on reliability engineering, incident response, capacity planning.
- **Expectation**: You keep AWS services operational.

---

## 2. Interview Process

```
Application → Online Assessment (optional) → Phone Screen (60 min) 
→ Onsite (5 rounds, 60 min each) → Bar Raiser → Offer
```

### Online Assessment (for SDE/SysDE roles)
- **Length**: 90-120 minutes.
- **Content**: 2 coding problems (Easy-Medium). Work style assessment (behavioral survey).
- **Tip**: Practice LeetCode Easy-Medium. Focus on arrays, strings, hash maps.

### Phone Screen
- **Length**: 60 minutes.
- **Content**: 1 LeetCode Easy-Medium coding problem + 2-3 behavioral questions.
- **Example coding**: "Two sum," "Valid parentheses," "Reverse a linked list."
- **Example behavioral**: "Tell me about a time you went above and beyond."
- **Tip**: Every behavioral answer MUST use STAR format.

### Onsite Rounds

#### Round 1: Coding (60 min)
- **Difficulty**: LeetCode Easy-Medium. No Hard.
- **Topics**: Arrays, strings, linked lists, stacks, queues, trees.
- **Expectation**: Working code. Edge cases. Walk through after writing.
- **Language**: Java, Python, C++, C#.

#### Round 2: Coding (60 min)
- **Same difficulty**. May involve object-oriented design.
- **Example**: "Design a parking lot." "Design an elevator system."
- **Expectation**: Clean class hierarchy. Inheritance, composition, polymorphism.

#### Round 3: System Design (60 min)
- **Topics**: Design Amazon S3, DynamoDB, Prime Video, shopping cart, URL shortener.
- **Key areas**: Scalability, availability, fault tolerance, cost.
- **AWS focus**: EC2, S3, VPC, Lambda, ELB, Auto Scaling, DynamoDB.
- **Tip**: Connect design decisions to Leadership Principles (Customer Obsession, Frugality).

#### Round 4: Behavioral (60 min) — LP Deep Dive
- **5+ STAR stories**. Must cover distinct Leadership Principles.
- **Common questions**:
  - "Tell me about a time you took a calculated risk." (Bias for Action)
  - "Tell me about a time you had to dive deep into a problem." (Dive Deep)
  - "Tell me about a time you were customer-obsessed." (Customer Obsession)
  - "Tell me about a time you invented and simplified." (Invent and Simplify)
- **Tip**: Have 2 stories ready for each LP. Explicitly name the LP in your answer.

#### Round 5: Bar Raiser (60 min)
- **Independent interviewer** — not from the hiring team.
- **Mix**: 1 coding problem + 2-3 behavioral questions + 1 system design (optional).
- **Purpose**: Ensure the candidate meets Amazon's long-term bar.
- **Tip**: The Bar Raiser can veto an offer even with positive feedback from others.

---

## 3. Amazon Leadership Principles (16)

| LP | Prep Story Idea |
|----|-----------------|
| Customer Obsession | Went above and beyond for an internal/external customer |
| Ownership | Took end-to-end ownership of a project or incident |
| Invent and Simplify | Automated a manual process, reduced complexity |
| Are Right, A Lot | Made a data-driven decision that was initially unpopular |
| Learn and Be Curious | Learned a new technology for a project |
| Hire and Develop the Best | Mentored a junior engineer to success |
| Insist on the Highest Standards | Refused to ship until quality bar was met |
| Think Big | Proposed a multi-region architecture |
| Bias for Action | Rolled back a bad deployment without waiting for approval |
| Frugality | Right-sized infrastructure, reduced cost |
| Earn Trust | Was transparent about an outage in the postmortem |
| Dive Deep | Debugged a memory leak down to the kernel level |
| Have Backbone; Disagree and Commit | Disagreed with a decision but committed after |
| Deliver Results | Completed a project ahead of schedule |
| Strive to be Earth's Best Employer | Created inclusive on-call rotation |
| Success and Scale Bring Broad Responsibility | Implemented security best practices |

---

## 4. Key Technical Areas

### AWS
| Service | Depth | Example Question |
|---------|-------|------------------|
| EC2 | Expert | "How do you right-size EC2 instances?" |
| S3 | Expert | "How does S3 achieve 11 9s of durability?" |
| VPC | Expert | "Design a VPC with public/private subnets, NAT gateway, VPN." |
| IAM | Expert | "What's the principle of least privilege? Design IAM roles for EC2 + S3." |
| Lambda | Deep | "How does Lambda scale? What's the cold start problem?" |
| ECS/EKS | Deep | "Compare ECS vs EKS for container orchestration." |
| RDS/Aurora | Deep | "How do you set up RDS for HA with read replicas?" |
| DynamoDB | Deep | "Design a DynamoDB table for a high-traffic e-commerce app." |
| ElastiCache | Deep | "When would you use Redis vs Memcached?" |
| CloudFormation/CDK | Deep | "Compare CloudFormation vs Terraform. How does CDK differ?" |
| Route53 | Moderate | "How does Route53 latency-based routing work?" |
| CloudFront | Moderate | "How would you use CloudFront with S3 for a global app?" |

### Linux
| Topic | Depth | Example |
|-------|-------|---------|
| Shell scripting | Expert | "Write a script to parse logs and extract error rates." |
| Process management | Deep | "How do you find and kill a zombie process?" |
| Networking | Deep | "How do you check open ports and connections?" |
| Performance | Deep | "How do you identify disk I/O bottlenecks?" |
| Systemd | Moderate | "How do you create a systemd service that restarts on failure?" |

### DevOps Tools
| Tool | Depth | Example Question |
|------|-------|------------------|
| Terraform | Expert | "Design a Terraform module for a multi-tier web app." |
| Docker | Expert | "How do you optimize a Dockerfile for production?" |
| Kubernetes | Deep | "Explain the pod lifecycle. How do probes work?" |
| Jenkins/GitHub Actions | Deep | "Design a CI/CD pipeline with multiple environments." |
| Prometheus/Grafana | Deep | "How do you set up monitoring for a new service?" |
| Ansible | Moderate | "Write an Ansible playbook to configure Nginx." |

---

## 5. STAR Story Bank (DevOps Examples)

### Story 1: Customer Obsession + Bias for Action
```
S: A production incident caused a 15-minute outage. Customer complaints were rising.
T: I needed to restore service and communicate with affected customers.
A: I identified the root cause (misconfigured ALB), fixed it, and manually notified
   top 5 customers via email with an apology and timeline.
R: Service restored. Customer satisfaction remained high. Implemented automated
   ALB health check alerts to prevent recurrence.
```

### Story 2: Dive Deep + Ownership
```
S: A database query was timing out intermittently. No one could find the root cause.
T: I owned the investigation end-to-end.
A: I analyzed slow query logs, found a missing index, created a migration,
   and deployed to production after testing.
R: Query time dropped from 3s to 50ms. No recurrence. Shared findings with the team.
```

### Story 3: Invent and Simplify
```
S: Deployment process required 5 engineers and took 4 hours.
T: I needed to automate.
A: Designed a CI/CD pipeline with Jenkins + Docker + Terraform. Blue-green
   deployment with automated rollback.
R: Deployment time from 4 hours to 15 minutes. 100% automation. 1 engineer.
```

---

## 6. System Design — Amazon Focus

### Common Topics
- Design Amazon's shopping cart.
- Design a URL shortener (like TinyURL).
- Design an online marketplace.
- Design a video streaming platform.
- Design a distributed key-value store.

### Key Considerations
1. **Scale**: Amazon handles millions of requests per second.
2. **Availability**: 99.99%+ uptime.
3. **Consistency**: Some services need strong consistency (order processing).
4. **Cost**: Align with Frugality LP.
5. **Security**: Encrypt data at rest and in transit.

---

## 7. Study Resources

### Books
- _The Amazon Way_ (John Rossman).
- _Designing Data-Intensive Applications_ (Kleppmann).
- _AWS Well-Architected Framework_ (whitepaper).

### Online
- LeetCode Easy-Medium (arrays, strings, trees, linked lists).
- AWS Documentation (especially IAM, VPC, S3, EC2, DynamoDB).
- Amazon Leadership Principles (memorized).

---

## 8. Preparation Checklist

- [ ] Memorize all 16 Leadership Principles.
- [ ] Prepare 2 STAR stories per LP (32 stories total).
- [ ] Practice LeetCode Easy-Medium (30+ problems).
- [ ] Master AWS core services (EC2, S3, VPC, IAM, Lambda, DynamoDB).
- [ ] System design practice (5+ designs).
- [ ] Review Terraform, Docker, CI/CD fundamentals.
- [ ] Mock interview with STAR format.
- [ ] Prepare "Tell me about yourself" (1 min, links to first job → now).

---

_End of AMAZON_DEVOPS_INTERVIEW_GUIDE.md_