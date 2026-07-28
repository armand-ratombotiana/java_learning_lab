package com.java.module.foreign;

import java.util.*;
import java.util.stream.*;

/**
 * Lab 01: Java Module System — module structure, exports, requires,
 * services, migration patterns.
 *
 * Note: This file is a conceptual demo. In a real module system,
 * module-info.java would be at the source root.
 */
public class ModuleSystemLab {

    // --- Simulated service SPI ---
    public interface SpellChecker {
        List<String> suggest(String word);
    }

    public static class EnglishSpellChecker implements SpellChecker {
        private static final Set<String> DICT = Set.of("hello", "world", "java");
        @Override
        public List<String> suggest(String word) {
            return DICT.stream()
                    .filter(w -> w.startsWith(word.substring(0, 1)))
                    .toList();
        }
    }

    public static class FrenchSpellChecker implements SpellChecker {
        private static final Set<String> DICT = Set.of("bonjour", "monde", "java");
        @Override
        public List<String> suggest(String word) {
            return DICT.stream()
                    .filter(w -> w.startsWith(word.substring(0, 1)))
                    .toList();
        }
    }

    // --- ServiceLoader-style discovery (no actual module path) ---
    private final List<SpellChecker> checkers;

    public ModuleSystemLab() {
        this.checkers = List.of(new EnglishSpellChecker(), new FrenchSpellChecker());
    }

    public List<String> checkAll(String word) {
        return checkers.stream()
                .flatMap(c -> c.suggest(word).stream())
                .distinct()
                .toList();
    }

    // --- Simulated qualified export behavior ---
    public static class InternalHelper {
        public static String sanitize(String input) {
            return input.toLowerCase().trim();
        }
    }

    // --- Demo ---
    public static void main(String[] args) {
        var lab = new ModuleSystemLab();
        var suggestions = lab.checkAll("H");
        System.out.println("Suggestions for 'H': " + suggestions);

        // Using internal helper
        System.out.println("Sanitized: " + InternalHelper.sanitize("  Hello World  "));
    }
}
