# Code Deep Dive: RAG Platform

## Document Ingestion

`DocumentIngestor` uses a strategy pattern for different file formats. PDF parsing uses PDFBox with layout preservation. HTML uses JSoup with configurable CSS selectors for main content extraction. The `RecursiveCharacterTextSplitter` tries splitting on double newlines, then single newlines, then spaces, then characters to hit the target chunk size.

## Embedding Service

`EmbeddingService` wraps ONNX Runtime with the Sentence-BERT model (all-MiniLM-L6-v2, 384-dim). It preprocesses text using the BERT tokenizer (WordPiece). Batched inference processes up to 32 chunks at once. The model tokenizes, runs transformer forward, computes mean pooling, and normalizes the output.

## Reranker

`RerankerService` loads a cross-encoder ONNX model (MS MARCO MiniLM). For each query+chunk pair, it constructs [CLS] query [SEP] chunk [SEP] input, runs inference, and extracts the relevance logit. It processes pairs in parallel with a configurable thread pool.

## Query Service

`QueryService` orchestrates the pipeline: embed query -> search vector DB (k=20) -> rerank -> top-5 -> build prompt -> call LLM -> return answer with citations. It uses CompletableFuture for parallel calls where possible.
