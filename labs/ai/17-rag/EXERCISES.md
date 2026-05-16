# RAG (Retrieval Augmented Generation) - EXERCISES

## Exercise 1: Document Chunking
Chunk "Hello world. This is a test document. It has multiple sentences." with chunk size 10 words.

```java
public List<String> chunkDocument() {
    String text = "Hello world. This is a test document. It has multiple sentences.";
    int chunkSize = 10;
    
    List<String> chunks = new ArrayList<>();
    String[] words = text.split("\\s+");
    
    StringBuilder chunk = new StringBuilder();
    int count = 0;
    
    for (String word : words) {
        chunk.append(word).append(" ");
        count++;
        
        if (count >= chunkSize) {
            chunks.add(chunk.toString().trim());
            chunk = new StringBuilder();
            count = 0;
        }
    }
    
    if (count > 0) {
        chunks.add(chunk.toString().trim());
    }
    
    return chunks;
    // Result: ["Hello world. This is a test document. It has", 
    //          "multiple sentences."]
}
```

## Exercise 2: Similarity Score Calculation
Compute cosine similarity between query=[1,0] and doc=[0.5,0.5]

```java
public double computeSimilarity() {
    double[] query = {1, 0};
    double[] doc = {0.5, 0.5};
    
    double dot = query[0] * doc[0] + query[1] * doc[1];
    double normQ = Math.sqrt(1);
    double normD = Math.sqrt(0.5);
    
    return dot / (normQ * normD);
    // Result: 0.5 / 0.707 ≈ 0.707
}
```

## Exercise 3: RAG Pipeline Trace
Given question "What is ML?" and docs ["Machine learning is..."], trace the RAG pipeline.

```java
public String ragPipeline() {
    String question = "What is ML?";
    List<String> docs = Arrays.asList(
        "Machine learning is a subset of artificial intelligence.",
        "ML algorithms learn from data.",
        "Deep learning uses neural networks."
    );
    
    // Step 1: Embed question
    double[] queryEmbed = embed(question);
    
    // Step 2: Retrieve top-k
    List<String> retrieved = retrieveTopK(queryEmbed, docs, 2);
    
    // Step 3: Build context
    String context = String.join("\n", retrieved);
    
    // Step 4: Generate prompt
    String prompt = "Context: " + context + "\n\nQuestion: " + question;
    
    // Step 5: Generate answer
    String answer = generate(prompt);
    
    return answer;
}
```

## Exercise 4: Hybrid Search Fusion
Combine semantic (scores: [0.9, 0.8]) and keyword (scores: [0.7, 0.6]) with alpha=0.6.

```java
public double hybridScore() {
    double alpha = 0.6;
    double semanticScore = 0.9;
    double keywordScore = 0.7;
    
    double hybridScore = alpha * semanticScore + (1 - alpha) * keywordScore;
    
    return hybridScore;
    // Result: 0.6 * 0.9 + 0.4 * 0.7 = 0.54 + 0.28 = 0.82
}
```

## Exercise 5: Re-Ranking Calculation
Compute F1 score for query terms found in document.

```java
public double computeRelevance() {
    String query = "machine learning";
    String document = "Machine learning is a type of artificial intelligence.";
    
    Set<String> queryTerms = new HashSet<>(Arrays.asList("machine", "learning"));
    Set<String> docTerms = new HashSet<>(Arrays.asList(
        document.toLowerCase().split("\\s+")
    ));
    
    int matches = 0;
    for (String term : queryTerms) {
        if (docTerms.contains(term)) matches++;
    }
    
    double precision = (double) matches / queryTerms.size();
    double recall = (double) matches / docTerms.size();
    
    double f1 = 2 * precision * recall / (precision + recall);
    
    return f1;
    // Result: 2 * 1 * 0.33 / 1.33 ≈ 0.5
}
```

## Exercise 6: BM25 IDF Computation
Compute IDF for term appearing in 5 out of 100 documents.

```java
public double computeIDF() {
    int numDocs = 100;
    int docsWithTerm = 5;
    
    double idf = Math.log((numDocs - docsWithTerm + 0.5) / 
                          (docsWithTerm + 0.5) + 1);
    
    return idf;
    // Result: log((95+0.5)/(5+0.5)+1) = log(17.27+1) ≈ log(18.27) ≈ 2.9
}
```

---

## Solutions

### Exercise 1:
```java
// Words split and grouped into chunks of 10
// Remaining words form final chunk
```

### Exercise 2:
```java
// Cosine similarity computed as normalized dot product
// Result: ~0.707
```

### Exercise 3:
```java
// RAG: embed → retrieve → context → prompt → generate
// Each step transforms the data
```

### Exercise 4:
```java
// Hybrid score combines both signals
// Alpha controls contribution weights
```

### Exercise 5:
```java
// F1 balances precision and recall
// Used in re-ranking relevance
```

### Exercise 6:
```java
// IDF penalizes common terms
// Rare terms have higher IDF weight
```