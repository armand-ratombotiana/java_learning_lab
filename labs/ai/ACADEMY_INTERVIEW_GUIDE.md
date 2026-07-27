# AI/ML Academy Interview Guide

## Overview

This guide covers interview preparation for AI/ML roles across major companies. Each section details role types, interview rounds, and company-specific expectations.

---

## Table of Contents

1. [Google (DeepMind, Gemini)](#1-google-deepmind-gemini)
2. [Meta (FAIR, Llama)](#2-meta-fair-llama)
3. [OpenAI](#3-openai)
4. [Anthropic](#4-anthropic)
5. [Microsoft (Copilot)](#5-microsoft-copilot)
6. [Amazon (Alexa, SageMaker)](#6-amazon-alexa-sagemaker)
7. [Apple (ML/Intelligence)](#7-apple-mlintelligence)
8. [NVIDIA](#8-nvidia)
9. [Databricks](#9-databricks)
10. [Scale AI](#10-scale-ai)
11. [Cohere](#11-cohere)
12. [Mistral](#12-mistral)
13. [Hugging Face](#13-hugging-face)

---

## 1. Google (DeepMind, Gemini)

### Role Types

**ML Engineer**
- Builds production ML systems serving billions of users
- Focus on Search, YouTube, Ads, Google Cloud AI
- Requires strong software engineering + ML fundamentals
- Level range: L3 (entry) through L8 (Distinguished)

**AI Researcher (DeepMind/Google Research)**
- Publishes at NeurIPS, ICML, ICLR, CVPR
- Works on fundamental research (transformers, scaling, RL, multimodal)
- PhD required or equivalent publication record
- Google Brain, DeepMind, or Google Research divisions

**Applied Scientist**
- Bridges research and product
- Adapts SOTA models for Google products
- Strong publication record expected
- Works with product teams to deploy ML systems

**ML Infrastructure Engineer**
- Builds training/serving infrastructure
- Works on TPU pods, data pipelines, feature stores
- Expertise in distributed systems, Kubernetes, GPU/TPU optimization

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| ML Coding | 45 min | Implement ML algorithms from scratch, numpy-style |
| ML System Design | 45 min | Design end-to-end ML systems |
| Research Deep Dive | 45 min | Present and discuss past research/projects |
| Algorithm Coding | 45 min | Standard LeetCode (hard/medium) |
| Googleyness | 30 min | Leadership, ambiguity, collaboration |
| Technical Phone Screen | 45 min | ML fundamentals + coding |

### Key Preparation Areas

- **TPU vs GPU**: Understand TPU architecture and when to use each
- **Scalable ML**: Parameter servers, data parallelism, model parallelism
- **Gemini**: Multimodal architecture, MoE, context window
- **Transformers**: Deep understanding of attention mechanisms
- **Distributed Training**: Ring all-reduce, gradient accumulation, ZeRO
- **Google Stack**: TensorFlow, JAX, Kubeflow, Dataflow, BigQuery

### Sample Questions

1. Design a YouTube recommendation system serving billions of users
2. Implement multi-head attention from scratch
3. How would you train a 1 trillion parameter model across TPU pods?
4. Design a real-time fraud detection system for Google Pay
5. Explain how AlphaGo/AlphaFold works and key innovations
6. How would you evaluate search quality improvements with online metrics?

### Resources

- DeepMind papers (AlphaFold, AlphaGo, Gemini, Gato, Chinchilla)
- Google ML Crash Course
- JAX documentation and tutorials
- "Designing Data-Intensive Applications" (Kleppmann)

---

## 2. Meta (FAIR, Llama)

### Role Types

**ML Engineer (Product)**
- Applied ML on Facebook, Instagram, WhatsApp, Ads
- Focus on recommendation systems, ranking, ads optimization
- Works with billions of daily active users
- Strong coding expectations (LeetCode hard)

**AI Research Scientist (FAIR)**
- Fundamental research at Facebook AI Research
- Publishes at top venues (NeurIPS, ICML, CVPR, EMNLP)
- Open source contributions (PyTorch, Llama, Detectron)
- PhD required for research roles

**Applied ML Scientist**
- Develops new ML models for Meta products
- Combines research with production impact
- Strong publication record + engineering skills
- Works on content understanding, integrity, AR/VR

**ML Infra/Platform Engineer**
- Builds PyTorch, ONNX, Glow, FBLearner Flow
- Distributed training infrastructure
- GPU cluster scheduling and optimization

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| ML System Design | 45 min | Design large-scale ML systems |
| Coding (Algorithm) | 45 min | LeetCode medium/hard (Python/Java/C++) |
| ML Coding | 45 min | Implement ML algorithms from scratch |
| Behavioral (ML) | 30 min | ML project deep dive, failures, impact |
| Research Presentation | 45 min | Present past research (research roles) |
| Technical Screen | 45 min | ML fundamentals + system design basics |

### Key Preparation Areas

- **Recommendation Systems**: Collaborative filtering, deep retrieval, DLRM
- **PyTorch**: Deep understanding of autograd, distributed, JIT
- **Llama Architecture**: Grouped query attention, RoPE, SwiGLU
- **Social Graph ML**: Graph neural networks, Node2Vec, GraphSAGE
- **Large Scale Training**: FSDP, activation checkpointing, tensor parallelism
- **Ads Ranking**: CTR prediction, multi-task learning, real-time bidding

### Sample Questions

1. Design the Facebook News Feed ranking system
2. Implement a transformer layer from scratch in PyTorch
3. How does Meta train Llama 3 across 16K+ GPUs?
4. Design a real-time ad auction system with ML
5. How would you detect hate speech at Meta's scale?
6. Explain PyTorch's distributed data parallel implementation

### Resources

- FAIR publications (ConvNets, Detection, Segment Anything)
- Llama 3 paper and architecture blog posts
- PyTorch distributed tutorial
- "Recommender Systems Handbook"

---

## 3. OpenAI

### Role Types

**Research Scientist**
- Pushes frontier of AI capabilities
- Works on GPT, DALL-E, Whisper, Codex
- Publishes research (but may be delayed for safety)
- PhD or equivalent research experience

**Applied ML Engineer**
- Builds production systems around OpenAI APIs
- Works on safety, alignment, reliability
- Optimizes inference, latency, cost
- Strong software engineering background

**Member of Technical Staff (MTS)**
- Generalist AI engineer/researcher
- Works across multiple teams
- Highly selective, expects breadth + depth

**Safety Researcher**
- Alignment, interpretability, robustness
- RLHF, constitutional AI, red-teaming
- Focus on understanding and controlling AI systems

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| Research Discussion | 60 min | Discuss past papers, technical depth |
| ML Coding | 45 min | Implement models, training loops |
| System Design | 45 min | Design ML training/serving systems |
| General Coding | 45 min | Algorithms, data structures |
| Behavioral | 45 min | Safety mindset, research taste, collaboration |
| Take-home (some roles) | Varies | Research project or implementation |

### Key Preparation Areas

- **Transformer Architecture**: Deep understanding of GPT series
- **Scaling Laws**: Chinchilla, compute-optimal training
- **RLHF**: PPO, reward modeling, DPO
- **Safety**: Red-teaming, jailbreaks, alignment faking
- **Multimodal**: GPT-4V/Vision, DALL-E, audio
- **Reasoning**: Chain-of-thought, self-consistency, tree-of-thought

### Sample Questions

1. Implement GPT-2 from scratch including training loop
2. Design a system to detect and prevent prompt injection
3. How would you scale training to 100K GPUs?
4. Explain the KL divergence term in RLHF and why it matters
5. Design a reliable evaluation pipeline for LLM capabilities
6. How would you detect malicious use of an API?

### Resources

- OpenAI publications (GPT-1/2/3/4, InstructGPT, DALL-E, Whisper)
- Andrej Karpathy's tutorials (nanoGPT, Zero to Hero)
- "Scaling Laws for Neural Language Models" (Kaplan et al.)
- "Training Language Models to Follow Instructions" (Ouyang et al.)

---

## 4. Anthropic

### Role Types

**Research Scientist (Alignment)**
- Safety and alignment research
- Constitutional AI, interpretability, mechanistic interpretability
- Strong mathematical background
- Publishes alignment-focused research

**Applied ML Engineer**
- Builds Claude API and product
- Optimizes inference, latency, safety systems
- Works on evaluations, red-teaming
- Strong engineering + ML background

**Research Engineer**
- Bridges research and engineering
- Implements new architectures and training methods
- Runs large-scale experiments
- Comfortable with distributed systems

**Safety/Policy Researcher**
- AI governance, responsible scaling
- Safety evaluations and benchmarking
- Policy and standards development

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| Research Deep Dive | 60 min | Aligned research, papers discussion |
| ML Coding | 45 min | Implementation and analysis |
| System Design | 45 min | ML infrastructure, safety systems |
| Coding | 45 min | Python/C++ algorithms |
| Behavioral | 45 min | Safety motivations, research taste |
| Technical Screen | 45 min | ML fundamentals + alignment basics |

### Key Preparation Areas

- **Constitutional AI**: Self-play, RLAIF, harmlessness training
- **Interpretability**: Activation patching, SAEs, logit lens
- **Safety**: Responsible scaling, capability evaluations
- **Claude**: Model architecture, context window, tool use
- **Mechanistic Interpretability**: Circuits, features, superposition
- **RLHF vs Constitutional AI**: Differences, advantages

### Sample Questions

1. Design a safety evaluation pipeline for a new model release
2. Implement a sparse autoencoder for interpretability
3. How would you design an AI system with guaranteed safety properties?
4. Explain the limitations of RLHF and how Constitutional AI addresses them
5. Design a monitoring system for detecting alignment failures
6. How would you measure "honesty" in a language model?

### Resources

- Anthropic publications (Constitutional AI, Interpretability)
- "Toy Models of Superposition" (Elhage et al.)
- Anthropic's safety policy documents
- Mechanistic interpretability tutorials

---

## 5. Microsoft (Copilot)

### Role Types

**ML Engineer (Azure AI/Copilot)**
- Builds AI features into Microsoft products
- Copilot across Office 365, GitHub, Azure
- Strong engineering + ML integration skills

**Applied AI Scientist**
- Advances ML research at MSR (Microsoft Research)
- Publishes at top venues
- PhD preferred
- Works on language, vision, multimodal

**ML/AI Infrastructure Engineer**
- Azure ML platform, ONNX Runtime
- Large-scale training infrastructure
- MLOps and deployment pipelines

**Research Intern**
- Work on impactful research problems
- Publication opportunity
- Mentorship from MSR researchers

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| ML Design | 45 min | ML system architecture |
| Coding | 45 min | General algorithms (C#/Python/C++) |
| ML Coding | 45 min | Implement ML algorithms |
| Research Discussion | 45 min | Research roles: papers presentation |
| Behavioral | 30 min | Collaboration, growth mindset |
| System Design | 45 min | Distributed systems (infra roles) |

### Key Preparation Areas

- **Copilot Stack**: Prompt flow, retrieval augmentation, fine-tuning
- **Azure ML**: Pipeline, AutoML, responsible AI
- **ONNX**: Model optimization, quantization, ONNX Runtime
- **Turing Models**: Microsoft's large language models
- **DeepSpeed**: ZeRO optimization, model parallelism
- **Responsible AI**: Fairness, interpretability, privacy

### Sample Questions

1. Design GitHub Copilot's code suggestion system
2. How would you finetune a model for Microsoft Office domain?
3. Design a multi-tenant ML serving system on Azure
4. Implement LoRA fine-tuning from scratch
5. How would you measure and reduce bias in search results?
6. Design a system for continuous model retraining with data drift

### Resources

- Microsoft Research publications
- DeepSpeed documentation
- ONNX Runtime tutorials
- Azure ML documentation

---

## 6. Amazon (Alexa, SageMaker)

### Role Types

**Applied Scientist**
- ML models for products (Alexa, Search, Recommendation)
- Strong research background + engineering
- PhD required or equivalent publications

**ML Engineer (SageMaker/AWS AI)**
- Builds ML platforms and tools
- SageMaker, Bedrock, Rekognition, Comprehend
- Strong software engineering background

**Data Scientist (ML)**
- Applied ML on business problems
- Recommendation, forecasting, personalization
- Less research, more application

**Research Scientist (AWS AI/AGI)**
- Fundamental research
- Alexa Prize, AGI research
- Publications at top venues

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| ML Deep Dive | 60 min | In-depth ML fundamentals |
| Coding | 45 min | LeetCode medium/hard |
| ML System Design | 45 min | End-to-end ML pipelines |
| Bar Raiser | 60 min | Behavioral + leadership principles |
| Research Presentation | 45 min | Past research/projects |
| Technical Phone Screen | 60 min | ML + coding basics |

### Key Preparation Areas

- **Leadership Principles**: Customer obsession, ownership, dive deep
- **Alexa AI**: ASR, NLU, TTS, wake word detection
- **SageMaker**: Platform features, built-in algorithms, deployment
- **Bedrock**: Foundation model access, agents, RAG
- **Personalization**: Item-to-item CF, deep learning recommenders
- **Forecasting**: Demand forecasting, time-series at scale

### Sample Questions

1. Design Alexa's conversational AI pipeline (ASR -> NLU -> TTS)
2. How would you build a fraud detection system for Amazon?
3. Design an ML system for Amazon product recommendations
4. Implement K-means clustering from scratch with optimizations
5. How would you detect and handle out-of-distribution inputs?
6. Design a demand forecasting system for inventory management

### Resources

- Amazon Science publications
- AWS Machine Learning documentation
- "Working Backwards" (Amazon leadership)
- Amazon JP (Justification Papers) examples

---

## 7. Apple (ML/Intelligence)

### Role Types

**ML Engineer (Siri/Core ML)**
- On-device ML for Apple products
- Siri, Photos, Face ID, Keyboard
- Privacy-preserving ML expertise
- Strong C++/Objective-C/Swift skills

**ML Research Scientist**
- Apple's ML research teams
- Publishes at top venues
- Works on computer vision, NLP, speech
- PhD required

**Applied ML Engineer**
- Brings research to production
- Model optimization for on-device
- Core ML Tools, quantization, pruning
- Cross-functional collaboration

**Data Scientist**
- ML for Apple operations
- Supply chain, retail, marketing
- Less focus on publications

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| ML Coding | 45 min | Numeric algorithms, ML from scratch |
| System Design | 45 min | On-device ML system design |
| Coding (C/C++) | 45 min | Performance-critical implementation |
| Research Presentation | 45 min | Past work (research roles) |
| Behavioral | 30 min | Cross-team collaboration |
| Privacy in ML | 30 min | Differential privacy, federated learning |

### Key Preparation Areas

- **On-device ML**: Core ML, ML Compute, ANE (Apple Neural Engine)
- **Privacy**: Differential privacy, federated learning, on-device processing
- **Vision**: Face ID, ARKit, VN frameworks
- **Siri**: ASR, NLU, on-device models
- **Model Optimization**: Quantization (INT8), pruning, distillation
- **Metal Performance Shaders**: GPU computation, MPS backend

### Sample Questions

1. Design an on-device photo classification system with privacy
2. Implement a face detection pipeline optimized for mobile
3. How would you train a model using federated learning on iOS?
4. Design a keyboard autocomplete system running entirely on-device
5. Implement model quantization (FP32 -> INT8) with calibration
6. How would you handle model updates without user data leaving the device?

### Resources

- Apple ML Research publications
- Core ML documentation and WWDC sessions
- "Differential Privacy" (Dwork & Roth)
- Apple's Privacy Policy / On-device processing

---

## 8. NVIDIA

### Role Types

**ML Engineer (Deep Learning)**
- Optimizes deep learning models
- Works on libraries (cuDNN, TensorRT, cuBLAS)
- Strong C++/CUDA skills
- Understands GPU architecture deeply

**Data Scientist (Applied ML)**
- ML for NVIDIA's internal tools
- Recommendation, forecasting
- Python, PyTorch, RAPIDS

**AI Research Scientist**
- NVIDIA Research
- Publishes at top venues
- Robotics, autonomous vehicles, graphics
- Strong publication record

**ML Platform Engineer**
- Builds training/serving infrastructure
- GPU cluster management, MLOps
- Software engineering focus

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| CUDA/Math Coding | 60 min | GPU kernel implementation |
| ML Algorithms | 45 min | Deep ML understanding |
| System Design | 45 min | GPU cluster, distributed training |
| Coding | 45 min | C++/Python algorithms |
| Behavioral | 30 min | Collaboration, innovation |
| Research Presentation | 45 min | Research roles |

### Key Preparation Areas

- **CUDA Programming**: Kernel launches, shared memory, warp-level primitives
- **GPU Architecture**: SM, tensor cores, memory hierarchy, NVLink
- **cuDNN**: Convolution algorithms, memory optimization
- **TensorRT**: Model optimization, calibration, deployment
- **NCCL**: All-reduce, ring topology, multi-GPU communication
- **Megatron-LM**: Model parallelism, tensor broadcasting
- **RAPIDS**: cuDF, cuML, GPU-accelerated data science

### Sample Questions

1. Write a CUDA kernel for matrix multiplication with shared memory
2. How would you optimize training of Llama 3 on H100 GPUs?
3. Design a GPU cluster for training a multimodal foundation model
4. Implement a fused attention kernel in CUDA
5. How does tensor core perform matrix multiply-accumulate?
6. Design a gradient communication scheme for 1000+ GPU training

### Resources

- CUDA Programming Guide
- NVIDIA Deep Learning Performance Guide
- Megatron-LM and NeMo documentation
- GPU Gems series

---

## 9. Databricks

### Role Types

**ML Engineer (Mosaic AI)**
- Builds ML tools and platforms
- Mosaic AI, Model Serving, Feature Store
- PySpark, MLflow, Unity Catalog

**Applied ML Scientist**
- Applied research on ML methods
- Focus on LLMs, RAG, agents
- Publications + engineering skills

**Data Scientist (ML)**
- Customer-facing ML work
- Solution architecture
- Strong communication skills

**ML Infra Engineer**
- Large-scale data/ML infrastructure
- Apache Spark, Delta Lake
- Distributed systems focus

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| ML Coding | 45 min | Spark ML, MLflow, implementation |
| System Design | 45 min | Data + ML pipelines |
| Coding (Algorithms) | 45 min | Python/Java/Scala |
| ML Fundamentals | 45 min | Deep ML knowledge |
| Behavioral | 30 min | Collaboration, customer focus |
| Product Sense | 30 min | ML product intuition |

### Key Preparation Areas

- **Apache Spark**: MLlib, Spark SQL, DataFrame API
- **MLflow**: Experiment tracking, model registry, deployment
- **Delta Lake**: Lakehouse architecture, CDC
- **Feature Store**: Feast, feature engineering
- **LLMOps**: Prompt engineering, RAG, fine-tuning
- **Unity Catalog**: Data governance, lineage

### Sample Questions

1. Design a feature engineering pipeline at petabyte scale
2. Implement distributed training with PySpark and MLflow
3. How would you build a Lakehouse for ML training?
4. Design a real-time model serving system with feature freshness
5. Implement a custom UDF for feature transformation in Spark
6. Design an experiment tracking system for 1000+ experiments/day

### Resources

- Databricks ML documentation
- "Learning Spark" (Spark documentation)
- MLflow documentation
- Delta Lake architecture docs

---

## 10. Scale AI

### Role Types

**ML Engineer**
- Builds ML models for data labeling and quality
- Improves model accuracy through data
- Works on foundation model evaluation

**Data Engine Engineer**
- Platform for RLHF data generation
- LLM evaluation and testing frameworks
- Data quality and diversity optimization

**Applied Research Engineer**
- Advances ML techniques for data efficiency
- Active learning, model-assisted labeling
- Research + production balance

**Customer ML Engineer**
- Works with enterprise customers
- Custom ML solutions for data pipelines
- Consulting + engineering mix

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| ML Coding | 45 min | Custom model implementation |
| System Design | 45 min | Data labeling platform design |
| Coding | 45 min | General algorithms |
| Product Sense | 30 min | Data quality and efficiency |
| Behavioral | 30 min | Collaboration, autonomy |

### Key Preparation Areas

- **RLHF Data**: Preference data, reward modeling
- **Active Learning**: Uncertainty sampling, diversity sampling
- **Model Evaluation**: Benchmarking, human evaluation
- **Data Quality**: Inter-annotator agreement, quality metrics
- **Foundation Models**: GPT, Claude, Gemini evaluation
- **Labeling Tools**: UI/UX for data annotation

### Sample Questions

1. Design a platform for collecting RLHF preference data
2. How would you detect and improve low-quality labels in a dataset?
3. Design a model-assisted labeling tool that learns from annotators
4. Implement active learning for text classification
5. How would you evaluate the safety of a new LLM release?
6. Design a benchmarking system for vision-language models

### Resources

- Scale AI blog (data engine, RLHF)
- "Human-in-the-loop Machine Learning" (Monarch)
- Active learning literature

---

## 11. Cohere

### Role Types

**ML Researcher**
- Advances foundational models
- Training, retrieval, multimodal
- Publications at top venues

**ML Engineer (Platform)**
- Model training/serving infrastructure
- Distributed training, optimization
- Strong engineering background

**Applied ML Engineer**
- Customer deployments
- RAG, embedding customization
- Solution prototyping

**Research Engineer**
- Research + Engineering combined
- Implements and experiments with new ideas
- Strong both in theory and practice

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| ML System Design | 45 min | Training/serving architecture |
| ML Coding | 45 min | Model implementation |
| Research Discussion | 45 min | Technical depth, papers |
| Coding | 45 min | Python algorithms |
| Behavioral | 30 min | Team collaboration |

### Key Preparation Areas

- **Embeddings**: Semantic search, cosine similarity, hybrid search
- **RAG Architecture**: Retrieval, re-ranking, generation
- **Command Models**: Generation, chat, summarization
- **Model Optimization**: Quantization, distillation
- **Search**: Dense retrieval, sparse retrieval (BM25)

### Sample Questions

1. Design a retrieval-augmented generation system for enterprise search
2. Implement embedding-based semantic search from scratch
3. How would you evaluate RAG quality end-to-end?
4. Design an efficient nearest neighbor search system
5. Implement a two-stage retrieval + reranking pipeline
6. How would you customize embeddings for a specific domain?

### Resources

- Cohere research papers
- "Retrieval-Augmented Generation" (Lewis et al.)
- Vector database documentation (Pinecone, Weaviate, Qdrant)

---

## 12. Mistral

### Role Types

**Research Scientist**
- Efficient LLM architectures
- MoE, sparse transformers, quantization
- Publications at top venues

**ML Engineer**
- Training infrastructure
- Open source model development
- Distributed training optimization

**Applied ML Engineer**
- Mistral APIs and products
- Le Chat, enterprise deployments
- Customer solutions

**Research Engineer**
- Combined research and engineering
- Runs experiments, analyzes results
- Implements novel architectures

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| Research Discussion | 60 min | Papers, efficiency research |
| ML Coding | 45 min | Efficient implementation |
| System Design | 45 min | Training infrastructure |
| Coding | 45 min | Python/C++ algorithms |
| Behavioral | 30 min | French tech culture |

### Key Preparation Areas

- **MoE (Mixture of Experts)**: Sparse routing, load balancing
- **Sliding Window Attention**: Efficient attention patterns
- **Quantization**: GPTQ, AWQ, GGUF
- **Efficient Architectures**: Compound attention, SWA
- **Open Source**: Model release, community building
- **Inference Optimization**: vLLM, TensorRT, CTranslate2

### Sample Questions

1. Implement a Mixture of Experts transformer layer
2. Design a KV cache optimization for long context inference
3. How would you train an efficient 7B model that beats competitors?
4. Implement sliding window attention with block sparsity
5. Design an inference server handling 100K+ requests/minute
6. How does quantization affect model quality vs latency?

### Resources

- Mistral research papers (Mixtral, Mistral 7B)
- "Mixture of Experts" tutorials (Switch Transformer)
- vLLM documentation

---

## 13. Hugging Face

### Role Types

**ML Engineer (Open Source)**
- Maintains Transformers, Diffusers, PEFT libraries
- Community engagement
- Open source development skills

**Research Scientist**
- Advances ML research
- Public datasets and benchmarks
- Publications + open source

**ML Developer Advocate**
- Creates educational content
- Community building
- Technical communication

**Infrastructure Engineer**
- Inference API, Spaces, Hub
- Large-scale serving
- Distributed systems

### Interview Rounds

| Round | Duration | Focus |
|-------|----------|-------|
| ML Coding | 45 min | Transformers library contribution |
| System Design | 45 min | Model serving infrastructure |
| Coding | 45 min | Python algorithms |
| Open Source | 30 min | Previous contributions |
| Behavioral | 30 min | Community values |

### Key Preparation Areas

- **Transformers Library**: Architecture, customization, training
- **PEFT**: LoRA, QLoRA, Adapters, Prefix Tuning
- **Diffusers**: Stable Diffusion pipeline, scheduler
- **Tokenizers**: Training, optimization, fast tokenizers
- **Model Hub**: Upload, versioning, community
- **Spaces**: Gradio apps, GPU hosting

### Sample Questions

1. Contribute a new model architecture to Transformers library
2. Design the architecture for Hugging Face Inference API
3. Implement LoRA fine-tuning compatible with PEFT library
4. How would you design a dataset versioning system?
5. Design a pipeline for community-contributed model evaluation
6. Implement a CustomPipeline for a novel task

### Resources

- Transformers source code
- Hugging Face documentation and course
- PEFT library codebase
- "Natural Language Processing with Transformers" (Tunstall et al.)
