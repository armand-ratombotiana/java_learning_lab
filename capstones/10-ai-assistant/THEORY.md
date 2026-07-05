# Theory: AI Assistant

## LLM Architecture
Large Language Models (LLMs) are transformer-based neural networks trained on massive text corpora. They predict the next token given a sequence of previous tokens. Key concepts: attention mechanism, context window, temperature sampling, top-k/top-p sampling.

## Tool Calling (Function Calling)
LLMs can be augmented with external tools via structured output. The model generates a JSON object specifying the tool name and arguments. The system executes the tool and returns the result. This enables LLMs to perform actions beyond text generation.

## System Prompts
The system prompt defines the assistant's behavior, capabilities, constraints, and persona. It's injected at the beginning of the conversation context. Careful prompt engineering is critical for reliable tool calling and safe behavior.

## Conversation Management
Managing context windows: sliding window of recent messages, summarization of old context, or RAG-based retrieval of conversation history. Token counting ensures the context window is not exceeded.

## Agent Loop
1. User input -> LLM (with tools and conversation history)
2. LLM generates response (text or tool call)
3. If text: return to user
4. If tool call: execute tool, append result to messages
5. Repeat from step 1 with updated context
6. Loop terminates when LLM produces text response or reaches max iterations
