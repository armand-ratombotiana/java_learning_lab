# Capstone Projects Index

![Status](https://img.shields.io/badge/status-active-success.svg)
![Capstones](https://img.shields.io/badge/capstones-8-blue)
![Java](https://img.shields.io/badge/Java-21+-red)
![Scope](https://img.shields.io/badge/scope-portfolio--grade-brightgreen)

## Overview

Eight portfolio-grade capstone projects demonstrating mastery of distributed systems, e-commerce, message brokers, vector databases, RAG pipelines, ML platforms, AI agents, and distributed compute engines. Each project includes 24 documentation files, 7 subdirectories, Java source code with real business logic, and comprehensive JUnit 5 tests.

## Capstone Projects

| # | Project | Description | Java Classes | Tests | Tech Stack |
|---|---------|-------------|--------------|-------|------------|
| 01 | [E-Commerce Platform](./01-ecommerce-platform) | Full-stack e-commerce with product catalog, shopping cart, order state machine, payment processing, inventory management, collaborative filtering recommendations, admin analytics | ProductCatalog, ShoppingCart, OrderStateMachine, PaymentProcessor, InventoryManager, RecommendationEngine, AdminAnalytics | 7 test classes, 50+ test methods | Java 21, BigDecimal, ConcurrentHashMap, JUnit 5 |
| 02 | [Distributed Cache](./02-distributed-cache) | Mini distributed cache from scratch: consistent hashing ring, pluggable eviction (LRU/LFU/TTL), partition management, replication, gossip protocol | ConsistentHashRing, EvictionPolicy, PartitionManager, CacheClient, ReplicationManager, GossipProtocol | 5 test classes, 30+ test methods | Java 21, MD5 hashing, ConcurrentSkipListMap, ThreadPool |
| 03 | [Mini Kafka](./03-mini-kafka) | Kafka-like message broker with topic/partition management, sync/async producer, consumer with group coordination, log compaction, offset management | TopicPartition, MessageBroker, ProducerClient, ConsumerClient, ConsumerGroup, LogSegment, OffsetManager | 6 test classes, 35+ test methods | Java 21, CopyOnWriteArrayList, ScheduledExecutor, DataOutputStream |
| 04 | [Vector Database](./04-vector-database) | Vector indexing with brute-force + HNSW approximate search, cosine/L2/inner product similarity, metadata filtering, file-based persistence | VectorIndex, HNSWGraph, CosineSimilarity, VectorStore | 4 test classes, 25+ test methods | Java 21, HNSW algorithm, Java serialization, PriorityQueue |
| 05 | [RAG Platform](./05-rag-platform) | Complete RAG pipeline: document ingestion, chunking (fixed/semantic/recursive), embedding interface, vector store, dense/hybrid retrieval, reranking, context assembly, evaluation metrics | DocumentIngestor, ChunkingStrategy, EmbeddingInterface, VectorStore, Retriever, Reranker, ContextBuilder, RAGEvaluator | 6 test classes, 30+ test methods | Java 21, Mock embeddings, Cosine similarity, Stream API |
| 06 | [ML Platform](./06-ml-platform) | ML lifecycle platform: feature store (online/offline), training pipeline, experiment tracking, model registry, REST serving, drift detection (PSI), A/B testing | FeatureStore, TrainingPipeline, ExperimentTracker, ModelRegistry, ModelServer, DriftDetector, ABTestFramework | 7 test classes, 35+ test methods | Java 21, PSI statistics, ConcurrentHashMap, ThreadPool |
| 07 | [Autonomous Agent Platform](./07-autonomous-agent-platform) | AI agent runtime with observe-think-act loop, tool registry, short/long-term memory, ReAct planning, multi-agent orchestration, monitoring | AgentRuntime, ToolRegistry, AgentMemory, PlanningEngine, MultiAgentOrchestrator, AgentMonitor | 6 test classes, 30+ test methods | Java 21, CompletableFuture, CountDownLatch, CopyOnWriteArrayList |
| 08 | [Mini Spark](./08-mini-spark) | Simplified Spark engine: RDD abstraction, transformations (map/filter/flatMap/reduceByKey), actions (collect/count/reduce), hash shuffle, DAG scheduler, task execution | RDD, PairRDD, SparkContext, DAGScheduler, TaskExecutor, ShuffleManager | 6 test classes, 35+ test methods | Java 21, Parallel streams, ForkJoinPool, CompletableFuture |

## Structure

Each capstone follows a consistent structure:

```
XX-capstone-name/
├── README.md              # Project overview and getting started
├── THEORY.md              # In-depth concept explanations
├── MATH_FOUNDATION.md     # Mathematical concepts and formulas
├── CODE_DEEP_DIVE.md      # Annotated implementation walkthroughs
├── ARCHITECTURE.md        # System design and component architecture
├── SECURITY.md            # Security considerations
├── PERFORMANCE.md         # Performance analysis and optimization
├── REFACTORING.md         # Code refactoring patterns
├── DEBUGGING.md           # Debugging strategies
├── COMMON_MISTAKES.md     # Frequent pitfalls
├── STEP_BY_STEP.md        # Implementation guide
├── VISUAL_GUIDE.md        # Visual diagrams
├── INTERNALS.md           # Internal workings
├── HOW_IT_WORKS.md        # High-level explanation
├── MENTAL_MODELS.md       # Conceptual models
├── HISTORY.md             # Historical context
├── WHY_IT_MATTERS.md      # Real-world impact
├── WHY_IT_EXISTS.md       # Problem domain
├── REFERENCES.md          # External resources
├── REFLECTION.md          # Self-assessment prompts
├── INTERVIEW.md           # Interview questions
├── FLASHCARDS.md          # Key concept review cards
├── EXERCISES.md           # Practice exercises
├── QUIZ.md                # Knowledge assessment
├── BENCHMARK/             # Performance benchmarks
├── CHALLENGE/             # Additional challenges
├── DIAGRAMS/              # Architecture diagrams
├── MINI_PROJECT/          # Mini project templates
├── REAL_WORLD_PROJECT/    # Real-world case studies
├── SOLUTION/              # Solution templates
├── TESTS/                 # Additional test resources
└── src/
    ├── main/java/com/capstone/  # Production Java sources
    └── test/java/com/capstone/  # JUnit 5 test sources
```

## Total Stats

- **Capstones**: 8
- **Documentation files**: 192 (24 per capstone)
- **Java source files**: 50+
- **Java test files**: 47
- **Test methods**: 270+
- **Subdirectories**: 56 (7 per capstone)
- **Lines of code**: 10,000+

## Prerequisites

- Java 21+ JDK
- JUnit 5 (included via Maven/Gradle dependency)
- IDE: IntelliJ IDEA, VS Code, or Eclipse

## Learning Path

```
01 ──→ 02 ──→ 03 ──→ 04 ──→ 05 ──→ 06 ──→ 07 ──→ 08
Ecom   Cache   Kafka   Vector  RAG     ML      Agent   Spark
```

Start with 01 (E-Commerce) for a familiar domain. Progress through 02-04 for distributed systems depth. Tackle 05-06 for ML/AI pipelines. Finish with 07-08 for advanced agent and compute engine concepts.
