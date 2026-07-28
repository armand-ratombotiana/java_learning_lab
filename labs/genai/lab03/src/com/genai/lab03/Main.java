package com.genai.lab03;

import java.util.*;
import java.util.regex.*;

/**
 * Prompt Engineering Patterns
 * 
 * Demonstrates prompt templates, few-shot learning, chain-of-thought,
 * structured output parsing, and prompt versioning in Java.
 */
public class Main {

    /** Prompt template with variable substitution. */
    static class PromptTemplate {
        final String template;
        final List<String> variables;

        PromptTemplate(String template) {
            this.template = template;
            this.variables = new ArrayList<>();
            Matcher m = Pattern.compile("\\{\\{(\\w+)}}").matcher(template);
            while (m.find()) variables.add(m.group(1));
        }

        String render(Map<String, String> values) {
            String result = template;
            for (String var : variables) {
                result = result.replace("{{" + var + "}}",
                    values.getOrDefault(var, "MISSING"));
            }
            return result;
        }
    }

    /** Few-shot example container. */
    static class FewShotPrompt {
        final String instruction;
        final List<Map.Entry<String, String>> examples = new ArrayList<>();

        FewShotPrompt(String instruction) { this.instruction = instruction; }

        void addExample(String input, String output) {
            examples.add(Map.entry(input, output));
        }

        String build(String query) {
            StringBuilder sb = new StringBuilder(instruction).append("\n\n");
            for (int i = 0; i < examples.size(); i++) {
                sb.append("Example ").append(i + 1).append(":\n");
                sb.append("Input: ").append(examples.get(i).getKey()).append("\n");
                sb.append("Output: ").append(examples.get(i).getValue()).append("\n\n");
            }
            sb.append("Input: ").append(query).append("\nOutput:");
            return sb.toString();
        }
    }

    /** Chain-of-thought prompt builder. */
    static class CoTPrompt {
        static String build(String question) {
            return "Question: " + question + "\nLet's think step by step.\n";
        }

        static String extractAnswer(String response) {
            String[] lines = response.split("\n");
            return lines[lines.length - 1];
        }
    }

    /** Structured output parser for JSON-like responses. */
    static class StructuredParser {
        static Map<String, String> parseJson(String text) {
            Map<String, String> result = new LinkedHashMap<>();
            Matcher m = Pattern.compile("\"(\\w+)\"\\s*:\\s*\"([^\"]+)\"").matcher(text);
            while (m.find()) result.put(m.group(1), m.group(2));
            return result;
        }
    }

    /** Prompt versioning with hash-based identity. */
    static class PromptVersion {
        final String id;
        final String content;
        final long createdAt;

        PromptVersion(String content) {
            this.content = content;
            this.id = Integer.toHexString(content.hashCode());
            this.createdAt = System.currentTimeMillis();
        }
    }

    public static void main(String[] args) {
        PromptTemplate tpl = new PromptTemplate(
            "Translate to {{language}}: {{text}}");
        Map<String, String> vals = Map.of("language", "French", "text", "Hello");
        System.out.println("=== Prompt Template ===");
        System.out.println(tpl.render(vals));

        FewShotPrompt fewShot = new FewShotPrompt("Classify sentiment as positive or negative.");
        fewShot.addExample("I love this!", "positive");
        fewShot.addExample("This is terrible.", "negative");
        System.out.println("\n=== Few-Shot Prompt ===");
        System.out.println(fewShot.build("It's okay I guess."));

        System.out.println("\n=== Chain-of-Thought ===");
        String cot = CoTPrompt.build("If a train travels 120 km in 2 hours, what is its speed?");
        System.out.println(cot);

        String jsonResponse = "{\"name\": \"Alice\", \"role\": \"engineer\"}";
        System.out.println("\n=== Structured Parse ===");
        System.out.println(StructuredParser.parseJson(jsonResponse));

        PromptVersion pv = new PromptVersion("Translate {{text}} to {{lang}}");
        System.out.println("\n=== Prompt Version ===");
        System.out.println("ID: " + pv.id + ", created: " + pv.createdAt);

        System.out.println("\nAll prompt engineering components validated.");
    }
}
