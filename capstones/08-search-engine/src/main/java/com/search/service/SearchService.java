package com.search.service;

import com.search.core.DocumentStore;
import com.search.core.InvertedIndex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final InvertedIndex invertedIndex;
    private final DocumentStore documentStore;

    public void indexDocument(String id, String content, Map<String, Object> metadata) {
        documentStore.addDocument(id, content, metadata);
        invertedIndex.indexDocument(id, content, metadata);
        log.info("Indexed document: {}", id);
    }

    public SearchResult search(String query, int topK) {
        log.info("Searching for: {} (top {})", query, topK);

        List<InvertedIndex.ScoredDocument> scoredDocs = invertedIndex.search(query, topK);
        List<SearchHit> hits = scoredDocs.stream()
            .map(sd -> {
                var doc = documentStore.getDocument(sd.docId());
                return new SearchHit(
                    sd.docId(),
                    doc.map(DocumentStore.IndexedDocument::getContent).orElse(""),
                    sd.score(),
                    doc.map(d -> d.getHighlightedContent(query)).orElse("")
                );
            })
            .toList();

        return new SearchResult(query, hits.size(), hits);
    }

    public SearchResult searchBoolean(String query, int topK) {
        List<InvertedIndex.ScoredDocument> scoredDocs = invertedIndex.searchBoolean(query, topK);
        List<SearchHit> hits = scoredDocs.stream()
            .map(sd -> {
                var doc = documentStore.getDocument(sd.docId());
                return new SearchHit(
                    sd.docId(),
                    doc.map(DocumentStore.IndexedDocument::getContent).orElse(""),
                    sd.score(),
                    null
                );
            })
            .toList();

        return new SearchResult(query, hits.size(), hits);
    }

    public Map<String, Long> getFacets(String field) {
        return invertedIndex.getFacets(field);
    }

    public record SearchResult(String query, int totalHits, List<SearchHit> hits) {}
    public record SearchHit(String id, String content, double score, String highlight) {}
}