package com.capstone.rag;

import java.util.*;
import java.util.stream.Collectors;

public class Retriever {
    private final VectorStore vectorStore;
    private final Reranker reranker;
    private final ContextBuilder contextBuilder;
    private int topK = 5;
    private double alpha = 0.5;

    public record RetrievalResult(String id, float score, String text, Map<String, String> metadata, RetrievalMethod method) {}
    public enum RetrievalMethod { DENSE, KEYWORD, HYBRID }

    public Retriever(VectorStore vectorStore, Reranker reranker, ContextBuilder contextBuilder) {
        this.vectorStore = vectorStore;
        this.reranker = reranker;
        this.contextBuilder = contextBuilder;
    }

    public List<RetrievalResult> denseRetrieve(String query) {
        var results = vectorStore.search(query, topK);
        return results.stream()
            .map(r -> new RetrievalResult(r.id(), r.score(), r.text(), r.metadata(), RetrievalMethod.DENSE))
            .collect(Collectors.toList());
    }

    public List<RetrievalResult> keywordRetrieve(String query) {
        String[] queryTerms = query.toLowerCase().split("\\s+");
        List<VectorStore.SearchResult> allResults = new ArrayList<>();
        for (String term : queryTerms) {
            allResults.addAll(vectorStore.search(term, topK));
        }
        Map<String, List<VectorStore.SearchResult>> grouped = allResults.stream()
            .collect(Collectors.groupingBy(VectorStore.SearchResult::id));
        return grouped.entrySet().stream()
            .map(e -> {
                double avgScore = e.getValue().stream().mapToDouble(VectorStore.SearchResult::score).average().orElse(0);
                var first = e.getValue().get(0);
                return new RetrievalResult(first.id(), (float) avgScore, first.text(), first.metadata(), RetrievalMethod.KEYWORD);
            })
            .sorted(Comparator.comparingDouble(RetrievalResult::score).reversed())
            .limit(topK)
            .collect(Collectors.toList());
    }

    public List<RetrievalResult> hybridRetrieve(String query) {
        var dense = denseRetrieve(query);
        var keyword = keywordRetrieve(query);
        Map<String, RetrievalResult> merged = new HashMap<>();
        for (RetrievalResult r : dense) merged.put(r.id(), r);
        for (RetrievalResult r : keyword) {
            merged.merge(r.id(), r, (a, b) -> new RetrievalResult(
                a.id(), a.score() * (float) alpha + b.score() * (float) (1 - alpha),
                a.text(), a.metadata(), RetrievalMethod.HYBRID));
        }
        return merged.values().stream()
            .sorted(Comparator.comparingDouble(RetrievalResult::score).reversed())
            .limit(topK)
            .collect(Collectors.toList());
    }

    public String retrieveAndBuildContext(String query) {
        List<RetrievalResult> results = hybridRetrieve(query);
        List<String> texts = results.stream().map(RetrievalResult::text).collect(Collectors.toList());
        List<Double> scores = results.stream().map(r -> (double) r.score()).collect(Collectors.toList());
        return contextBuilder.buildContext(query, texts, scores);
    }

    public List<RetrievalResult> retrieveWithRerank(String query) {
        var results = denseRetrieve(query);
        return reranker.rerank(query, results);
    }

    public void setTopK(int topK) { this.topK = topK; }
    public void setAlpha(double alpha) { this.alpha = alpha; }
}
