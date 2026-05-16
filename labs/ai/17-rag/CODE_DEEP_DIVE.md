# RAG (Retrieval Augmented Generation) - CODE DEEP DIVE

## Java Implementations

### 1. Document Chunking

```java
public class DocumentChunker {
    private int chunkSize = 512;
    private int overlap = 50;
    private String separator = "\n";
    
    public DocumentChunker(int chunkSize, int overlap) {
        this.chunkSize = chunkSize;
        this.overlap = overlap;
    }
    
    public List<TextChunk> chunkDocument(Document document) {
        String text = document.getContent();
        
        String[] paragraphs = text.split(separator);
        
        List<TextChunk> chunks = new ArrayList<>();
        
        StringBuilder currentChunk = new StringBuilder();
        int currentSize = 0;
        
        for (String para : paragraphs) {
            int paraSize = para.length();
            
            if (currentSize + paraSize > chunkSize && currentSize > 0) {
                chunks.add(createChunk(document.getId(), currentChunk.toString(), chunks.size()));
                
                String overlapText = currentChunk.toString();
                int overlapStart = Math.max(0, overlapText.length() - overlap);
                currentChunk = new StringBuilder(overlapText.substring(overlapStart));
                currentSize = currentChunk.length();
            }
            
            currentChunk.append(para).append(separator);
            currentSize += paraSize + separator.length();
        }
        
        if (currentSize > 0) {
            chunks.add(createChunk(document.getId(), currentChunk.toString(), chunks.size()));
        }
        
        return chunks;
    }
    
    private TextChunk createChunk(String docId, String text, int chunkId) {
        return new TextChunk(docId, chunkId, text);
    }
    
    public List<TextChunk> chunkBySentences(Document document) {
        List<String> sentences = splitIntoSentences(document.getContent());
        
        List<TextChunk> chunks = new ArrayList<>();
        
        StringBuilder currentChunk = new StringBuilder();
        int currentTokens = 0;
        
        for (String sentence : sentences) {
            int sentenceTokens = estimateTokens(sentence);
            
            if (currentTokens + sentenceTokens > chunkSize && currentTokens > 0) {
                chunks.add(createChunk(document.getId(), currentChunk.toString(), chunks.size()));
                
                currentChunk = new StringBuilder();
                currentTokens = 0;
            }
            
            currentChunk.append(sentence).append(" ");
            currentTokens += sentenceTokens;
        }
        
        if (currentTokens > 0) {
            chunks.add(createChunk(document.getId(), currentChunk.toString(), chunks.size()));
        }
        
        return chunks;
    }
    
    private List<String> splitIntoSentences(String text) {
        List<String> sentences = new ArrayList<>();
        
        String[] parts = text.split("(?<=[.!?])\\s+");
        
        for (String part : parts) {
            if (!part.trim().isEmpty()) {
                sentences.add(part.trim());
            }
        }
        
        return sentences;
    }
    
    private int estimateTokens(String text) {
        return text.split("\\s+").length;
    }
}
```

### 2. Vector Store Implementation

```java
public class VectorStore {
    private Map<String, VectorEntry> entries;
    private int dimensions = 384;
    private String metricType = "cosine";
    
    public VectorStore() {
        this.entries = new HashMap<>();
    }
    
    public void add(VectorEntry entry) {
        entries.put(entry.getId(), entry);
    }
    
    public void addBatch(List<VectorEntry> batch) {
        for (VectorEntry entry : batch) {
            add(entry);
        }
    }
    
    public List<SearchResult> search(double[] queryEmbedding, int topK) {
        List<ScoredEntry> scores = new ArrayList<>();
        
        for (VectorEntry entry : entries.values()) {
            double score;
            
            switch (metricType) {
                case "cosine":
                    score = cosineSimilarity(queryEmbedding, entry.getEmbedding());
                    break;
                case "euclidean":
                    score = -euclideanDistance(queryEmbedding, entry.getEmbedding());
                    break;
                default:
                    score = cosineSimilarity(queryEmbedding, entry.getEmbedding());
            }
            
            scores.add(new ScoredEntry(entry, score));
        }
        
        scores.sort((a, b) -> Double.compare(b.score, a.score));
        
        List<SearchResult> results = new ArrayList<>();
        
        for (int i = 0; i < Math.min(topK, scores.size()); i++) {
            results.add(new SearchResult(
                scores.get(i).entry.getId(),
                scores.get(i).entry.getContent(),
                scores.get(i).score
            ));
        }
        
        return results;
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
    
    private double euclideanDistance(double[] a, double[] b) {
        double sum = 0;
        
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        
        return Math.sqrt(sum);
    }
    
    public void delete(String id) {
        entries.remove(id);
    }
    
    public int size() {
        return entries.size();
    }
}

class VectorEntry {
    private String id;
    private double[] embedding;
    private String content;
    private Map<String, Object> metadata;
    
    public VectorEntry(String id, double[] embedding, String content) {
        this.id = id;
        this.embedding = embedding;
        this.content = content;
    }
    
    public String getId() { return id; }
    public double[] getEmbedding() { return embedding; }
    public String getContent() { return content; }
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

### 3. RAG Pipeline

```java
public class RAGPipeline {
    private EmbeddingGenerator embeddingGenerator;
    private VectorStore vectorStore;
    private LLMGenerator llmGenerator;
    private DocumentChunker chunker;
    private int topK = 5;
    private double minSimilarity = 0.7;
    
    public RAGPipeline() {
        this.embeddingGenerator = new SentenceTransformer(384);
        this.vectorStore = new VectorStore();
        this.llmGenerator = new LLMGenerator();
        this.chunker = new DocumentChunker(512, 50);
    }
    
    public void indexDocuments(List<Document> documents) {
        for (Document doc : documents) {
            List<TextChunk> chunks = chunker.chunkDocument(doc);
            
            for (TextChunk chunk : chunks) {
                double[] embedding = embeddingGenerator.encode(chunk.getText());
                
                VectorEntry entry = new VectorEntry(
                    chunk.getId(),
                    embedding,
                    chunk.getText()
                );
                
                vectorStore.add(entry);
            }
        }
        
        System.out.println("Indexed " + vectorStore.size() + " chunks");
    }
    
    public String answer(String question) {
        double[] queryEmbedding = embeddingGenerator.encode(question);
        
        List<SearchResult> retrieved = vectorStore.search(queryEmbedding, topK);
        
        List<SearchResult> filtered = retrieved.stream()
            .filter(r -> r.getScore() >= minSimilarity)
            .collect(Collectors.toList());
        
        if (filtered.isEmpty()) {
            return "I couldn't find relevant information to answer your question.";
        }
        
        String context = buildContext(filtered);
        
        String prompt = buildPrompt(question, context);
        
        String answer = llmGenerator.generate(prompt);
        
        return answer;
    }
    
    private String buildContext(List<SearchResult> results) {
        StringBuilder context = new StringBuilder();
        
        for (int i = 0; i < results.size(); i++) {
            context.append("Context ").append(i + 1).append(":\n");
            context.append(results.get(i).getContent()).append("\n\n");
        }
        
        return context.toString();
    }
    
    private String buildPrompt(String question, String context) {
        return "Based on the following context, answer the question.\n\n" +
               "Context:\n" + context + "\n" +
               "Question: " + question + "\n" +
               "Answer:";
    }
    
    public String answerWithCitations(String question) {
        double[] queryEmbedding = embeddingGenerator.encode(question);
        
        List<SearchResult> retrieved = vectorStore.search(queryEmbedding, topK);
        
        String context = buildContext(retrieved);
        
        String prompt = buildPromptWithCitations(question, context);
        
        String answer = llmGenerator.generate(prompt);
        
        return formatWithCitations(answer, retrieved);
    }
    
    private String buildPromptWithCitations(String question, String context) {
        return "Based on the following context, answer the question.\n" +
               "Include [Source N] citations where N is the context number.\n\n" +
               "Context:\n" + context + "\n" +
               "Question: " + question + "\n" +
               "Answer:";
    }
    
    private String formatWithCitations(String answer, List<SearchResult> results) {
        return answer;
    }
}

class TextChunk {
    private String id;
    private int chunkId;
    private String text;
    
    public TextChunk(String docId, int chunkId, String text) {
        this.id = docId + "_" + chunkId;
        this.chunkId = chunkId;
        this.text = text;
    }
    
    public String getId() { return id; }
    public String getText() { return text; }
}

class SearchResult {
    private String id;
    private String content;
    private double score;
    
    public SearchResult(String id, String content, double score) {
        this.id = id;
        this.content = content;
        this.score = score;
    }
    
    public String getId() { return id; }
    public String getContent() { return content; }
    public double getScore() { return score; }
}
```

### 4. Hybrid Search

```java
public class HybridSearch {
    private VectorStore semanticStore;
    private KeywordSearch keywordSearch;
    private float alpha = 0.5;
    
    public HybridSearch() {
        this.semanticStore = new VectorStore();
        this.keywordSearch = new KeywordSearch();
    }
    
    public List<SearchResult> search(String query, int topK) {
        List<SearchResult> semanticResults = semanticStore.search(
            getEmbedding(query), topK * 2
        );
        
        List<SearchResult> keywordResults = keywordSearch.search(query, topK * 2);
        
        Map<String, SearchResult> combined = new LinkedHashMap<>();
        
        for (SearchResult r : semanticResults) {
            combined.put(r.getId(), r);
        }
        
        for (SearchResult r : keywordResults) {
            if (combined.containsKey(r.getId())) {
                SearchResult existing = combined.get(r.getId());
                double fusedScore = alpha * existing.getScore() + 
                                   (1 - alpha) * r.getScore();
                combined.put(r.getId(), new SearchResult(
                    existing.getId(), 
                    existing.getContent(), 
                    fusedScore
                ));
            } else {
                combined.put(r.getId(), new SearchResult(
                    r.getId(), 
                    r.getContent(), 
                    (1 - alpha) * r.getScore()
                ));
            }
        }
        
        return combined.values().stream()
            .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
            .limit(topK)
            .collect(Collectors.toList());
    }
    
    private double[] getEmbedding(String text) {
        double[] embed = new double[384];
        Random rand = new Random(text.hashCode());
        
        for (int i = 0; i < 384; i++) {
            embed[i] = rand.nextDouble();
        }
        
        return embed;
    }
}

class KeywordSearch {
    private Map<String, Set<String>> invertedIndex;
    
    public KeywordSearch() {
        this.invertedIndex = new HashMap<>();
    }
    
    public void index(String id, String content) {
        String[] tokens = content.toLowerCase().split("\\s+");
        
        for (String token : tokens) {
            invertedIndex.computeIfAbsent(token, k -> new HashSet<>()).add(id);
        }
    }
    
    public List<SearchResult> search(String query, int topK) {
        String[] queryTokens = query.toLowerCase().split("\\s+");
        
        Map<String, Integer> docScores = new HashMap<>();
        
        for (String token : queryTokens) {
            Set<String> docs = invertedIndex.getOrDefault(token, Collections.emptySet());
            
            for (String doc : docs) {
                docScores.merge(doc, 1, Integer::sum);
            }
        }
        
        return docScores.entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
            .limit(topK)
            .map(e -> new SearchResult(e.getKey(), "", e.getValue() * 0.1))
            .collect(Collectors.toList());
    }
}
```

### 5. Re-Ranking

```java
public class Reranker {
    
    public List<SearchResult> rerank(String query, List<SearchResult> candidates) {
        List<ScoredCandidate> scored = new ArrayList<>();
        
        for (SearchResult candidate : candidates) {
            double relevanceScore = computeRelevance(query, candidate.getContent());
            
            double finalScore = 0.7 * candidate.getScore() + 0.3 * relevanceScore;
            
            scored.add(new ScoredCandidate(candidate, finalScore));
        }
        
        scored.sort((a, b) -> Double.compare(b.score, a.score));
        
        return scored.stream()
            .map(s -> new SearchResult(s.candidate.getId(), 
                                       s.candidate.getContent(), 
                                       s.score))
            .collect(Collectors.toList());
    }
    
    private double computeRelevance(String query, String content) {
        String[] queryTokens = query.toLowerCase().split("\\s+");
        String[] contentTokens = content.toLowerCase().split("\\s+");
        
        Set<String> contentSet = new HashSet<>(Arrays.asList(contentTokens));
        
        int matches = 0;
        
        for (String token : queryTokens) {
            if (contentSet.contains(token)) {
                matches++;
            }
        }
        
        double precision = (double) matches / queryTokens.length;
        double recall = (double) matches / contentSet.size();
        
        if (precision + recall == 0) return 0;
        
        return 2 * precision * recall / (precision + recall);
    }
    
    public List<SearchResult> crossEncoderRerank(String query, 
                                                List<SearchResult> candidates) {
        double[] scores = computeCrossEncoderScores(query, candidates);
        
        List<ScoredCandidate> ranked = new ArrayList<>();
        
        for (int i = 0; i < candidates.size(); i++) {
            ranked.add(new ScoredCandidate(candidates.get(i), scores[i]));
        }
        
        ranked.sort((a, b) -> Double.compare(b.score, a.score));
        
        return ranked.stream()
            .map(s -> new SearchResult(s.candidate.getId(), 
                                       s.candidate.getContent(), 
                                       s.score))
            .collect(Collectors.toList());
    }
    
    private double[] computeCrossEncoderScores(String query, 
                                                List<SearchResult> candidates) {
        double[] scores = new double[candidates.size()];
        
        for (int i = 0; i < candidates.size(); i++) {
            String combined = query + " [SEP] " + candidates.get(i).getContent();
            
            scores[i] = computeSimilarity(combined);
        }
        
        double max = Arrays.stream(scores).max().orElse(1);
        
        for (int i = 0; i < scores.length; i++) {
            scores[i] /= max;
        }
        
        return scores;
    }
    
    private double computeSimilarity(String text) {
        return Math.random() * 0.5 + 0.5;
    }
}

class ScoredCandidate {
    SearchResult candidate;
    double score;
    
    ScoredCandidate(SearchResult candidate, double score) {
        this.candidate = candidate;
        this.score = score;
    }
}
```

### 6. BM25 Retrieval

```java
public class BM25Retrieval {
    private Map<String, Map<String, Integer>> termFrequencies;
    private Map<String, Double> documentLengths;
    private double avgDocLength;
    private double k1 = 1.5;
    private double b = 0.75;
    
    public void index(List<Document> documents) {
        termFrequencies = new HashMap<>();
        documentLengths = new HashMap<>();
        
        int totalLength = 0;
        
        for (Document doc : documents) {
            String[] tokens = doc.getContent().toLowerCase().split("\\s+");
            
            Map<String, Integer> tf = new HashMap<>();
            
            for (String token : tokens) {
                tf.merge(token, 1, Integer::sum);
            }
            
            termFrequencies.put(doc.getId(), tf);
            documentLengths.put(doc.getId(), (double) tokens.length);
            totalLength += tokens.length;
        }
        
        avgDocLength = (double) totalLength / documents.size();
    }
    
    public List<SearchResult> search(String query, int topK) {
        String[] queryTokens = query.toLowerCase().split("\\s+");
        
        Map<String, Double> scores = new HashMap<>();
        
        for (String docId : termFrequencies.keySet()) {
            double score = computeBM25(docId, queryTokens);
            scores.put(docId, score);
        }
        
        return scores.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(topK)
            .map(e -> new SearchResult(e.getKey(), "", e.getValue()))
            .collect(Collectors.toList());
    }
    
    private double computeBM25(String docId, String[] queryTokens) {
        double score = 0;
        
        Map<String, Integer> tf = termFrequencies.get(docId);
        double docLength = documentLengths.get(docId);
        
        for (String term : queryTokens) {
            int termFreq = tf.getOrDefault(term, 0);
            
            if (termFreq > 0) {
                double idf = computeIDF(term);
                double tfScore = (termFreq * (k1 + 1)) / 
                               (termFreq + k1 * (1 - b + b * (docLength / avgDocLength)));
                
                score += idf * tfScore;
            }
        }
        
        return score;
    }
    
    private double computeIDF(String term) {
        int numDocsWithTerm = 0;
        
        for (Map<String, Integer> tf : termFrequencies.values()) {
            if (tf.containsKey(term)) {
                numDocsWithTerm++;
            }
        }
        
        int totalDocs = termFrequencies.size();
        
        return Math.log((totalDocs - numDocsWithTerm + 0.5) / 
                       (numDocsWithTerm + 0.5) + 1);
    }
}
```