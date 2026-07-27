# Amazon Security Engineer — Interview Guide

> Complete preparation guide for security engineering roles at Amazon and AWS.
> Covers AWS Security, GuardDuty, IAM, and Amazon's Leadership Principles.

---

## Role Overview

| Aspect | Detail |
|--------|--------|
| **Positions** | Security Engineer, Application Security Engineer, Security Solutions Architect |
| **Levels** | L4 (Entry) to L8 (VP/Distinguished) |
| **Locations** | Seattle, Arlington, Austin, Dublin, Sydney, Hyderabad |
| **Interview Difficulty** | Very High |
| **Coding Bar** | LeetCode Medium |

## Interview Rounds

| Round | Focus | Duration | Key Topics |
|-------|-------|----------|------------|
| **Phone Screen** | Technical + Leadership Principles | 60 min | Security knowledge + behavioral |
| **Technical Phone** | Security design | 60 min | Secure architecture scenarios |
| **Onsite Coding** | Algorithms | 60 min | Data structures, problem-solving |
| **Onsite Security** | Security architecture | 60 min | Design secure cloud systems |
| **Onsite Leadership** | Bar Raiser | 60 min | Leadership Principles deep dive |
| **Onsite Manager** | Career conversation | 45 min | Role alignment, growth |

## Amazon Leadership Principles for Security

| Principle | Security Application |
|-----------|---------------------|
| **Customer Obsession** | Security decisions prioritize customer trust and safety |
| **Ownership** | Own security outcomes end-to-end, never say "not my job" |
| **Dive Deep** | Understand root causes, don't stop at surface-level fixes |
| **Have Backbone** | Disagree and commit on security decisions |
| **Insist on Highest Standards** | Don't compromise on security; continually raise the bar |
| **Bias for Action** | Speed matters in security response — calculated speed |
| **Deliver Results** | Security controls must ship, not just be designed |

## AWS-Specific Topics

### AWS Security Services
- **GuardDuty**: Threat detection across accounts, ML-based anomaly detection
- **Security Hub**: Centralized security findings aggregation
- **Inspector**: Automated vulnerability assessment
- **Macie**: PII discovery and protection in S3
- **WAF**: Web application firewall, rate-based rules
- **Shield Advanced**: DDoS protection
- **CloudTrail**: API audit logging
- **Config**: Resource configuration tracking

### IAM Deep Dive
- Policy evaluation logic (explicit deny > explicit allow)
- Permission boundaries (maximum permissions for roles)
- Service Control Policies (SCPs) for organization-wide guardrails
- Resource-based policies vs identity-based policies
- Policy conditions: SourceIp, MFA, VpcSourceIp

### Network Security
- Security Groups (stateful, instance-level)
- NACLs (stateless, subnet-level)
- VPC Endpoints (PrivateLink)
- Flow Logs for network monitoring

### Data Protection
- S3: Block Public Access, Object Lock, encryption settings
- KMS: CMK vs AWS-managed keys, automatic key rotation
- CloudHSM: FIPS 140-2 Level 3 HSM
- Certificate Manager: SSL/TLS certificate provisioning

## Common Interview Questions

1. Design a secure multi-account AWS organization
2. How does GuardDuty detect anomalous API calls?
3. Design an IAM policy with least privilege for a serverless application
4. How would you detect and respond to a compromised AWS access key?
5. Design a VPC architecture for a PCI-compliant workload
6. How do S3 bucket policies differ from IAM policies?
7. Design a cross-account access strategy with minimal blast radius
8. Walk through a ransomware response in AWS

## Behavioral Questions

1. Tell me about a time you had to convince a team to implement a security control they resisted
2. Describe a security incident you led the response for
3. How do you stay current with AWS security services and threats?
4. Tell me about a time you failed to prevent a security issue
5. How do you balance security requirements with shipping velocity?

## Recommended Preparation

- AWS Security Best Practices whitepaper
- Well-Architected Framework — Security Pillar
- AWS re:Invent security sessions on YouTube
- IAM documentation — practice writing policies
- GuardDuty finding types and remediation
- Prepare LP stories aligned to security contexts
