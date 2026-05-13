package com.rag.service;

import com.rag.model.DocumentMetadata;
import com.rag.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<DocumentMetadata> getDocuments() {
        return documentRepository.findAll();
    }

    public Optional<DocumentMetadata> getDocument(String id) {
        return documentRepository.findById(id);
    }
}