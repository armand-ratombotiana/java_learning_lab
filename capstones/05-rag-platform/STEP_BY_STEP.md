# Step by Step: RAG Platform

## Uploading a Document

1. POST /api/v1/documents with file multipart upload
2. DocumentIngestor saves raw file and spawns async processing job
3. Parser extracts text: PDFBox for PDFs, JSoup for HTML
4. RecursiveCharacterTextSplitter chunks: target 512 tokens, overlap 128
5. Each chunk gets metadata: source file, chunk index, page number
6. EmbeddingService computes 384-dim vector for each chunk
7. VectorStore inserts all vectors with metadata in batch
8. Job status updated to COMPLETED
