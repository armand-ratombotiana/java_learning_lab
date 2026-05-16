# Prompt Engineering - EXERCISES

## Exercise 1: Build Few-Shot Prompt
Create a prompt to classify sentiment with 3 examples.

```java
public String buildSentimentPrompt() {
    FewShotPromptBuilder builder = new FewShotPromptBuilder();
    
    builder.setSystemPrompt("You are a sentiment classifier.");
    builder.setTaskDescription("Classify the sentiment as positive, negative, or neutral.");
    
    builder.addExample("This product is amazing!", "positive");
    builder.addExample("Terrible experience, would not recommend.", "negative");
    builder.addExample("The product arrived on time.", "neutral");
    
    return builder.build("I love the new features!");
    // Output: Complete prompt with examples
}
```

## Exercise 2: Chain-of-Thought for Math
Build a CoT prompt for solving algebra problems.

```java
public String mathCoT() {
    String problem = "If 3x + 7 = 22, what is x?";
    
    return CoTPromptBuilder.buildMathPrompt(problem);
    // Output: Step-by-step prompt for math
}
```

## Exercise 3: Format Output with Template
Create JSON extraction prompt using template.

```java
public String extractPrompt() {
    return PromptTemplate.extraction()
        .withVariable("entity_types", "name, email, phone")
        .withVariable("input_text", "Contact John at john@example.com or 555-1234")
        .render();
    // Output: Formatted extraction prompt
}
```

## Exercise 4: Build Role Prompt
Create prompt with "expert" role for code review.

```java
public String codeReviewRole() {
    RolePromptBuilder builder = new RolePromptBuilder();
    
    return builder.buildRolePrompt("expert", 
        "Review this code for bugs and improvements:\n\n" +
        "public void process(List items) {\n" +
        "  for (int i = 0; i <= items.size(); i++) {\n" +
        "    System.out.println(items.get(i));\n" +
        "  }\n" +
        "}");
}
```

## Exercise 5: Self-Consistency with Multiple Paths
Build prompt that solves problem 3 ways.

```java
public String multiPath() {
    String problem = "What is 25 * 48?";
    
    return SelfConsistencyPrompt.buildMultiPathPrompt(problem, 3);
}
```

## Exercise 6: Evaluate Prompt Quality
Analyze prompt for quality metrics.

```java
public void evaluatePrompt() {
    String prompt = "You are a helpful assistant. " +
                    "Answer the following questions. " +
                    "For example: Input: 2+2 Output: 4";
    
    PromptEvaluator evaluator = new PromptEvaluator();
    PromptMetrics metrics = evaluator.analyzePrompt(prompt);
    
    System.out.println("Length: " + metrics.length);
    System.out.println("Has examples: " + metrics.hasExamples);
    System.out.println("Has instructions: " + metrics.hasInstructions);
}
```

---

## Solutions

### Exercise 1:
```java
// Complete few-shot prompt with system message, task, and 3 examples
```

### Exercise 2:
```java
// CoT prompt includes: given, formula, calculation, final answer
```

### Exercise 3:
```java
// Template renders with variables substituted
```

### Exercise 4:
```java
// Expert role adds detailed persona and guidelines
```

### Exercise 5:
```java
// Multiple approaches prompt enables self-consistency checking
```

### Exercise 6:
```java
// Metrics show prompt has examples and instructions
```