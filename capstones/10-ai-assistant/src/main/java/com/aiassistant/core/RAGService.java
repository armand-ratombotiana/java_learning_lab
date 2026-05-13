package com.aiassistant.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RAGService {

    private final Map<String, List<KnowledgeEntry>> knowledgeBase;

    public RAGService() {
        this.knowledgeBase = new HashMap<>();
        initializeDefaultKnowledge();
    }

    private void initializeDefaultKnowledge() {
        addKnowledge("general", "Java is a high-level, class-based, object-oriented programming language", 0.9);
        addKnowledge("general", "Spring Boot is a framework for building production-ready applications", 0.9);
        addKnowledge("general", "Microservices architecture decomposes applications into small, independent services", 0.8);
        addKnowledge("coding", "Use @Autowired for dependency injection in Spring", 0.7);
        addKnowledge("coding", "Entity classes in JPA should use @Entity and @Id annotations", 0.7);
    }

    public void addKnowledge(String category, String content, double relevance) {
        knowledgeBase.computeIfAbsent(category, k -> new ArrayList<>())
            .add(new KnowledgeEntry(category, content, relevance, System.currentTimeMillis()));
    }

    public List<String> retrieve(String query, int topK) {
        log.info("Retrieving knowledge for: {}", query);

        List<ScoredEntry> scored = new ArrayList<>();

        for (List<KnowledgeEntry> entries : knowledgeBase.values()) {
            for (KnowledgeEntry entry : entries) {
                double score = calculateRelevance(query, entry.content());
                if (score > 0.3) {
                    scored.add(new ScoredEntry(entry, score));
                }
            }
        }

        return scored.stream()
            .sorted((a, b) -> Double.compare(b.score, a.score))
            .limit(topK)
            .map(s -> s.entry().content())
            .collect(Collectors.toList());
    }

    private double calculateRelevance(String query, String content) {
        String[] queryTerms = query.toLowerCase().split("\\s+");
        String contentLower = content.toLowerCase();

        int matches = 0;
        for (String term : queryTerms) {
            if (contentLower.contains(term)) {
                matches++;
            }
        }

        return matches / (double) queryTerms.length;
    }

    public record KnowledgeEntry(String category, String content, double relevance, long timestamp) {}
    public record ScoredEntry(KnowledgeEntry entry, double score) {}
}