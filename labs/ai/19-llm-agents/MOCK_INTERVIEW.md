# Mock Interview: LLM Agents

## Question 1: Agent Architecture
**Q**: Design an LLM agent system that can browse the web and answer questions.

**A**:
```python
class WebAgent:
    def __init__(self, llm, tools):
        self.llm = llm
        self.tools = {
            "search": self.search_web,
            "extract": self.extract_content,
            "summarize": self.summarize_page,
            "calculate": self.calculate
        }

    def run(self, task, max_steps=10):
        messages = [{"role": "system",
                     "content": "You are a web research agent. Use tools to answer queries."}]
        messages.append({"role": "user", "content": task})

        for step in range(max_steps):
            response = self.llm.chat(messages, tools=list(self.tools.keys()))
            messages.append(response)

            if response.get("tool_call"):
                tool = response["tool_call"]["name"]
                args = response["tool_call"]["arguments"]
                result = self.tools[tool](**args)
                messages.append({"role": "tool", "content": str(result)})
            else:
                return response["content"]

        return "Max steps reached"
```

Key components: LLM + tool definitions + state management (messages) + execution loop.

## Question 2: Tool Calling
**Q**: How do you define and execute tool calls for an LLM agent?

**A**: Tool calling uses function schema definitions that the LLM outputs as structured JSON.

```python
tools = [{
    "type": "function",
    "function": {
        "name": "search_web",
        "description": "Search the web for information",
        "parameters": {
            "type": "object",
            "properties": {
                "query": {"type": "string", "description": "Search query"},
                "num_results": {"type": "integer", "default": 5}
            },
            "required": ["query"]
        }
    }
}]

# LLM returns:
# {"name": "search_web", "arguments": {"query": "latest AI breakthroughs 2026", "num_results": 10}}
```

## Question 3: Memory & State
**Q**: How do you manage conversation state and long-term memory in agents?

**A**: Three types of memory:
1. **Short-term (context window)**: Recent conversation history. Limited by context length.
2. **Working memory**: Summarized key information from current task.
3. **Long-term memory**: External storage (vector DB, key-value store).

```python
class AgentMemory:
    def __init__(self, vector_store):
        self.short_term = []
        self.vector_store = vector_store

    def add(self, content, importance=1.0):
        self.short_term.append(content)
        if len(self.short_term) > 10:
            summary = self.summarize(self.short_term[:-5])
            self.vector_store.add(summary)
            self.short_term = self.short_term[-5:]

    def retrieve_relevant(self, query, k=3):
        return self.vector_store.search(query, k=k) + self.short_term
```

## Question 4: Planning & Decomposition
**Q**: How does an LLM agent decompose complex tasks into sub-tasks?

**A**: Techniques:
- **Plan-then-execute**: Generate complete plan first, then execute each step
- **ReAct**: Interleaved reasoning (Thought-Action-Observation)
- **Plan-and-Solve**: Initial plan + dynamic adaptation
- **Tree-of-Thoughts**: Explore multiple plan branches

```python
def plan_and_execute(task):
    plan = llm.generate(f"Break down this task into subtasks: {task}")
    for step in plan["steps"]:
        result = execute_step(step)
        if result["needs_replanning"]:
            plan = llm.generate(f"Original plan: {plan}\nCurrent status: {result}\nRevise plan:")
```

## Question 5: Agent Evaluation
**Q**: How do you evaluate LLM agent performance?

**A**: Multi-faceted evaluation:
- **Task success rate**: Did agent complete the assigned task?
- **Efficiency**: Number of steps, tokens used, time taken
- **Robustness**: Handles errors, unexpected inputs, tool failures
- **Safety**: Avoids harmful actions, respects constraints
- **Cost**: API calls, compute, tokens per task

**Benchmarks**:
- SWE-Bench (software engineering)
- GAIA (general AI assistant)
- WebArena (web tasks)
- AgentBench (multi-domain)
- ToolBench (tool use)

**Human evaluation**: Task completion quality, coherence, helpfulness
