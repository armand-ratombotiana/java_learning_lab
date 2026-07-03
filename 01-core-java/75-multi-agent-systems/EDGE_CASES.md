# Edge Cases & Pitfalls: Multi-Agent Systems

Building Multi-Agent Systems introduces complex failure modes that go beyond the issues seen with single agents.

## 1. Agent Deadlocks (Infinite Debates)
When two agents are instructed to collaborate or critique each other (e.g., a Coder and a Reviewer), they can get trapped in an infinite loop. The Reviewer rejects the code, the Coder rewrites it slightly, the Reviewer rejects it again for a different reason, ad infinitum.
*   **Mitigation**:
    *   Implement a strict "max turns" counter for the conversation.
    *   Introduce a "Tie-Breaker" or "Manager" agent that forces a decision after N iterations.
    *   Prompt the Reviewer to be constructive and accept "good enough" code after a certain point.

## 2. Context Loss in Delegation (The Telephone Game)
In a hierarchical system, the Manager agent might summarize the user's request before passing it to the Worker agent. Critical details can be lost in this translation, causing the Worker to generate useless output.
*   **Mitigation**:
    *   Pass the raw user input alongside the Manager's instructions.
    *   Use a shared "Blackboard" or global state object where all agents can read the original immutable request and project constraints.

## 3. Exponential Cost Multipliers
A single user request to a Manager agent might trigger 5 sub-agents, each doing 3 turns of thought/action, each utilizing a 128k token context window. A single $0.01 request can quickly balloon to a $2.00 request.
*   **Mitigation**:
    *   Use smaller, cheaper, and faster models (e.g., GPT-4o-mini, Claude 3 Haiku, or local Llama 3 8B) for specialized worker agents, reserving the expensive "frontier" models only for the Manager/Orchestrator.
    *   Strictly monitor token budgets per request trace.

## 4. Tool Collision and State Corruption
If multiple agents have access to the same tools (e.g., a database write tool or a file system modifier) and run concurrently, they might overwrite each other's work or corrupt the shared state.
*   **Mitigation**:
    *   Enforce strict boundaries: Only the "DB Agent" can write to the database. Other agents must ask the DB Agent to perform the write.
    *   Implement optimistic locking or transactional boundaries in the tools themselves.

## 5. Over-Engineering (The "Agentic" Hammer)
Not every problem requires a Multi-Agent System. Using a 3-agent swarm to parse a CSV file is slower, more expensive, and more error-prone than a 10-line Java method.
*   **Mitigation**:
    *   Always default to deterministic code (standard Java logic) first.
    *   Use single-agent tool calling second.
    *   Only reach for MAS when the problem requires distinct personas, separate context windows, or complex multi-step reasoning that a single prompt cannot handle reliably.