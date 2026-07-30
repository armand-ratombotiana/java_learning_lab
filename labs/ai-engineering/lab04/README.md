# Lab 04: AI Agent Frameworks

## Learning Objectives
- Implement the ReAct (Reasoning + Acting) pattern
- Build a tool-using agent with planning capabilities
- Design a multi-agent orchestration system
- Understand agent memory and state management

## Concepts Covered
- **ReAct Pattern**: Thought → Action → Observation → Thought loop
- **Tool Use**: Connecting LLMs to external APIs and functions
- **Memory**: Conversation history, working memory, long-term memory
- **Orchestration**: Coordinator agents delegating to specialist agents
- **Planning**: Breaking complex tasks into sub-steps

## Setup
```bash
cd lab04
javac src/com/aiengineering/lab04/AiAgentFrameworksDemo.java
java com.aiengineering.lab04.AiAgentFrameworksDemo
```

## Key Takeaways
- ReAct provides interpretable step-by-step reasoning
- Tool use dramatically expands agent capabilities
- Multi-agent systems handle complex workflows better
