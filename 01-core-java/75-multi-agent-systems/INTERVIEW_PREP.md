# Interview Preparation: Multi-Agent Systems

This document covers advanced questions related to orchestrating multiple AI agents, state management, and debugging complex AI workflows.

## Q1: Why would you choose a Multi-Agent System (MAS) over a single agent with a large system prompt and many tools?
**Answer:**
1.  **Separation of Concerns**: A single agent with 20 tools and a 5-page system prompt suffers from "attention dilution." It might use the wrong tool or forget constraints. MAS isolates context; a "DB Agent" only knows about the database, reducing hallucinations.
2.  **Security/Blast Radius**: You can apply different IAM roles or permissions to different agents. A "Read-Only Agent" cannot accidentally drop a table, whereas a single omnipotent agent could.
3.  **Parallelism**: In a MAS, independent sub-tasks (e.g., researching three different competitors) can be dispatched to multiple worker agents simultaneously, drastically reducing overall latency.

## Q2: Explain the difference between a Hierarchical (Manager-Worker) pattern and a Graph-based state machine (like LangGraph) for agent orchestration.
**Answer:**
*   **Hierarchical**: Relies on the LLM's reasoning to drive the flow. The Manager LLM decides *if*, *when*, and *which* worker to call. It is highly flexible but prone to going off-script or getting stuck in loops if the Manager hallucinates.
*   **Graph-based**: The orchestration flow is defined in deterministic code (a directed graph). Node A (Agent A) executes, updates a shared state, and deterministic edges decide whether to route to Node B or Node C based on that state. It is much more predictable, easier to debug, and better for strict enterprise workflows, though less "autonomous."

## Q3: How do you handle "Context Loss" when a Manager agent delegates a task to a Worker agent?
**Answer:**
When a Manager summarizes a task for a Worker, it often drops critical constraints (the "Telephone Game" effect). 
To handle this:
1.  **Shared State/Blackboard**: Instead of passing instructions via the prompt, the Manager writes the task to a shared data structure (e.g., a JSON object in memory). The Worker is programmed to read from this structure directly.
2.  **Pass-through Context**: Programmatically inject the original user prompt into the Worker's context window alongside the Manager's specific instructions.

## Q4: You notice your MAS is consuming tokens at an exponential rate, driving up API costs. How do you optimize it?
**Answer:**
1.  **Model Routing**: Stop using the most expensive model (e.g., GPT-4o) for everything. Use the expensive model for the Orchestrator/Manager, but use cheaper, faster models (e.g., GPT-4o-mini or Claude Haiku) for the Worker agents performing narrow, well-defined tasks.
2.  **Limit Iterations**: Enforce strict caps on how many turns agents can take when debating or retrying tool calls.
3.  **Optimize Tool Payloads**: Ensure tools return concise data. If a Worker agent searches a database, the tool should return a summary or the top 5 results, not a 10,000-row JSON array.

## Q5: How do you test and debug a Multi-Agent System?
**Answer:**
Testing MAS is notoriously difficult because of non-determinism multiplied across several models.
1.  **Tracing**: Use observability tools (like LangSmith, Phoenix, or Spring AI Advisors) to visualize the entire trace: User -> Manager -> Worker 1 -> Tool -> Worker 1 -> Manager. You must be able to see the exact prompt and response at every node.
2.  **Unit Testing Agents**: Test Worker agents in isolation. Provide a mocked Manager input and assert that the Worker calls the correct tool or returns the expected format.
3.  **Evaluation Frameworks**: Use LLM-as-a-Judge frameworks (like RAGAS) to evaluate the final output against a golden dataset, ensuring the multi-agent collaboration actually improves quality compared to a single agent.