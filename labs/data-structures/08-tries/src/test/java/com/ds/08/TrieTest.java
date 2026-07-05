package com.ds08;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

public class TrieTest {

    private Trie trie;

    @BeforeEach
    void setUp() {
        trie = new Trie();
        trie.insert("hello");
        trie.insert("help");
        trie.insert("world");
        trie.insert("word");
        trie.insert("work");
        trie.insert("worker");
    }

    @Test
    void searchExactWords() {
        assertTrue(trie.search("hello"));
        assertTrue(trie.search("help"));
        assertTrue(trie.search("world"));
    }

    @Test
    void searchNonExistent() {
        assertFalse(trie.search("hell"));
        assertFalse(trie.search("he"));
        assertFalse(trie.search("working"));
        assertFalse(trie.search("xyz"));
    }

    @Test
    void startsWith() {
        assertTrue(trie.startsWith("hel"));
        assertTrue(trie.startsWith("wor"));
        assertTrue(trie.startsWith("work"));
        assertFalse(trie.startsWith("xyz"));
    }

    @Test
    void autocomplete() {
        List<String> results = trie.autocomplete("hel");
        assertTrue(results.contains("hello"));
        assertTrue(results.contains("help"));
        assertEquals(2, results.size());
    }

    @Test
    void autocompleteNoResults() {
        List<String> results = trie.autocomplete("xyz");
        assertTrue(results.isEmpty());
    }

    @Test
    void autocompleteFullWord() {
        List<String> results = trie.autocomplete("work");
        assertTrue(results.contains("work"));
        assertTrue(results.contains("worker"));
    }

    @Test
    void deleteWord() {
        assertTrue(trie.delete("help"));
        assertFalse(trie.search("help"));
        assertTrue(trie.search("hello"));
    }

    @Test
    void deleteNonExistent() {
        assertFalse(trie.delete("xyz"));
    }

    @Test
    void deleteAndReinsert() {
        trie.delete("world");
        assertFalse(trie.search("world"));
        trie.insert("world");
        assertTrue(trie.search("world"));
    }

    @Test
    void countWords() {
        assertEquals(6, trie.countWords());
        trie.insert("new");
        assertEquals(7, trie.countWords());
        trie.delete("hello");
        assertEquals(6, trie.countWords());
    }

    @Test
    void getAllWords() {
        List<String> words = trie.getAllWords();
        assertEquals(6, words.size());
        assertTrue(words.containsAll(List.of("hello", "help", "world", "word", "work", "worker")));
    }

    @Test
    void isEmpty() {
        assertFalse(trie.isEmpty());
        Trie emptyTrie = new Trie();
        assertTrue(emptyTrie.isEmpty());
    }

    @Test
    void clear() {
        trie.clear();
        assertTrue(trie.isEmpty());
        assertEquals(0, trie.countWords());
    }

    @Test
    void insertDuplicate() {
        trie.insert("hello");
        assertTrue(trie.search("hello"));
    }

    @Test
    void emptyString() {
        trie.insert("");
        assertTrue(trie.search(""));
        assertTrue(trie.startsWith(""));
    }

    @Test
    void autocompleteEmptyPrefix() {
        List<String> results = trie.autocomplete("");
        assertEquals(6, results.size());
    }
}
