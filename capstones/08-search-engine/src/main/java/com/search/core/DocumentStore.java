package com.search.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class DocumentStore {

    private final Map<String, IndexedDocument> documents;
    private final Map<String, Map<String, Object>> fields;

    public DocumentStore() {
        this.documents = new ConcurrentHashMap<>();
        this.fields = new ConcurrentHashMap<>();
    }

    public void addDocument(String id, String content, Map<String, Object> metadata) {
        IndexedDocument doc = new IndexedDocument(id, content, metadata, System.currentTimeMillis());
        documents.put(id, doc);
        fields.put(id, metadata);
        log.debug("Added document: {}", id);
    }

    public Optional<IndexedDocument> getDocument(String id) {
        return Optional.ofNullable(documents.get(id));
    }

    public List<IndexedDocument> getDocuments(List<String> ids) {
        return ids.stream()
            .map(documents::get)
            .filter(Objects::nonNull)
            .toList();
    }

    public void deleteDocument(String id) {
        documents.remove(id);
        fields.remove(id);
        log.debug("Deleted document: {}", id);
    }

    public long size() {
        return documents.size();
    }

    @Data
    public static class IndexedDocument {
        private final String id;
        private final String content;
        private final Map<String, Object> metadata;
        private final long timestamp;

        public String getHighlightedContent(String query) {
            if (content == null) return "";
            String[] words = query.toLowerCase().split("\\s+");
            String result = content;
            for (String word : words) {
                if (word.length() > 2) {
                    result = result.replaceAll("(?i)(" + word + ")", "<em>$1</em>");
                }
            }
            return result;
        }
    }
}