# AWS Quizzes

## Quiz 1: Basic Concepts

**Question 1**: Which AWS service provides virtual servers?
- A) EC2
- B) Lambda
- C) ECS
- D) EKS

**Answer**: A

---

**Question 2**: What is the maximum timeout for Lambda?
- A) 1 minute
- B) 5 minutes
- C) 15 minutes
- D) 1 hour

**Answer**: C

---

**Question 3**: Which storage service is object-based?
- A) EBS
- B) EFS
- C) S3
- D) FSx

**Answer**: C

---

**Question 4**: What does IAM stand for?
- A) Internet Access Management
- B) Identity and Access Management
- C) Instance Access Manager
- D) Identity Access Monitor

**Answer**: B

---

**Question 5**: Which service is serverless?
- A) EC2
- B) RDS
- C) Lambda
- D) ECS

**Answer**: C

---

## Quiz 2: EC2

**Question 1**: What is the default user for Amazon Linux?
- A) root
- B) ubuntu
- C) ec2-user
- D) admin

**Answer**: C

---

**Question 2**: What does Auto Scaling Group provide?
- A) Manual scaling
- B) Automatic scaling
- C) No scaling
- D) Fixed number of instances

**Answer**: B

---

**Question 3**: Which is NOT an EC2 instance type?
- A) t3.micro
- B) m5.large
- C) i3.xlarge
- D) s3.standard

**Answer**: D

---

**Question 4**: What is a Launch Template?
- A) Virtual machine image
- B) Configuration for instance launch
- C) Auto Scaling policy
- D) Load balancer config

**Answer**: B

---

**Question 5**: Which storage is attached to EC2 instance?
- A) S3
- B) EFS
- C) EBS
- D) Glacier

**Answer**: C

---

## Quiz 3: Lambda

**Question 1**: What is cold start?
- A) Function running slowly
- B) First invocation initialization
- C) Error in function
- D) Timeout issue

**Answer**: B

---

**Question 2**: How is Lambda priced?
- A) Per hour
- B) Per request and execution time
- C) Fixed monthly fee
- D) Per GB

**Answer**: B

---

**Question 3**: What is the minimum Lambda memory?
- A) 128 MB
- B) 256 MB
- C) 512 MB
- D) 64 MB

**Answer**: A

---

**Question 4**: How do you increase Lambda CPU?
- A) Add more cores
- B) Increase memory (CPU scales with memory)
- C) Use custom runtime
- D) Add layers

**Answer**: B

---

**Question 5**: What is provisioned concurrency?
- A) Automatic scaling
- B) Pre-warmed instances
- C) Reserved capacity
- D) Cold start prevention

**Answer**: B

---

## Quiz 4: S3

**Question 1**: What is S3 bucket name?
- A) Globally unique
- B) Region-specific unique
- C) Account-specific
- D) Can be any name

**Answer**: A

---

**Question 2**: What does versioning enable?
- A) Faster uploads
- B) Keep multiple versions
- C) Compression
- D) Encryption

**Answer**: B

**Question 3**: Which storage class is cheapest?
- A) Standard
- B) IA
- C) Glacier
- D) Intelligent-Tiering

**Answer**: C

---

**Question 4**: What is the max object size in S3?
- A) 1 TB
- B) 5 TB
- C) 10 TB
- D) Unlimited

**Answer**: B

---

**Question 5**: What is prefix in S3?
- A) File extension
- B) Folder path
- C) Bucket name
- D) Region

**Answer**: B

---

## Quiz 5: RDS

**Question 1**: Which is NOT a supported RDS engine?
- A) PostgreSQL
- B) MySQL
- C) MongoDB
- D) Oracle

**Answer**: C

---

**Question 2**: What does Multi-AZ provide?
- A) Read replicas
- B) High availability
- C) Cost savings
- D) Backup

**Answer**: B

---

**Question 3**: What is the purpose of Read Replica?
- A) Backup
- B) Read scalability
- C) Failover
- D) Encryption

**Answer**: B

---

**Question 4**: How long can RDS backup retention be?
- A) 7 days
- B) 35 days
- C) 90 days
- D) Unlimited

**Answer**: B

---

**Question 5**: What is deletion protection?
- A) Prevent accidental deletion
- B) Prevent read access
- C) Prevent writes
- D) Prevent modifications

**Answer**: A

---

## Answer Key

| Quiz | Q1 | Q2 | Q3 | Q4 | Q5 |
|------|-----|-----|-----|-----|-----|
| 1    | A  | C  | C  | B  | C  |
| 2    | C  | B  | D  | B  | C  |
| 3    | B  | B  | A  | B  | B  |
| 4    | A  | B  | C  | B  | B  |
| 5    | C  | B  | B  | B  | A  |