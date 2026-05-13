package com.search.controller;

import com.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/documents")
    public ResponseEntity<Map<String, String>> indexDocument(@RequestBody Map<String, Object> request) {
        String id = (String) request.get("id");
        String content = (String) request.get("content");
        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) request.getOrDefault("metadata", Map.of());

        searchService.indexDocument(id, content, metadata);
        return ResponseEntity.ok(Map.of("status", "indexed", "id", id));
    }

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> search(@RequestBody Map<String, Object> request) {
        String query = (String) request.get("query");
        int topK = request.containsKey("topK") ? ((Number) request.get("topK")).intValue() : 10;
        boolean useBoolean = (Boolean) request.getOrDefault("boolean", false);

        SearchService.SearchResult result;
        if (useBoolean) {
            result = searchService.searchBoolean(query, topK);
        } else {
            result = searchService.search(query, topK);
        }

        return ResponseEntity.ok(Map.of(
            "query", result.query(),
            "totalHits", result.totalHits(),
            "hits", result.hits().stream()
                .map(h -> Map.of(
                    "id", h.id(),
                    "score", h.score(),
                    "content", h.content().substring(0, Math.min(200, h.content().length())),
                    "highlight", h.highlight() != null ? h.highlight().substring(0, Math.min(200, h.highlight().length())) : ""
                ))
                .toList()
        ));
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<Map<String, Object>> getDocument(@PathVariable String id) {
        return searchService.getDocument(id)
            .map(doc -> ResponseEntity.ok((Map<String, Object>) Map.of(
                "id", doc.getId(),
                "content", doc.getContent(),
                "metadata", doc.getMetadata()
            )))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(Map.of(
            "totalDocuments", searchService.getDocumentCount(),
            "indexSize", searchService.getIndexSize()
        ));
    }

    private java.util.Optional<com.search.core.DocumentStore.IndexedDocument> getDocument(String id) {
        return java.util.Optional.empty();
    }
}