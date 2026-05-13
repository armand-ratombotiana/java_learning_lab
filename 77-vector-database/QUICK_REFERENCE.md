# Vector Database Quick Reference

## Embedding Generation
```java
String text = "Java is a programming language";
float[] vector = embeddingModel.embed(text);
// vector.length = 384 (all-MiniLM-L6-v2)
// vector.length = 1536 (OpenAI Ada-002)
```

## Similarity Metrics

| Metric | Description |
|--------|-------------|
| Cosine Similarity | Measures angle between vectors |
| Euclidean Distance | Straight-line distance |
| Dot Product | Projection similarity |
| Manhattan Distance | Sum of absolute differences |

## Vector Databases

### Pinecone
```java
Index index = new PineconeIndex("my-index");
index.upsert(List.of(new Vector("id-1", vector, Map.of("text", text))));
List<ScoredVector> results = index.query(vector, 5);
```

### ChromaDB
```java
ChromaClient chroma = new ChromaClient(client, "http://localhost:8000");
Collection collection = chroma.getOrCreateCollection("docs");
collection.add(ids, embeddings, metadatas, documents);
QueryResult result = collection.query(queryEmbedding, 10);
```

### Weaviate
```java
WeaviateClient client = new WeaviateClient("http://localhost:8080");
client.schema().createClass(schema);
client.data().creator()
    .withClassName("Document")
    .withProperties(Map.of("text", content))
    .withVector(vector).run();
GraphQLResponse response = client.graphQL().get()
    .withNearVector(nearVector).withLimit(10).run();
```

### Qdrant
```java
QdrantClient client = new QdrantClient(QdrantGrpcClient.newBuilder()
    .setHost("localhost").setPort(6334).build());
client.upsertAsync("collection_name", List.of(pointStruct)).get();
SearchPoints search = SearchPoints.newBuilder()
    .setCollectionName("collection_name")
    .addAllVector(vector)
    .setLimit(10).build();
```

## Use Cases
- Semantic Search: Find documents by meaning, not keywords
- RAG: Retrieval-Augmented Generation for LLMs
- Recommendation: Similar items based on embeddings
- Anomaly Detection: Find outliers in embedding space
- Deduplication: Find near-duplicate documents