# RAG (Retrieval Augmented Generation) - MINI PROJECT

## Project: Question Answering System

Build a RAG-based Q&A system over a knowledge base.

### Implementation

```java
public class QAEngine {
    private EmbeddingGenerator embeddingGen;
    private VectorStore vectorStore;
    private LLMGenerator llm;
    private DocumentChunker chunker;
    
    public QAEngine() {
        this.embeddingGen = new SimpleEmbedding(384);
        this.vectorStore = new VectorStore();
        this.llm = new LLMGenerator();
        this.chunker = new DocumentChunker(256, 50);
    }
    
    public void loadDocuments(List<Document> docs) {
        System.out.println("Loading " + docs.size() + " documents...");
        
        for (Document doc : docs) {
            List<TextChunk> chunks = chunker.chunkDocument(doc);
            
            for (TextChunk chunk : chunks) {
                double[] embed = embeddingGen.encode(chunk.getText());
                
                VectorEntry entry = new VectorEntry(
                    chunk.getId(),
                    embed,
                    chunk.getText()
                );
                
                vectorStore.add(entry);
            }
        }
        
        System.out.println("Indexed " + vectorStore.size() + " chunks");
    }
    
    public String answer(String question) {
        double[] queryEmbed = embeddingGen.encode(question);
        
        List<SearchResult> results = vectorStore.search(queryEmbed, 5);
        
        if (results.isEmpty()) {
            return "No relevant information found.";
        }
        
        String context = buildContext(results);
        
        String prompt = buildPrompt(question, context);
        
        String answer = llm.generate(prompt);
        
        return answer;
    }
    
    private String buildContext(List<SearchResult> results) {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < results.size(); i++) {
            sb.append("Document ").append(i + 1).append(":\n");
            sb.append(results.get(i).getContent()).append("\n\n");
        }
        
        return sb.toString();
    }
    
    private String buildPrompt(String question, String context) {
        return "Based on the following documents, answer the question.\n\n" +
               "Documents:\n" + context + "\n" +
               "Question: " + question + "\n" +
               "Answer concisely:";
    }
}

class SimpleEmbedding implements EmbeddingGenerator {
    private int dim = 384;
    
    public double[] encode(String text) {
        double[] embed = new double[dim];
        
        String[] words = text.toLowerCase().split("\\s+");
        
        for (String word : words) {
            int seed = word.hashCode();
            
            for (int i = 0; i < dim; i++) {
                embed[i] += Math.sin(seed + i);
            }
        }
        
        double norm = 0;
        for (double v : embed) norm += v * v;
        norm = Math.sqrt(norm);
        
        for (int i = 0; i < dim; i++) {
            embed[i] /= (norm + 1e-10);
        }
        
        return embed;
    }
}

class LLMGenerator {
    public String generate(String prompt) {
        return "Based on the provided context, the answer would be: " +
               prompt.substring(0, Math.min(50, prompt.length())) + "...";
    }
}
```

### Testing the System

```java
public class TestQA {
    public static void main(String[] args) {
        QAEngine qa = new QAEngine();
        
        List<Document> docs = Arrays.asList(
            new Document("doc1", "Java is a high-level programming language. " +
                "It was developed by Sun Microsystems in 1995. " +
                "Java is known for its write-once-run-anywhere philosophy."),
            
            new Document("doc2", "Python is an interpreted, high-level language. " +
                "Created by Guido van Rossum in 1991. " +
                "Python emphasizes code readability with significant indentation."),
            
            new Document("doc3", "Machine learning is a subset of AI. " +
                "It enables systems to learn from data. " +
                "Types include supervised, unsupervised, and reinforcement learning.")
        );
        
        qa.loadDocuments(docs);
        
        String[] questions = {
            "What is Java?",
            "Who created Python?",
            "What is machine learning?"
        };
        
        for (String q : questions) {
            System.out.println("\nQ: " + q);
            System.out.println("A: " + qa.answer(q));
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
```

### Adding Hybrid Search

```java
public class HybridQA extends QAEngine {
    private BM25Retrieval bm25;
    private double alpha = 0.7;
    
    public HybridQA() {
        super();
        this.bm25 = new BM25Retrieval();
    }
    
    public String answerHybrid(String question) {
        double[] queryEmbed = embed(question);
        
        List<SearchResult> semantic = vectorStore.search(queryEmbed, 10);
        
        List<SearchResult> keyword = bm25.search(question, 10);
        
        List<SearchResult> fused = fuseResults(semantic, keyword);
        
        return generateAnswer(question, fused);
    }
    
    private List<SearchResult> fuseResults(List<SearchResult> sem, 
                                          List<SearchResult> kw) {
        Map<String, SearchResult> combined = new LinkedHashMap<>();
        
        for (SearchResult r : sem) {
            combined.put(r.getId(), r);
        }
        
        for (SearchResult r : kw) {
            if (combined.containsKey(r.getId())) {
                double fused = alpha * combined.get(r.getId()).getScore() + 
                              (1 - alpha) * r.getScore();
                combined.put(r.getId(), new SearchResult(r.getId(), r.getContent(), fused));
            }
        }
        
        return combined.values().stream()
            .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
            .limit(5)
            .collect(Collectors.toList());
    }
}
```

## Deliverables

- [ ] Implement document chunking
- [ ] Build vector store
- [ ] Create embedding generator
- [ ] Build RAG pipeline
- [ ] Add BM25 retrieval
- [ ] Implement hybrid search
- [ ] Evaluate on Q&A dataset
- [ ] Add citation tracking