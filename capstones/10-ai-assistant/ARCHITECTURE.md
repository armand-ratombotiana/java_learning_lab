# Architecture: AI Assistant

## High-Level Architecture
```
[Web UI / Slack / API Client]
         |
    [REST API / WebSocket]
         |
    [ChatService]
         |
    [AgentExecutor] ------> [LLM Client] ------> [OpenAI / Anthropic / vLLM]
         |                        |
    [ToolRegistry]         [Streaming SSE]
         |
    [Calculator] [WebSearch] [SearchDocs] [SendEmail]
         |
    [ConversationManager]
         |
    [Memory Store (Redis/PostgreSQL)]
```

## Technology Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.x + WebFlux (SSE streaming)
- **Build**: Maven
- **LLM Providers**: OpenAI API, Anthropic API, vLLM (local)
- **Tool Schema**: JSON Schema (via NetworkNT)
- **Conversation Store**: PostgreSQL + Redis (session cache)
- **Streaming**: Server-Sent Events (SSE) via Spring WebFlux
- **Containerization**: Docker + docker-compose
- **Monitoring**: Micrometer + Prometheus (token usage, latency, tool call frequency)

## API Endpoints
- `POST /api/v1/chat` — Send message (non-streaming)
- `GET /api/v1/chat/stream` — Send message (SSE streaming)
- `GET /api/v1/conversations/{id}` — Get conversation history
- `DELETE /api/v1/conversations/{id}` — Clear conversation
- `GET /api/v1/tools` — List available tools
- `POST /api/v1/tools/register` — Register a new tool (admin)
