# Google DeepMind AI Interview Guide

## Table of Contents
1. [Company Overview](#company-overview)
2. [Company Background & Culture](#company-background--culture)
3. [Interview Process Overview](#interview-process-overview)
4. [Role Types](#role-types)
5. [Key Projects to Know](#key-projects-to-know)
6. [Key Technical Areas](#key-technical-areas)
7. [Coding Expectations](#coding-expectations)
8. [Research Discussion](#research-discussion)
9. [System Design Topics](#system-design-topics)
10. [Behavioral Questions](#behavioral-questions)
11. [Sample Questions & Answers](#sample-questions--answers)
12. [Resources & Further Reading](#resources--further-reading)

---

## Company Overview

- **Founded:** 2010 (DeepMind), merged with Google Brain in 2023
- **Founders:** Demis Hassabis, Shane Legg, Mustafa Suleyman
- **Parent Company:** Alphabet Inc. (Google)
- **Headquarters:** London, UK (primary), with offices in Mountain View, Paris, Montreal
- **Key Products:** Gemini, AlphaGo, AlphaFold, AlphaZero, WaveNet, GNN breakthroughs
- **Employees:** ~2,000+ (combined DeepMind + Google Brain)
- **Mission:** Solve intelligence to advance science and benefit humanity

DeepMind represents one of the world's premier AI research laboratories, combining the intellectual depth of a world-class research institute with the resources and infrastructure of Google.

---

## Company Background & Culture

### The DeepMind Legacy
DeepMind was founded in 2010 with the ambitious mission of "solving intelligence." Acquired by Google in 2014 for approximately $500M, DeepMind operated semi-autonomously until the 2023 merger with Google Brain to form Google DeepMind.

### The Google Brain Merger (2023)
The merger created a single, unified AI research division within Google:
- Combines DeepMind's research culture with Google Brain's engineering scale
- Brings together teams behind Transformer (Google Brain), AlphaFold (DeepMind), and Gemini
- Creates the largest concentrated AI research organization in the world
- Unified access to Google's compute infrastructure (TPUs, data centers)

### Research Culture
- **Publication-first:** DeepMind publishes prolifically in Nature, NeurIPS, ICML, ICLR
- **Interdisciplinary:** Teams include neuroscientists, physicists, mathematicians, engineers
- **Long-term thinking:** Projects can run for years before producing results
- **Open collaboration:** Internal tools and research findings shared across teams
- **Competitive:** Publish-or-perish culture within a supportive environment

### Key Principles
1. **Scientific Rigor:** Every claim must be backed by thorough experimentation
2. **Reproducibility:** Results must be reproducible by others
3. **Interdisciplinary Thinking:** Solutions often come from combining fields
4. **Responsible AI:** Ethics review for all research projects

---

## Interview Process Overview

### Stage 1: Recruiter Screen (30-45 minutes)
- Background and experience assessment
- Research interest alignment
- Discussion of DeepMind's mission and values
- Logistics (location, team preference, timeline)

### Stage 2: Coding Assessment (60 minutes)
- Strong algorithmic focus — expect harder-than-average LeetCode problems
- Language options: Python, C++, or Go (Python strongly preferred)
- Focus on correctness, efficiency, and clean code
- May include ML-specific coding (less common at this stage)

### Stage 3: Technical Phone Screen (60-90 minutes)
**Varies by role:**
- **Research Scientist:** Paper discussion, research methodology, experimental design
- **Research Engineer:** Coding + ML system design, distributed systems
- **Applied Scientist:** ML fundamentals, coding, problem-solving

### Stage 4: Onsite Interview (4-6 hours)
Typically 4-5 interviews:

1. **Research Presentation (60 min)**
   - Present your best research (published or in progress)
   - Audience includes researchers and engineers
   - Heavy Q&A — expect deep probing

2. **ML Coding (45-60 min)**
   - Implement ML algorithms from scratch
   - Debug and optimize existing ML code
   - Analyze computational complexity

3. **Algorithmic Coding (45-60 min)**
   - Advanced algorithms and data structures
   - Mathematical problem-solving
   - Concurrency and optimization

4. **System Design (45-60 min)**
   - ML infrastructure design
   - Large-scale training systems
   - Data pipeline architecture

5. **Research Discussion (45 min)**
   - Brainstorm novel research ideas
   - Discuss open problems in the field
   - Evaluate proposed approaches

6. **Behavioral & Fit (30-45 min)**
   - Collaboration and teamwork
   - Intellectual curiosity
   - Interdisciplinary approach

### Stage 5: Final Review
- Hiring committee review
- Research director approval
- Compensation and offer

---

## Role Types

### 1. Research Scientist
- **Focus:** Advancing the state of AI research, publishing papers
- **Requirements:** PhD in ML, CS, neuroscience, or related field; strong publication record
- **Key Skills:** Deep learning theory, experimental design, mathematical modeling
- **Interview Weight:** Research (50%), ML knowledge (25%), Coding (25%)

### 2. Research Engineer
- **Focus:** Building infrastructure and tools for research
- **Requirements:** MS/PhD in CS or related; strong software engineering background
- **Key Skills:** Distributed systems, large-scale ML, performance optimization
- **Interview Weight:** Coding (40%), System Design (30%), Research (30%)

### 3. Applied Scientist
- **Focus:** Applying DeepMind research to Google products
- **Requirements:** PhD preferred; experience shipping ML systems
- **Key Skills:** ML engineering, product thinking, experimentation
- **Interview Weight:** ML Knowledge (35%), System Design (30%), Coding (20%), Behavioral (15%)

### 4. Software Engineer (ML)
- **Focus:** Building platforms for ML development
- **Requirements:** Strong CS fundamentals, interest in ML
- **Key Skills:** Distributed systems, infrastructure, ML pipelines
- **Interview Weight:** Coding (50%), System Design (35%), ML (15%)

### 5. Research Intern
- **Focus:** Contributing to ongoing research projects
- **Requirements:** Currently pursuing PhD, strong research track record
- **Duration:** 12-16 weeks typically
- **Interview:** Research discussion + coding screen

---

## Key Projects to Know

### AlphaGo (2016)
- **What:** First AI to defeat a world champion Go player (Lee Sedol)
- **Why it matters:** Go has more board positions than atoms in the universe; classic tree search was intractable
- **Technical approach:** Monte Carlo Tree Search + deep neural networks (policy and value networks)
- **Key innovations:** Self-play training, combining search with learned heuristics
- **Be ready to discuss:** Why Go was considered a grand challenge, how MCTS works, role of reinforcement learning

### AlphaZero (2017)
- **What:** Mastered Go, Chess, and Shogi without human knowledge
- **Why it matters:** General-purpose game-playing AI, no domain-specific heuristics
- **Technical approach:** Self-play RL, single neural network for all games
- **Key innovations:** Tabula rasa learning, unified architecture

### AlphaFold (2021)
- **What:** Solved protein structure prediction (CASP14 grand challenge)
- **Why it matters:** 50-year grand challenge in biology, enables drug discovery
- **Technical approach:** Transformer architecture adapted for protein data, Evoformer module
- **Key innovations:** Multiple sequence alignment integration, structure module
- **Be ready to discuss:** Protein folding problem, AlphaFold architecture, structure prediction metrics

### Gemini (2023-2024)
- **What:** Google's most capable multimodal AI model
- **Competes with:** GPT-4, Claude 3
- **Key features:** Native multimodality (text, image, audio, video, code), long context
- **Architecture:** Decoder-only transformer, mixture-of-experts variants
- **Training:** Massive TPU v5 clusters, efficient distributed training

### GNN Research
- **Key contributions:** Graph neural network theory, applications in chemistry and physics
- **Notable work:** GraphNet, Message Passing Neural Networks
- **Applications:** Drug discovery, materials science, particle physics (ATLAS collaboration)

### Reinforcement Learning (Core Strength)
- **DQN (2013):** Deep Q-Learning for Atari
- **Rainbow (2017):** Combining DQN improvements
- **IMPALA (2018):** Scalable distributed RL
- **Dreamer (2020-2023):** Model-based RL world models
- **SAC, PPO:** Standard algorithms developed/refined at DeepMind

---

## Key Technical Areas

### Reinforcement Learning

**Must-Know Algorithms:**
- **DQN:** Experience replay, target network, epsilon-greedy
- **PPO:** Surrogate objective, clipped updates, trust regions
- **SAC:** Maximum entropy RL, soft Q-learning, automatic temperature tuning
- **AlphaZero-style MCTS:** Selection, expansion, simulation, backpropagation
- **Model-based RL:** World models, planning in latent space

**Advanced Topics:**
- Multi-agent RL (MADDPG, QMIX)
- Inverse RL (learning reward functions from demonstrations)
- Offline RL (learning from static datasets)
- Hierarchical RL (options, subgoals)
- Distributional RL (distributed value functions)

**Be Ready For:**
- Derive policy gradient theorem
- Compare on-policy vs off-policy algorithms
- Discuss exploration vs exploitation trade-offs
- Analyze convergence properties of RL algorithms

### Game Theory & Multi-Agent Systems

**Key Concepts:**
- Nash equilibrium
- Cooperative vs competitive games
- Social welfare functions
- Mechanism design
- Regret minimization

**DeepMind Contributions:**
- AlphaStar (StarCraft II) — multi-agent with imperfect information
- Diplomacy (Cicero) — natural language negotiation + strategic reasoning
- Player of Games — general-purpose game-solving AI

### Generative Models

**Key Architectures:**
- **VAEs:** Variational inference, reparameterization trick
- **GANs:** Generator/discriminator, Nash equilibrium view
- **Diffusion Models:** Forward/reverse processes, score matching
- **Autoregressive Models:** Transformer-based generative models
- **Flow-based Models:** Normalizing flows, invertible transformations

**DeepMind Contributions:**
- WaveNet (raw audio generation)
- VQ-VAE (discrete latent representations)
- Gopher, Chinchilla (scaling laws for language models)

### Protein Folding & Computational Biology

**AlphaFold Architecture:**
- **Input:** Amino acid sequence, multiple sequence alignments
- **Evoformer:** Specialized transformer blocks for pairwise representations
- **Structure Module:** Iterative refinement of 3D coordinates
- **Output:** Predicted 3D coordinates and confidence metrics

**Related Work:**
- AlphaFold-Multimer (protein complexes)
- Protein design (generating new protein sequences)
- Drug discovery applications

### Scaling Laws & Efficient Training

**Key Papers:**
- "Scaling Laws for Neural Language Models" (Kaplan et al.)
- "Training Compute-Optimal Large Language Models" (Chinchilla, Hoffmann et al.)
- "Efficient Large-Scale Language Model Training on GPU Clusters"

**Key Concepts:**
- Compute-optimal training (Chinchilla optimal)
- Model parallelism strategies (tensor, pipeline, data parallel)
- Memory-efficient training (gradient checkpointing, offloading)
- Mixed precision training (FP16, BF16, FP8)

---

## Coding Expectations

### Algorithmic Coding

**Difficulty:** Medium-Hard LeetCode, sometimes LeetCode Hard

**Topics:**
- Dynamic programming (complex state definitions)
- Graph algorithms (shortest paths, connectivity, flows)
- Advanced tree data structures (segment trees, Fenwick trees, tries)
- String algorithms (KMP, suffix arrays, regex)
- Computational geometry (rare but possible)
- Concurrency and parallel algorithms

**Mathematical Fundamentals:**
- Probability theory — conditional probability, Bayes rule, expectations
- Linear algebra — matrix operations, eigenvalues, SVD
- Calculus — gradients, optimization, Lagrange multipliers
- Information theory — entropy, KL divergence, mutual information

**Study Approach:**
- LeetCode: Focus on medium DP and graph problems
- Cracking the Coding Interview for fundamentals
- Competitive programming background is a plus

### ML Coding

**Common Tasks:**
1. **Implement from scratch:**
   - Linear regression with gradient descent
   - Logistic regression
   - K-means clustering
   - PCA
   - Neural network with backpropagation
   - Attention mechanism

2. **Extensions and improvements:**
   - Add regularization (L1, L2, dropout)
   - Implement mini-batch training
   - Add momentum or Adam optimizer
   - Implement early stopping

3. **Debugging and optimization:**
   - Given buggy ML code, find and fix issues
   - Improve convergence of a training loop
   - Analyze and fix vanishing/exploding gradients

**Expectation:** Code must be correct, numerically stable, and reasonably efficient. You should be able to discuss the underlying math.

### JAX/Flax (Preferred at DeepMind)

**Why JAX:**
- Functional programming approach (no side effects)
- Automatic gradient computation (`grad`)
- JIT compilation (`jit`) for performance
- Vectorization (`vmap`) and parallelization (`pmap`)
- XLA compilation for TPU/GPU

**Key JAX Concepts:**
- Pure functions and transformations
- PRNG state management
- `vmap`, `pmap`, `shard_map`
- `pytree` structure
- Custom gradients with `custom_vjp`

### PyTorch/TensorFlow

While JAX is preferred, PyTorch knowledge is also valuable:
- Know PyTorch well enough for coding interviews
- Be familiar with TensorFlow concepts (especially if interviewing for product-facing roles)
- TPU usage is more common with TensorFlow/JAX than PyTorch

---

## Research Discussion

### Presenting Your Paper

**Structure:**
1. **Problem & Motivation (2-3 min):** What problem are you solving and why does it matter?
2. **Related Work (2 min):** How does your work differ from prior approaches?
3. **Method Details (5-7 min):** Precise description of your approach
4. **Experimental Setup (3-4 min):** Datasets, baselines, metrics
5. **Results (5 min):** Main results, ablation studies, analysis
6. **Discussion (2-3 min):** Limitations, future work, broader impact

**Anticipate Deep Questions:**
- "Why didn't you try X instead of Y?"
- "What happens if you remove component Z?"
- "Can you prove convergence of your method?"
- "How does this scale to larger models/datasets?"
- "What are the failure modes of your approach?"

### Analyzing Others' Research

**Interviewers May Ask You To:**
- Review a paper you haven't seen before (you'll be given time)
- Critique methodology and experimental design
- Suggest follow-up experiments
- Identify potential overclaims or statistical flaws
- Propose real-world applications

**Approach:**
1. Start with understanding the problem and why it's hard
2. Identify the key insight/contribution
3. Evaluate the experimental evidence — are the claims supported?
4. Think about alternative approaches
5. Consider limitations and edge cases

### Brainstorming Novel Research Ideas

**Interviewers may ask:** "Here's a problem. How would you approach it?"

**Process:**
1. **Clarify the problem:** Define inputs, outputs, constraints, success metrics
2. **Literature connection:** How does this relate to existing work?
3. **Propose approach:** Outline your method at a high level
4. **Justify choices:** Why this approach over alternatives?
5. **Anticipate challenges:** What might go wrong? How would you detect it?
6. **Evaluation plan:** How would you measure success?

---

## System Design Topics

### Large-Scale Training Systems

**Architecture Components:**
- **Data pipeline:** Sharded datasets, TFRecord/ArrayRecord format, data preprocessing (MapReduce/Beam)
- **Model parallelism:** Tensor parallelism (megatron-style), pipeline parallelism, data parallelism
- **Optimization:** ZeRO optimizer stages (ZeRO-1,2,3), FSDP
- **Checkpointing:** Fault-tolerant training, checkpoint compression
- **Monitoring:** Training metrics, hardware utilization, loss curves

**Scaling Challenges:**
- **Communication:** Network bandwidth between GPUs/TPUs
- **Memory:** Model weights, optimizer states, activations
- **Throughput:** Batch size scaling, gradient accumulation
- **Stability:** Loss spikes, NaN detection, automatic recovery

**DeepMind-Specific:**
- TPU pod topology (2D/3D torus)
- Configuration optimization for TPUs
- Custom collective communication (Google's NCCL equivalent)

### Inference Infrastructure

**Requirements:**
- Low latency for interactive applications
- High throughput for batch processing
- Cost efficiency at Google scale

**Design Decisions:**
- Batching strategies (static vs dynamic, continuous batching)
- Quantization (INT8, FP8, weight-only quantization)
- Pruning and distillation
- KV-cache management
- Speculative decoding
- Model sharding across TPU/GPU pods

### Data Pipeline Design

**Key Considerations:**
- Data sources (web crawl, scientific databases, proprietary data)
- Cleaning and deduplication at scale
- Privacy and PII redaction
- Training-validation-test splits
- Data versioning and lineage
- A/B testing infrastructure

**DeepMind Scale:**
- Petabytes of training data
- Thousands of preprocessing workers
- Real-time data augmentation
- Automated quality filtering

### ML Platform Design

**Components:**
- Experiment tracking (metadata store, metric logging)
- Hyperparameter optimization (Bayesian optimization, population-based training)
- Model registry and versioning
- Evaluation framework (benchmark suites, custom metrics)
- Model deployment and serving
- Monitoring and alerting

---

## Behavioral Questions

### Intellectual Curiosity

**Expected Questions:**
- "Tell me about a research problem that fascinated you."
- "What's a paper outside your area that inspired you?"
- "How do you stay current with AI research?"
- "Describe a time you went deep on a topic outside your expertise."

**How to Answer Well:**
- Show genuine excitement for learning
- Connect interests across domains
- Demonstrate sustained engagement (not just breadth)
- Reference specific examples of self-directed learning

### Interdisciplinary Approach

**Expected Questions:**
- "How have you incorporated ideas from other fields into your work?"
- "Tell me about a collaboration with someone from a different discipline."
- "What field outside CS/AI has influenced your thinking?"
- "How do you communicate technical concepts across disciplines?"

**How to Answer Well:**
- Give concrete examples of cross-disciplinary work
- Show respect and curiosity for other fields
- Demonstrate ability to translate between domains
- Acknowledge what you learned from collaborators

### Collaboration & Teamwork

**Expected Questions:**
- "Describe a difficult collaboration and how you made it work."
- "How do you handle disagreement about research methodology?"
- "Tell me about a time someone challenged your results."
- "How do you mentor junior researchers or engineers?"

**How to Answer Well:**
- Use specific, detailed examples
- Show self-awareness and growth
- Demonstrate generosity toward collaborators
- Balance confidence with openness to feedback

### Research Philosophy

**Expected Questions:**
- "What makes a research problem worth solving?"
- "How do you decide between pursuing a high-risk, high-reward idea vs a safer incremental project?"
- "What's your approach to experimental design?"
- "How do you deal with negative results?"

**How to Answer Well:**
- Show structured thinking about research strategy
- Demonstrate resilience and learning from failure
- Balance ambition with rigor
- Connect to DeepMind's mission

---

## Sample Questions & Answers

### Technical: Reinforcement Learning

**Q:** "Derive the policy gradient theorem. Explain how it's used in practice."

**A:** "The policy gradient theorem provides a way to estimate the gradient of the expected return with respect to policy parameters without differentiating through the environment dynamics.

**Derivation:**
Start with the objective: J(θ) = E[Σ γt rt | πθ]

The gradient can be shown to be:
∇J(θ) = E[∇log πθ(a|s) * Qπ(s,a)]

Key insight: the gradient doesn't depend on the derivative of the state distribution, only on the policy itself.

**In practice:**
- REINFORCE: Use Monte Carlo returns to estimate Q
- Actor-Critic: Use a learned value function as baseline (reduces variance)
- PPO: Clips updates to maintain trust region
- SAC: Adds entropy bonus for exploration"

### Technical: AlphaFold

**Q:** "Explain the Evoformer module in AlphaFold. Why is it effective for protein structure prediction?"

**A:** "The Evoformer is a specialized neural network block that processes two core representations simultaneously:

1. **MSA (Multiple Sequence Alignment) representation:** Rows = sequences, Columns = residue positions
2. **Pair representation:** Pairwise features between all residue positions

**Key innovations:**
- **Paired attention:** MSA rows attend to each other, with pair information biasing the attention
- **Outer product mean:** Updates pair representation using MSA information
- **Triangle attention:** Triangular multiplicative updates enforce geometric consistency

**Why it works:**
- Proteins evolve as a whole — the MSA captures coevolutionary information
- Pair representation enforces 3D spatial constraints (distance, angles)
- The triangular structure respects that protein geometry is 3D (triangle inequality)"
- Iterative refinement through 48 Evoformer blocks builds progressively better representations"

### Coding: ML Implementation

**Q:** "Implement K-means clustering from scratch. How would you extend it to handle large-scale data?"

**Key points for solution:**
- Initialize centroids (K-means++ initialization)
- Expectation step: assign points to nearest centroid
- Maximization step: recompute centroids
- Convergence criteria

**Large-scale extensions:**
- Mini-batch K-means
- K-means with approximate nearest neighbors
- Using LSH for efficient assignment
- MapReduce implementation for distributed data

### Research: Idea Generation

**Q:** "You want to train an RL agent that can play any Atari game without seeing the reward function. How would you approach this?"

**A thought process:**
1. **Inverse RL problem:** Learn reward function from demonstrations or exploration
2. **Options:**
   - Imitation learning from human gameplay
   - Curiosity-driven exploration with learned intrinsic rewards
   - Meta-RL that adapts to new reward structures quickly
   - Causal reasoning about game mechanics
3. **Evaluation:** How do you measure success?
4. **Challenges:** Diversity of games, sparse rewards in many Atari games, generalizing across game types

---

## Resources & Further Reading

### Must-Read DeepMind Papers
1. "Human-level control through deep reinforcement learning" (DQN, Nature 2015)
2. "Mastering the game of Go with deep neural networks and tree search" (AlphaGo, Nature 2016)
3. "Highly accurate protein structure prediction with AlphaFold" (Nature 2021)
4. "Emergent abilities of large language models" (2022)
5. "Training compute-optimal large language models" (Chinchilla, 2022)
6. "Gemini: A Family of Highly Capable Multimodal Models" (2023)
7. "Mastering Atari, Go, Chess and Shogi by Planning with a Learned Model" (MuZero, 2020)

### Foundational Papers
1. "Attention Is All You Need" (Vaswani et al., 2017)
2. "Deep Learning" (Goodfellow, Bengio, Courville) — textbook
3. "Reinforcement Learning: An Introduction" (Sutton & Barto) — textbook
4. "Pattern Recognition and Machine Learning" (Bishop) — textbook

### Preparation Resources
- LeetCode (Hard level algorithm problems)
- DeepMind's open-source releases (JAX, Haiku, Optax)
- DeepMind Podcast (interviews with researchers)
- NeurIPS/ICML proceedings for latest AI research

### Technical Areas to Master
1. Reinforcement Learning (theory + algorithms)
2. Deep Learning (transformers, CNNs, GNNs)
3. Optimization (SGD variants, second-order methods)
4. Probability and Statistics (Bayesian methods, hypothesis testing)
5. Linear Algebra (SVD, eigenvalues, matrix calculus)
6. Information Theory (entropy, mutual information)

---

*Preparing for DeepMind requires deep technical knowledge, strong mathematical foundations, and the ability to think creatively about research problems. Focus on understanding the fundamentals deeply rather than memorizing surface-level facts. Good luck!*
