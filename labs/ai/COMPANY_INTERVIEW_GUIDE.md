# Company Interview Guide for AI/ML Roles

Detailed interview processes for AI/ML roles at top companies, including round-by-round breakdowns, question patterns, and preparation strategies.

---

## 1. Google AI Interview

### Interview Process Overview

1. **Application / Recruiter Screen** (30 min)
2. **Phone Screen** (45-60 min)
   - Technical: ML fundamentals + coding
   - Sometimes two separate screens
3. **On-site (Virtual) - 4-5 rounds**
   - ML Design (45 min)
   - Coding x2 (45 min each)
   - ML Coding (45 min)
   - Googleyness (30-45 min)
4. **Hiring Committee Review**
5. **Team Matching** (if passed HC)
6. **Offer**

### Round Details

#### ML Design Round (45 min)

**Focus**: Design end-to-end ML systems with production considerations.

**Structure**:
- 5 min: Clarify requirements and constraints
- 15 min: Define ML problem (task type, metrics, data)
- 15 min: Design solution (model architecture, training, evaluation)
- 10 min: Production considerations (scaling, monitoring, deployment)

**Common Topics**:
- Recommendation system (YouTube, Search, Play Store)
- Content moderation (toxic content, spam)
- Ranking (web search, ads)
- Forecasting (cloud capacity, demand)
- NLP systems (Smart Reply, translation)
- Vision systems (image search, auto-caption)

**Evaluation Criteria**:
- Problem decomposition
- ML fundamentals understanding
- System design trade-offs
- Scaling and productionization thinking
- Communication clarity

**Practice Questions**:
```
1. Design YouTube video recommendation system
2. Design Google Smart Reply
3. Design real-time spam detection for Gmail
4. Design voice search for Google Assistant
5. Design Google Photos auto-tagging
6. Design news personalization for Google News
```

#### Algorithm Coding (45 min)

**Focus**: Standard LeetCode problems.

**Common Patterns**:
- Arrays and strings (subarray, sliding window)
- Trees and graphs (BFS, DFS, shortest path)
- Dynamic programming (sequences, grid)
- Hash maps and sets
- Sorting and searching

**Tips**:
- Use Python for readability
- Optimize for time/space complexity
- Write clean, well-structured code
- Test with edge cases

#### ML Coding (45 min)

**Focus**: Implement ML algorithms from scratch (no external ML libraries).

**Common Problems**:
```
1. Linear regression with gradient descent
2. K-means clustering
3. K-nearest neighbors (KNN)
4. Logistic regression (binary/multinomial)
5. Decision tree (ID3, CART)
6. PCA from scratch
7. Naive Bayes classifier
8. Feed-forward neural network (forward + backward)
9. Attention mechanism (scaled dot-product)
10. Word2Vec (skip-gram with negative sampling)
```

**Expectations**:
- Clean implementation with correct math
- Efficiency considerations
- Handling edge cases (empty data, NaN values)
- Testing on sample data

#### Googleyness (30-45 min)

**Evaluation Dimensions**:
- **Ambiguity**: How you handle uncertain/undefined problems
- **Collaboration**: How you work with cross-functional teams
- **Growth Mindset**: How you learn from failures and feedback
- **Impact**: How you prioritize and deliver results

**Sample Questions**:
```
1. Tell me about a time you disagreed with your manager
2. Describe a project that failed and what you learned
3. How do you handle ambiguity in requirements?
4. Tell me about a time you helped a team member
5. Describe a time you had to make a decision with incomplete data
6. What would you do if you found a bug in production code?
```

---

## 2. Meta AI Interview

### Interview Process Overview

1. **Recruiter Screen** (15-20 min)
2. **Technical Screen** (45 min coding)
   - One or two coding problems (LeetCode medium)
3. **Virtual On-site - 4-5 rounds**
   - ML System Design (45 min)
   - Coding x2 (45 min each)
   - ML Coding (45 min)
   - Behavioral / ML Project Deep Dive (45 min)
4. **Team Fit** (45 min, sometimes)
5. **Offer**

### Round Details

#### ML System Design (45 min)

**Focus**: Design large-scale ML systems serving billions of users.

**Structure**:
- 10 min: Requirements gathering
- 10 min: Data pipeline design
- 15 min: Model design and training
- 10 min: Serving, evaluation, iteration

**Common Topics**:
- News Feed ranking
- Ads relevance and auction
- Recommendation systems (Groups, Events, Marketplace)
- Content understanding (images, video, text)
- Integrity systems (misinformation, hate speech, harassment)
- Notifications optimization

**Key Considerations**:
- Real-time vs batch constraints
- Feature freshness and latency
- Cold start problem
- Experimentation framework
- Model retraining frequency
- Handling billions of daily active users

**Sample Questions**:
```
1. Design Facebook News Feed ranking
2. Design Instagram Explore recommendation system
3. Design Meta's ad delivery and auction system
4. Design content moderation pipeline for image uploads
5. Design Facebook friend suggestion system
6. Design Instagram Reels recommendation
```

#### Coding Rounds (45 min x 2)

**Focus**: LeetCode medium/hard problems.

**Common Topics**:
- Dynamic programming (especially 2D DP)
- Graphs (topological sort, connected components)
- Trees (BST, paths, serialization)
- Strings (palindromes, pattern matching)
- Arrays (intervals, subarrays)

**Meta-specific patterns**:
- Binary search on answers
- Intervals merging
- String manipulation
- Design and implement classes with methods
- Real-world data processing (logs, events)

#### ML Coding (45 min)

**Focus**: Implement and analyze ML models.

**Common Problems**:
```
1. Implement linear regression with multiple features
2. Implement logistic regression for classification
3. Implement neural network with one hidden layer
4. Implement k-means with initialization strategies
5. Implement convolutional operation
6. Implement transformer attention (multi-head)
7. Implement batch normalization forward/backward
8. Implement gradient descent with momentum/Adam
```

**Expectations**:
- Working code with numpy
- Explain time/space complexity
- Discuss training considerations
- Compare alternative approaches

#### Behavioral / ML Project Deep Dive (45 min)

**Focus**: Real projects, impact, failures, leadership.

**Meta-specific Behavioral Framework (STAR)**:
- **S**ituation: Context of the project
- **T**ask: What you needed to accomplish
- **A**ction: What you specifically did
- **R**esult: Measurable outcomes

**Sample Questions**:
```
1. Tell me about the most impactful ML project you worked on
2. Describe a time when your model failed in production
3. How do you approach feature engineering for a new problem?
4. Tell me about a time you had to make trade-offs between accuracy and latency
5. Describe a conflict with a teammate and how you resolved it
6. How do you ensure your ML models are fair and unbiased?
```

**Meta Leadership Principles**:
- Move fast with impact
- Be open and iterate
- Focus on long-term impact
- Build trust through transparency

---

## 3. OpenAI / Anthropic Interview

### Interview Process Overview

1. **Application + Recruiter Screen** (30 min)
2. **Take-home Assignment** (Optional, varies by role)
3. **Technical Phone Screen** (60 min)
   - Research discussion or coding
4. **Virtual On-site - 4-5 rounds**
   - Research Deep Dive (60 min)
   - ML Coding (45-60 min)
   - System Design (45 min)
   - General Coding (45 min)
   - Values/Behavioral (45 min)
5. **Reference Checks**
6. **Offer**

### Round Details

#### Research Deep Dive (60 min)

**Focus**: Past research, technical depth, papers.

**Structure**:
- 15 min: Candidate presents past work (share screen + slides)
- 30 min: Deep Q&A on methodology, trade-offs, alternatives
- 15 min: Extensions and novel directions

**What They Look For**:
- Deep understanding of your own work
- Ability to explain complex concepts clearly
- Critical thinking about limitations
- Creativity for future directions
- Knowledge of related work

**Tips**:
- Prepare 3 papers/projects to discuss
- Know the mathematical details
- Be ready to discuss failure modes
- Suggest improvements and future work

#### ML Coding (45 min)

**Focus**: Implementation skill and ML understanding.

**OpenAI/Anthropic Specifics**:
- Implement transformer components (attention, FFN, layer norm)
- Training loop from scratch (forward, loss, backward, update)
- Custom loss functions (contrastive, triplet, policy gradient)
- Probabilistic models (Gaussian mixture, Bayesian linear regression)
- Optimization algorithms (Adam, SHB, AdaGrad)

**Sample Problems**:
```
1. Implement scaled dot-product attention with masking
2. Implement a full transformer block (pre-norm architecture)
3. Implement LoRA fine-tuning module
4. Implement beam search for sequence generation
5. Implement RLHF reward model training
6. Implement Mixture of Experts routing
```

#### System Design (45 min)

**Focus**: ML training/serving infrastructure.

**Common Topics**:
- Training infrastructure for large models
- Inference serving at scale
- Evaluation and benchmarking pipelines
- Safety and red-teaming infrastructure
- Data pipeline for pre-training

**Sample Questions**:
```
1. Design training infrastructure for a 175B parameter model
2. Design an API serving system for LLM inference
3. Design an automated red-teaming evaluation pipeline
4. Design a data pipeline for RLHF preference collection
5. Design internal benchmarking and regression testing system
```

#### Values/Behavioral (45 min)

**Focus**: Safety mindset, research taste, collaboration.

**OpenAI-Anthropic Differences**:

| Dimension | OpenAI | Anthropic |
|-----------|--------|-----------|
| Safety approach | Iterative deployment, learning from real world | Conservative, safety first |
| Research philosophy | Scale is key, AGI via scaling | Interpretability, alignment |
| Culture | Fast-paced, move fast | Thoughtful, rigorous |
| Decision making | Founder-led | Research-informed consensus |

**Sample Questions**:
```
1. Why are you interested in AI safety/alignment?
2. How do you think about the risks from advanced AI?
3. Describe a time you prioritized safety over performance
4. How do you stay current with ML research?
5. What do you think is the most important problem in AI today?
6. Describe a research result you were skeptical of and why
```

---

## 4. Amazon AI Interview

### Interview Process Overview

1. **Recruiter Screen** (30 min)
2. **Online Assessment** (Optional)
   - Coding problems + work style assessment
3. **Technical Phone Screen** (60 min)
   - ML fundamentals + coding
4. **Virtual On-site - 4-5 rounds**
   - ML Deep Dive (60 min)
   - Coding (45 min)
   - ML System Design (45 min)
   - System Design (45 min, senior roles)
   - Bar Raiser (60 min)
5. **Debrief** (hiring committee)
6. **Offer**

### Round Details

#### ML Deep Dive (60 min)

**Focus**: Depth of ML knowledge, research ability.

**Structure**:
- 15 min: Project presentation
- 30 min: Cross-examination on methods
- 15 min: Fireside ML fundamentals

**Topics Covered**:
- Model architecture decisions
- Loss function design
- Regularization techniques
- Evaluation methodology
- Failure analysis
- Statistical significance

**Expected Depth**:
- Know the math behind every model you've used
- Understand why specific techniques work
- Be able to derive gradient updates
- Discuss bias-variance trade-offs
- Compare alternative approaches

#### Coding (45 min)

**Focus**: Problem-solving, clean code.

**Amazon Focus Areas**:
- Working at scale (large data structures)
- Object-oriented design
- Algorithmic efficiency
- Debugging and testing

**Common Problems**:
```
1. Find k closest elements
2. Merge intervals
3. LRU cache
4. Design a file system
5. Word search in grid
6. Design a recommendation scorer
7. Implement a moving average
```

#### Bar Raiser (60 min)

**Focus**: Leadership Principles, culture fit.

**Role**: The Bar Raiser is an interviewer from outside the team who ensures the hiring bar is maintained.

**Amazon Leadership Principles** (key ones for ML roles):
1. **Customer Obsession**: How do you build ML models with customer needs in mind?
2. **Ownership**: How do you take ownership of model quality?
3. **Dive Deep**: How do you analyze model failures to root cause?
4. **Learn and Be Curious**: How do you stay current with ML research?
5. **Bias for Action**: How do you balance speed vs rigor?
6. **Deliver Results**: How do you ensure models ship?

**Sample Questions**:
```
1. Tell me about a time you disagreed with a data analysis conclusion
2. Describe a model that did not work as expected in production
3. How do you handle data quality issues?
4. Tell me about a time you had to learn a new technique quickly
5. Describe a time you convinced stakeholders to adopt ML
6. How do you approach debugging a model with low accuracy?
7. Tell me about a time you took a calculated risk with a model
```

---

## 5. Apple AI Interview

### Interview Process Overview

1. **Recruiter Screen** (30 min)
2. **Technical Phone Screen** (45-60 min)
   - ML coding + math fundamentals
3. **On-site (Virtual) - 5-7 rounds**
   - ML Coding (45 min)
   - ML System Design (45 min)
   - Coding in C/C++/Swift (45 min)
   - Data Structures/Algorithms (45 min)
   - Privacy in ML (30 min)
   - Research Presentation (45 min, research roles)
   - Behavioral (30 min)
4. **Hiring Committee**
5. **Offer**

### Round Details

#### ML Coding (45 min)

**Focus**: Numerical algorithms, low-level optimization.

**Apple Specifics**:
- Performance is critical (you may need to optimize)
- On-device constraints (memory, power, compute)
- First-principles implementations

**Common Problems**:
```
1. Implement matrix multiplication with Strassen optimization
2. Implement convolution operation (im2col approach)
3. Quantize a neural network (FP32 -> INT8)
4. Implement nearest neighbor search with k-d tree
5. Implement bundle adjustment or optimization
6. Implement image processing kernels (Gaussian blur, edge detection)
```

#### Privacy in ML (30 min)

**Focus**: Apple's core value of privacy.

**Topics**:
- Differential privacy (epsilon, mechanisms)
- Federated learning (FedAvg, secure aggregation)
- On-device ML vs cloud ML trade-offs
- Private information retrieval
- Data minimization principles

**Sample Questions**:
```
1. Design a differentially private model training pipeline
2. How would you implement federated learning for keyboard predictions?
3. What privacy issues arise with embedding-based recommendations?
4. How do you handle user deletion requests in an ML system?
5. Design an on-device model update mechanism
```

#### C/C++/Swift Coding (45 min)

**Focus**: Systems programming for ML.

**Topics**:
- Memory management (allocators, buffers)
- SIMD and vectorization
- Threading and parallelism
- Metal Shading Language for GPU compute
- Core ML model format manipulation

**Sample Problems**:
```
1. Implement a custom allocator for ML model weights
2. Parallelize matrix multiplication with threads
3. Write a Metal kernel for element-wise operations
4. Parse and traverse Core ML model protobuf
5. Implement fast exponentiation for activation functions
```

---

## 6. Microsoft AI Interview

### Interview Process Overview

1. **Recruiter Screen** (30 min)
2. **Technical Phone Screen** (45-60 min)
   - Coding + ML fundamentals
3. **Virtual On-site - 4-5 rounds**
   - ML Design (45 min)
   - Coding x2 (45 min each)
   - ML Coding (45 min)
   - Behavioral/AZ (30-45 min)
4. **Hiring Committee** (ASAP)
5. **Offer**

### Round Details

#### ML Design (45 min)

**Focus**: Applied ML at Microsoft scale.

**Common Topics**:
- Office 365 Copilot features
- Azure AI services
- Search (Bing) ranking
- Gaming ML (Xbox)
- Enterprise ML solutions

**Sample Questions**:
```
1. Design Copilot for Excel (formula suggestion)
2. Design Bing Search answer generation
3. Design PowerPoint design suggestions
4. Design Outlook email prioritization
5. Design Teams meeting transcription and summarization
6. Design Azure anomaly detection service
```

#### Behavioral / "AZ" (30-45 min)

**Microsoft Culture**:
- Growth mindset
- Customer obsession
- Diversity and inclusion
- One Microsoft (collaboration)
- Innovation

**Sample Questions**:
```
1. Tell me about a time you learned a new technology for a project
2. Describe a time you received critical feedback and how you handled it
3. How do you handle projects with shifting priorities?
4. Tell me about a time you failed and what you learned
5. Describe how you would mentor a junior engineer
6. How do you approach making trade-offs in ML systems?
7. Tell me about a time you represented diverse perspectives
```

---

## 7. NVIDIA AI Interview

### Interview Process Overview

1. **Recruiter Screen** (30 min)
2. **Technical Phone Screen** (45-60 min)
   - CUDA/GPU programming basics
3. **Virtual On-site - 4-5 rounds**
   - GPU/CUDA Coding (60 min)
   - ML Algorithms (45 min)
   - System Design (45 min)
   - Coding (C++/Python, 45 min)
   - Behavioral (30 min)
4. **Offer**

### Round Details

#### GPU/CUDA Coding (60 min)

**Focus**: GPU programming, optimization, parallelism.

**Topics**:
- CUDA kernel implementation
- Shared memory optimization
- Warp-level primitives
- Memory coalescing
- Tensor core programming

**Sample Problems**:
```
1. Implement matrix multiplication in CUDA with shared memory tiling
2. Write a CUDA kernel for softmax (online softmax algorithm)
3. Implement batch normalization inference in CUDA
4. Write a custom attention kernel in CUDA
5. Implement all-reduce with ring topology in NCCL
6. Write a CUDA program for histogram computation
```

**Evaluation**:
- Correctness of parallel implementation
- Memory access pattern optimization
- Occupancy and resource utilization
- Understanding of GPU architecture
- Performance analysis (roofline model)

#### ML Algorithms (45 min)

**Focus**: Deep understanding of ML algorithms.

**Topics**:
- Deep learning optimization
- Mixed precision training
- Model parallelism strategies
- Quantization and pruning
- Graph optimization (TensorRT)

**Sample Questions**:
```
1. How do tensor cores work? What operations can they accelerate?
2. Compare data parallelism, model parallelism, and pipeline parallelism
3. How does quantization-aware training work?
4. Explain the roofline model and how to use it for optimization
5. Compare FP32, FP16, BF16, and INT8 precision trade-offs
6. How would you profile and optimize a PyTorch model?
```

---

## General Interview Preparation Timeline

### 4-12 Weeks Before Interview

**Week 1-2: Fundamentals Review**
- Review ML fundamentals (supervised, unsupervised, deep learning)
- Practice ML coding (implement algorithms from scratch)
- Review statistics and probability

**Week 3-4: System Design**
- Study ML system design patterns
- Practice designing 5-10 ML systems
- Review distributed systems concepts

**Week 5-6: Coding Preparation**
- LeetCode practice (medium/hard)
- Focus on company-specific patterns
- Time-boxed practice (45 min per problem)

**Week 7-8: Behavioral Preparation**
- Prepare STAR stories for 10+ scenarios
- Review company leadership principles
- Practice with mock interviews

**Week 9-12: Company-Specific**
- Research the company's recent work
- Read relevant papers
- Practice company-specific rounds
- Full mock interviews

### 1-2 Weeks Before

- Light review (no new topics)
- Rest and sleep well
- Prepare questions to ask interviewers
- Set up technical environment (coding setup, camera, mic)

### Day Of

- Review key formulas and concepts (quick reference sheet)
- Take breaks between rounds
- Stay focused, listen carefully
- Clarify before solving
- Communicate thinking process

---

## Company Comparison: Interview Difficulty

| Company | Coding Difficulty | ML Depth | System Design | Behavioral |
|---------|------------------|----------|---------------|------------|
| Google | High | High | High | Medium |
| Meta | High | Medium-High | High | Medium |
| OpenAI | Medium | Very High | High | High |
| Anthropic | Medium | Very High | High | High |
| Amazon | Medium | High | Medium | High |
| Apple | High | High | Medium | Medium |
| Microsoft | Medium | Medium | Medium | Medium |
| NVIDIA | High (CUDA) | High | Medium | Medium |
| Databricks | Medium | Medium | Medium | Low |
| Scale AI | Medium | Medium | Medium | Medium |
| Cohere | Medium | High | Medium | Medium |
| Mistral | Medium | Very High | Medium | Low |
| Hugging Face | Medium | Medium | Medium | Low |

---

## Quick Reference: Interview Focus by Role

| Role | Primary Focus | Secondary |
|------|---------------|-----------|
| ML Engineer | Coding, System Design | ML fundamentals |
| Research Scientist | Research Deep Dive, Math | ML Coding |
| Applied Scientist | ML System Design, Research | Coding |
| ML Infra Engineer | System Design, Coding | ML basics |
| Data Scientist | ML fundamentals, Statistics | Coding |
| Research Engineer | ML Coding, Research | System Design |
| Safety Researcher | Alignment, Interpretability | ML basics |

---

## Key Resources by Company

| Company | Resource |
|---------|----------|
| Google | "Designing Data-Intensive Applications", Google ML Crash Course |
| Meta | "Recommender Systems Handbook", PyTorch Documentation |
| OpenAI | GPT Papers, Andrej Karpathy's tutorials |
| Anthropic | Constitutional AI papers, Interpretability research |
| Amazon | AWS ML Documentation, "Working Backwards" |
| Apple | Core ML Documentation, WWDC Sessions |
| NVIDIA | CUDA Programming Guide, TensorRT Documentation |
| Databricks | Spark: The Definitive Guide, MLflow Docs |
| Scale AI | RLHF Literature, Human-in-the-Loop ML |
| Cohere | RAG Papers, Vector Database Documentation |
| Mistral | MoE Tutorials, Efficient Transformers |
| Hugging Face | Transformers Documentation, HF Course |
