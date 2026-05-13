package com.search.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class InvertedIndex {

    private final Map<String, Map<String, PostingsList>> index;
    private final Map<String, Integer> documentFrequencies;
    private int totalDocuments;

    public InvertedIndex() {
        this.index = new ConcurrentHashMap<>();
        this.documentFrequencies = new ConcurrentHashMap<>();
        this.totalDocuments = 0;
    }

    public void indexDocument(String docId, String content, Map<String, Object> fields) {
        String[] tokens = tokenize(content);
        Map<String, Integer> termFreqs = new HashMap<>();

        for (String token : tokens) {
            termFreqs.merge(token, 1, Integer::sum);
        }

        for (Map.Entry<String, Integer> entry : termFreqs.entrySet()) {
            String term = entry.getKey();
            int freq = entry.getValue();

            index.computeIfAbsent(term, k -> new ConcurrentHashMap<>())
                 .computeIfAbsent(docId, k -> new PostingsList())
                 .addPosition(freq);
        }

        for (String token : tokens) {
            documentFrequencies.merge(token, 1, Integer::sum);
        }

        totalDocuments++;
        log.debug("Indexed document {} with {} terms", docId, tokens.length);
    }

    public List<ScoredDocument> search(String query, int topK) {
        String[] queryTerms = tokenize(query);
        if (queryTerms.length == 0) {
            return List.of();
        }

        Map<String, Double> docScores = new HashMap<>();
        double idf = calculateIDF(queryTerms.length);

        for (String term : queryTerms) {
            Map<String, PostingsList> postings = index.get(term);
            if (postings == null) continue;

            for (Map.Entry<String, PostingsList> entry : postings.entrySet()) {
                String docId = entry.getKey();
                PostingsList postingsList = entry.getValue();

                double tf = calculateTF(postingsList);
                double score = idf * tf;

                docScores.merge(docId, score, Double::sum);
            }
        }

        return docScores.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(topK)
            .map(e -> new ScoredDocument(e.getKey(), e.getValue()))
            .toList();
    }

    public List<ScoredDocument> searchBoolean(String query, int topK) {
        query = query.trim();

        if (query.contains(" AND ")) {
            return searchAnd(query.split(" AND "), topK);
        } else if (query.contains(" OR ")) {
            return searchOr(query.split(" OR "), topK);
        }

        return search(query, topK);
    }

    private List<ScoredDocument> searchAnd(String[] terms, int topK) {
        List<Set<String>> docSets = new ArrayList<>();
        for (String term : terms) {
            Map<String, PostingsList> postings = index.get(term.trim());
            if (postings != null) {
                docSets.add(new HashSet<>(postings.keySet()));
            }
        }

        if (docSets.isEmpty()) return List.of();

        Set<String> intersection = new HashSet<>(docSets.get(0));
        for (int i = 1; i < docSets.size(); i++) {
            intersection.retainAll(docSets.get(i));
        }

        List<ScoredDocument> results = new ArrayList<>();
        for (String docId : intersection) {
            results.add(new ScoredDocument(docId, 1.0));
        }
        results.sort((a, b) -> Double.compare(b.score, a.score));
        return results.subList(0, Math.min(topK, results.size()));
    }

    private List<ScoredDocument> searchOr(String[] terms, int topK) {
        Set<String> allDocs = new HashSet<>();
        for (String term : terms) {
            Map<String, PostingsList> postings = index.get(term.trim());
            if (postings != null) {
                allDocs.addAll(postings.keySet());
            }
        }

        List<ScoredDocument> results = new ArrayList<>();
        for (String docId : allDocs) {
            results.add(new ScoredDocument(docId, 1.0));
        }
        return results.subList(0, Math.min(topK, results.size()));
    }

    private String[] tokenize(String text) {
        return text.toLowerCase()
            .replaceAll("[^a-z0-9\\s]", "")
            .split("\\s+")
            .filter(t -> t.length() > 2)
            .toArray(String[]::new);
    }

    private double calculateTF(PostingsList postings) {
        int termFreq = postings.getTotalFrequency();
        return 1 + Math.log(termFreq);
    }

    private double calculateIDF(int numTerms) {
        if (totalDocuments == 0) return 0;
        double avgDf = documentFrequencies.values().stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(1);
        return Math.log(totalDocuments / Math.max(avgDf, 1));
    }

    public Map<String, Long> getFacets(String field) {
        Map<String, Long> facets = new HashMap<>();
        for (Map<String, PostingsList> postings : index.values()) {
            for (String docId : postings.keySet()) {
                facets.merge(docId, 1L, Long::sum);
            }
        }
        return facets;
    }

    @Data
    public static class PostingsList {
        private int totalFrequency = 0;
        private final List<Integer> positions = new ArrayList<>();

        public void addPosition(int frequency) {
            this.totalFrequency += frequency;
            this.positions.add(totalFrequency);
        }

        public int getTotalFrequency() {
            return totalFrequency;
        }
    }

    public record ScoredDocument(String docId, double score) {}
}