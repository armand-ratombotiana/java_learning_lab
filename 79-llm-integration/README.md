# 79 - LLM Integration

A module for integrating Large Language Models into Java applications using various APIs.

## Overview

This module covers direct LLM integration with providers like OpenAI, Anthropic, Ollama, and HuggingFace using Java HTTP clients.

## Key Topics

- **OpenAI API**: GPT-4, GPT-3.5 integration
- **Ollama**: Local LLM deployment
- **Anthropic Claude**: Claude API integration
- **Azure OpenAI**: Enterprise deployment
- **HuggingFace**: Open-source models
- **Streaming**: Server-Sent Events for responses

## Technology Stack

- OkHttp
- Java HttpClient
- Gson for JSON
- SSE for streaming

## Getting Started

```bash
cd 79-llm-integration
mvn compile exec:java
```

## Requirements

- Java 11+
- Maven 3.6+
- API keys for LLM providers

## References

- OpenAI API Docs: https://platform.openai.com/docs
- Ollama: https://ollama.ai/
- Anthropic: https://www.anthropic.com/