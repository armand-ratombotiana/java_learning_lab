package com.rag.controller;

import com.rag.model.DocumentMetadata;
import com.rag.service.RAGService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rag")
@RequiredArgsConstructor
public class RAGController {

    private final RAGService ragService;

    @PostMapping("/documents")
    public ResponseEntity<Map<String, Object>> uploadDocument(
            @RequestParam("file") MultipartFile file) throws IOException {

        DocumentMetadata doc = ragService.ingestDocument(
            file.getOriginalFilename(),
            file.getContentType(),
            file.getInputStream()
        );

        return ResponseEntity.ok(Map.of(
            "documentId", doc.getId(),
            "fileName", doc.getFileName(),
            "chunks", doc.getChunks().size(),
            "status", doc.getStatus()
        ));
    }

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> query(@RequestBody Map<String, Object> request) {
        String question = (String) request.get("question");
        int topK = request.containsKey("topK") ? ((Number) request.get("topK")).intValue() : 5;

        RAGService.QueryResult result = ragService.query(question, topK);

        return ResponseEntity.ok(Map.of(
            "question", result.question(),
            "answer", result.answer(),
            "sources", result.sources().stream()
                .map(s -> Map.of("text", s.text().substring(0, Math.min(100, s.text().length())), "score", s.score()))
                .toList()
        ));
    }

    @GetMapping("/documents")
    public ResponseEntity<List<DocumentMetadata>> listDocuments() {
        return ResponseEntity.ok(ragService.getDocuments());
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<DocumentMetadata> getDocument(@PathVariable String id) {
        return ragService.getDocument(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}