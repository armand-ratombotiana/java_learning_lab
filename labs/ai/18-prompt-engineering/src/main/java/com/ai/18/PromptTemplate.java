package com.ai18;

import java.util.*;

public class PromptTemplate {
    private String template;
    private List<String> variables;

    public PromptTemplate(String template) {
        this.template = template;
        this.variables = new ArrayList<>();
        int start = 0;
        while (true) {
            int open = template.indexOf("{{", start);
            if (open < 0) break;
            int close = template.indexOf("}}", open);
            if (close < 0) break;
            variables.add(template.substring(open + 2, close).trim());
            start = close + 2;
        }
    }

    public String fill(Map<String, String> values) {
        String result = template;
        for (Map.Entry<String, String> e : values.entrySet())
            result = result.replace("{{" + e.getKey() + "}}", e.getValue());
        return result;
    }

    public List<String> getVariables() { return variables; }

    public String chainOfThought(String question, String reasoning, String answer) {
        return "Question: " + question + "\nReasoning: " + reasoning + "\nAnswer: " + answer;
    }

    public String fewShotPrompt(List<Map.Entry<String, String>> examples, String query) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < examples.size(); i++) {
            sb.append("Example ").append(i + 1).append(":\n");
            sb.append("Input: ").append(examples.get(i).getKey()).append("\n");
            sb.append("Output: ").append(examples.get(i).getValue()).append("\n\n");
        }
        sb.append("Input: ").append(query).append("\nOutput:");
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println("=== Prompt Template Demo ===");
        PromptTemplate pt = new PromptTemplate("Translate the following {{language}} text: {{text}}");
        System.out.println("Template: " + pt.template);
        System.out.println("Variables: " + pt.getVariables());
        Map<String, String> values = new HashMap<>();
        values.put("language", "French");
        values.put("text", "Hello, how are you?");
        System.out.println("Filled: " + pt.fill(values));
        String cot = pt.chainOfThought("What is 2+2?", "We add 2 and 2 together", "4");
        System.out.println("\nChain of thought:\n" + cot);
        List<Map.Entry<String, String>> examples = List.of(
            Map.entry("The cat sat on the mat", "Le chat s'est assis sur le tapis"),
            Map.entry("The dog runs fast", "Le chien court vite")
        );
        String fewShot = pt.fewShotPrompt(examples, "I love programming");
        System.out.println("\nFew-shot prompt:\n" + fewShot);
    }
}
