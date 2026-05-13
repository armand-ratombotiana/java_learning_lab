# Vector Database - Portfolio Capstone

## Overview
Custom high-performance vector database with HNSW indexing, approximate nearest neighbor search, and similarity search capabilities.

## Architecture
```
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│   Insert     │───▶│   HNSW       │───▶│   Index      │
│   Requests   │    │   Layer      │    │   Storage    │
└──────────────┘    └──────────────┘    └──────────────┘
                          │
       ┌──────────────────┤──────────────────┐
       │                  │                  │
       ▼                  ▼                  ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│   Search     │   │   Distance   │   │   Result    │
│   Query      │   │   Compute    │   │   Ranking   │
└──────────────┘   └──────────────┘   └──────────────┘
```

## Features
- HNSW (Hierarchical Navigable Small World) indexing
- Cosine, Euclidean, Dot Product similarity
- CRUD operations for vectors
- Batch processing
- Filtering support
- Persistence to disk

## Quick Start
```bash
cd 04-vector-database
docker-compose up -d
```