# Visual Guide: RAG Platform

## Pipeline Architecture
```
                  +-----------+
                  | Document  |
                  | Upload    |
                  +-----+-----+
                        |
                  +-----v-----+
                  | Parse &   |
                  | Clean     |
                  +-----+-----+
                        |
                  +-----v-----+
                  | Chunk     |
                  +-----+-----+
                        |
                  +-----v-----+
                  | Embed     |
                  | (SBERT)   |
                  +-----+-----+
                        |
                  +-----v-----+     +----------+
                  | Vector DB |     | Query    |
                  +-----+-----+     +-----+----+
                        |                 |
                        +-------+---------+
                                |
                          +-----v-----+
                          | Retrieve  |
                          | Top-K     |
                          +-----+-----+
                                |
                          +-----v-----+
                          | Reranker  |
                          | (CrossEnc)|
                          +-----+-----+
                                |
                          +-----v-----+
                          | Prompt    |
                          | Builder   |
                          +-----+-----+
                                |
                          +-----v-----+
                          | LLM       |
                          | Generate  |
                          +-----+-----+
                                |
                          +-----v-----+
                          | Answer +  |
                          | Citations |
                          +-----------+
```

## Component Diagram
```
[Web UI] --> [RAG API] --> [QueryService]
                              |
           +------------------+------------------+
           |                  |                  |
    [EmbeddingService]  [VectorStore]    [LLMService]
           |                  |                  |
    [ONNX Runtime]      [HNSW Index]     [OpenAI / vLLM]
           |                  |
    [Sentence-BERT]    [RerankerService]
```
