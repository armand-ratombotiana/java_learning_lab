# RAG Platform - Portfolio Capstone

## Overview
Production-ready Retrieval-Augmented Generation system with LangChain4j, multiple vector stores, and advanced document processing.

## Architecture
```
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│   Document   │───▶│   Chunking   │───▶│   Embedding  │
│   Ingestion  │    │   Pipeline   │    │   Generator  │
└──────────────┘    └──────────────┘    └──────────────┘
                                            │
                        ┌───────────────────┼───────────────────┐
                        │                   │                   │
                        ▼                   ▼                   ▼
                ┌──────────────┐    ┌──────────────┐    ┌──────────────┐
                │   Milvus    │    │  Pinecone    │    │   Chroma    │
                │   Store     │    │   Store      │    │   Store     │
                └──────────────┘    └──────────────┘    └──────────────┘
                                           │
                        ┌──────────────────┘
                        ▼
                ┌──────────────┐    ┌──────────────┐
                │   Retrieve  │───▶│   Generate   │
                │   Context   │    │   Response  │
                └──────────────┘    └──────────────┘
```

## Tech Stack
- **Framework**: Spring Boot 3.2.x
- **RAG**: LangChain4j
- **Vector Stores**: Milvus, Pinecone, Chroma
- **LLM**: OpenAI, Anthropic, Local models
- **Document Processing**: Apache Tika, PDFBox

## Features
- Multi-vector store support
- Advanced document chunking
- Hybrid search (vector + keyword)
- Reranking with cross-encoders
- Streaming responses
- Citation tracking

## Quick Start
```bash
cd 05-rag-platform
docker-compose up -d
```