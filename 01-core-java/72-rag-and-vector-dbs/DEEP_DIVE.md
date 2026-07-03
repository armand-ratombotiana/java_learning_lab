# Deep Dive: RAG & Vector Databases

## 1. Introduction to RAG (Retrieval-Augmented Generation)
Retrieval-Augmented Generation (RAG) is an architectural pattern that improves the efficacy of Large Language Model (LLM) applications by grounding the model on external sources of knowledge before generating a response. This solves the problem of LLMs hallucinating or lacking up-to-date, domain-specific information.

### The RAG Pipeline
1. **Ingestion**: Documents are loaded, split into smaller chunks, converted into numerical representations (embeddings), and stored in a Vector Database.
2. **Retrieval**: A user query is converted into an embedding, and the Vector DB performs a similarity search to find the most relevant document chunks.
3. **Generation**: The retrieved chunks are injected into the prompt alongside the original query, and the LLM generates a well-informed response.

## 2. Embeddings
Embeddings are high-dimensional vector representations of text. Words or sentences with similar meanings are located closer together in this vector space.
*   **Models**: OpenAI (`text-embedding-3-small`), HuggingFace (BGE, MiniLM).
*   **Dimensions**: E.g., OpenAI embeddings often have 1536 dimensions.

## 3. Vector Databases
Standard relational databases are not optimized for similarity searches on high-dimensional vectors. Vector databases use specialized indexes (like HNSW - Hierarchical Navigable Small World) to perform Approximate Nearest Neighbor (ANN) searches efficiently.
*   **Examples**: Chroma, Pinecone, Weaviate, Milvus, Qdrant, and `pgvector` (PostgreSQL extension).

## 4. Spring AI & RAG
Spring AI provides abstractions for the entire RAG pipeline:
*   `DocumentReader`: For reading PDFs, JSON, Text, etc.
*   `TextSplitter`: For chunking documents (e.g., `TokenTextSplitter`).
*   `EmbeddingModel`: To generate embeddings.
*   `VectorStore`: Abstraction over various vector databases.

### Example: Storing a Document
```java
List<Document> documents = new TextReader("classpath:data.txt").get();
TextSplitter splitter = new TokenTextSplitter();
List<Document> chunks = splitter.apply(documents);
vectorStore.accept(chunks);
```

### Example: Retrieving and Generating
```java
List<Document> similarDocuments = vectorStore.similaritySearch(query);
String context = similarDocuments.stream().map(Document::getContent).collect(Collectors.joining("\n"));
Prompt prompt = new Prompt(new SystemMessage("Use this context: " + context), new UserMessage(query));
ChatResponse response = chatModel.call(prompt);
```