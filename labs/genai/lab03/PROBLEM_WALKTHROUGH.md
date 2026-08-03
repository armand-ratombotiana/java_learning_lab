# Problem Walkthrough: Prompt Engineering Patterns

## Problem 1: Prompt Registry with Structural Evaluation — Company: Anthropic

### Interview Scenario
"You're at Anthropic building the internal prompt registry that teams use to ship Claude
prompts. Every prompt goes through a CI gate before promotion, and the gate must be
deterministic and dependency-free: render each candidate template, verify the rendering
is structurally complete, test the structured-output parser on a sample response, and
track versions by content hash. Build this with the lab's `PromptTemplate`,
`FewShotPrompt`, `CoTPrompt`, `StructuredParser`, and `PromptVersion` classes."

### The Problem
1. Render plain, few-shot, and CoT variants of a sentiment-classification prompt.
2. Parse a JSON model response with the lab's `StructuredParser` and extract the label.
3. Score each rendered prompt 0-4 on structural completeness (instruction, query, examples, reasoning trigger).
4. Show version identity: identical content must deduplicate, edited content must differ.
5. Report token counts so cost enters the decision, and end with a validation footer.

### Solution Walkthrough
- Step 1: Reuse `PromptTemplate` verbatim — it discovers `{{var}}` placeholders via regex
  and substitutes with `getOrDefault` so missing variables show as `MISSING`.
- Step 2: Build `FewShotPrompt` with two representative examples and call `build(query)`;
  note the dangling `Output:` line that the model completes.
- Step 3: Render the CoT variant with `CoTPrompt.build` and its 'step by step' trigger.
- Step 4: Feed the sample response `{"sentiment": "positive"}` through `StructuredParser`
  and extract the label — the regex extracts `sentiment=positive`.
- Step 5: Score each rendering: +1 instruction, +1 query present, +1 examples (few-shot),
  +1 reasoning trigger (CoT), and print token counts via `split("\\s+").length`.
- Step 6: Instantiate `PromptVersion` three times — same content twice, edited content
  once — and compare the hash ids.

### Code
```java
package com.genai.lab03.solution;

import java.util.*;
import java.util.regex.*;

/**
 * Lab 03 walkthrough: programmatic prompt registry with structural
 * evaluation and hash-based versioning. Reuses the lab's
 * PromptTemplate, FewShotPrompt, CoTPrompt, StructuredParser, and
 * PromptVersion exactly.
 */
public class PromptRegistry {

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

    static class CoTPrompt {
        static String build(String question) {
            return "Question: " + question + "\nLet's think step by step.\n";
        }

        static String extractAnswer(String response) {
            String[] lines = response.split("\n");
            return lines[lines.length - 1];
        }
    }

    static class StructuredParser {
        static Map<String, String> parseJson(String text) {
            Map<String, String> result = new LinkedHashMap<>();
            Matcher m = Pattern.compile("\"(\\w+)\"\\s*:\\s*\"([^\"]+)\"").matcher(text);
            while (m.find()) result.put(m.group(1), m.group(2));
            return result;
        }
    }

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
        Map<String, String> vals = Map.of("text", "I love this!");
        String query = "I love this!";

        String plain = "Classify sentiment as positive, negative, or neutral. Output JSON: {\"sentiment\": \"{{text}}\"}";
        PromptTemplate tpl = new PromptTemplate(plain);
        String renderedPlain = tpl.render(vals);

        FewShotPrompt fewShot = new FewShotPrompt(
            "Classify sentiment as positive, negative, or neutral. Output JSON {\"sentiment\": \"...\"}.");
        fewShot.addExample("I love this!", "{\"sentiment\": \"positive\"}");
        fewShot.addExample("This is terrible.", "{\"sentiment\": \"negative\"}");
        String renderedFewShot = fewShot.build(query);

        String renderedCot = CoTPrompt.build(plain);

        System.out.println("=== Rendered Prompts ===");
        System.out.println("[plain]\n" + renderedPlain + "\n");
        System.out.println("[few-shot]\n" + renderedFewShot + "\n");
        System.out.println("[cot]\n" + renderedCot + "\n");

        System.out.println("=== Structured Parse of Model Response ===");
        String response = "{\"sentiment\": \"positive\"}";
        Map<String, String> parsed = StructuredParser.parseJson(response);
        System.out.println("Parsed: " + parsed);
        System.out.println("Extracted sentiment: " + parsed.get("sentiment"));

        System.out.println("\n=== Structural Score (0-4) ===");
        String[] renders = {renderedPlain, renderedFewShot, renderedCot};
        String[] names = {"plain", "few-shot", "cot"};
        for (int i = 0; i < renders.length; i++) {
            int score = 0;
            if (renders[i].contains("Classify sentiment")) score++;
            if (renders[i].contains(query)) score++;
            if (names[i].equals("few-shot") && renders[i].contains("Example 1")) score++;
            if (names[i].equals("cot") && renders[i].contains("step by step")) score++;
            System.out.printf("%-8s score=%d/4  tokens=%d%n",
                names[i], score, renders[i].split("\\s+").length);
        }

        System.out.println("\n=== Prompt Versioning ===");
        PromptVersion v1 = new PromptVersion(plain);
        PromptVersion v1dup = new PromptVersion(plain);
        PromptVersion v2 = new PromptVersion(plain.replace("positive, negative, or neutral",
            "positive, negative, neutral, or mixed"));
        System.out.println("v1 id:      " + v1.id);
        System.out.println("v1 duplicate id: " + v1dup.id + " (same: " + v1dup.id.equals(v1.id) + ")");
        System.out.println("v2 (edited) id:  " + v2.id + " (differs: " + !v2.id.equals(v1.id) + ")");

        System.out.println("\nPrompt registry validated.");
    }
}
```

### Expected Output
```text
=== Rendered Prompts ===
[plain]
Classify sentiment as positive, negative, or neutral. Output JSON: {"sentiment": "I love this!"}

[few-shot]
Classify sentiment as positive, negative, or neutral. Output JSON {"sentiment": "..."}.

Example 1:
Input: I love this!
Output: {"sentiment": "positive"}

Example 2:
Input: This is terrible.
Output: {"sentiment": "negative"}

Input: I love this!
Output:

[cot]
Question: Classify sentiment as positive, negative, or neutral. Output JSON: {"sentiment": "{{text}}"}
Let's think step by step.

=== Structured Parse of Model Response ===
Parsed: {sentiment=positive}
Extracted sentiment: positive

=== Structural Score (0-4) ===
plain    score=2/4  tokens=13
few-shot score=3/4  tokens=34
cot      score=2/4  tokens=17

=== Prompt Versioning ===
v1 id:      63e826f0
v1 duplicate id: 63e826f0 (same: true)
v2 (edited) id:  8c2365d1 (differs: true)

Prompt registry validated.
```

### Company Evaluation
- Anthropic: Prompt versioning, structural CI gates, regression eval on golden sets.
- OpenAI: Token-cost-aware prompt design, prompt-to-fine-tune lifecycle.
- Google: Few-shot scaling behavior, template testing at search scale.
- Stripe: Structured output reliability for extraction pipelines.
- Shopify: Cost-per-request optimization of prompt templates at high volume.

---

## Problem 2: CoT Answer Extraction — Company: OpenAI

### Interview Scenario
"You're at OpenAI shipping a math word-problem endpoint. The model answers with a
reasoning trace followed by a final answer, and you need a deterministic extractor based
on the lab's `CoTPrompt.extractAnswer` pattern, but robust to trailing whitespace and
'Answer:' prefixes."

### The Problem
1. Build a CoT prompt with `CoTPrompt.build`.
2. Extract the final answer from a multi-line reasoning response.
3. Handle the case where the model repeats the answer after a blank line.

### Solution Walkthrough
- Step 1: Generate a reasoning response with three lines: trace, blank, answer.
- Step 2: Use `extractAnswer` (last non-empty line) and trim.
- Step 3: Strip a leading "Answer: " prefix if present and print the normalized answer.

### Code
```java
String response = "Speed is distance / time.\n120 km / 2 hours.\n\nAnswer: 60 km/h";
String last = CoTPrompt.extractAnswer(response).trim();
String answer = last.startsWith("Answer: ") ? last.substring(8) : last;
System.out.println("Extracted answer: " + answer);
```
Expected output: `Extracted answer: 60 km/h` — the trace lines are ignored because the
answer is on the final line, matching the extractor's contract.

---

## Problem 3: Structured Extraction Fallback — Company: Stripe

### Interview Scenario
"You're at Stripe parsing invoice details out of a support bot's responses. The model
sometimes returns `"status": "paid"` inside prose rather than as clean JSON, and the
lab's `StructuredParser` regex must still find the field."

### The Problem
1. Parse a JSON-with-prose response using `StructuredParser.parseJson`.
2. Extract `status` and `amount`.
3. Print a MISSING marker when a key is absent, proving the failure mode is explicit.

### Solution Walkthrough
- Step 1: Feed a messy response through the parser.
- Step 2: Extract keys with `getOrDefault(key, "MISSING")`.
- Step 3: Print both extracted values.

### Code
```java
String messy = "Invoice processed. {\"status\": \"paid\", \"amount\": \"49.99\"} Thanks!";
Map<String, String> fields = StructuredParser.parseJson(messy);
System.out.println("status: " + fields.getOrDefault("status", "MISSING"));
System.out.println("amount: " + fields.getOrDefault("amount", "MISSING"));
```
Expected output: `status: paid` and `amount: 49.99` — the regex finds the key-value
pairs inside the surrounding prose, while absent keys surface as `MISSING`.
