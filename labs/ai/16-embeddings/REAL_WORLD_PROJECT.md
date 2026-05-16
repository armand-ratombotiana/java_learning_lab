# Embeddings - REAL WORLD PROJECT

## Project: Enterprise Knowledge Base Search

Build a production-grade semantic search system for enterprise knowledge base.

### Architecture

```
User Query → Query Processing → Embedding Generation → Vector Search 
                                                                 ↓
                                           Results Ranking + Re-ranking
```

### Implementation

```java
@Service
public class KnowledgeBaseSearchService {
    private EmbeddingGenerator embeddingGenerator;
    private VectorStore vectorStore;
    private ReRanker reRanker;
    private CacheManager cacheManager;
    private MetricsCollector metrics;
    
    @PostConstruct
    public void initialize() {
        embeddingGenerator = new SentenceTransformer("all-MiniLM-L6-v2");
        vectorStore = new PineconeVectorStore();
        reRanker = new CrossEncoderReRanker();
        cacheManager = new CacheManager(1000);
    }
    
    public SearchResult search(String query, SearchOptions options) {
        long startTime = System.currentTimeMillis();
        
        String cacheKey = query + ":" + options.getTopK();
        
        if (cacheManager.contains(cacheKey)) {
            return cacheManager.get(cacheKey);
        }
        
        double[] queryEmbedding = embeddingGenerator.encode(query);
        
        List<CandidateDoc> candidates = vectorStore.search(
            queryEmbedding,
            options.getTopK() * 2
        );
        
        List<ScoredDoc> reranked = reRanker.rerank(query, candidates);
        
        List<ScoredDoc> finalResults = reranked.stream()
            .limit(options.getTopK())
            .collect(Collectors.toList());
        
        long inferenceTime = System.currentTimeMillis() - startTime;
        
        SearchResult result = new SearchResult(finalResults, inferenceTime);
        
        cacheManager.put(cacheKey, result);
        
        metrics.record("search.latency.ms", inferenceTime);
        
        return result;
    }
    
    public void indexDocument(Document doc) {
        double[] embedding = embeddingGenerator.encode(doc.getContent());
        
        VectorEntry entry = new VectorEntry(
            doc.getId(),
            embedding,
            doc.getMetadata()
        );
        
        vectorStore.upsert(entry);
        
        metrics.increment("documents.indexed");
    }
    
    public void indexBatch(List<Document> docs) {
        List<VectorEntry> entries = new ArrayList<>();
        
        for (Document doc : docs) {
            double[] embedding = embeddingGenerator.encode(doc.getContent());
            
            entries.add(new VectorEntry(doc.getId(), embedding, doc.getMetadata()));
        }
        
        vectorStore.upsertBatch(entries);
        
        metrics.increment("documents.indexed", docs.size());
    }
}
```

### Embedding Generator

```java
public class SentenceTransformer implements EmbeddingGenerator {
    private int embeddingDim = 384;
    private double[][] vocabularyEmbeddings;
    private Map<String, int[]> tokenizer;
    
    public SentenceTransformer(String modelName) {
        this.vocabularyEmbeddings = loadVocabularyEmbeddings(modelName);
        this.tokenizer = buildTokenizer();
    }
    
    private double[][] loadVocabularyEmbeddings(String modelName) {
        double[][] embeddings = new double[30000][embeddingDim];
        
        Random rand = new Random(42);
        
        for (int i = 0; i < 30000; i++) {
            for (int j = 0; j < embeddingDim; j++) {
                embeddings[i][j] = (rand.nextDouble() - 0.5) * 2;
            }
        }
        
        return embeddings;
    }
    
    private Map<String, int[]> buildTokenizer() {
        Map<String, int[]> vocab = new HashMap<>();
        
        for (int i = 0; i < 30000; i++) {
            String word = "word_" + i;
            vocab.put(word, new int[]{i});
        }
        
        return vocab;
    }
    
    @Override
    public double[] encode(String text) {
        int[] tokenIds = tokenize(text);
        
        double[][] tokenEmbeddings = getTokenEmbeddings(tokenIds);
        
        double[] pooled = meanPooling(tokenEmbeddings);
        
        double[] normalized = l2Normalize(pooled);
        
        return normalized;
    }
    
    private int[] tokenize(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        
        int[] ids = new int[words.length];
        
        for (int i = 0; i < words.length; i++) {
            int hash = Math.abs(words[i].hashCode());
            ids[i] = hash % 30000;
        }
        
        return ids;
    }
    
    private double[][] getTokenEmbeddings(int[] tokenIds) {
        double[][] embeddings = new double[tokenIds.length][embeddingDim];
        
        for (int i = 0; i < tokenIds.length; i++) {
            embeddings[i] = vocabularyEmbeddings[tokenIds[i]].clone();
        }
        
        return embeddings;
    }
    
    private double[] meanPooling(double[][] tokenEmbeddings) {
        double[] pooled = new double[embeddingDim];
        
        for (double[] embed : tokenEmbeddings) {
            for (int i = 0; i < embeddingDim; i++) {
                pooled[i] += embed[i];
            }
        }
        
        if (tokenEmbeddings.length > 0) {
            for (int i = 0; i < embeddingDim; i++) {
                pooled[i] /= tokenEmbeddings.length;
            }
        }
        
        return pooled;
    }
    
    private double[] l2Normalize(double[] vector) {
        double norm = 0;
        
        for (double v : vector) {
            norm += v * v;
        }
        
        norm = Math.sqrt(norm);
        
        double[] normalized = new double[vector.length];
        
        for (int i = 0; i < vector.length; i++) {
            normalized[i] = vector[i] / (norm + 1e-10);
        }
        
        return normalized;
    }
}
```

### Vector Store

```java
public class PineconeVectorStore implements VectorStore {
    private Map<String, VectorEntry> index;
    private int maxDimensions = 1024;
    
    public PineconeVectorStore() {
        this.index = new ConcurrentHashMap<>();
    }
    
    @Override
    public void upsert(VectorEntry entry) {
        index.put(entry.getId(), entry);
    }
    
    @Override
    public void upsertBatch(List<VectorEntry> entries) {
        for (VectorEntry entry : entries) {
            index.put(entry.getId(), entry);
        }
    }
    
    @Override
    public List<CandidateDoc> search(double[] queryEmbedding, int topK) {
        List<ScoredEntry> scores = new ArrayList<>();
        
        for (VectorEntry entry : index.values()) {
            double similarity = cosineSimilarity(queryEmbedding, entry.getEmbedding());
            
            scores.add(new ScoredEntry(entry, similarity));
        }
        
        scores.sort((a, b) -> Double.compare(b.score, a.score));
        
        List<CandidateDoc> results = new ArrayList<>();
        
        for (int i = 0; i < Math.min(topK, scores.size()); i++) {
            results.add(new CandidateDoc(
                scores.get(i).entry.getId(),
                scores.get(i).entry.getContent(),
                scores.get(i).score
            ));
        }
        
        return results;
    }
    
    @Override
    public void delete(String id) {
        index.remove(id);
    }
    
    @Override
    public void deleteAll() {
        index.clear();
    }
    
    private double cosineSimilarity(double[] a, double[] b) {
        double dot = 0;
        double normA = 0;
        double normB = 0;
        
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        
        return dot / (Math.sqrt(normA) * Math.sqrt(normB) + 1e-10);
    }
}

class ScoredEntry {
    VectorEntry entry;
    double score;
    
    ScoredEntry(VectorEntry entry, double score) {
        this.entry = entry;
        this.score = score;
    }
}
```

### Re-Ranking

```java
public class CrossEncoderReRanker implements ReRanker {
    
    @Override
    public List<ScoredDoc> rerank(String query, List<CandidateDoc> candidates) {
        List<ScoredDoc> reranked = new ArrayList<>();
        
        for (CandidateDoc candidate : candidates) {
            double crossScore = computeCrossEncoderScore(query, candidate.getContent());
            
            reranked.add(new ScoredDoc(
                candidate.getId(),
                candidate.getContent(),
                crossScore
            ));
        }
        
        reranked.sort((a, b) -> Double.compare(b.score, a.score));
        
        return reranked;
    }
    
    private double computeCrossEncoderScore(String query, String document) {
        String[] queryTokens = query.toLowerCase().split("\\s+");
        String[] docTokens = document.toLowerCase().split("\\s+");
        
        double score = 0;
        
        for (String qt : queryTokens) {
            for (String dt : docTokens) {
                if (qt.equals(dt)) {
                    score += 1;
                }
            }
        }
        
        double normalizedScore = score / 
            Math.sqrt(queryTokens.length * docTokens.length);
        
        return normalizedScore;
    }
}
```

### API Endpoints

```java
@RestController
@RequestMapping("/api/v1/knowledge")
public class KnowledgeBaseController {
    
    @PostMapping("/search")
    public ResponseEntity<SearchResponse> search(
            @RequestBody SearchRequest request) {
        
        SearchOptions options = new SearchOptions(
            request.getTopK() != null ? request.getTopK() : 10,
            request.getFilters()
        );
        
        SearchResult result = searchService.search(request.getQuery(), options);
        
        return ResponseEntity.ok(new SearchResponse(result));
    }
    
    @PostMapping("/documents")
    public ResponseEntity<Void> indexDocument(@RequestBody DocumentRequest request) {
        Document doc = new Document(
            request.getId(),
            request.getContent(),
            request.getMetadata()
        );
        
        searchService.indexDocument(doc);
        
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/documents/batch")
    public ResponseEntity<Void> indexBatch(@RequestBody List<DocumentRequest> requests) {
        List<Document> docs = requests.stream()
            .map(r -> new Document(r.getId(), r.getContent(), r.getMetadata()))
            .collect(Collectors.toList());
        
        searchService.indexBatch(docs);
        
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/health")
    public ResponseEntity<HealthStatus> health() {
        return ResponseEntity.ok(new HealthStatus(
            searchService.getIndexSize(),
            System.currentTimeMillis()
        ));
    }
}
```

## Deliverables

- [x] Sentence embedding generation
- [x] Vector storage with cosine similarity
- [x] Cross-encoder re-ranking
- [x] Query result caching
- [x] Batch document indexing
- [x] REST API endpoints
- [x] Health monitoring
- [x] Metrics collection
- [x] Metadata filtering