# Performance: RAG Platform

## Throughput Targets
- Document ingestion: < 30s for 100-page PDF (parse + chunk + embed)
- Query response: < 2s for retrieval + rerank, < 5s including LLM generation
- Embedding throughput: ~100 chunks/second (CPU), ~500/s (GPU via ONNX)
- Reranker throughput: ~50 pairs/second (CPU)

## Bottlenecks
- **PDF parsing**: PDFBox is slow for complex layouts. Consider OCR for scanned docs.
- **Embedding inference**: Most expensive part. Use GPU or quantized models (int8).
- **LLM generation**: Dominates latency. Use smaller models for simple queries (e.g., Mistral 7B > GPT-4 for fast answers).
- **Vector search**: Large collections (1M+ chunks) need efficient ANN.

## Optimization Strategies
- Cache embeddings for unchanged documents
- Use batch embedding for ingestion
- Pre-compute query embeddings for frequently asked questions
- Stream LLM responses (TTFB < 500ms)
- Use GPUs for model inference (CUDA via ONNX Runtime)
- Reduce reranking candidates (top-20 instead of top-50 if recall sufficient)
