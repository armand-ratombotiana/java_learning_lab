# LLM Agents - Complete Theory

## 1. Agent Architecture

### 1.1 Components
- Planning: decompose tasks
- Memory: store history
- Tools: external capabilities
- Execution: select and run actions

### 1.2 React Pattern
- Observe: get current state
- Think: decide action
- Act: execute tool
- Reflect: update memory

## 2. Tool Use

### 2.1 Types
- Search: web, document
- Code: execution, analysis
- File: read, write
- API: external services

### 2.2 Tool Definition
- Name
- Description
- Parameters
- Return type

### 2.3 Tool Selection
- Match to task
- Chain multiple tools
- Handle errors

## 3. Planning

### 3.1 Task Decomposition
- Zero-shot: "achieve goal by..."
- Few-shot: examples of planning

### 3.2 Subgoal Planning
Break into manageable steps
Track progress

### 3.3 Replanning
Adapt when plans fail
Recover from errors

## 4. Memory

### 4.1 Short-term
- Recent context
- Conversation history
- Current task state

### 4.2 Long-term
- Persistent knowledge
- User preferences
- Learned procedures

### 4.3 Memory Types
- Episodic: experiences
- Semantic: facts
- Procedural: how to do things

## Java Implementation

```java
public class LLMAgent {
    private LLM llm;
    private Tool[] tools;
    private Memory memory;
    private Plan plan;
    
    public LLMAgent(LLM llm, Tool[] tools) {
        this.llm = llm;
        this.tools = tools;
        this.memory = new Memory();
    }
    
    public String run(String task) {
        // Plan
        String planStr = llm.generate(
            "Break down this task into steps:\n" + task
        );
        plan = parsePlan(planStr);
        
        // Execute
        for (PlanStep step : plan.steps) {
            if (step.isToolCall) {
                String result = executeTool(step.toolName, step.params);
                memory.addObservation(step.description, result);
                
                // Replan if needed
                if (step.failed) {
                    String replan = llm.generate(
                        "Task failed. Replan:\n" +
                        "Task: " + task + "\n" +
                        "Progress: " + memory.getHistory() + "\n" +
                        "Error: " + step.error
                    );
                    plan = parsePlan(replan);
                }
            }
        }
        
        // Final answer
        return llm.generate(
            "Based on the results, answer:\n" + task + "\n\n" +
            "Results: " + memory.getSummary()
        );
    }
    
    private String executeTool(String name, Map<String, String> params) {
        for (Tool tool : tools) {
            if (tool.name.equals(name)) {
                try {
                    return tool.execute(params);
                } catch (Exception e) {
                    return "Error: " + e.getMessage();
                }
            }
        }
        return "Tool not found: " + name;
    }
    
    public static class Tool {
        String name;
        String description;
        Map<String, String> parameters;
        
        public String execute(Map<String, String> params) {
            // Execute tool logic
            return "";
        }
    }
    
    public static class Memory {
        List<String> history = new ArrayList<>();
        
        public void addObservation(String desc, String result) {
            history.add(desc + " -> " + result);
        }
        
        public String getHistory() {
            return String.join("\n", history);
        }
        
        public String getSummary() {
            return "Completed " + history.size() + " steps";
        }
    }
}
```

## 5. Applications

### 5.1 Autonomous Agents
- Research assistants
- Code generation and debugging
- Data analysis

### 5.2 Multi-Agent
- Role-based collaboration
- Debate and consensus
- Hierarchical control

## 6. Challenges

### 6.1 Planning Quality
- Suboptimal plans
- Cascading errors

### 6.2 Tool Reliability
- API failures
- Format errors

### 6.3 Memory Limits
- Context overflow
- Information loss