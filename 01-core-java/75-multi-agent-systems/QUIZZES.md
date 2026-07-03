# Quizzes: Multi-Agent Systems

Test your knowledge on orchestrating multiple AI agents, architecture patterns, and common pitfalls.

## Quiz 1: MAS Concepts

**Q1: What is a primary architectural reason to use a Multi-Agent System instead of a single, massive prompt?**
- A) To increase the context window limit of the base model.
- B) To enforce Separation of Concerns, giving each agent only the specific context and tools it needs.
- C) To bypass API rate limits.
- D) To automatically fine-tune the models in real-time.
*Answer: B*

**Q2: Which orchestration pattern involves a main agent delegating tasks to specialized sub-agents?**
- A) Joint / Debate Pattern
- B) Sequential Pipeline
- C) Hierarchical (Manager-Worker) Pattern
- D) Map-Reduce
*Answer: C*

## Quiz 2: Java Implementation & State

**Q1: In the context of Spring AI, how can you most easily implement a "Worker Agent" that a "Manager Agent" can invoke?**
- A) By running the Worker Agent in a separate Docker container and using gRPC.
- B) By wrapping the Worker Agent's `ChatClient` execution inside a `java.util.function.Function` and registering it as a Tool for the Manager.
- C) By writing a custom Vector Database.
- D) By using the `TextSplitter` class.
*Answer: B*

**Q2: When Agent A passes instructions to Agent B, what is the "Telephone Game" risk?**
- A) The API connection drops due to network latency.
- B) Agent A summarizes the original user request poorly, causing Agent B to lose critical context and generate a bad result.
- C) The models start speaking in different human languages.
- D) The system runs out of memory.
*Answer: B*

## Quiz 3: Edge Cases

**Q1: How do you prevent an infinite debate between a "Coder Agent" and a "Reviewer Agent"?**
- A) Ensure both agents use the exact same system prompt.
- B) Only use GPT-4.
- C) Implement a strict "max turns" counter or introduce a tie-breaker mechanism.
- D) Give both agents access to the database.
*Answer: C*