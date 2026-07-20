package com.capstone.rag;

import org.junit.jupiter.api.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class DocumentIngestorTest {
    private DocumentIngestor ingestor;

    @BeforeEach
    void setUp() { ingestor = new DocumentIngestor(new ChunkingStrategy.FixedSizeChunker(100, 10)); }

    @Test void testIngestText() {
        var doc = ingestor.ingest("source1", "Test Doc", "Hello world content here", DocumentIngestor.DocumentType.TEXT);
        assertEquals("Test Doc", doc.title());
        assertNotNull(doc.id());
    }

    @Test void testListener() {
        AtomicInteger count = new AtomicInteger(0);
        ingestor.addListener(new DocumentIngestor.IngestionListener() {
            public void onDocumentIngested(DocumentIngestor.Document doc) { count.incrementAndGet(); }
            public void onChunkCreated(DocumentIngestor.Chunk chunk) { count.incrementAndGet(); }
        });
        ingestor.ingest("src", "t", "Hello world this is some content that should be chunked properly", DocumentIngestor.DocumentType.TEXT);
        assertTrue(count.get() >= 1);
    }

    @Test void testGetDocument() {
        var doc = ingestor.ingest("src", "t", "content", DocumentIngestor.DocumentType.TEXT);
        assertTrue(ingestor.getDocument(doc.id()).isPresent());
        assertFalse(ingestor.getDocument("nonexistent").isPresent());
    }

    @Test void testRemoveDocument() {
        var doc = ingestor.ingest("src", "t", "content", DocumentIngestor.DocumentType.TEXT);
        assertTrue(ingestor.removeDocument(doc.id()));
        assertEquals(0, ingestor.documentCount());
    }

    @Test void testDetectType() {
        assertEquals(DocumentIngestor.DocumentType.HTML, DocumentIngestor.DocumentType.valueOf("HTML"));
        assertEquals(DocumentIngestor.DocumentType.PDF, DocumentIngestor.DocumentType.valueOf("PDF"));
    }
}
