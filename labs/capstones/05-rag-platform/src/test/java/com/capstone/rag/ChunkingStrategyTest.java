package com.capstone.rag;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ChunkingStrategyTest {
    private DocumentIngestor.Document doc;

    @BeforeEach
    void setUp() {
        String content = "This is a test document. It has multiple sentences. " +
            "We need to split it into chunks. The chunks should be meaningful. " +
            "Each chunk should have enough context. This is important for retrieval.";
        doc = new DocumentIngestor.Document("doc-1", "Test", "src", content,
            DocumentIngestor.DocumentType.TEXT, System.currentTimeMillis());
    }

    @Test void testFixedSizeChunker() {
        var chunker = new ChunkingStrategy.FixedSizeChunker(50, 10);
        var chunks = chunker.chunk(doc);
        assertTrue(chunks.size() >= 2);
        assertEquals("doc-1-chunk-0", chunks.get(0).id());
    }

    @Test void testSemanticChunker() {
        var chunker = new ChunkingStrategy.SemanticChunker(200, 20);
        var chunks = chunker.chunk(doc);
        assertTrue(chunks.size() >= 1);
        assertTrue(chunks.get(0).content().length() > 0);
    }

    @Test void testRecursiveChunker() {
        var chunker = new ChunkingStrategy.RecursiveChunker(100, 0);
        var chunks = chunker.chunk(doc);
        assertTrue(chunks.size() >= 1);
    }

    @Test void testChunkMetadata() {
        var chunker = new ChunkingStrategy.FixedSizeChunker(200, 20);
        var chunks = chunker.chunk(doc);
        assertEquals("fixed", chunks.get(0).metadata().get("strategy"));
    }

    @Test void testEmptyDocument() {
        var emptyDoc = new DocumentIngestor.Document("empty", "Empty", "src", "",
            DocumentIngestor.DocumentType.TEXT, System.currentTimeMillis());
        var chunker = new ChunkingStrategy.FixedSizeChunker(100, 10);
        var chunks = chunker.chunk(emptyDoc);
        assertTrue(chunks.isEmpty());
    }
}
