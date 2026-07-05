# Architecture: Search Engine

## High-Level Architecture
```
[Indexing API] -> [IndexWriter] -> [Analyzer] -> [Tokenizer]
                          |                            |
                    [Segment Buffer]              [Token Filters]
                          |                       (lowercase, stem)
                    [Flush Policy]                      |
                          |                            |
                    [Directory] <----- [Segment Files]
                          |
                    [IndexReader] <---- [Query API]
                          |
                    [IndexSearcher] --> [QueryParser] --> [Query Tree]
                          |                                    |
                    [BM25 Scorer] <= [Weight] <= [Scorer per Segment]
                          |
                    [TopDocs Collector]
                          |
                    [Search Results]
```

## Technology Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.x (REST API)
- **Build**: Maven
- **Storage**: File system (index segments) + optional memory-mapped
- **Analysis**: Custom tokenizer + Porter stemmer
- **Encoding**: VInt (variable-length integer) for postings
- **Compression**: LZ4 for stored fields
- **Containerization**: Docker
- **Monitoring**: Micrometer + Prometheus

## API
- `POST /index/{indexName}/docs` — Index document
- `GET /index/{indexName}/search?q=query` — Search
- `DELETE /index/{indexName}/docs/{id}` — Delete document
- `POST /index/{indexName}/refresh` — Refresh (make recent index visible)
- `GET /index/{indexName}/stats` — Index statistics
