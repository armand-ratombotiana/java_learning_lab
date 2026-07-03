# Quizzes: Advanced Prompting & Agents

Test your knowledge on structured outputs, function calling, and agent architectures.

## Quiz 1: Prompting Techniques

**Q1: What is the primary benefit of "Chain of Thought" (CoT) prompting?**
- A) It reduces the number of tokens required for the prompt.
- B) It forces the LLM to output valid JSON.
- C) It improves performance on complex reasoning tasks by asking the model to explain its steps before answering.
- D) It prevents prompt injection attacks.
*Answer: C*

**Q2: Providing three examples of desired input/output pairs in a prompt before asking the main question is an example of:**
- A) Zero-Shot Prompting
- B) Few-Shot Prompting
- C) Fine-Tuning
- D) RAG
*Answer: B*

## Quiz 2: Structured Output & Function Calling

**Q1: In Spring AI, how do you ensure an LLM returns a response that maps to a specific Java Record?**
- A) By using `BeanOutputConverter` and appending its formatting instructions to the prompt.
- B) By compiling the Java Record into the LLM's weights.
- C) By using a Vector Database.
- D) By throwing an exception if the format is wrong.
*Answer: A*

**Q2: When using Function Calling in Spring AI, who actually executes the Java function?**
- A) The LLM (e.g., OpenAI servers) executes the Java code remotely.
- B) The Spring Boot application executes the function locally after the LLM requests it.
- C) The Vector Database executes it.
- D) The user's browser executes it.
*Answer: B*

## Quiz 3: AI Agents

**Q1: What does the acronym ReAct stand for in the context of AI Agents?**
- A) Rest API Actions
- B) Reason + Act
- C) Reactive Architecture
- D) Retrieve and Compute
*Answer: B*

**Q2: Which of the following is a major risk when building autonomous AI agents?**
- A) The agent might execute tools in an infinite loop without reaching a conclusion.
- B) The agent might run out of database connections.
- C) The agent will automatically fine-tune itself.
- D) The agent will refuse to use JSON.
*Answer: A*