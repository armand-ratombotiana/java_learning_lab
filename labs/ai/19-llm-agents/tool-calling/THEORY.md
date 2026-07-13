# LLM Agents & Tool Calling Theory

## 💡 The Problem: LLMs are Isolated
A standard Large Language Model (LLM) is essentially a highly advanced autocomplete engine trapped in a box.
- It cannot check the current weather.
- It cannot query your production database.
- It cannot send an email.
- It cannot perform complex math accurately (it guesses the next token, it doesn't compute arithmetic).

To build truly autonomous AI systems, the LLM must be able to interact with the outside world.

## 🤖 The Solution: Agents and Tool Calling
An **Agent** is an LLM equipped with a set of tools (functions) it can invoke to accomplish a goal.

Instead of just generating text for the user, the LLM generates a structured request (usually JSON) asking the system to run a specific function. The system runs the Java/Python function, gets the result, and feeds it back into the LLM's prompt.

### The ReAct Framework (Reasoning + Acting)
The industry standard for building Agents is the **ReAct** pattern. The Agent operates in a continuous loop:
1. **Thought**: The LLM reasons about what it needs to do next.
2. **Action**: The LLM decides to call a specific tool with specific arguments.
3. **Observation**: The system executes the tool and returns the result to the LLM.
4. *(Repeat until the goal is achieved)*.

**Example**:
- *User*: "What is the weather in Paris, and what should I pack?"
- *Thought*: "I need to know the weather in Paris to recommend clothing."
- *Action*: `call_tool("get_weather", {"location": "Paris, France"})`
- *Observation*: `{"temp": "12C", "condition": "Rainy"}`
- *Thought*: "It is raining and 12C. I should recommend a jacket and umbrella."
- *Final Answer*: "It's currently 12°C and rainy in Paris. You should pack a warm jacket and an umbrella."