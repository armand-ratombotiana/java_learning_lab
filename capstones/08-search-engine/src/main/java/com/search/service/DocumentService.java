package com.search.service;

import com.search.core.DocumentStore;
import com.search.core.InvertedIndex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class DocumentService {

    private final DocumentStore documentStore;

    public DocumentService(DocumentStore documentStore) {
        this.documentStore = documentStore;
    }

    public Optional<DocumentStore.IndexedDocument> getDocument(String id) {
        return documentStore.getDocument(id);
    }

    public long getDocumentCount() {
        return documentStore.size();
    }

    public long getIndexSize() {
        return documentStore.size();
    }

    public void deleteDocument(String id) {
        documentStore.deleteDocument(id);
    }
}