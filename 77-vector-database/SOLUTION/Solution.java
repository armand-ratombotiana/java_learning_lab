package com.learning.vector;

import io.pinecone.Pinecone;
import io.pinecone.proto.*;
import io.milvus.client.*;
import io.milvus.param.*;
import io.milvus.response.*;
import io.trycatcher.chroma.client.ChromaClient;
import io.trycatcher.chroma.client.ChromaResponse;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.OnnxBertBiEncoder;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.*;
import java.util.stream.Collectors;

public class Solution {

    public static class PineconeExample {

        public Pinecone createClient(String apiKey) {
            return new Pinecone.Builder(apiKey).build();
        }

        public IndexConnection createIndexConnection(Pinecone client, String indexName) {
            return client.connect(new IndexConnection.Builder(indexName).build());
        }

        public void upsertVectors(IndexConnection connection, List<String> ids,
                                  List<float[]> vectors, Map<String, String> metadata) {
            List<Vector> vectorList = new ArrayList<>();
            for (int i = 0; i < ids.size(); i++) {
                Vector.Builder builder = Vector.newBuilder()
                    .setId(ids.get(i))
                    .addAllValues(toDoubleList(vectors.get(i)));
                if (metadata != null) {
                    builder.putAllMetadata(metadata);
                }
                vectorList.add(builder.build());
            }

            UpsertRequest request = UpsertRequest.newBuilder()
                .setNamespace("default")
                .addAllVectors(vectorList)
                .build();

            connection.upsert(request);
        }

        public QueryResult queryVectors(IndexConnection connection, float[] queryVector,
                                        int topK, Map<String, String> filter) {
            QueryRequest.Builder builder = QueryRequest.newBuilder()
                .setNamespace("default")
                .addAllQuery(toDoubleList(queryVector))
                .setTopK(topK)
                .setIncludeMetadata(true)
                .setIncludeValues(true);

            if (filter != null) {
                builder.setFilter(filter);
            }

            return connection.query(builder.build());
        }

        public void deleteVectors(IndexConnection connection, List<String> ids) {
            DeleteRequest request = DeleteRequest.newBuilder()
                .setNamespace("default")
                .addAllIds(ids)
                .build();
            connection.delete(request);
        }

        public List<String> getNamespaces(IndexConnection connection) {
            return connection.describe().getResult().getNamespaceSummaryList()
                .stream()
                .map(NamespaceSummary::getName)
                .collect(Collectors.toList());
        }
    }

    public static class MilvusExample {

        public MilvusServiceClient createClient(String host, int port, String token) {
            return new MilvusServiceClient(ConnectParam.newBuilder()
                .withHost(host)
                .withPort(port)
                .withToken(token)
                .build());
        }

        public void createCollection(MilvusServiceClient client, String collectionName, int dimension) {
            CreateCollectionParam param = CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withShardsNum(2)
                .withDisableAutoID(false)
                .build();

            FieldType fieldId = FieldType.newBuilder()
                .withName("id")
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(true)
                .build();

            FieldType fieldVector = FieldType.newBuilder()
                .withName("vector")
                .withDataType(DataType.FloatVector)
                .withDimension(dimension)
                .build();

            FieldType fieldText = FieldType.newBuilder()
                .withName("text")
                .withDataType(DataType.VarChar)
                .withMaxLength(65535)
                .build();

            param.addFieldType(fieldId);
            param.addFieldType(fieldVector);
            param.addFieldType(fieldText);

            client.createCollection(param);
        }

        public void insertVectors(MilvusServiceClient client, String collectionName,
                                  List<float[]> vectors, List<String> texts) {
            List<InsertParam.Field> fields = new ArrayList<>();

            List<Long> ids = new ArrayList<>();
            for (int i = 0; i < vectors.size(); i++) {
                ids.add((long) i);
            }
            fields.add(new InsertParam.Field("id", ids));

            fields.add(new InsertParam.Field("vector", vectors));

            fields.add(new InsertParam.Field("text", texts));

            InsertParam param = InsertParam.newBuilder()
                .withCollectionName(collectionName)
                .withFields(fields)
                .build();

            client.insert(param);
        }

        public List<QueryResponseWrapper> searchVectors(MilvusServiceClient client,
                                                        String collectionName, float[] queryVector, int topK) {
            List<List<Float>> vectorList = new ArrayList<>();
            vectorList.add(toDoubleList(queryVector));

            SearchParam param = SearchParam.newBuilder()
                .withCollectionName(collectionName)
                .withVectorFieldName("vector")
                .withVectorType(DataType.FloatVector)
                .withSearchVectors(vectorList)
                .withTopK(topK)
                .withConsistencyLevel(ConsistencyLevel.EVENTUALLY)
                .build();

            return client.search(param);
        }

        public void deleteCollection(MilvusServiceClient client, String collectionName) {
            DropCollectionParam param = DropCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();
            client.dropCollection(param);
        }
    }

    public static class ChromaExample {

        public ChromaClient createClient(String host) {
            return new ChromaClient.Builder()
                .host(host)
                .build();
        }

        public void createCollection(ChromaClient client, String collectionName) {
            client.createCollection(collectionName);
        }

        public void addDocuments(ChromaClient client, String collectionName,
                                 List<String> documents, List<String> ids) {
            client.collection(collectionName).add(documents, ids);
        }

        public List<ChromaResponse> query(ChromaClient client, String collectionName,
                                           String query, int nResults) {
            return client.collection(collectionName)
                .query(nResults, Collections.singletonList(query), null);
        }

        public void deleteCollection(ChromaClient client, String collectionName) {
            client.deleteCollection(collectionName);
        }

        public void updateDocuments(ChromaClient client, String collectionName,
                                    List<String> documents, List<String> ids) {
            client.collection(collectionName).update(documents, ids);
        }
    }

    public static class EmbeddingExample {

        public EmbeddingModel createEmbeddingModel() {
            return new OnnxBertBiEncoder();
        }

        public float[] generateEmbedding(EmbeddingModel model, String text) {
            return model.embed(text).content();
        }

        public List<float[]> generateEmbeddings(EmbeddingModel model, List<String> texts) {
            return texts.stream()
                .map(text -> model.embed(text).content())
                .toList();
        }

        public float calculateCosineSimilarity(float[] vec1, float[] vec2) {
            float dotProduct = 0;
            float norm1 = 0;
            float norm2 = 0;

            for (int i = 0; i < vec1.length; i++) {
                dotProduct += vec1[i] * vec2[i];
                norm1 += vec1[i] * vec1[i];
                norm2 += vec2[i] * vec2[i];
            }

            return dotProduct / (float) Math.sqrt(norm1 * norm2);
        }
    }

    public static class InMemoryVectorStoreExample {

        public EmbeddingStore<TextSegment> createInMemoryStore() {
            return new InMemoryEmbeddingStore<>();
        }

        public void addEmbeddings(EmbeddingStore<TextSegment> store,
                                  List<float[]> embeddings, List<String> texts) {
            List<TextSegment> segments = texts.stream()
                .map(TextSegment::from)
                .toList();

            for (int i = 0; i < embeddings.size(); i++) {
                store.add(new dev.langchain4j.data.embedding.Embedding(embeddings.get(i)), segments.get(i));
            }
        }

        public List<dev.langchain4j.store.embedding.RelevantEmbedding<TextSegment>>
                findRelevant(EmbeddingStore<TextSegment> store, float[] queryEmbedding, int maxResults) {
            return store.findRelevant(queryEmbedding, maxResults);
        }
    }

    public static class HybridSearchExample {

        public List<String> hybridSearch(EmbeddingStore<TextSegment> vectorStore,
                                          List<Map<String, Object>> metadataResults,
                                          String keyword, int topK) {
            List<String> results = new ArrayList<>();

            List<dev.langchain4j.store.embedding.RelevantEmbedding<TextSegment>> semanticResults =
                vectorStore.findRelevant(embed("keyword".getBytes()), topK);

            for (var result : metadataResults) {
                if (result.containsKey("text") &&
                    ((String) result.get("text")).toLowerCase().contains(keyword.toLowerCase())) {
                    results.add((String) result.get("text"));
                }
            }

            for (var sr : semanticResults) {
                if (!results.contains(sr.embedded().text())) {
                    results.add(sr.embedded().text());
                }
            }

            return results.stream().limit(topK).toList();
        }

        private float[] embed(byte[] bytes) {
            float[] result = new float[384];
            for (int i = 0; i < result.length; i++) {
                result[i] = bytes[i % bytes.length] / 255.0f;
            }
            return result;
        }
    }

    private static List<Float> toDoubleList(float[] array) {
        List<Float> list = new ArrayList<>();
        for (float f : array) {
            list.add(f);
        }
        return list;
    }

    public static void main(String[] args) {
        System.out.println("Vector Database Solutions");
        System.out.println("=========================");

        PineconeExample pinecone = new PineconeExample();
        System.out.println("Pinecone examples available");

        MilvusExample milvus = new MilvusExample();
        System.out.println("Milvus examples available");

        ChromaExample chroma = new ChromaExample();
        System.out.println("Chroma examples available");

        EmbeddingExample embedding = new EmbeddingExample();
        EmbeddingModel model = embedding.createEmbeddingModel();
        System.out.println("Embedding model created");

        InMemoryVectorStoreExample inMemoryStore = new InMemoryVectorStoreExample();
        EmbeddingStore<TextSegment> store = inMemoryStore.createInMemoryStore();
        System.out.println("In-memory vector store ready");

        HybridSearchExample hybridSearch = new HybridSearchExample();
        System.out.println("Hybrid search utilities available");
    }
}