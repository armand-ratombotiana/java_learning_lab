# Lab 04: AI Agent Frameworks — Interview Q&A

## FAANG-Level Questions

### Q1: Explain the ReAct pattern and how it differs from a simple chain-of-thought prompt.

**A:** ReAct interleaves reasoning (Thought) with tool-use actions (Action) and environment feedback (Observation) in a closed loop. Simple CoT only produces reasoning steps without interacting with external systems. ReAct enables agents to gather real information, verify facts, and recover from errors dynamically.

### Q2: How would you design a multi-agent system for customer support?

**A:** Use a coordinator agent that classifies the query and routes to specialized agents (billing, technical, account). Each specialist has its own tools and knowledge base. The coordinator maintains conversation context and can escalate to a human agent if confidence is low. Agents communicate via a shared message bus with structured schemas.

### Q3: What are the failure modes of tool-calling agents and how do you mitigate them?

**A:** Key failures: (1) Tool selection errors — use structured tool definitions with clear descriptions; (2) Infinite loops — set max steps and timeout; (3) Hallucinated tool outputs — validate observations against schemas; (4) Cost explosions — rate-limit tool calls and set budget caps.

### Q4: Design a memory system for a conversational agent.

**A:** Use a three-tier memory: (1) sliding window of recent turns for immediate context; (2) summarization of older conversation history; (3) vector database for long-term recall of facts, preferences, and entities. The agent explicitly writes important facts to long-term memory and retrieves them via semantic search when relevant.

### Q5: How would you evaluate agent performance beyond task completion rate?

**A:** Track: (1) number of steps/tool calls per task; (2) cost per task; (3) success rate by task category; (4) recovery rate from errors; (5) latency distribution; (6) human intervention rate. Build a replay system that compares agent trajectories against ground-truth optimal paths.