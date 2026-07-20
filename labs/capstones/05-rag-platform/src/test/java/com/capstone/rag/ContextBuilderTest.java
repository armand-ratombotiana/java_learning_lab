package com.capstone.rag;

import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ContextBuilderTest {
    private ContextBuilder builder;

    @BeforeEach
    void setUp() { builder = new ContextBuilder(); }

    @Test void testBuildContext() {
        String context = builder.buildContext("What is Java?",
            List.of("Java is a programming language.", "Java runs on the JVM."));
        assertTrue(context.contains("Java is a programming language"));
        assertTrue(context.contains("What is Java?"));
    }

    @Test void testBuildContextWithScores() {
        builder.setIncludeScores(true);
        String context = builder.buildContext("test",
            List.of("text one", "text two"), List.of(0.9, 0.5));
        assertTrue(context.contains("Score:"));
    }

    @Test void testBuildPrompt() {
        String prompt = builder.buildPrompt("You are a helpful assistant.",
            "Some context here.", "What is X?");
        assertTrue(prompt.contains("You are a helpful assistant"));
        assertTrue(prompt.contains("Some context here"));
        assertTrue(prompt.contains("What is X?"));
    }

    @Test void testMaxLength() {
        builder.setMaxContextLength(50);
        String context = builder.buildContext("Q",
            List.of("This is a very long piece of text that should be truncated by the max context length limit."));
        assertTrue(context.length() < 200);
    }

    @Test void testCustomTemplate() {
        builder.setTemplate("{{context}}\n\nQ: {{query}}");
        String context = builder.buildContext("hello", List.of("world"));
        assertTrue(context.contains("Q: hello"));
    }
}
