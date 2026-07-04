# Capstone 04: Vector Database

Welcome to the **Vector Database Capstone Project**. This is a portfolio-grade project where you will build a functional, distributed Vector Database from scratch in Java.

## 🎯 Project Objective
Build a system capable of storing dense vectors (embeddings) and performing highly efficient similarity searches (K-Nearest Neighbors) over millions of records.

## 🧠 Core Architecture
- **Storage Engine**: In-memory and disk-backed storage for vectors and metadata.
- **Indexing**: Implementation of Hierarchical Navigable Small World (HNSW) graphs for Approximate Nearest Neighbor (ANN) search.
- **Distance Metrics**: Cosine Similarity, L2 (Euclidean) Distance, Inner Product.
- **API**: REST/gRPC endpoints for ingestion and querying.

## 📂 Project Structure
1. [ARCHITECTURE.md](./ARCHITECTURE.md) - System design and component breakdown.
2. [HNSW_INTERNALS.md](./HNSW_INTERNALS.md) - Deep dive into the HNSW algorithm.
3. [IMPLEMENTATION_GUIDE.md](./IMPLEMENTATION_GUIDE.md) - Step-by-step guide to building the database.