package com.vector;

import com.vector.core.VectorStore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VectorController {

    private final VectorStore vectorStore;
    private int nextId = 1;

    @PostMapping("/collections/{collection}/vectors")
    public Map<String, Object> insertVector(
            @PathVariable String collection,
            @RequestBody Map<String, Object> request) {

        List<Double> vectorList = ((List<?>) request.get("vector")).stream()
            .map(v -> ((Number) v).doubleValue())
            .collect(Collectors.toList());

        double[] vector = new double[vectorList.size()];
        for (int i = 0; i < vectorList.size(); i++) {
            vector[i] = vectorList.get(i);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) request.getOrDefault("metadata", Map.of());

        int id = nextId++;
        vectorStore.insert(collection, id, vector, metadata);

        return Map.of("id", id, "status", "inserted");
    }

    @PostMapping("/collections/{collection}/search")
    public Map<String, Object> search(
            @PathVariable String collection,
            @RequestBody Map<String, Object> request) {

        List<Double> queryList = ((List<?>) request.get("query")).stream()
            .map(v -> ((Number) v).doubleValue())
            .collect(Collectors.toList());

        double[] query = new double[queryList.size()];
        for (int i = 0; i < queryList.size(); i++) {
            query[i] = queryList.get(i);
        }

        int topK = ((Number) request.getOrDefault("topK", 10)).intValue();

        List<VectorStore.SearchResult> results = vectorStore.search(collection, query, topK);

        return Map.of(
            "results", results.stream()
                .map(r -> Map.of(
                    "id", r.id(),
                    "distance", r.distance(),
                    "metadata", r.metadata()
                ))
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/vectors/{id}")
    public Map<String, Object> getVector(@PathVariable int id) {
        return vectorStore.get(id)
            .map(r -> Map.<String, Object>of(
                "id", r.id(),
                "vector", r.vector(),
                "metadata", r.metadata(),
                "timestamp", r.timestamp()
            ))
            .orElse(Map.of("error", "not found"));
    }

    @DeleteMapping("/collections/{collection}/vectors/{id}")
    public Map<String, Object> deleteVector(
            @PathVariable String collection,
            @PathVariable int id) {
        vectorStore.delete(id, collection);
        return Map.of("status", "deleted");
    }
}