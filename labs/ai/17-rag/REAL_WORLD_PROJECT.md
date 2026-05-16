# RAG (Retrieval Augmented Generation) - REAL WORLD PROJECT

## Project: Enterprise Customer Support Assistant

Build a production RAG system for customer support.

### Architecture

```
User Query → Cache Check → Embed → Hybrid Search → Re-rank → LLM → Response
                 ↓
           Cache Miss: Continue Pipeline
```

### Implementation

```java
@Service
public class SupportAssistantService {
    private EmbeddingGenerator embeddingGenerator;
    private VectorStore vectorStore;
    private BM25Retrieval bm25Retrieval;
    private Reranker reranker;
    private LLMClient llmClient;
    private QueryCache cache;
    private MetricsCollector metrics;
    private double alpha = 0.6;
    
    @PostConstruct
    public void initialize() {
        embeddingGenerator = new SentenceTransformer("all-MiniLM-L6-v2");
        vectorStore = new VectorStore();
        bm25Retrieval = new BM25Retrieval();
        reranker = new Reranker();
        llmClient = new OpenAIClient();
        cache = new QueryCache(1000);
    }
    
    public SupportResponse answerQuestion(SupportRequest request) {
        long startTime = System.currentTimeMillis();
        
        String query = request.getQuestion();
        
        if (cache.contains(query)) {
            return cache.get(query);
        }
        
        double[] queryEmbedding = embeddingGenerator.encode(query);
        
        List<SearchResult> semanticResults = vectorStore.search(queryEmbedding, 20);
        
        List<SearchResult> keywordResults = bm25Retrieval.search(query, 20);
        
        List<SearchResult> hybridResults = fuseResults(semanticResults, keywordResults);
        
        List<SearchResult> reranked = reranker.crossEncoderRerank(query, hybridResults);
        
        List<SearchResult> finalResults = reranked.stream()
            .limit(10)
            .collect(Collectors.toList());
        
        String context = buildContext(finalResults);
        
        String prompt = buildPrompt(query, context);
        
        String answer = llmClient.generate(prompt);
        
        SupportResponse response = new SupportResponse(
            answer,
            finalResults.stream()
                .map(r -> new Citation(r.getId(), r.getContent().substring(0, 100)))
                .collect(Collectors.toList()),
            System.currentTimeMillis() - startTime
        );
        
        cache.put(query, response);
        
        metrics.record("support.latency.ms", response.getLatency());
        
        return response;
    }
    
    public void indexKnowledgeBase(List<SupportArticle> articles) {
        System.out.println("Indexing " + articles.size() + " articles...");
        
        DocumentChunker chunker = new DocumentChunker(512, 50);
        
        for (SupportArticle article : articles) {
            List<TextChunk> chunks = chunker.chunkDocument(
                new Document(article.getId(), article.getContent())
            );
            
            for (TextChunk chunk : chunks) {
                double[] embedding = embeddingGenerator.encode(chunk.getText());
                
                VectorEntry entry = new VectorEntry(
                    chunk.getId(),
                    embedding,
                    chunk.getText()
                );
                
                vectorStore.add(entry);
            }
            
            bm25Retrieval.index(
                Collections.singletonList(
                    new Document(article.getId(), article.getContent())
                )
            );
        }
        
        System.out.println("Indexed " + vectorStore.size() + " chunks");
    }
    
    private List<SearchResult> fuseResults(List<SearchResult> semantic, 
                                           List<SearchResult> keyword) {
        Map<String, SearchResult> combined = new LinkedHashMap<>();
        
        for (SearchResult r : semantic) {
            combined.put(r.getId(), r);
        }
        
        for (SearchResult r : keyword) {
            if (combined.containsKey(r.getId())) {
                double fusedScore = alpha * combined.get(r.getId()).getScore() + 
                                   (1 - alpha) * r.getScore();
                combined.put(r.getId(), new SearchResult(
                    r.getId(), r.getContent(), fusedScore
                ));
            } else {
                combined.put(r.getId(), new SearchResult(
                    r.getId(), r.getContent(), (1 - alpha) * r.getScore()
                ));
            }
        }
        
        return combined.values().stream()
            .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
            .collect(Collectors.toList());
    }
    
    private String buildContext(List<SearchResult> results) {
        StringBuilder context = new StringBuilder();
        
        for (int i = 0; i < results.size(); i++) {
            context.append("Source ").append(i + 1).append(":\n");
            context.append(results.get(i).getContent()).append("\n\n");
        }
        
        return context.toString();
    }
    
    private String buildPrompt(String question, String context) {
        return "You are a helpful customer support assistant. " +
               "Answer the question based on the provided sources. " +
               "If the answer cannot be found in the sources, say so.\n\n" +
               "Sources:\n" + context + "\n" +
               "Question: " + question + "\n" +
               "Answer:";
    }
}
```

### API Endpoints

```java
@RestController
@RequestMapping("/api/v1/support")
public class SupportController {
    
    @PostMapping("/ask")
    public ResponseEntity<SupportResponse> askQuestion(
            @RequestBody SupportRequest request) {
        
        SupportResponse response = supportService.answerQuestion(request);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/index")
    public ResponseEntity<IndexResponse> indexArticles(
            @RequestBody List<SupportArticle> articles) {
        
        supportService.indexKnowledgeBase(articles);
        
        return ResponseEntity.ok(new IndexResponse(
            articles.size(),
            "Successfully indexed"
        ));
    }
    
    @GetMapping("/health")
    public ResponseEntity<HealthCheck> health() {
        return ResponseEntity.ok(new HealthCheck(
            supportService.getIndexSize(),
            cache.getHitRate(),
            metrics.getAverageLatency()
        ));
    }
}
```

### Cache Implementation

```java
public class QueryCache {
    private Map<String, SupportResponse> cache;
    private int maxSize;
    private long maxAge = 3600000;
    
    public QueryCache(int maxSize) {
        this.cache = new LinkedHashMap<String, SupportResponse>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > maxSize;
            }
        };
    }
    
    public boolean contains(String query) {
        return cache.containsKey(query);
    }
    
    public SupportResponse get(String query) {
        return cache.get(query);
    }
    
    public void put(String query, SupportResponse response) {
        cache.put(query, response);
    }
    
    public double getHitRate() {
        return 0;
    }
}
```

### Monitoring

```java
@Component
public class SupportMetrics {
    private Map<String, Double> latencies = new ConcurrentHashMap<>();
    private AtomicInteger requests = new AtomicInteger(0);
    
    public void record(String metric, double value) {
        latencies.merge(metric, value, (a, b) -> (a + b) / 2);
    }
    
    public void increment(String metric) {
        requests.incrementAndGet();
    }
    
    public double getAverageLatency() {
        return latencies.getOrDefault("support.latency.ms", 0.0);
    }
    
    public int getTotalRequests() {
        return requests.get();
    }
    
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        metrics.put("total_requests", getTotalRequests());
        metrics.put("avg_latency_ms", getAverageLatency());
        metrics.put("cache_hit_rate", cache.getHitRate());
        
        return metrics;
    }
}
```

### Document Ingestion

```java
@Service
public class DocumentIngestionService {
    private SupportAssistantService supportService;
    private DocumentParser parser;
    
    public void ingestFromSource(SourceType type, String source) {
        switch (type) {
            case PDF:
                ingestPDF(source);
                break;
            case HTML:
                ingestHTML(source);
                break;
            case MARKDOWN:
                ingestMarkdown(source);
                break;
        }
    }
    
    private void ingestPDF(String path) {
        String content = parser.extractText(path);
        
        SupportArticle article = new SupportArticle(
            UUID.randomUUID().toString(),
            extractTitle(content),
            content,
            extractMetadata(content)
        );
        
        supportService.indexKnowledgeBase(Collections.singletonList(article));
    }
    
    private void ingestHTML(String url) {
        String html = fetchContent(url);
        String content = parser.stripHTML(html);
        
        SupportArticle article = new SupportArticle(
            UUID.randomUUID().toString(),
            extractTitle(content),
            content,
            Collections.emptyMap()
        );
        
        supportService.indexKnowledgeBase(Collections.singletonList(article));
    }
    
    private String extractTitle(String content) {
        String[] lines = content.split("\n");
        
        return lines.length > 0 ? lines[0] : "Untitled";
    }
    
    private Map<String, Object> extractMetadata(String content) {
        Map<String, Object> metadata = new HashMap<>();
        
        metadata.put("length", content.length());
        metadata.put("timestamp", System.currentTimeMillis());
        
        return metadata;
    }
}
```

## Deliverables

- [x] RAG pipeline implementation
- [x] Hybrid search (semantic + keyword)
- [x] Cross-encoder re-ranking
- [x] Query result caching
- [x] Document ingestion pipeline
- [x] REST API endpoints
- [x] Health monitoring
- [x] Metrics collection
- [x] Multi-format support (PDF, HTML)