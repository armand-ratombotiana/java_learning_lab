package com.learning.lab.module77;

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Module 77: Vector Database Integration");
        System.out.println("=".repeat(60));

        vectorDbOverview();
        embeddingGeneration();
        storageOperations();
        similaritySearch();
        indexingStrategies();
        filtering();
        hybridSearch();
        metadataHandling();
        batchOperations();
        queryOptimization();
        databaseConnections();
        useCases();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Module 77 Lab Complete!");
        System.out.println("=".repeat(60));
    }

    static void vectorDbOverview() {
        System.out.println("\n--- Vector Database Overview ---");
        System.out.println("Specialized databases for storing and searching high-dimensional vectors");

        System.out.println("\nWhy Vector Databases?");
        System.out.println("   - Semantic search (not just keyword)");
        System.out.println("   - Similarity at scale");
        System.out.println("   - AI/ML integration");
        System.out.println("   - Unstructured data handling");

        System.out.println("\nPopular Vector Databases:");

        System.out.println("\n1. Pinecone:");
        System.out.println("   - Fully managed");
        System.out.println("   - Serverless option");
        System.out.println("   - Real-time indexing");

        System.out.println("\n2. Weaviate:");
        System.out.println("   - Open source");
        System.out.println("   - GraphQL API");
        System.out.println("   - Multi-model (text, images)");

        System.out.println("\n3. Milvus:");
        System.out.println("   - Cloud-native");
        System.out.println("   - Horizontal scaling");
        System.out.println("   - Rich data types");

        System.out.println("\n4. Qdrant:");
        System.out.println("   - Rust-based");
        System.out.println("   - High performance");
        System.out.println("   - Filtering support");

        System.out.println("\n5. Chroma:");
        System.out.println("   - Lightweight");
        System.out.println("   - Python-first");
        System.out.println("   - Embeddable");

        System.out.println("\n6. pgvector:");
        System.out.println("   - PostgreSQL extension");
        System.out.println("   - Existing DB integration");
        System.out.println("   - ACID compliance");
    }

    static void embeddingGeneration() {
        System.out.println("\n--- Embedding Generation ---");
        System.out.println("Converting data to vector representations");

        System.out.println("\nEmbedding Types:");

        System.out.println("\n1. Text Embeddings:");
        String[] textExamples = {
            "The cat sat on the mat",
            "A feline was sitting on a rug",
            "Machine learning is fascinating"
        };
        double[][] textEmbeddings = generateEmbeddings(textExamples);
        System.out.println("   Input: " + textExamples[0]);
        System.out.println("   Vector: [" + formatVector(textEmbeddings[0]) + "]");

        System.out.println("\n2. Image Embeddings:");
        System.out.println("   - ResNet, VGG, CLIP");
        System.out.println("   - Extract from CNN layers");
        System.out.println("   - 512-2048 dimensions");

        System.out.println("\n3. Audio Embeddings:");
        System.out.println("   - Wav2Vec, AudioCLIP");
        System.out.println("   - Spectrogram-based");
        System.out.println("   - Speech recognition");

        System.out.println("\nEmbedding Models:");
        Map<String, Integer> models = new LinkedHashMap<>();
        models.put("OpenAI text-embedding-3-small", 1536);
        models.put("OpenAI text-embedding-3-large", 3072);
        models.put("Cohere embed-multilingual", 1024);
        models.put("HuggingFace sentence-transformers", 384);
        System.out.println("   " + models);

        System.out.println("\nBatch Embedding:");
        System.out.println("   List<String> texts = loadDocuments();");
        System.out.println("   EmbeddingRequest request = new BatchEmbeddingRequest(texts);");
        System.out.println("   List<Embedding> embeddings = model.embed(request);");
    }

    static double[][] generateEmbeddings(String[] texts) {
        double[][] embeddings = new double[texts.length][];
        Random rand = new Random(42);
        for (int i = 0; i < texts.length; i++) {
            embeddings[i] = new double[4];
            for (int j = 0; j < embeddings[i].length; j++) {
                embeddings[i][j] = rand.nextDouble();
            }
        }
        return embeddings;
    }

    static String formatVector(double[] vector) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(4, vector.length); i++) {
            sb.append(String.format("%.3f", vector[i])).append(", ");
        }
        return sb.append("...").toString();
    }

    static void storageOperations() {
        System.out.println("\n--- Storage Operations ---");
        System.out.println("Storing and managing vectors in databases");

        System.out.println("\nBasic Operations:");

        System.out.println("\n1. Insert (Upsert):");
        System.out.println("   UpsertRequest request = UpsertRequest.builder()");
        System.out.println("       .namespace(\"documents\")");
        System.out.println("       .vectors(vectors)");
        System.out.println("       .ids(ids)");
        System.out.println("       .build();");
        System.out.println("   UpsertResponse response = vectorDB.upsert(request);");

        System.out.println("\n2. Update:");
        System.out.println("   UpdateRequest request = UpdateRequest.builder()");
        System.out.println("       .id(\"doc-123\")");
        System.out.println("       .vector(newEmbedding)");
        System.out.println("       .build();");
        System.out.println("   vectorDB.update(request);");

        System.out.println("\n3. Delete:");
        System.out.println("   DeleteRequest request = DeleteRequest.builder()");
        System.out.println("       .ids(Arrays.asList(\"doc-123\", \"doc-456\"))");
        System.out.println("       .namespace(\"documents\")");
        System.out.println("       .build();");
        System.out.println("   vectorDB.delete(request);");

        System.out.println("\n4. Get/Fetch:");
        System.out.println("   FetchRequest request = FetchRequest.builder()");
        System.out.println("       .ids(Arrays.asList(\"doc-123\"))");
        System.out.println("       .build();");
        System.out.println("   FetchResponse response = vectorDB.fetch(request);");
        System.out.println("   Vector retrieved = response.getVector(\"doc-123\");");

        System.out.println("\nVector Metadata:");
        System.out.println("   Map<String, Object> metadata = Map.of(");
        System.out.println("       \"source\", \"web\",");
        System.out.println("       \"created_at\", \"2024-01-15\",");
        System.out.println("       \"category\", \"tutorial\")");
    }

    static void similaritySearch() {
        System.out.println("\n--- Similarity Search ---");
        System.out.println("Finding similar vectors in the database");

        System.out.println("\nSimilarity Metrics:");

        System.out.println("\n1. Cosine Similarity:");
        System.out.println("   - Angle between vectors");
        System.out.println("   - Best for text embeddings");
        double[] v1 = {0.1, 0.3, 0.5, 0.7};
        double[] v2 = {0.2, 0.3, 0.4, 0.6};
        double cosine = cosineSimilarity(v1, v2);
        System.out.printf("   Example: %.4f%n", cosine);

        System.out.println("\n2. Euclidean Distance (L2):");
        System.out.println("   - Straight-line distance");
        System.out.println("   - Works well for dense vectors");
        double euclidean = euclideanDistance(v1, v2);
        System.out.printf("   Example: %.4f%n", euclidean);

        System.out.println("\n3. Dot Product:");
        System.out.println("   - Angular similarity");
        System.out.println("   - Efficient for normalized vectors");
        double dot = dotProduct(v1, v2);
        System.out.printf("   Example: %.4f%n", dot);

        System.out.println("\nSearch Query:");
        System.out.println("   QueryRequest request = QueryRequest.builder()");
        System.out.println("       .vector(queryVector)");
        System.out.println("       .topK(10)");
        System.out.println("       .namespace(\"documents\")");
        System.out.println("       .build();");
        System.out.println("   QueryResponse response = vectorDB.query(request);");
        System.out.println("   ");
        System.out.println("   for (Match match : response.getMatches()) {");
        System.out.println("       String id = match.getId();");
        System.out.println("       double score = match.getScore();");
        System.out.println("   }");
    }

    static double cosineSimilarity(double[] a, double[] b) {
        double dot = dotProduct(a, b);
        double magA = Math.sqrt(Arrays.stream(a).map(x -> x * x).sum());
        double magB = Math.sqrt(Arrays.stream(b).map(x -> x * x).sum());
        return dot / (magA * magB);
    }

    static double euclideanDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum);
    }

    static double dotProduct(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    static void indexingStrategies() {
        System.out.println("\n--- Indexing Strategies ---");
        System.out.println("Optimizing search performance with indexes");

        System.out.println("\nIndex Types:");

        System.out.println("\n1. Flat Index (Brute Force):");
        System.out.println("   - Exact nearest neighbor");
        System.out.println("   - No indexing overhead");
        System.out.println("   - Good for small datasets");
        System.out.println("   Index: flat { metric: cosine }");

        System.out.println("\n2. HNSW (Hierarchical Navigable Small World):");
        System.out.println("   - Graph-based approach");
        System.out.println("   - Approximate nearest neighbors");
        System.out.println("   - Good balance of speed/accuracy");
        System.out.println("   Index: hnsw { m: 16, efConstruction: 200 }");

        System.out.println("\n3. IVF (Inverted File):");
        System.out.println("   - Clustering-based");
        System.out.println("   - Search within clusters");
        System.out.println("   - Requires training");
        System.out.println("   Index: ivf { nlist: 100, nprobe: 10 }");

        System.out.println("\n4. PQ (Product Quantization):");
        System.out.println("   - Compress vectors");
        System.out.println("   - Memory efficient");
        System.out.println("   - Good for large datasets");
        System.out.println("   Index: pq { m: 8, nbits: 8 }");

        System.out.println("\nIndex Configuration:");
        Map<String, Object> indexConfig = new LinkedHashMap<>();
        indexConfig.put("metric", "cosine");
        indexConfig.put("m", 16);
        indexConfig.put("efConstruction", 200);
        indexConfig.put("efSearch", 50);
        System.out.println("   Config: " + indexConfig);

        System.out.println("\nTrade-offs:");
        System.out.println("   - Speed vs Accuracy");
        System.out.println("   - Memory vs Quality");
        System.out.println("   - Build time vs Query time");
    }

    static void filtering() {
        System.out.println("\n--- Metadata Filtering ---");
        System.out.println("Combining vector search with structured filters");

        System.out.println("\nFilter Types:");

        System.out.println("\n1. Pre-filtering:");
        System.out.println("   - Apply filter before search");
        System.out.println("   - Exact results");
        System.out.println("   - May miss nearby vectors");
        System.out.println("   QueryRequest req = QueryRequest.builder()");
        System.out.println("       .filter(Filter.equals(\"category\", \"tutorial\"))");
        System.out.println("       .build();");

        System.out.println("\n2. Post-filtering:");
        System.out.println("   - Search first, then filter");
        System.out.println("   - Faster initial search");
        System.out.println("   - May return fewer results");
        System.out.println("   QueryRequest req = QueryRequest.builder()");
        System.out.println("       .filter(Filter.gte(\"year\", 2023))");
        System.out.println("       .build();");

        System.out.println("\nFilter Expressions:");
        String[] filters = {
            "category == 'tutorial'",
            "year >= 2023 AND language == 'en'",
            "source IN ['web', 'pdf']",
            "NOT (status == 'deleted')"
        };
        for (String f : filters) {
            System.out.println("   - " + f);
        }

        System.out.println("\nComplex Filtering:");
        System.out.println("   Filter filter = Filter.and(");
        System.out.println("       Filter.equals(\"type\", \"article\"),");
        System.out.println("       Filter.gte(\"rating\", 4.0),");
        System.out.println("       Filter.in(\"tags\", Arrays.asList(\"java\", \"ml\"))");
        System.out.println("   );");
    }

    static void hybridSearch() {
        System.out.println("\n--- Hybrid Search ---");
        System.out.println("Combining keyword and vector search");

        System.out.println("\nHybrid Approaches:");

        System.out.println("\n1. Weighted Combination:");
        System.out.println("   - Combine BM25 and vector scores");
        System.out.println("   - Alpha weight (0-1) for balance");
        System.out.println("   alpha = 0.5 gives equal weight");
        double alpha = 0.5;
        double bm25Score = 0.75;
        double vectorScore = 0.85;
        double hybridScore = alpha * bm25Score + (1 - alpha) * vectorScore;
        System.out.printf("   Hybrid score: %.4f%n", hybridScore);

        System.out.println("\n2. Reciprocal Rank Fusion (RRF):");
        System.out.println("   - Combine multiple result lists");
        System.out.println("   - Rank-based fusion");
        System.out.println("   double rrfScore = 1.0 / (rank + k);");
        System.out.println("   k is usually 60");

        System.out.println("\n3. Query Expansion:");
        System.out.println("   - Expand query with synonyms");
        System.out.println("   - Use LLM to generate expansions");
        System.out.println("   - Improve recall");

        System.out.println("\nImplementation:");
        System.out.println("   HybridQueryRequest req = HybridQueryRequest.builder()");
        System.out.println("       .textQuery(\"java tutorial\")");
        System.out.println("       .vectorQuery(embedding)");
        System.out.println("       .alpha(0.6)");
        System.out.println("       .build();");
        System.out.println("   HybridResponse response = db.hybridSearch(req);");
    }

    static void metadataHandling() {
        System.out.println("\n--- Metadata Handling ---");
        System.out.println("Storing and querying vector metadata");

        System.out.println("\nMetadata Schema:");
        System.out.println("   VectorRecord record = VectorRecord.builder()");
        System.out.println("       .id(\"doc-123\")");
        System.out.println("       .vector(embedding)");
        System.out.println("       .metadata(Map.of(");
        System.out.println("           \"title\", \"Java Tutorial\",");
        System.out.println("           \"author\", \"John Doe\",");
        System.out.println("           \"created\", Instant.now(),");
        System.out.println("           \"views\", 1000,");
        System.out.println("           \"tags\", Arrays.asList(\"java\", \"beginner\")");
        System.out.println("       ))");
        System.out.println("       .build();");

        System.out.println("\nMetadata Indexing:");
        System.out.println("   - Index common filter fields");
        System.out.println("   - Avoid indexing large text");
        System.out.println("   - Use appropriate data types");

        System.out.println("\nQuery with Metadata:");
        System.out.println("   QueryRequest req = QueryRequest.builder()");
        System.out.println("       .vector(query)");
        System.out.println("       .topK(10)");
        System.out.println("       .includeMetadata(true)");
        System.out.println("       .build();");
        System.out.println("   ");
        System.out.println("   for (Match m : results) {");
        System.out.println("       Map<String, Object> meta = m.getMetadata();");
        System.out.println("       String title = (String) meta.get(\"title\");");
        System.out.println("   }");

        System.out.println("\nUpdate Metadata:");
        System.out.println("   UpdateRequest req = UpdateRequest.builder()");
        System.out.println("       .id(\"doc-123\")");
        System.out.println("       .metadata(Map.of(\"views\", 1001))");
        System.out.println("       .build();");
    }

    static void batchOperations() {
        System.out.println("\n--- Batch Operations ---");
        System.out.println("Efficiently processing large volumes");

        System.out.println("\nBatch Upsert:");
        System.out.println("   List<VectorRecord> records = new ArrayList<>();");
        System.out.println("   for (Document doc : documents) {");
        System.out.println("       Vector embedding = embedder.embed(doc.getText());");
        System.out.println("       records.add(VectorRecord.builder()");
        System.out.println("           .id(doc.getId())");
        System.out.println("           .vector(embedding)");
        System.out.println("           .metadata(doc.getMetadata())");
        System.out.println("           .build());");
        System.out.println("   }");
        System.out.println("   vectorDB.upsertBatch(records, batchSize);");

        System.out.println("\nBatch Query:");
        System.out.println("   List<double[]> queryVectors = queries.stream()");
        System.out.println("       .map(q -> embedder.embed(q))");
        System.out.println("       .collect(Collectors.toList());");
        System.out.println("   ");
        System.out.println("   BatchQueryResponse resp = vectorDB.batchQuery(");
        System.out.println("       queryVectors, topK);");

        System.out.println("\nParallel Processing:");
        System.out.println("   ExecutorService executor = Executors.newFixedThreadPool(4);");
        System.out.println("   List<Future> futures = executor.submit(() -> {");
        System.out.println("       // Process batch");
        System.out.println("   });");
        System.out.println("   executor.shutdown();");

        System.out.println("\nPerformance Tips:");
        System.out.println("   - Batch size: 100-1000 vectors");
        System.out.println("   - Async operations when possible");
        System.out.println("   - Connection pooling");
        System.out.println("   - Monitor rate limits");
    }

    static void queryOptimization() {
        System.out.println("\n--- Query Optimization ---");
        System.out.println("Improving search performance");

        System.out.println("\nOptimization Techniques:");

        System.out.println("\n1. Query Optimization:");
        System.out.println("   - Limit result set (topK)");
        System.out.println("   - Use approximate search");
        System.out.println("   - Cache frequent queries");

        System.out.println("\n2. Index Tuning:");
        System.out.println("   - Increase efSearch for accuracy");
        System.out.println("   - Tune m for memory/speed");
        System.out.println("   - Regular index rebuilds");

        Map<String, Object> tuneParams = new LinkedHashMap<>();
        tuneParams.put("efSearch", 100);
        tuneParams.put("topK", 10);
        tuneParams.put("timeout", "5s");
        System.out.println("   Params: " + tuneParams);

        System.out.println("\n3. Result Caching:");
        System.out.println("   Cache<QueryKey, QueryResponse> cache = Caffeine.newBuilder()");
        System.out.println("       .maximumSize(10000)");
        System.out.println("       .expireAfterWrite(Duration.ofMinutes(10))");
        System.out.println("       .build();");
        System.out.println("   - Cache common queries");
        System.out.println("   - Invalidate on updates");

        System.out.println("\n4. Connection Pooling:");
        System.out.println("   VectorDBConfig config = VectorDBConfig.builder()");
        System.out.println("       .maxConnections(50)");
        System.out.println("       .connectionTimeout(Duration.ofSeconds(30))");
        System.out.println("       .build();");
    }

    static void databaseConnections() {
        System.out.println("\n--- Database Connections ---");
        System.out.println("Connecting to various vector databases");

        System.out.println("\nPinecone Connection:");
        System.out.println("   Pinecone pinecone = Pinecone.builder()");
        System.out.println("       .withApiKey(System.getenv(\"PINECONE_API_KEY\"))");
        System.out.println("       .withEnvironment(\"us-west1-gcp\")");
        System.out.println("       .build();");
        System.out.println("   Index index = pinecone.index(\"my-index\");");

        System.out.println("\nWeaviate Connection:");
        System.out.println("   WeaviateClient client = Weaviate.builder()");
        System.out.println("       .url(\"http://localhost:8080\")");
        System.out.println("       .build();");
        System.out.println("   client.connect();");

        System.out.println("\nQdrant Connection:");
        System.out.println("   QdrantClient client = QdrantClient.builder()");
        System.out.println("       .withHost(\"localhost\")");
        System.out.println("       .withPort(6333)");
        System.out.println("       .withApiKey(System.getenv(\"QDRANT_API_KEY\"))");
        System.out.println("       .build();");

        System.out.println("\npgvector Connection:");
        System.out.println("   Connection conn = DriverManager.getConnection(");
        System.out.println("       \"jdbc:postgresql://localhost:5432/mydb\",");
        System.out.println("       \"user\", \"password\");");
        System.out.println("   // Use standard JDBC + SQL");

        System.out.println("\nEnvironment Configuration:");
        System.out.println("   Map<String, String> env = Map.of(");
        System.out.println("       \"PINECONE_API_KEY\", System.getenv(\"API_KEY\"),");
        System.out.println("       \"OPENAI_API_KEY\", System.getenv(\"OPENAI_KEY\")");
        System.out.println("   );");
    }

    static void useCases() {
        System.out.println("\n--- Use Cases ---");
        System.out.println("Real-world applications of vector databases");

        System.out.println("\n1. Semantic Search:");
        System.out.println("   - Question answering systems");
        System.out.println("   - Document retrieval");
        System.out.println("   - Code search");

        System.out.println("\n2. Recommendation Systems:");
        System.out.println("   - Product recommendations");
        System.out.println("   - Content personalization");
        System.out.println("   - Similar item discovery");

        System.out.println("\n3. Image/Video Search:");
        System.out.println("   - Reverse image search");
        System.out.println("   - Video similarity");
        System.out.println("   - Face recognition");

        System.out.println("\n4. Anomaly Detection:");
        System.out.println("   - Fraud detection");
        System.out.println("   - Network intrusion");
        System.out.println("   - Quality control");

        System.out.println("\n5. RAG (Retrieval Augmented Generation):");
        System.out.println("   - Knowledge base for LLM");
        System.out.println("   - Enterprise document search");
        System.out.println("   - Customer support automation");

        System.out.println("\n6. Deduplication:");
        System.out.println("   - Finding duplicate content");
        System.out.println("   - Record matching");
        System.out.println("   - Similarity detection");

        System.out.println("\nArchitecture Pattern:");
        System.out.println("   [User Query] -> [Embed] -> [Vector DB] -> [Top K Results]");
        System.out.println("        -> [LLM with Context] -> [Generated Response]");
    }
}