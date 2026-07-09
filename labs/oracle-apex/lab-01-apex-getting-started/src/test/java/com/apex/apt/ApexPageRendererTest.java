package com.apex.apt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class ApexPageRendererTest {
    private ApexPageRenderer renderer;
    @BeforeEach void setUp() { renderer = ApexPageRenderer.createSamplePage(); }

    @Test void shouldRenderPage() {
        var result = renderer.renderPage(1, Map.of());
        assertTrue(result.html().contains("Dashboard"));
        assertTrue(result.html().contains("Employees"));
        assertTrue(result.html().contains("Sales Chart"));
        assertTrue(result.pageId() == 1);
    }

    @Test void shouldThrowForMissingPage() {
        assertThrows(IllegalArgumentException.class, () -> renderer.renderPage(999, Map.of()));
    }

    @Test void shouldLogRendering() {
        renderer.renderPage(1, Map.of());
        assertFalse(renderer.getRenderLog().isEmpty());
    }

    @Test void shouldCreateComponents() {
        var page = renderer.createPage(10, "Test", "42");
        assertEquals(10, page.id());
        assertEquals("Test", page.name());
    }
}