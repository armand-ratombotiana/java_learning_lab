# 🎓 Engineering Academies - Complete Guide

Comprehensive deep-dive academies for specialized engineering disciplines. Each academy contains atomic micro-labs with standardized structure:

- **THEORY.md** - Complete theoretical foundation
- **CODE_DEEP_DIVE.md** - Java implementations with explanations
- **MATH_FOUNDATION.md** - Mathematical proofs and derivations (AI/Math academies)
- **EXERCISES.md** - 20+ practice problems with solutions
- **QUIZ.md** - 30 assessment questions with answers
- **FLASHCARDS.md** - Quick review cards for spaced learning
- **MINI_PROJECT.md** - Hands-on mini project
- **REAL_WORLD_PROJECT.md** - Production-ready project

---

## 🚀 AI Academy (`labs/ai/`)

Complete 20-lab curriculum for mastering Machine Learning, Deep Learning, and Generative AI with Java implementations.

### Learning Path

```
Linear Algebra → Probability → Calculus → Optimization
                ↓
            Statistics
                ↓
        ML Fundamentals → Regression → Classification
                ↓                    ↓
            Clustering → Dimensionality Reduction
                ↓
        Neural Networks → CNN → RNN → Transformers
                ↓                    ↓
            LLM Agents ← Fine-tuning ← RAG Systems
```

### Lab Index

| Lab | Topic | Hours | Difficulty |
|-----|-------|-------|------------|
| 01 | [Linear Algebra for ML](./ai/01-linear-algebra-for-ml/) | 10 | ⭐⭐ |
| 02 | [Probability for ML](./ai/02-probability-for-ml/) | 8 | ⭐⭐ |
| 03 | [Calculus for ML](./ai/03-calculus-for-ml/) | 8 | ⭐⭐⭐ |
| 04 | [Optimization](./ai/04-optimization/) | 8 | ⭐⭐⭐ |
| 05 | [Statistics](./ai/05-statistics/) | 6 | ⭐⭐ |
| 06 | [ML Fundamentals](./ai/06-ml-fundamentals/) | 8 | ⭐⭐ |
| 07 | [Regression](./ai/07-regression/) | 10 | ⭐⭐⭐ |
| 08 | [Classification](./ai/08-classification/) | 10 | ⭐⭐⭐ |
| 09 | [Clustering](./ai/09-clustering/) | 8 | ⭐⭐ |
| 10 | [Dimensionality Reduction](./ai/10-dimensionality-reduction/) | 8 | ⭐⭐⭐ |
| 11 | [Neural Networks](./ai/11-neural-networks/) | 12 | ⭐⭐⭐⭐ |
| 12 | [CNN](./ai/12-convolutional-neural-networks/) | 12 | ⭐⭐⭐⭐ |
| 13 | [RNN](./ai/13-recurrent-neural-networks/) | 12 | ⭐⭐⭐⭐ |
| 14 | [Transformers](./ai/14-transformers/) | 15 | ⭐⭐⭐⭐ |
| 15 | [RAG Systems](./ai/15-rag-systems/) | 10 | ⭐⭐⭐ |
| 16 | [LLM Agents](./ai/16-llm-agents/) | 12 | ⭐⭐⭐⭐ |
| 17 | [Fine-tuning](./ai/17-fine-tuning/) | 15 | ⭐⭐⭐⭐ |
| 18 | [Vector Databases](./ai/18-vector-databases/) | 8 | ⭐⭐⭐ |
| 19 | [Prompt Engineering](./ai/19-prompt-engineering/) | 6 | ⭐⭐ |
| 20 | [MLOps](./ai/20-mlops/) | 10 | ⭐⭐⭐ |

**Total: 200+ hours of hands-on learning**

### Key Concepts by Lab

#### Mathematical Foundations
- **Lab 01**: Vectors, matrices, operations, eigenvalues, SVD, PCA
- **Lab 02**: Bayes theorem, distributions (Normal, Bernoulli, Poisson), entropy
- **Lab 03**: Derivatives, partial derivatives, chain rule, gradients
- **Lab 04**: Gradient descent, Adam, RMSProp, loss landscapes

#### Machine Learning
- **Lab 06**: Supervised vs unsupervised, overfitting, cross-validation
- **Lab 07**: Linear regression, Ridge, Lasso, polynomial regression
- **Lab 08**: Logistic regression, SVM, decision trees, random forests
- **Lab 09**: K-means, DBSCAN, hierarchical clustering, silhouette score

#### Deep Learning
- **Lab 11**: Perceptron, activation functions, backpropagation, optimizers
- **Lab 12**: Convolutions, pooling, ResNet, transfer learning
- **Lab 13**: LSTM, GRU, seq2seq, attention mechanism

#### Generative AI
- **Lab 14**: Attention, multi-head attention, BERT, GPT, transformer architecture
- **Lab 15**: Retrieval systems, chunking, hybrid search, reranking
- **Lab 16**: Tool use, chain-of-thought, ReAct, planning agents
- **Lab 17**: LoRA, QLoRA, PEFT, domain adaptation

---

## ☁️ Cloud Engineering Academy (`labs/cloud/`)

Master cloud infrastructure, containers, and Kubernetes with hands-on labs.

### Learning Path

```
AWS Fundamentals → AWS Compute → AWS Storage → AWS Database
        ↓               ↓            ↓           ↓
    Networking     Docker      Kubernetes   Terraform
        ↓
    Service Mesh (Istio)
```

### Lab Index

| Lab | Topic | Hours | Focus Area |
|-----|-------|-------|------------|
| 01 | [AWS Fundamentals](./cloud/01-aws-fundamentals/) | 6 | EC2, S3, IAM, VPC |
| 02 | [AWS Compute](./cloud/02-aws-compute/) | 6 | Lambda, ECS, Fargate |
| 03 | [AWS Storage](./cloud/03-aws-storage/) | 5 | EFS, EBS, Glacier |
| 04 | [AWS Database](./cloud/04-aws-database/) | 6 | RDS, DynamoDB, ElastiCache |
| 05 | [AWS Networking](./cloud/05-aws-networking/) | 5 | Route 53, CloudFront |
| 06 | [Docker & Containers](./cloud/06-docker-containers/) | 8 | Images, compose, networking |
| 07 | [Kubernetes](./cloud/07-kubernetes/) | 10 | Pods, services, Helm |
| 08 | [Terraform IaC](./cloud/08-terraform/) | 8 | Modules, state, providers |

### Key Concepts by Lab

#### AWS Core Services
- **Lab 01**: IAM policies, VPC subnets, security groups, availability zones
- **Lab 02**: Serverless functions, container orchestration, auto-scaling
- **Lab 03**: Object storage lifecycle, block storage, archive tiers
- **Lab 04**: Relational vs NoSQL, caching strategies, replication

#### Container Technologies
- **Lab 06**: Multi-stage builds, Docker Compose, overlay networks, volumes
- **Lab 07**: Deployments, services, ingress, ConfigMaps, secrets, Helm charts
- **Lab 08**: Infrastructure as code, modules, state management, workspaces

---

## 🧮 Math Academy (`labs/math/`)

Mathematical foundations for ML and data science with Java implementations.

### Lab Index

| Lab | Topic | Applications |
|-----|-------|-------------|
| 01 | [Linear Algebra](./math/01-linear-algebra/) | ML transformations, PCA |
| 02 | [Calculus](./math/02-calculus/) | Gradient descent, backprop |
| 03 | [Probability](./math/03-probability/) | Bayesian inference |
| 04 | [Statistics](./math/04-statistics/) | Hypothesis testing |
| 05 | [Optimization](./math/05-optimization/) | Training algorithms |
| 06 | [Information Theory](./math/06-information-theory/) | Entropy, cross-entropy |
| 07 | [Graph Theory](./math/07-graph-theory/) | Networks, recommendations |
| 08 | [Number Theory](./math/08-number-theory/) | Cryptography, hashing |
| 09 | [Combinatorics](./math/09-combinatorics/) | Counting, algorithms |
| 10 | [Signal Processing](./math/10-signal-processing/) | FFT, filters |

---

## 🏗️ System Design Academy (`labs/system-design/`)

Master distributed systems architecture and design patterns.

### Lab Index

| Lab | Topic | Key Concepts |
|-----|-------|-------------|
| 01 | [Architecture Patterns](./system-design/01-architecture-patterns/) | Layered, microservices, CQRS |
| 02 | [Scalability](./system-design/02-scalability/) | Sharding, partitioning |
| 03 | [Availability](./system-design/03-availability/) | Failover, redundancy |

### Design Principles

1. **Scalability**: Horizontal scaling, caching, async processing
2. **Availability**: Redundancy, failover, circuit breakers
3. **Consistency**: CAP theorem, eventual consistency, consensus
4. **Maintainability**: Modularity, observability, automation

---

## 📊 Data Science Academy (`labs/data-science/`)

End-to-end data science with Java-based implementations.

### Lab Index

| Lab | Topic | Focus |
|-----|-------|-------|
| 01 | Data Wrangling | Cleaning, transformation |
| 02 | EDA | Visualization, patterns |
| 03 | Feature Engineering | Scaling, encoding |
| 04 | Model Training | Cross-validation |
| 05 | Model Evaluation | Metrics, benchmarking |
| 06 | Pipelines | Orchestration |
| 07 | Production ML | Serving, monitoring |

---

## 🏆 Portfolio Capstone Projects (`capstones/`)

Real-world projects demonstrating production-ready skills.

| # | Project | Domain | Complexity |
|---|---------|--------|------------|
| 01 | Banking Platform | Financial services | ⭐⭐⭐⭐ |
| 02 | Fraud Detection | Security/ML | ⭐⭐⭐⭐ |
| 03 | Recommendation Engine | ML | ⭐⭐⭐ |
| 04 | Vector Database | Search | ⭐⭐⭐⭐ |
| 05 | RAG Platform | GenAI | ⭐⭐⭐⭐ |
| 06 | Distributed Cache | Infrastructure | ⭐⭐⭐ |
| 07 | Event Streaming | Messaging | ⭐⭐⭐ |
| 08 | Search Engine | Search | ⭐⭐⭐⭐ |
| 09 | ML Platform | MLOps | ⭐⭐⭐⭐⭐ |
| 10 | AI Assistant | GenAI | ⭐⭐⭐⭐⭐ |

---

## 📚 Learning Resources

### Prerequisites
- **Java**: 21+ proficiency
- **Mathematics**: High school level for math academy
- **Data Structures**: Arrays, lists, maps, trees

### Recommended Order
1. Start with **AI Academy Lab 01-05** (math foundations)
2. Continue with **AI Academy Lab 06-10** (ML basics)
3. Progress to **AI Academy Lab 11-14** (deep learning)
4. Explore **GenAI topics** (Labs 15-20)
5. Parallel: Cloud Academy for deployment skills

### Assessment
- Each lab includes 30-question quiz
- Flashcards for spaced repetition
- Mini-projects for hands-on practice
- Real-world projects for portfolio building

---

## 🎯 Quick Navigation

| Academy | Labs | Hours |
|---------|------|-------|
| AI Academy | 20 | 200+ |
| Cloud Academy | 8 | 54 |
| Math Academy | 10 | 80 |
| System Design | 8 | 64 |
| Data Science | 7 | 56 |

**Total: 53 labs, 450+ hours of content**