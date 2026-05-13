# Prompt Engineering - Complete Theory

## 1. Basic Principles

### 1.1 Clear Instructions
- Be specific about format
- Break into steps
- Specify constraints

### 1.2 Provide Context
- Relevant background
- Examples
- Chain of thought

### 1.3 Role Assignment
- Act as expert
- Define persona
- Set tone

## 2. Common Patterns

### 2.1 Zero-Shot
No examples, direct task

### 2.2 Few-Shot
Examples in prompt
Task-Description + Examples + Query

### 2.3 Chain of Thought (CoT)
"Think step by step"
Encourages reasoning

### 2.4 Self-Consistency
Multiple reasoning paths
Majority vote

## 3. Advanced Techniques

### 3.1 Tree of Thoughts
Explore multiple branches
Evaluate and select

### 3.2 ReAct (Reason + Act)
Thought -> Action -> Observation
Iterative problem solving

### 3.3 Constitutional AI
Principles for alignment
Self-critique and revision

## 4. Prompt Templates

### 4.1 Classification
"You are a classifier. Category: [CATEGORIES]. Text: [TEXT]. Output:"

### 4.2 Summarization
"Summarize the following in [LENGTH]. Focus on [ASPECTS]. Text: [TEXT]"

### 4.3 Question Answering
"Based on the following context, answer the question. Context: [CTX] Question: [Q] Answer:"

## Java Implementation

```java
public class PromptTemplate {
    private String template;
    private Map<String, String> variables;
    
    public static String classification(String categories, String text) {
        return String.format(
            "Classify the following text into one of these categories: %s.\n" +
            "Text: %s\n" +
            "Category:", categories, text
        );
    }
    
    public static String fewShotQa(String question, Example[] examples) {
        StringBuilder sb = new StringBuilder();
        sb.append("Answer questions based on the given examples.\n\n");
        
        for (Example ex : examples) {
            sb.append("Q: ").append(ex.question).append("\n");
            sb.append("A: ").append(ex.answer).append("\n\n");
        }
        
        sb.append("Q: ").append(question).append("\n");
        sb.append("A:");
        
        return sb.toString();
    }
    
    public static String chainOfThought(String problem) {
        return String.format(
            "Solve the following problem step by step.\n" +
            "Problem: %s\n\n" +
            "Step 1:", problem
        );
    }
    
    public static String react(String observation) {
        return String.format(
            "Given the observation '%s', what should I think next? " +
            "Think about what action to take.", observation
        );
    }
}
```

## 5. Best Practices

### 5.1 Iteration
- Test and refine
- Try multiple formats
- Measure performance

### 5.2 Limitations
- Token limits
- Model capabilities
- Consistency issues

### 5.3 Security
- Prompt injection
- Information leakage
- Jailbreaking