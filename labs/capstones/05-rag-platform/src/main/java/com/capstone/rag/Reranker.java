package com.capstone.rag;

import java.util.*;
import java.util.stream.Collectors;

public class Reranker {
    private final EmbeddingInterface embeddingFn;

    public Reranker(EmbeddingInterface embeddingFn) {
        this.embeddingFn = embeddingFn;
    }

    public List<Retriever.RetrievalResult> rerank(String query, List<Retriever.RetrievalResult> candidates) {
        if (candidates.isEmpty()) return candidates;
        float[] queryVec = embeddingFn.embed(query);
        return candidates.parallelStream()
            .map(c -> {
                float[] docVec = embeddingFn.embed(c.text());
                float score = crossSimilarity(queryVec, docVec);
                return new Retriever.RetrievalResult(c.id(), score, c.text(), c.metadata(), c.method());
            })
            .sorted(Comparator.comparingDouble(Retriever.RetrievalResult::score).reversed())
            .collect(Collectors.toList());
    }

    public List<VectorStore.SearchResult> rerank(String query, List<VectorStore.SearchResult> candidates) {
        if (candidates.isEmpty()) return candidates;
        float[] queryVec = embeddingFn.embed(query);
        return candidates.parallelStream()
            .map(c -> {
                float[] docVec = embeddingFn.embed(c.text());
                float score = crossSimilarity(queryVec, docVec);
                return new VectorStore.SearchResult(c.id(), score, c.text(), c.metadata());
            })
            .sorted(Comparator.comparingDouble(VectorStore.SearchResult::score).reversed())
            .collect(Collectors.toList());
    }

    private float crossSimilarity(float[] a, float[] b) {
        float dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i]; na += a[i] * a[i]; nb += b[i] * b[i];
        }
        return (float) (dot / (Math.sqrt(na) * Math.sqrt(nb) + 1e-10));
    }
}
