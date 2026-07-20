package com.capstone.rag;

import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RAGEvaluatorTest {
    private RAGEvaluator evaluator;
    private Retriever retriever;

    @BeforeEach
    void setUp() {
        var embed = new EmbeddingInterface.MockEmbedding(8);
        var store = new VectorStore(embed);
        store.addText("1", "Paris is the capital of France", Map.of());
        store.addText("2", "London is the capital of the United Kingdom", Map.of());
        evaluator = new RAGEvaluator();
        evaluator.addSample(new RAGEvaluator.EvaluationSample(
            "What is the capital of France?", List.of("1"), "Paris"));
        var reranker = new Reranker(embed);
        var contextBuilder = new ContextBuilder();
        retriever = new Retriever(store, reranker, contextBuilder);
    }

    @Test void testEvaluate() {
        var results = retriever.hybridRetrieve("capital of France");
        var eval = evaluator.evaluate(retriever, results);
        assertNotNull(eval);
        assertTrue(eval.recall() >= 0);
        assertTrue(eval.precision() >= 0);
    }

    @Test void testAggregateResults() {
        evaluator.evaluateAll(retriever);
        var agg = evaluator.getAggregateResults();
        assertFalse(agg.isEmpty());
    }

    @Test void testDetailedResults() {
        var results = retriever.hybridRetrieve("capital of France");
        evaluator.evaluate(retriever, results);
        assertEquals(1, evaluator.getDetailedResults().size());
    }

    @Test void testClear() {
        var results = retriever.hybridRetrieve("capital of France");
        evaluator.evaluate(retriever, results);
        evaluator.clear();
        assertTrue(evaluator.getDetailedResults().isEmpty());
    }
}
