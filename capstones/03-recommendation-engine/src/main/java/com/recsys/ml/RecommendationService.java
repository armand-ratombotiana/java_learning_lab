package com.recsys.ml;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final MatrixFactorizationModel collaborativeFilter;
    private final ContentBasedFilter contentFilter;

    public List<RecommendedItem> getRecommendations(String userId, int limit) {
        log.info("Getting recommendations for user: {}", userId);

        List<RecommendedItem> collabRecs = collaborativeFilter.recommendItems(userId, limit).stream()
            .map(itemId -> new RecommendedItem(itemId, "COLLABORATIVE", 0.0))
            .toList();

        List<RecommendedItem> contentRecs = contentFilter.recommendItems(userId, limit).stream()
            .map(itemId -> new RecommendedItem(itemId, "CONTENT", 0.0))
            .toList();

        Map<String, Double> itemScores = new HashMap<>();
        for (int i = 0; i < collabRecs.size(); i++) {
            double score = 1.0 - (i / (double) collabRecs.size());
            itemScores.merge(collabRecs.get(i).itemId(), score * 0.6, Double::sum);
        }
        for (int i = 0; i < contentRecs.size(); i++) {
            double score = 1.0 - (i / (double) contentRecs.size());
            itemScores.merge(contentRecs.get(i).itemId(), score * 0.4, Double::sum);
        }

        return itemScores.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(limit)
            .map(e -> new RecommendedItem(e.getKey(), "HYBRID", e.getValue()))
            .toList();
    }

    public record RecommendedItem(String itemId, String source, double score) {}
}