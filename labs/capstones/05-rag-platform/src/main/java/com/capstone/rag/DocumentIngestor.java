package com.capstone.rag;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class DocumentIngestor {
    private final Map<String, Document> documents = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(0);
    private final List<IngestionListener> listeners = new ArrayList<>();
    private final ChunkingStrategy chunker;

    public interface IngestionListener {
        void onDocumentIngested(Document doc);
        void onChunkCreated(Chunk chunk);
    }

    public record Document(String id, String title, String source, String rawContent, DocumentType type, long ingestedAt) {}
    public record Chunk(String id, String documentId, String content, int chunkIndex, Map<String, Object> metadata) {}

    public enum DocumentType { PDF, HTML, TEXT, MARKDOWN, UNKNOWN }

    public DocumentIngestor(ChunkingStrategy chunker) {
        this.chunker = chunker;
    }

    public Document ingest(String source, String title, String rawContent, DocumentType type) {
        String id = "doc-" + idGen.incrementAndGet();
        Document doc = new Document(id, title, source, rawContent, type, System.currentTimeMillis());
        documents.put(id, doc);
        for (IngestionListener l : listeners) l.onDocumentIngested(doc);
        List<Chunk> chunks = chunker.chunk(doc);
        for (Chunk c : chunks) {
            for (IngestionListener l : listeners) l.onChunkCreated(c);
        }
        return doc;
    }

    public Document ingestFile(Path filePath) throws IOException {
        String content = Files.readString(filePath);
        String fileName = filePath.getFileName().toString();
        DocumentType type = detectType(fileName);
        return ingest(filePath.toString(), fileName, content, type);
    }

    public Optional<Document> getDocument(String id) {
        return Optional.ofNullable(documents.get(id));
    }

    public List<Document> getAllDocuments() { return List.copyOf(documents.values()); }

    public boolean removeDocument(String id) {
        return documents.remove(id) != null;
    }

    public void addListener(IngestionListener listener) { listeners.add(listener); }

    public int documentCount() { return documents.size(); }

    public void clear() { documents.clear(); idGen.set(0); }

    private DocumentType detectType(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return switch (ext) {
            case "pdf" -> DocumentType.PDF;
            case "html", "htm" -> DocumentType.HTML;
            case "md" -> DocumentType.MARKDOWN;
            case "txt" -> DocumentType.TEXT;
            default -> DocumentType.UNKNOWN;
        };
    }
}
