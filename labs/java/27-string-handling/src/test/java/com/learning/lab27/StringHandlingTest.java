package com.learning.lab27;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class StringHandlingTest {

    @Test
    @DisplayName("String intern returns canonical representation")
    void stringIntern() {
        String s1 = new String("hello");
        String s2 = s1.intern();
        String s3 = "hello";
        assertSame(s2, s3);
    }

    @Test
    @DisplayName("String pool shares literals")
    void stringPoolSharing() {
        String a = "hello";
        String b = "hello";
        assertSame(a, b);
    }

    @Test
    @DisplayName("StringBuilder builds strings efficiently")
    void stringBuilderBuilds() {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello");
        sb.append(" ");
        sb.append("World");
        assertEquals("Hello World", sb.toString());
    }

    @Test
    @DisplayName("StringBuilder insert at position")
    void stringBuilderInsert() {
        StringBuilder sb = new StringBuilder("HelloWorld");
        sb.insert(5, " ");
        assertEquals("Hello World", sb.toString());
    }

    @Test
    @DisplayName("StringBuilder reverse")
    void stringBuilderReverse() {
        StringBuilder sb = new StringBuilder("abc");
        assertEquals("cba", sb.reverse().toString());
    }

    @Test
    @DisplayName("StringBuffer is thread-safe")
    void stringBufferThreadSafe() {
        StringBuffer sb = new StringBuffer();
        sb.append("thread");
        sb.append("safe");
        assertEquals("threadsafe", sb.toString());
    }

    @Test
    @DisplayName("Text block creates multi-line string")
    void textBlock() {
        String json = """
            {
                "name": "test",
                "value": 42
            }
            """;
        assertTrue(json.contains("\"name\": \"test\""));
        assertTrue(json.contains("\"value\": 42"));
    }

    @Test
    @DisplayName("Text block preserves indentation")
    void textBlockIndentation() {
        String block = """
                line1
                    line2
                line3
                """;
        String[] lines = block.split("\n");
        assertEquals(4, lines.length);
    }

    @Test
    @DisplayName("String transform applies function")
    void stringTransform() {
        String result = "hello".transform(s -> s.toUpperCase());
        assertEquals("HELLO", result);
    }

    @Test
    @DisplayName("String translateEscapes")
    void stringTranslateEscapes() {
        String escaped = "line1\\nline2";
        String translated = escaped.translateEscapes();
        assertTrue(translated.contains("\n"));
    }

    @Test
    @DisplayName("String strip vs trim")
    void stringStripVsTrim() {
        assertEquals("hello", "  hello  ".strip());
        assertTrue("  hello  ".stripIndent().length() > 0);
    }

    @Test
    @DisplayName("String repeat repeats string")
    void stringRepeat() {
        assertEquals("AAA", "A".repeat(3));
    }

    @Test
    @DisplayName("String isBlank checks whitespace only")
    void stringIsBlank() {
        assertTrue("   ".isBlank());
        assertFalse("  a  ".isBlank());
    }

    @Test
    @DisplayName("String lines splits into stream")
    void stringLines() {
        String multi = "a\nb\nc";
        var lines = multi.lines().toList();
        assertEquals(3, lines.size());
        assertEquals("a", lines.get(0));
    }
}
