# Embeddings - MINI PROJECT

## Project: Document Retrieval System

Build a semantic search system using word embeddings for document retrieval.

### Implementation

```java
public class DocumentRetrieval {
    private SkipGramModel embeddingModel;
    private List<Document> documents;
    private Map<Integer, double[]> docEmbeddings;
    private int embeddingSize = 100;
    
    public DocumentRetrieval() {
        this.embeddingModel = new SkipGramModel(10000, embeddingSize);
        this.documents = new ArrayList<>();
        this.docEmbeddings = new HashMap<>();
    }
    
    public void addDocument(String id, String content) {
        Document doc = new Document(id, content);
        
        documents.add(doc);
        
        double[] embedding = computeDocEmbedding(content);
        
        docEmbeddings.put(documents.size() - 1, embedding);
    }
    
    private double[] computeDocEmbedding(String content) {
        String[] tokens = content.toLowerCase().split("\\s+");
        
        double[] embedding = new double[embeddingSize];
        
        for (String token : tokens) {
            int tokenId = Math.abs(token.hashCode()) % 10000;
            double[] wordEmbed = embeddingModel.getWordEmbedding(tokenId);
            
            for (int i = 0; i < embeddingSize; i++) {
                embedding[i] += wordEmbed[i];
            }
        }
        
        if (tokens.length > 0) {
            for (int i = 0; i < embeddingSize; i++) {
                embedding[i] /= tokens.length;
            }
        }
        
        return embedding;
    }
    
    public List<SearchResult> search(String query, int topK) {
        double[] queryEmbedding = computeDocEmbedding(query);
        
        List<ScoredDoc> scores = new ArrayList<>();
        
        for (int i = 0; i < documents.size(); i++) {
            double sim = cosineSimilarity(queryEmbedding, docEmbeddings.get(i));
            
            scores.add(new ScoredDoc(i, sim));
        }
        
        scores.sort((a, b) -> Double.compare(b.score, a.score));
        
        List<SearchResult> results = new ArrayList<>();
        
        for (int i = 0; i < Math.min(topK, scores.size()); i++) {
            int docIdx = scores.get(i).docIdx;
            results.add(new SearchResult(
                documents.get(docIdx).getId(),
                documents.get(docIdx).getContent(),
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
    
    public void trainFromCorpus(List<String> texts) {
        List<int[]> centerWords = new ArrayList<>();
        List<int[]> contextWords = new ArrayList<>();
        
        int windowSize = 2;
        
        for (String text : texts) {
            String[] tokens = text.toLowerCase().split("\\s+");
            
            for (int i = 0; i < tokens.length; i++) {
                int centerId = Math.abs(tokens[i].hashCode()) % 10000;
                
                List<Integer> context = new ArrayList<>();
                
                for (int j = Math.max(0, i - windowSize); 
                     j < Math.min(tokens.length, i + windowSize + 1); j++) {
                    if (i != j) {
                        context.add(Math.abs(tokens[j].hashCode()) % 10000);
                    }
                }
                
                if (!context.isEmpty()) {
                    centerWords.add(new int[]{centerId});
                    contextWords.add(context.stream().mapToInt(Integer::intValue).toArray());
                }
            }
        }
        
        for (int epoch = 0; epoch < 5; epoch++) {
            for (int i = 0; i < centerWords.size(); i++) {
                embeddingModel.train(centerWords.get(i), contextWords.get(i));
            }
            
            System.out.println("Epoch " + (epoch + 1) + " complete");
        }
    }
}

class Document {
    private String id;
    private String content;
    
    public Document(String id, String content) {
        this.id = id;
        this.content = content;
    }
    
    public String getId() { return id; }
    public String getContent() { return content; }
}

class ScoredDoc {
    int docIdx;
    double score;
    
    public ScoredDoc(int docIdx, double score) {
        this.docIdx = docIdx;
        this.score = score;
    }
}

class SearchResult {
    String docId;
    String content;
    double score;
    
    public SearchResult(String docId, String content, double score) {
        this.docId = docId;
        this.content = content;
        this.score = score;
    }
}
```

### Query Expansion

```java
public class QueryExpander {
    private SkipGramModel model;
    
    public QueryExpander(SkipGramModel model) {
        this.model = model;
    }
    
    public List<String> expandQuery(String query) {
        String[] tokens = query.toLowerCase().split("\\s+");
        
        List<String> expanded = new ArrayList<>(Arrays.asList(tokens));
        
        for (String token : tokens) {
            int tokenId = Math.abs(token.hashCode()) % 10000;
            
            int[] similar = model.mostSimilar(tokenId, 3);
            
            for (int id : similar) {
                expanded.add("word_" + id);
            }
        }
        
        return expanded;
    }
    
    public String rewriteQuery(String query, String rewriteStrategy) {
        switch (rewriteStrategy) {
            case "expand":
                return String.join(" ", expandQuery(query));
            case "synonym":
                return query;
            case "hybrid":
                return query;
            default:
                return query;
        }
    }
}
```

### Testing

```java
public class TestDocumentRetrieval {
    public static void main(String[] args) {
        DocumentRetrieval retrieval = new DocumentRetrieval();
        
        List<String> corpus = Arrays.asList(
            "machine learning is a subset of artificial intelligence",
            "deep learning uses neural networks with multiple layers",
            "natural language processing deals with text data",
            "computer vision focuses on image recognition",
            "reinforcement learning learns through trial and error"
        );
        
        System.out.println("Training embeddings...");
        retrieval.trainFromCorpus(corpus);
        
        System.out.println("\nIndexing documents...");
        retrieval.addDocument("doc1", corpus.get(0));
        retrieval.addDocument("doc2", corpus.get(1));
        retrieval.addDocument("doc3", corpus.get(2));
        retrieval.addDocument("doc4", corpus.get(3));
        retrieval.addDocument("doc5", corpus.get(4));
        
        System.out.println("\nSearching for 'neural networks'...");
        List<SearchResult> results = retrieval.search("neural networks", 3);
        
        for (SearchResult result : results) {
            System.out.println(result.docId + " (score: " + 
                String.format("%.4f", result.score) + "): " + 
                result.content.substring(0, Math.min(50, result.content.length())));
        }
    }
}
```

## Deliverables

- [ ] Implement Word2Vec Skip-Gram model
- [ ] Train embeddings from corpus
- [ ] Build document indexing
- [ ] Implement cosine similarity search
- [ ] Add query expansion
- [ ] Evaluate retrieval precision
- [ ] Handle OOV words gracefully