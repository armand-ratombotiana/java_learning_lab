package com.rag.pipeline;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilmr6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    public EmbeddingService() {
        this.embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        this.embeddingStore = new InMemoryEmbeddingStore<>();
    }

    public List<float[]> createEmbeddings(List<String> texts) {
        log.info("Creating embeddings for {} texts", texts.size());
        return texts.stream()
            .map(text -> {
                Embedding embedding = embeddingModel.embed(text).content();
                return toFloatArray(embedding.vectorAsList());
            })
            .collect(Collectors.toList());
    }

    public float[] createEmbedding(String text) {
        Embedding embedding = embeddingModel.embed(text).content();
        return toFloatArray(embedding.vectorAsList());
    }

    public void storeEmbeddings(List<String> texts, List<String> chunkIds) {
        for (int i = 0; i < texts.size(); i++) {
            TextSegment segment = TextSegment.from(texts.get(i));
            embeddingModel.embed(segment);
            embeddingStore.add(embeddingModel.embed(segment).content(), segment);
        }
        log.info("Stored {} embeddings", texts.size());
    }

    public List<SearchResult> search(String query, int topK) {
        Embedding queryEmbedding = embeddingModel.embed(query).content();

        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
            .queryEmbedding(queryEmbedding.vectorAsList().stream()
                .map(Double::floatValue)
                .collect(Collectors.toList()))
            .maxResults(topK)
            .build();

        var results = embeddingStore.search(request);

        return results.matches().stream()
            .map(match -> new SearchResult(
                match.embedded().text(),
                match.score()
            ))
            .collect(Collectors.toList());
    }

    private float[] toFloatArray(List<Double> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i).floatValue();
        }
        return array;
    }

    public record SearchResult(String text, double score) {}
}