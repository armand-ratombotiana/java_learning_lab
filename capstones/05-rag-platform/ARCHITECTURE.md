# Architecture: RAG Platform

## High-Level Architecture
```
[Web UI / API Client] --> [RAG API Gateway]
                               |
                      [QueryService]
                          |        |
                   [Embedding] [Vector DB]
                          |        |
                   [ONNX Runtime] [HNSW]
                          |        |
                   [Reranker] [Metadata Filter]
                          |
                   [LLM Service]
                          |
                  [OpenAI / vLLM / Ollama]
```

## Technology Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.x
- **Build**: Maven
- **Vector DB**: Custom HNSW (from capstone 04) or embedded
- **ML Models**: ONNX Runtime Java
- **LLM**: OpenAI API / Anthropic / vLLM (local)
- **File Parsing**: PDFBox, JSoup, CommonMark
- **Storage**: PostgreSQL (document metadata), S3 (raw files)
- **Queue**: Redis/SQS (async ingestion jobs)
- **Containerization**: Docker + docker-compose

## API Endpoints
- `POST /api/v1/documents` — Upload document
- `GET /api/v1/documents/{id}` — Get document status
- `DELETE /api/v1/documents/{id}` — Delete document
- `POST /api/v1/query` — Ask question (RAG)
- `POST /api/v1/collections` — Create knowledge base collection
