# Retrieval-Augmented Generation (RAG) - Complete Theory

## 1. Motivation

### 1.1 Limitations of LLMs
- Hallucinations
- Outdated knowledge
- No access to private data

### 1.2 RAG Solution
Retrieve relevant documents
Generate with retrieved context

## 2. Retrieval Pipeline

### 2.1 Document Processing
- Chunk documents (token limit)
- Overlap for continuity

### 2.2 Embedding Generation
- Encode chunks to vectors
- Store in vector database

### 2.3 Similarity Search
- Encode query
- Find top-k similar chunks

## 3. Vector Stores

### 3.1 Types
- Dense: embeddings (Pinecone, Weaviate)
- Sparse: BM25 (Elasticsearch)
- Hybrid: combination

### 3.2 Indexing
- Flat: exact search
- IVF: inverted file index
- HNSW: hierarchical navigable small world

## 4. Advanced Retrieval

### 4.1 Reranking
Cross-encoder scores top candidates

### 4.2 Query Expansion
- Subqueries
- Hypothetical document embedding (HyDE)

### 4.3 Hybrid Search
Combine dense and sparse

## Java Implementation

```java
public class SimpleRAG {
    private EmbeddingModel embeddingModel;
    private Map<String, Vector> documentEmbeddings;
    private Map<String, String> documents;
    
    public SimpleRAG(EmbeddingModel model) {
        this.embeddingModel = model;
        this.documentEmbeddings = new HashMap<>();
        this.documents = new HashMap<>();
    }
    
    public void indexDocument(String id, String content) {
        documents.put(id, content);
        Vector embedding = embeddingModel.encode(content);
        documentEmbeddings.put(id, embedding);
    }
    
    public RetrievedChunk[] retrieve(String query, int topK) {
        Vector queryEmbedding = embeddingModel.encode(query);
        
        // Compute similarities
        List<ScoredChunk> scores = new ArrayList<>();
        for (Map.Entry<String, Vector> entry : documentEmbeddings.entrySet()) {
            double sim = cosineSimilarity(queryEmbedding, entry.getValue());
            scores.add(new ScoredChunk(entry.getKey(), sim));
        }
        
        // Sort and take top k
        Collections.sort(scores, (a, b) -> Double.compare(b.score, a.score));
        
        RetrievedChunk[] results = new RetrievedChunk[topK];
        for (int i = 0; i < topK && i < scores.size(); i++) {
            String id = scores.get(i).id;
            results[i] = new RetrievedChunk(
                id,
                documents.get(id),
                scores.get(i).score
            );
        }
        return results;
    }
    
    public String generate(String query, String context) {
        // Would integrate with LLM API
        String prompt = "Context: " + context + "\n\nQuestion: " + query;
        return callLLM(prompt);
    }
    
    public String answer(String query, int topK) {
        RetrievedChunk[] chunks = retrieve(query, topK);
        String context = joinContexts(chunks);
        return generate(query, context);
    }
}
```

## 5. Evaluation

### 5.1 Retrieval Metrics
- Precision@K
- Recall@K
- MRR (Mean Reciprocal Rank)
- NDCG

### 5.2 End-to-End
- Answer quality
- Citation accuracy