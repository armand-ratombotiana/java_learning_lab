# LLM Agents - EXERCISES

## Exercise 1: Trace ReAct Loop
Trace one iteration of ReAct for task "What's 5 + 3?"

```java
public String traceReAct() {
    String task = "What's 5 + 3?";
    
    // Thought: Need to calculate, use calculator tool
    String thought = "Thought: I need to perform this calculation. " +
                     "Action: calculate(5 + 3)";
    
    // Action: calculate(5 + 3)
    String action = "calculate(5 + 3)";
    
    // Observation: 8
    String observation = "Observation: 8";
    
    // Since we have answer, next would be FINAL_ANSWER: 8
    
    return "Task -> Thought -> Action -> Observation -> Final Answer";
}
```

## Exercise 2: Implement Tool
Create a tool that searches a simple database.

```java
public class DatabaseTool implements Tool {
    private Map<String, String> database;
    
    public DatabaseTool() {
        database = new HashMap<>();
        database.put("user1", "John, age 30, email john@example.com");
        database.put("user2", "Jane, age 25, email jane@example.com");
    }
    
    public String getName() { return "lookup"; }
    public String getDescription() { return "Look up data in database"; }
    
    public String execute(String input) {
        String key = input.replace("lookup ", "").trim();
        
        return database.getOrDefault(key, "Not found");
    }
}
```

## Exercise 3: Create Plan
Decompose "Plan a vacation" into steps.

```java
public String decomposeGoal() {
    PlanningAgent agent = new PlanningAgent();
    
    Plan plan = agent.createPlan("Plan a vacation to Japan");
    
    // Steps: 1) Determine budget 2) Choose dates 3) Book flights 4) 
    //        Book hotel 5) Plan activities 6) Get visa 7) Pack
    
    return plan.toString();
}
```

## Exercise 4: Memory Operation
Add and retrieve from memory.

```java
public void memoryOps() {
    AgentMemory memory = new AgentMemory();
    
    memory.add("User prefers detailed explanations", MemoryType.LONG_TERM);
    memory.add("Last task was code review", MemoryType.SHORT_TERM);
    
    String context = memory.retrieve("code");
    
    System.out.println(context);
}
```

## Exercise 5: Build Agent Loop
Combine components into agent loop.

```java
public String agentLoop() {
    AgentLoop agent = new AgentLoop(new MockLLM());
    
    String result = agent.run("Find information about Java");
    
    return result;
}
```

---

## Solutions

### Exercise 1:
```java
// ReAct: Think → Act → Observe → Think → Final Answer
```

### Exercise 2:
```java
// Tool implemented with description, name, execute method
```

### Exercise 3:
```java
// Plan decomposed into actionable steps
```

### Exercise 4:
```java
// Memory stores and retrieves based on relevance
```

### Exercise 5:
```java
// Full agent loop: memory + tools + ReAct + LLM
```