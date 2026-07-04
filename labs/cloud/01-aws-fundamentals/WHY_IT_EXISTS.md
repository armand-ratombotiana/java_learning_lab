# Why AWS Fundamentals Exists

## The Problem Before Cloud
Before AWS (launched 2006), organizations had to provision physical servers, networking gear, and data center space. This required:
- **Capital expenditure**: Millions upfront for hardware
- **Months of lead time**: Order, ship, rack, and configure hardware
- **Capacity guessing**: Over-provision for peaks, or under-provision and lose revenue
- **Global reach**: Building data centers worldwide required enormous investment

## AWS's Answer
AWS exists to provide **utility computing** — compute and storage on demand, pay-as-you-go, at global scale. The fundamentals (EC2, S3, IAM, VPC) form the atomic building blocks from which all cloud architectures are composed.

## Why These Four Pillars?
- **EC2**: Compute is the core of every application. Virtual servers replace physical hardware.
- **S3**: Durable, scalable object storage is the backbone of data lakes, backups, and static hosting.
- **IAM**: Without identity and access control, multi-tenant cloud is impossible. Security must be built in.
- **VPC**: Network isolation enables enterprises to trust the cloud with sensitive workloads.

## Java Context
Java applications running on AWS typically use EC2 as the compute layer, S3 for asset storage, IAM for SDK credentials, and VPC for network isolation. Every subsequent cloud lab builds on these primitives.
