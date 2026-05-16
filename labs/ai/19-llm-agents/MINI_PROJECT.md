# LLM Agents - MINI PROJECT

## Project: Research Assistant Agent

Build an agent that researches topics and compiles reports.

### Implementation

```java
public class ResearchAgent {
    private ReActAgent agent;
    private ToolRegistry tools;
    private AgentMemory memory;
    
    public ResearchAgent() {
        this.tools = new ToolRegistry();
        this.tools.register(new WebSearchTool());
        this.tools.register(new NotesTool());
        
        this.memory = new AgentMemory();
        
        List<Tool> toolList = Arrays.asList(
            tools.get("search"),
            tools.get("notes")
        );
        
        this.agent = new ReActAgent(new MockLLM(), toolList);
    }
    
    public String research(String topic) {
        String task = "Research " + topic + " and create a summary";
        
        String result = agent.run(task);
        
        memory.add("Researched: " + topic, MemoryType.EPISODIC);
        
        return result;
    }
}

class WebSearchTool implements Tool {
    public String getName() { return "search"; }
    public String getDescription() { return "Search the web"; }
    public String execute(String input) {
        return "Results for: " + input;
    }
}

class NotesTool implements Tool {
    public String getName() { return "notes"; }
    public String getDescription() { return "Save notes"; }
    public String execute(String input) {
        return "Saved: " + input;
    }
}

public class TestResearch {
    public static void main(String[] args) {
        ResearchAgent agent = new ResearchAgent();
        
        String result = agent.research("machine learning");
        
        System.out.println(result);
    }
}
```

## Deliverables

- [ ] Implement ReAct agent
- [ ] Add search tool
- [ ] Add memory system
- [ ] Create planning
- [ ] Test on tasks