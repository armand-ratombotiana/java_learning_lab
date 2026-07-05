# Internals: RAG Platform

## Core Components
- **DocumentIngestor**: Parser (PDFBox for PDF, JSoup for HTML, CommonMark for Markdown), chunker, metadata extractor
- **EmbeddingService**: Loads Sentence-BERT ONNX model, computes embeddings (384d for MiniLM), batched processing
- **VectorStore**: Wraps the Vector Database client (or embedded HNSW index) for storing/searching embeddings
- **RerankerService**: Cross-encoder model (MS MARCO or BGE-reranker) loaded via ONNX Runtime
- **PromptBuilder**: Constructs prompt with system instruction, retrieved context, and user query
- **LLMService**: HTTP client to LLM API (OpenAI, Anthropic, or local via vLLM/Ollama)
- **QueryService**: Orchestrates the full RAG pipeline

## Chunking Parameters
- Strategy: RecursiveCharacterTextSplitter (paragraph -> sentence -> word boundaries)
- Default chunk size: 512 tokens
- Overlap: 128 tokens
- Max chunks per document: unlimited

## Pipeline
```
Upload -> Parse -> Clean -> Chunk -> Embed -> Index -> Respond
                            Query -> Embed -> Search -> Rerank -> Augment -> Generate
```
