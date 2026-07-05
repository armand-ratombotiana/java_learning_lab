package com.ds08;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Trie Demo ===");
        Trie trie = new Trie();

        trie.insert("apple");
        trie.insert("app");
        trie.insert("application");
        trie.insert("apt");
        trie.insert("bat");
        trie.insert("batch");
        trie.insert("bath");
        trie.insert("batman");

        System.out.println("All words: " + trie.getAllWords());
        System.out.println("Total words: " + trie.countWords());
        System.out.println("Search 'apple': " + trie.search("apple"));
        System.out.println("Search 'app': " + trie.search("app"));
        System.out.println("Search 'appl': " + trie.search("appl"));
        System.out.println("Starts with 'ap': " + trie.startsWith("ap"));
        System.out.println("Starts with 'xyz': " + trie.startsWith("xyz"));

        System.out.println("\nAutocomplete for 'ap': " + trie.autocomplete("ap"));
        System.out.println("Autocomplete for 'bat': " + trie.autocomplete("bat"));
        System.out.println("Autocomplete for 'x': " + trie.autocomplete("x"));

        System.out.println("\nDelete 'app': " + trie.delete("app"));
        System.out.println("After delete, search 'app': " + trie.search("app"));
        System.out.println("After delete, search 'apple': " + trie.search("apple"));
        System.out.println("All words now: " + trie.getAllWords());
        System.out.println("Total words: " + trie.countWords());

        System.out.println("\n=== Dictionary Demo ===");
        Trie dictionary = new Trie();
        String[] words = {"cat", "car", "card", "care", "caret", "dog", "doge", "done", "dot"};
        for (String w : words) dictionary.insert(w);
        System.out.println("Dictionary words: " + dictionary.getAllWords());
        System.out.println("Autocomplete 'ca': " + dictionary.autocomplete("ca"));
        System.out.println("Autocomplete 'car': " + dictionary.autocomplete("car"));
        System.out.println("Autocomplete 'do': " + dictionary.autocomplete("do"));

        System.out.println("\n=== Trie Clear Demo ===");
        Trie t = new Trie();
        t.insert("temp");
        System.out.println("Before clear, isEmpty: " + t.isEmpty());
        t.clear();
        System.out.println("After clear, isEmpty: " + t.isEmpty());
    }
}
