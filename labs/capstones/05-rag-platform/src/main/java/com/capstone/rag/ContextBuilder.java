package com.capstone.rag;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ContextBuilder {
    private int maxContextLength = 4096;
    private String template = "Context information:\n{{context}}\n\nQuestion: {{query}}";
    private boolean includeScores = false;

    public ContextBuilder() {}

    public ContextBuilder(int maxContextLength, String template) {
        this.maxContextLength = maxContextLength;
        this.template = template;
    }

    public String buildContext(String query, List<String> relevantTexts, List<Double> scores) {
        StringBuilder context = new StringBuilder();
        List<Integer> indices = IntStream.range(0, relevantTexts.size())
            .boxed()
            .sorted((a, b) -> scores != null && scores.size() > b ?
                Double.compare(scores.get(b), scores.get(a)) : 0)
            .collect(Collectors.toList());

        for (int i : indices) {
            String text = relevantTexts.get(i);
            String prefix = includeScores && scores != null && i < scores.size()
                ? String.format("[Score: %.4f] ", scores.get(i))
                : "";
            String snippet = prefix + text.trim() + "\n\n";
            if (context.length() + snippet.length() > maxContextLength) break;
            context.append(snippet);
        }

        return template.replace("{{context}}", context.toString().trim()).replace("{{query}}", query);
    }

    public String buildContext(String query, List<String> relevantTexts) {
        return buildContext(query, relevantTexts, null);
    }

    public String buildPrompt(String systemPrompt, String context, String query) {
        return String.format("%s\n\n%s\n\nUser: %s\nAssistant:", systemPrompt, context, query);
    }

    public void setMaxContextLength(int maxContextLength) { this.maxContextLength = maxContextLength; }
    public void setTemplate(String template) { this.template = template; }
    public void setIncludeScores(boolean includeScores) { this.includeScores = includeScores; }
    public int getMaxContextLength() { return maxContextLength; }
}
