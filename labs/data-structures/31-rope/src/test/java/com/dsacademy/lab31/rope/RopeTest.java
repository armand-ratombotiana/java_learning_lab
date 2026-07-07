package com.dsacademy.lab31.rope;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class RopeTest {

    private Rope rope;

    @BeforeEach
    void setUp() {
        rope = new Rope();
    }

    @Test
    void testEmptyRope() {
        assertEquals(0, rope.length());
        assertEquals("", rope.toString());
    }

    @Test
    void testInitialRope() {
        Rope r = new Rope("hello");
        assertEquals(5, r.length());
        assertEquals("hello", r.toString());
    }

    @Test
    void testCharAt() {
        Rope r = new Rope("hello");
        assertEquals('h', r.charAt(0));
        assertEquals('e', r.charAt(1));
        assertEquals('l', r.charAt(2));
        assertEquals('l', r.charAt(3));
        assertEquals('o', r.charAt(4));
    }

    @Test
    void testCharAtOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> rope.charAt(0));
        Rope r = new Rope("hi");
        assertThrows(IndexOutOfBoundsException.class, () -> r.charAt(5));
    }

    @Test
    void testConcat() {
        Rope a = new Rope("Hello, ");
        Rope b = new Rope("World!");
        Rope c = a.concat(b);
        assertEquals(13, c.length());
        assertEquals("Hello, World!", c.toString());
    }

    @Test
    void testConcatWithEmpty() {
        Rope empty = new Rope();
        Rope a = new Rope("test");
        assertEquals("test", empty.concat(a).toString());
        assertEquals("test", a.concat(empty).toString());
    }

    @Test
    void testSplit() {
        Rope r = new Rope("HelloWorld");
        Rope left = r.split(5);
        assertEquals("Hello", left.toString());
        assertEquals("World", r.toString());
    }

    @Test
    void testSplitAtStart() {
        Rope r = new Rope("test");
        Rope left = r.split(0);
        assertEquals("", left.toString());
        assertEquals("test", r.toString());
    }

    @Test
    void testSplitAtEnd() {
        Rope r = new Rope("test");
        Rope left = r.split(4);
        assertEquals("test", left.toString());
        assertEquals("", r.toString());
    }

    @Test
    void testInsert() {
        Rope r = new Rope("Helorld");
        r.insert(3, "loW");
        assertEquals("HelloWorld", r.toString());
    }

    @Test
    void testInsertAtBeginning() {
        Rope r = new Rope("world");
        r.insert(0, "hello ");
        assertEquals("hello world", r.toString());
    }

    @Test
    void testInsertAtEnd() {
        Rope r = new Rope("hello");
        r.insert(5, " world");
        assertEquals("hello world", r.toString());
    }

    @Test
    void testDelete() {
        Rope r = new Rope("Hello XWorld!");
        r.delete(6, 8);
        assertEquals("Hello World!", r.toString());
    }

    @Test
    void testDeleteEntire() {
        Rope r = new Rope("test");
        r.delete(0, 4);
        assertEquals("", r.toString());
    }

    @Test
    void testSubstring() {
        Rope r = new Rope("Hello World");
        assertEquals("Hello", r.substring(0, 5));
        assertEquals("World", r.substring(6, 11));
        assertEquals(" ", r.substring(5, 6));
    }

    @Test
    void testSubstringFull() {
        Rope r = new Rope("test");
        assertEquals("test", r.substring(0, 4));
    }

    @Test
    void testSubstringEmpty() {
        Rope r = new Rope("test");
        assertEquals("", r.substring(0, 0));
    }

    @Test
    void testIndexOf() {
        Rope r = new Rope("Hello World");
        assertEquals(0, r.indexOf("Hello"));
        assertEquals(6, r.indexOf("World"));
        assertEquals(-1, r.indexOf("xyz"));
    }

    @Test
    void testLargeRope() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append((char) ('a' + (i % 26)));
        }
        String expected = sb.toString();
        Rope r = new Rope(expected);
        assertEquals(expected, r.toString());
        assertEquals(expected.length(), r.length());
        assertEquals('a', r.charAt(0));
        assertEquals(expected.charAt(5000), r.charAt(5000));
    }

    @Test
    void testRopeEditor() {
        RopeEditor editor = new RopeEditor();
        assertEquals(0, editor.length());
        editor.insert("Hello");
        assertEquals(5, editor.length());
        assertEquals("Hello", editor.getText());
        editor.insert(" World");
        assertEquals("Hello World", editor.getText());
        editor.moveCursor(5);
        editor.delete(5);
        assertEquals("Hello", editor.getText());
    }

    @Test
    void testMultipleConcats() {
        Rope r = new Rope("a");
        for (int i = 1; i < 100; i++) {
            r = r.concat(new Rope(String.valueOf((char) ('a' + i % 26))));
        }
        assertEquals(100, r.length());
    }

    @Test
    void testBalanceFactorOnLeaf() {
        Rope r = new Rope("test");
        assertEquals(0, r.balanceFactor());
    }
}
