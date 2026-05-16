# LLM Agents - CODE DEEP DIVE

## Java Implementations

### 1. ReAct Agent

```java
public class ReActAgent {
    private LLMClient llm;
    private List<Tool> tools;
    private int maxIterations = 10;
    
    public ReActAgent(LLMClient llm, List<Tool> tools) {
        this.llm = llm;
        this.tools = tools;
    }
    
    public AgentResponse run(String task) {
        String thought = "";
        String action = "";
        String observation = "";
        
        for (int i = 0; i < maxIterations; i++) {
            thought = think(task, action, observation);
            
            if (thought.contains("FINAL_ANSWER")) {
                return new AgentResponse(extractAnswer(thought), i + 1);
            }
            
            action = selectAction(thought);
            
            observation = executeAction(action);
            
            if (observation.contains("ERROR")) {
                return new AgentResponse("Error: " + observation, i + 1);
            }
        }
        
        return new AgentResponse("Max iterations reached", maxIterations);
    }
    
    private String think(String task, String prevAction, String prevObs) {
        String prompt = buildReActPrompt(task, prevAction, prevObs);
        
        return llm.generate(prompt);
    }
    
    private String buildReActPrompt(String task, String action, String observation) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Task: ").append(task).append("\n\n");
        
        if (!action.isEmpty()) {
            prompt.append("Action: ").append(action).append("\n");
            prompt.append("Observation: ").append(append(observation)).append("\n\n");
        }
        
        prompt.append("Available tools:\n");
        
        for (Tool tool : tools) {
            prompt.append("- ").append(tool.getName()).append(": ")
                   .append(tool.getDescription()).append("\n");
        }
        
        prompt.append("\nThink step by step. " +
                     "Format: Thought: ... Action: tool(input) " +
                     "Observation: ... until you can provide FINAL_ANSWER.");
        
        return prompt.toString();
    }
    
    private String selectAction(String thought) {
        for (Tool tool : tools) {
            if (thought.contains(tool.getName())) {
                return extractToolCall(thought, tool.getName());
            }
        }
        
        return "none";
    }
    
    private String executeAction(String action) {
        if (action.equals("none") || action.isEmpty()) {
            return "No action needed";
        }
        
        String[] parts = action.split("\\(");
        
        if (parts.length < 2) return "Invalid action format";
        
        String toolName = parts[0].trim();
        String args = parts[1].replace(")", "").trim();
        
        for (Tool tool : tools) {
            if (tool.getName().equals(toolName)) {
                return tool.execute(args);
            }
        }
        
        return "Tool not found: " + toolName;
    }
    
    private String extractAnswer(String thought) {
        int idx = thought.indexOf("FINAL_ANSWER:");
        
        if (idx >= 0) {
            return thought.substring(idx + 13).trim();
        }
        
        return thought;
    }
}

class AgentResponse {
    private String result;
    private int iterations;
    
    public AgentResponse(String result, int iterations) {
        this.result = result;
        this.iterations = iterations;
    }
    
    public String getResult() { return result; }
    public int getIterations() { return iterations; }
}
```

### 2. Tool System

```java
public class ToolRegistry {
    private Map<String, Tool> tools;
    
    public ToolRegistry() {
        this.tools = new HashMap<>();
        registerDefaultTools();
    }
    
    private void registerDefaultTools() {
        register(new SearchTool());
        register(new CalculatorTool());
        register(new WeatherTool());
        register(new FileReadTool());
    }
    
    public void register(Tool tool) {
        tools.put(tool.getName(), tool);
    }
    
    public Tool get(String name) {
        return tools.get(name);
    }
    
    public List<String> getToolNames() {
        return new ArrayList<>(tools.keySet());
    }
}

interface Tool {
    String getName();
    String getDescription();
    String execute(String input);
}

class SearchTool implements Tool {
    public String getName() { return "search"; }
    public String getDescription() { return "Search the web for information"; }
    
    public String execute(String input) {
        return "Search results for: " + input;
    }
}

class CalculatorTool implements Tool {
    public String getName() { return "calculate"; }
    public String getDescription() { return "Perform mathematical calculations"; }
    
    public String execute(String input) {
        try {
            String expr = input.replace("calculate ", "");
            double result = evaluateExpression(expr);
            return "Result: " + result;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    private double evaluateExpression(String expr) {
        return 42.0;
    }
}

class WeatherTool implements Tool {
    public String getName() { return "weather"; }
    public String getDescription() { return "Get weather information"; }
    
    public String execute(String input) {
        return "Weather in " + input + ": Sunny, 72°F";
    }
}

class FileReadTool implements Tool {
    public String getName() { return "read_file"; }
    public String getDescription() { return "Read contents of a file"; }
    
    public String execute(String input) {
        return "File content would be loaded here";
    }
}
```

### 3. Planning Agent

```java
public class PlanningAgent {
    private LLMClient llm;
    
    public Plan createPlan(String goal) {
        String decomposed = decomposeGoal(goal);
        
        List<Step> steps = parseSteps(decomposed);
        
        return new Plan(steps);
    }
    
    private String decomposeGoal(String goal) {
        String prompt = "Break down this goal into clear, actionable steps:\n\n" + goal;
        
        return llm.generate(prompt);
    }
    
    private List<Step> parseSteps(String decomposed) {
        List<Step> steps = new ArrayList<>();
        
        String[] lines = decomposed.split("\n");
        
        for (String line : lines) {
            if (line.trim().matches("^\\d+[\\.\\)]")) {
                steps.add(new Step(line.trim()));
            }
        }
        
        return steps;
    }
    
    public String executePlan(Plan plan, ToolRegistry tools) {
        StringBuilder results = new StringBuilder();
        
        for (int i = 0; i < plan.getSteps().size(); i++) {
            Step step = plan.getSteps().get(i);
            
            String result = executeStep(step, tools);
            
            results.append("Step ").append(i + 1).append(": ").append(result).append("\n");
            
            if (result.startsWith("Error")) {
                return results.toString() + "\nPlan failed at step " + (i + 1);
            }
        }
        
        return results.toString();
    }
    
    private String executeStep(Step step, ToolRegistry tools) {
        return "Executed: " + step.getDescription();
    }
}

class Plan {
    private List<Step> steps;
    
    public Plan(List<Step> steps) {
        this.steps = steps;
    }
    
    public List<Step> getSteps() { return steps; }
}

class Step {
    private String description;
    
    Step(String description) {
        this.description = description;
    }
    
    String getDescription() { return description; }
}
```

### 4. Memory System

```java
public class AgentMemory {
    private List<MemoryItem> shortTerm;
    private Map<String, MemoryItem> longTerm;
    private int maxShortTerm = 10;
    
    public AgentMemory() {
        this.shortTerm = new ArrayList<>();
        this.longTerm = new HashMap<>();
    }
    
    public void add(String content, MemoryType type) {
        MemoryItem item = new MemoryItem(content, type, System.currentTimeMillis());
        
        if (type == MemoryType.SHORT_TERM) {
            addToShortTerm(item);
        } else {
            longTerm.put(generateKey(content), item);
        }
    }
    
    private void addToShortTerm(MemoryItem item) {
        shortTerm.add(item);
        
        if (shortTerm.size() > maxShortTerm) {
            consolidateToLongTerm();
        }
    }
    
    private void consolidateToLongTerm() {
        if (!shortTerm.isEmpty()) {
            String key = generateKey(shortTerm.get(0).getContent());
            longTerm.put(key, shortTerm.get(0));
            shortTerm.remove(0);
        }
    }
    
    public String retrieve(String query) {
        StringBuilder context = new StringBuilder();
        
        context.append("Recent:\n");
        
        for (MemoryItem item : shortTerm) {
            context.append("- ").append(item.getContent()).append("\n");
        }
        
        context.append("\nRelevant history:\n");
        
        for (MemoryItem item : longTerm.values()) {
            if (containsRelevant(query, item.getContent())) {
                context.append("- ").append(item.getContent()).append("\n");
            }
        }
        
        return context.toString();
    }
    
    private boolean containsRelevant(String query, String content) {
        return content.toLowerCase().contains(query.toLowerCase());
    }
    
    private String generateKey(String content) {
        return String.valueOf(content.hashCode());
    }
}

class MemoryItem {
    private String content;
    private MemoryType type;
    private long timestamp;
    
    MemoryItem(String content, MemoryType type, long timestamp) {
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
    }
    
    String getContent() { return content; }
}

enum MemoryType {
    SHORT_TERM, LONG_TERM, EPISODIC, SEMANTIC
}
```

### 5. Agent Loop

```java
public class AgentLoop {
    private ReActAgent agent;
    private ToolRegistry tools;
    private AgentMemory memory;
    
    public AgentLoop(LLMClient llm) {
        this.tools = new ToolRegistry();
        this.memory = new AgentMemory();
        this.agent = new ReActAgent(llm, tools.getToolNames().stream()
            .map(tools::get)
            .collect(Collectors.toList()));
    }
    
    public String run(String task) {
        String context = memory.retrieve(task);
        
        String promptWithContext = context + "\n\nTask: " + task;
        
        AgentResponse response = agent.run(promptWithContext);
        
        memory.add("Task: " + task + " -> Result: " + response.getResult(), 
                   MemoryType.EPISODIC);
        
        return response.getResult();
    }
    
    public void clearMemory() {
        memory = new AgentMemory();
    }
}
```