# Portfolio-Grade Capstone Projects

This directory contains 10 production-ready, portfolio-grade Java capstone projects demonstrating advanced skills in distributed systems, machine learning, and modern software engineering.

## Projects Overview

| # | Project | Description | Tech Stack |
|---|---------|-------------|------------|
| 01 | [Banking Platform](./01-banking-platform/) | Full-stack microservices banking system | Spring Boot, Kafka, PostgreSQL |
| 02 | [Fraud Detection](./02-fraud-detection/) | ML-based real-time fraud detection | Spring ML, Kafka, Isolation Forest |
| 03 | [Recommendation Engine](./03-recommendation-engine/) | Collaborative filtering + content-based | Matrix Factorization, Redis |
| 04 | [Vector Database](./04-vector-database/) | Custom HNSW vector store | HNSW indexing, similarity search |
| 05 | [RAG Platform](./05-rag-platform/) | Production RAG with LangChain4j | LangChain4j, multiple vector stores |
| 06 | [Distributed Cache](./06-distributed-cache/) | Redis-like cache with cluster | Consistent hashing, Redis |
| 07 | [Event Streaming](./07-event-streaming/) | Mini Kafka implementation | Partitions, consumer groups |
| 08 | [Search Engine](./08-search-engine/) | Elasticsearch-like full-text search | TF-IDF, inverted index |
| 09 | [ML Platform](./09-ml-platform/) | End-to-end MLOps platform | Training, serving, monitoring |
| 10 | [AI Assistant](./10-ai-assistant/) | Multi-agent with RAG & tools | RAG, memory, tool calling |

## Quick Start

```bash
# Build all projects
for dir in capstones/*/; do
  cd "$dir" && mvn clean package -DskipTests && cd ../../
done

# Run with Docker
cd capstones/01-banking-platform
docker-compose up -d

# Run locally
cd capstones/01-banking-platform
./mvnw spring-boot:run
```

## Architecture Highlights

### Banking Platform (01)
```
API Gateway → Account Service → Kafka → Payment Service
                    ↓              ↓
                 PostgreSQL    Fraud Detection
```

### Vector Database (04)
```
Insert → HNSW Index → Graph Search → Top-K Results
```

### AI Assistant (10)
```
User Input → Planner → RAG/Memory/Tools → LLM Response
```

## Features

- **Production-Ready**: Docker, Kubernetes, CI/CD, monitoring
- **Test Coverage**: Unit tests, integration tests
- **Documentation**: Complete READMEs, API docs, architecture diagrams
- **Scalable**: Microservices, partitioning, clustering
- **Observability**: Prometheus, Grafana, distributed tracing

## License

MIT