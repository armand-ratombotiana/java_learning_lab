package com.capstone.agent;

import org.junit.jupiter.api.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ToolRegistryTest {
    private ToolRegistry registry;

    @BeforeEach
    void setUp() { registry = ToolRegistry.createDefaultTools(); }

    @Test void testDefaultTools() {
        assertEquals(4, registry.toolCount());
        assertTrue(registry.getToolNames().contains("search"));
        assertTrue(registry.getToolNames().contains("calculate"));
    }

    @Test void testExecuteSearch() {
        var result = registry.execute("search", "query=Java");
        assertEquals("Search results for: Java", result);
    }

    @Test void testExecuteCalculate() {
        var result = registry.execute("calculate", "expression=2+2");
        assertEquals("Result: 2+2", result);
    }

    @Test void testUnknownTool() {
        assertThrows(IllegalArgumentException.class, () -> registry.execute("nonexistent", ""));
    }

    @Test void testRegisterCustomTool() {
        registry.registerTool("greet", "Greets a person", List.of("name"),
            params -> "Hello, " + params.getOrDefault("name", "world") + "!");
        assertEquals(5, registry.toolCount());
        assertEquals("Hello, Alice!", registry.execute("greet", "name=Alice"));
    }

    @Test void testGetTool() {
        var tool = registry.getTool("search");
        assertTrue(tool.isPresent());
        assertEquals("search", tool.get().name());
    }
}
