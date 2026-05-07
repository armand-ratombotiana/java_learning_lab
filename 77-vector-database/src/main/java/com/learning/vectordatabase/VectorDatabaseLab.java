package com.learning.vectordatabase;

public class VectorDatabaseLab {

    public static void main(String[] args) {
        System.out.println("=== Vector Database Lab ===\n");

        System.out.println("1. Vector Embeddings:");
        System.out.println("   // Generate embeddings using sentence transformers");
        System.out.println("   String text = \"Java is a programming language\";");
        System.out.println("   float[] vector = embeddingModel.embed(text);");
        System.out.println("   // vector.length = 384 (all-MiniLM-L6-v2)");
        System.out.println("   // vector.length = 1536 (OpenAI Ada-002)");

        System.out.println("\n2. Pinecone:");
        System.out.println("   // Create index with 1536 dimensions (matches OpenAI)");
        System.out.println("   Index index = new PineconeIndex(\"my-index\");");
        System.out.println("   index.upsert(List.of(new Vector(\"id-1\", vector, Map.of(\"text\", text))));");
        System.out.println("   List<ScoredVector> results = index.query(vector, 5);");

        System.out.println("\n3. ChromaDB:");
        System.out.println("   HttpClient client = HttpClient.create();");
        System.out.println("   ChromaClient chroma = new ChromaClient(client, \"http://localhost:8000\");");
        System.out.println("   Collection collection = chroma.getOrCreateCollection(\"docs\");");
        System.out.println("   collection.add(ids, embeddings, metadatas, documents);");
        System.out.println("   QueryResult result = collection.query(queryEmbedding, 10);");

        System.out.println("\n4. Weaviate:");
        System.out.println("   WeaviateClient client = new WeaviateClient(\"http://localhost:8080\");");
        System.out.println("   client.schema().createClass(schema);");
        System.out.println("   client.data().creator()");
        System.out.println("       .withClassName(\"Document\")");
        System.out.println("       .withProperties(Map.of(\"text\", content))");
        System.out.println("       .withVector(vector).run();");
        System.out.println("   GraphQLResponse response = client.graphQL().get()");
        System.out.println("       .withNearVector(nearVector).withLimit(10).run();");

        System.out.println("\n5. Qdrant:");
        System.out.println("   QdrantClient client = new QdrantClient(QdrantGrpcClient.newBuilder");
        System.out.println("       .setHost(\"localhost\").setPort(6334).build());");
        System.out.println("   client.upsertAsync(\"collection_name\", List.of(pointStruct)).get();");
        System.out.println("   SearchPoints search = SearchPoints.newBuilder()");
        System.out.println("       .setCollectionName(\"collection_name\")");
        System.out.println("       .addAllVector(vector)");
        System.out.println("       .setLimit(10)");
        System.out.println("       .build();");

        System.out.println("\n6. Similarity Metrics:");
        System.out.println("   - Cosine Similarity: measures angle between vectors");
        System.out.println("   - Euclidean Distance: measures straight-line distance");
        System.out.println("   - Dot Product: measures projection similarity");
        System.out.println("   - Manhattan Distance: sum of absolute differences");

        System.out.println("\n7. Use Cases:");
        System.out.println("   - Semantic Search: Find documents by meaning, not keywords");
        System.out.println("   - RAG: Retrieval-Augmented Generation for LLMs");
        System.out.println("   - Recommendation: Similar items based on embeddings");
        System.out.println("   - Anomaly Detection: Find outliers in embedding space");
        System.out.println("   - Deduplication: Find near-duplicate documents");

        System.out.println("\n=== Vector Database Lab Complete ===");
    }
}