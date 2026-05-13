# AI Assistant - Portfolio Capstone

## Overview
Multi-agent AI assistant with RAG integration, tool calling, long-term memory, and conversation management.

## Architecture
```
┌─────────────────────────────────────────────────────────────────┐
│                    AI Assistant Platform                         │
│                                                               │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐       │
│  │   User     │────▶│   Agent     │────▶│   Planner   │       │
│  │  Message   │     │   Router    │     │             │       │
│  └─────────────┘     └──────┬──────┘     └──────┬──────┘       │
│                            │                    │              │
│       ┌────────────────────┼────────────────────┘              │
│       │                    │                                   │
│       ▼                    ▼                    ▼              │
│  ┌─────────┐         ┌─────────┐         ┌─────────┐         │
│  │   RAG   │         │  Tools  │         │ Memory  │         │
│  │ Module  │         │ Executor│         │ Manager │         │
│  └─────────┘         └─────────┘         └─────────┘         │
│       │                    │                    │              │
│       └────────────────────┼────────────────────┘              │
│                            ▼                                   │
│                    ┌─────────────┐                             │
│                    │   LLM       │                             │
│                    │  Response   │                             │
│                    └─────────────┘                             │
└─────────────────────────────────────────────────────────────────┘
```

## Features
- Multi-agent orchestration
- RAG with multiple data sources
- Tool calling (web search, calculator, etc.)
- Long-term and short-term memory
- Conversation history
- Streaming responses
- Context management

## Quick Start
```bash
cd 10-ai-assistant
docker-compose up -d
```