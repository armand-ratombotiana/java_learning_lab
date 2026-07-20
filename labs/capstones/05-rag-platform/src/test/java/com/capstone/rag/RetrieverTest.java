package com.capstone.rag;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class RetrieverTest {
    private VectorStore store;
    private Retriever retriever;

    @BeforeEach
    void setUp() {
        var embed = new EmbeddingInterface.MockEmbedding(8);
        store = new VectorStore(embed);
        store.addText("1", "Machine learning is a subset of artificial intelligence", Map.of());
        store.addText("2", "Deep learning uses neural networks with many layers", Map.of());
        store.addText("3", "Python is a popular programming language for data science", Map.of());
        var reranker = new Reranker(embed);
        var contextBuilder = new ContextBuilder();
        retriever = new Retriever(store, reranker, contextBuilder);
    }

    @Test void testDenseRetrieve() {
        var results = retriever.denseRetrieve("machine learning");
        assertFalse(results.isEmpty());
    }

    @Test void testKeywordRetrieve() {
        var results = retriever.keywordRetrieve("learning");
        assertFalse(results.isEmpty());
    }

    @Test void testHybridRetrieve() {
        var results = retriever.hybridRetrieve("machine learning");
        assertFalse(results.isEmpty());
    }

    @Test void testRetrieveWithRerank() {
        var results = retriever.retrieveWithRerank("machine learning");
        assertFalse(results.isEmpty());
    }

    @Test void testRetrieveAndBuildContext() {
        var context = retriever.retrieveAndBuildContext("machine learning");
        assertNotNull(context);
        assertTrue(context.contains("Question:"));
    }
}
