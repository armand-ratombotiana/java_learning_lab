# RAG Platform

A complete Retrieval-Augmented Generation pipeline in Java, implementing document ingestion with multiple chunking strategies, embedding model interface, vector store integration, dense/hybrid retrieval, reranking, context assembly, and evaluation metrics.

## Architecture Overview

```
┌────────────┐  ┌──────────────┐  ┌──────────────┐  ┌────────────┐
│ Document   │  │ Chunking     │  │ Embedding    │  │ Vector     │
│ Ingestor   │─►│ Strategy     │─►│ Interface    │─►│ Store      │
│ (PDF/HTML) │  │ (fixed/sem/  │  │ (Mock/API)   │  │             │
│            │  │  recursive)  │  │              │  │             │
└────────────┘  └──────────────┘  └──────────────┘  └──────┬──────┘
                                                           │
┌────────────┐  ┌──────────────┐  ┌──────────────┐         │
│ Evaluation │  │ Context      │◄─│ Reranker     │◄────────┘
│ Pipeline   │  │ Builder      │  │              │
│ (recall/   │  │              │  │              │
│  precision)│  │              │  │              │
└────────────┘  └──────────────┘  └──────────────┘
```

## Features

- **DocumentIngestor**: Multi-format (PDF, HTML, TEXT, MARKDOWN) ingestion with listener callbacks
- **ChunkingStrategy**: Fixed-size (configurable overlap), semantic (sentence-boundary), recursive (hierarchical separators)
- **EmbeddingInterface**: Abstract interface with mock (deterministic Gaussian) and OpenAI implementations
- **VectorStore**: Text/vector storage with cosine similarity search and metadata filtering
- **Retriever**: Dense (vector), keyword (term-matching), hybrid (weighted combination) retrieval
- **Reranker**: Cross-encoder style reranking using embedding similarity
- **ContextBuilder**: Context assembly with length limits, score inclusion, custom templates
- **RAGEvaluator**: Recall, precision, MRR, faithfulness, and answer relevance metrics

## Usage

```java
var embed = new MockEmbedding(384);
var store = new VectorStore(embed);
store.addText("1", "Paris is the capital of France", Map.of("lang", "en"));

var chunker = new RecursiveChunker(500, 0);
var ingestor = new DocumentIngestor(chunker);
ingestor.ingest("source", "My Doc", "Long text content...", DocumentType.TEXT);

var retriever = new Retriever(store, new Reranker(embed), new ContextBuilder());
var results = retriever.hybridRetrieve("What is the capital of France?");
```
