package com.capstone.rag;

import java.util.*;
import java.util.stream.Collectors;

public class RAGEvaluator {
    private final List<EvaluationSample> samples = new ArrayList<>();
    private final List<EvaluationResult> results = new ArrayList<>();

    public record EvaluationSample(String query, List<String> relevantIds, String expectedAnswer) {}
    public record EvaluationResult(String query, double recall, double precision, double mrr,
                                    double faithfulness, double answerRelevance) {}

    public void addSample(EvaluationSample sample) { samples.add(sample); }

    public EvaluationResult evaluate(Retriever retriever, List<Retriever.RetrievalResult> retrieved) {
        EvaluationSample sample = findSample(retrieved.isEmpty() ? "" : retrieved.get(0).id());
        if (sample == null) return new EvaluationResult("", 0, 0, 0, 0, 0);

        Set<String> retrievedIds = retrieved.stream().map(Retriever.RetrievalResult::id).collect(Collectors.toSet());
        Set<String> relevantIds = new HashSet<>(sample.relevantIds());

        long truePositives = retrievedIds.stream().filter(relevantIds::contains).count();
        double recall = relevantIds.isEmpty() ? 0 : (double) truePositives / relevantIds.size();
        double precision = retrievedIds.isEmpty() ? 0 : (double) truePositives / retrievedIds.size();

        double mrr = 0;
        for (int i = 0; i < retrieved.size(); i++) {
            if (relevantIds.contains(retrieved.get(i).id())) {
                mrr = 1.0 / (i + 1);
                break;
            }
        }

        EvaluationResult result = new EvaluationResult(
            sample.query(), recall, precision, mrr,
            calculateFaithfulness(sample, retrieved),
            calculateAnswerRelevance(sample, retrieved));
        results.add(result);
        return result;
    }

    public List<EvaluationResult> evaluateAll(Retriever retriever) {
        results.clear();
        for (EvaluationSample sample : samples) {
            var retrieved = retriever.hybridRetrieve(sample.query());
            evaluate(retriever, retrieved);
        }
        return getAggregateResults();
    }

    public List<EvaluationResult> getAggregateResults() {
        if (results.isEmpty()) return List.of();
        double avgRecall = results.stream().mapToDouble(EvaluationResult::recall).average().orElse(0);
        double avgPrecision = results.stream().mapToDouble(EvaluationResult::precision).average().orElse(0);
        double avgMRR = results.stream().mapToDouble(EvaluationResult::mrr).average().orElse(0);
        double avgFaithfulness = results.stream().mapToDouble(EvaluationResult::faithfulness).average().orElse(0);
        double avgRelevance = results.stream().mapToDouble(EvaluationResult::answerRelevance).average().orElse(0);
        return List.of(new EvaluationResult("AGGREGATE", avgRecall, avgPrecision, avgMRR, avgFaithfulness, avgRelevance));
    }

    public List<EvaluationResult> getDetailedResults() { return List.copyOf(results); }

    private EvaluationSample findSample(String retrievedId) {
        return samples.stream()
            .filter(s -> s.relevantIds().contains(retrievedId))
            .findFirst().orElse(null);
    }

    private double calculateFaithfulness(EvaluationSample sample, List<Retriever.RetrievalResult> retrieved) {
        if (retrieved.isEmpty()) return 0;
        long faithfulCount = retrieved.stream()
            .filter(r -> sample.expectedAnswer() != null && r.text().contains(sample.expectedAnswer()))
            .count();
        return (double) faithfulCount / retrieved.size();
    }

    private double calculateAnswerRelevance(EvaluationSample sample, List<Retriever.RetrievalResult> retrieved) {
        if (retrieved.isEmpty()) return 0;
        String query = sample.query().toLowerCase();
        long relevantCount = retrieved.stream()
            .filter(r -> r.text().toLowerCase().contains(query))
            .count();
        return (double) relevantCount / retrieved.size();
    }

    public void clear() { samples.clear(); results.clear(); }
}
