# Prompt Engineering - CODE DEEP DIVE

## Java Implementations

### 1. Few-Shot Prompt Builder

```java
public class FewShotPromptBuilder {
    private List<Example> examples;
    private String systemPrompt;
    private String taskDescription;
    
    public FewShotPromptBuilder() {
        this.examples = new ArrayList<>();
    }
    
    public void setSystemPrompt(String prompt) {
        this.systemPrompt = prompt;
    }
    
    public void addExample(String input, String output) {
        examples.add(new Example(input, output));
    }
    
    public String build(String query) {
        StringBuilder prompt = new StringBuilder();
        
        if (systemPrompt != null) {
            prompt.append(systemPrompt).append("\n\n");
        }
        
        if (taskDescription != null) {
            prompt.append(taskDescription).append("\n\n");
        }
        
        for (Example ex : examples) {
            prompt.append("Input: ").append(ex.input).append("\n");
            prompt.append("Output: ").append(ex.output).append("\n\n");
        }
        
        prompt.append("Input: ").append(query).append("\n");
        prompt.append("Output:");
        
        return prompt.toString();
    }
    
    public void setTaskDescription(String desc) {
        this.taskDescription = desc;
    }
}

class Example {
    String input;
    String output;
    
    Example(String input, String output) {
        this.input = input;
        this.output = output;
    }
}
```

### 2. Chain-of-Thought Prompt

```java
public class CoTPromptBuilder {
    
    public String buildReasoningPrompt(String question) {
        return "Let's think step by step.\n\n" +
               "Problem: " + question + "\n\n" +
               "Step 1: Understand what is being asked\n" +
               "Step 2: Identify key information\n" +
               "Step 3: Determine the approach\n" +
               "Step 4: Execute the solution\n" +
               "Step 5: Verify the answer\n\n" +
               "Now solve: " + question;
    }
    
    public String buildMathPrompt(String problem) {
        return "Solve the following math problem step by step:\n\n" +
               problem + "\n\n" +
               "Show your work:\n" +
               "1. What is given?\n" +
               "2. What formula applies?\n" +
               "3. Calculate step by step\n" +
               "4. Final answer with units";
    }
    
    public String buildComplexReasoning(String task) {
        return "Think through this problem systematically:\n\n" +
               "1. Break down the problem into parts\n" +
               "2. Analyze each component\n" +
               "3. Identify relationships between parts\n" +
               "4. Synthesize solution\n" +
               "5. Consider edge cases\n\n" +
               "Task: " + task + "\n\n" +
               "Let's work through this:";
    }
}
```

### 3. Prompt Templates

```java
public class PromptTemplate {
    private String template;
    private Map<String, String> variables;
    
    public PromptTemplate(String template) {
        this.template = template;
        this.variables = new HashMap<>();
    }
    
    public PromptTemplate withVariable(String name, String value) {
        variables.put(name, value);
        return this;
    }
    
    public String render() {
        String result = template;
        
        for (Map.Entry<String, String> var : variables.entrySet()) {
            result = result.replace("{{" + var.getKey() + "}}", var.getValue());
        }
        
        return result;
    }
    
    public static PromptTemplate classification() {
        return new PromptTemplate(
            "Classify the following {{text_type}} into one of these categories: " +
            "{{categories}}.\n\n" +
            "Text: {{input_text}}\n\n" +
            "Category:"
        );
    }
    
    public static PromptTemplate extraction() {
        return new PromptTemplate(
            "Extract the following entities from the text:\n" +
            "Entities: {{entity_types}}\n\n" +
            "Text: {{input_text}}\n\n" +
            "Format as JSON:"
        );
    }
    
    public static PromptTemplate summarization() {
        return new PromptTemplate(
            "Summarize the following {{text_type}} in {{num_sentences}} sentences:\n\n" +
            "Text: {{input_text}}\n\n" +
            "Summary:"
        );
    }
}
```

### 4. Role-Based Prompting

```java
public class RolePromptBuilder {
    
    public String buildRolePrompt(String role, String task) {
        RoleDefinition roleDef = getRoleDefinition(role);
        
        return roleDef.getPersona() + "\n\n" +
               "Task: " + task + "\n\n" +
               roleDef.getGuidelines();
    }
    
    private RoleDefinition getRoleDefinition(String role) {
        switch (role.toLowerCase()) {
            case "expert":
                return new RoleDefinition(
                    "You are a world-class expert in your field with decades of experience.",
                    "Provide detailed, accurate explanations. Use technical terms appropriately."
                );
            case "tutor":
                return new RoleDefinition(
                    "You are a patient, encouraging teacher helping students learn.",
                    "Break down complex concepts. Ask guiding questions. Encourage thinking."
                );
            case "critic":
                return new RoleDefinition(
                    "You are a rigorous analyst who evaluates arguments critically.",
                    "Identify flaws, suggest improvements, challenge assumptions."
                );
            default:
                return new RoleDefinition(
                    "You are a helpful assistant.",
                    "Provide clear, accurate information."
                );
        }
    }
}

class RoleDefinition {
    String persona;
    String guidelines;
    
    RoleDefinition(String persona, String guidelines) {
        this.persona = persona;
        this.guidelines = guidelines;
    }
    
    String getPersona() { return persona; }
    String getGuidelines() { return guidelines; }
}
```

### 5. Self-Consistency Prompting

```java
public class SelfConsistencyPrompt {
    
    public String buildMultiPathPrompt(String question, int paths) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Solve this problem using MULTIPLE different approaches, " +
                     "then verify which is correct.\n\n");
        
        prompt.append("Problem: ").append(question).append("\n\n");
        
        for (int i = 1; i <= paths; i++) {
            prompt.append("Approach ").append(i).append(":\n");
            prompt.append("1. Method description\n");
            prompt.append("2. Step-by-step solution\n");
            prompt.append("3. Result\n\n");
        }
        
        prompt.append("Cross-check all results and explain which answer is correct.");
        
        return prompt.toString();
    }
    
    public String buildVotingPrompt(String question, String[] answers) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Evaluate each solution and vote for the best answer.\n\n");
        
        prompt.append("Question: ").append(question).append("\n\n");
        
        for (int i = 0; i < answers.length; i++) {
            prompt.append("Option ").append(i + 1).append(": ").append(answers[i]).append("\n");
        }
        
        prompt.append("\nFor each option, explain:\n");
        prompt.append("1. Is it correct?\n");
        prompt.append("2. What are its strengths/weaknesses?\n\n");
        
        prompt.append("Final vote and reasoning:");
        
        return prompt.toString();
    }
}
```

### 6. Prompt Evaluation

```java
public class PromptEvaluator {
    
    public double evaluatePrompt(String prompt, List<TestCase> testCases, 
                                 LLMEvaluator llm) {
        int correct = 0;
        
        for (TestCase tc : testCases) {
            String result = llm.generate(prompt + "\n\nInput: " + tc.input);
            String expected = tc.expectedOutput;
            
            if (evaluateResult(result, expected)) {
                correct++;
            }
        }
        
        return (double) correct / testCases.size();
    }
    
    private boolean evaluateResult(String result, String expected) {
        return result.toLowerCase().contains(expected.toLowerCase()) ||
               expected.toLowerCase().contains(result.toLowerCase());
    }
    
    public PromptMetrics analyzePrompt(String prompt) {
        PromptMetrics metrics = new PromptMetrics();
        
        metrics.length = prompt.length();
        metrics.hasExamples = prompt.contains("Example");
        metrics.hasInstructions = prompt.contains("Instruction") || 
                                   prompt.contains("Task");
        metrics.hasConstraints = prompt.contains("must") || 
                                  prompt.contains("should not");
        
        return metrics;
    }
}

class TestCase {
    String input;
    String expectedOutput;
    
    TestCase(String input, String expectedOutput) {
        this.input = input;
        this.expectedOutput = expectedOutput;
    }
}

class PromptMetrics {
    int length;
    boolean hasExamples;
    boolean hasInstructions;
    boolean hasConstraints;
}

interface LLMEvaluator {
    String generate(String prompt);
}
```