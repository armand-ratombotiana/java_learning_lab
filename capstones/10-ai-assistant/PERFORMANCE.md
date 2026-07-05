# Performance: AI Assistant

## Throughput Targets
- First token latency: < 500ms (TTFB)
- Tool execution latency: < 2s (depending on tool)
- End-to-end response: < 10s (for typical 3-turn agent loop)
- Concurrent sessions: 100+ per serving instance

## Bottlenecks
- **LLM API latency**: Dominates response time. Use faster models (GPT-4o-mini vs GPT-4), local models (vLLM with quantization).
- **Token generation speed**: depends on model size. Small models: 100+ tok/s. Large: 20-50 tok/s.
- **Network latency**: Between assistant server and LLM API. Co-locate in same region.
- **Tool execution**: External API calls (weather, search) add latency. Cache results with short TTL.
- **Context window processing**: Large conversation histories increase processing time.

## Optimization Strategies
- Stream LLM responses (SSE) for better perceived performance
- Cache tool results for idempotent tools (calculator: same expression -> same result)
- Use local LLM (vLLM with AWQ quantization) for low-latency inference
- Parallel tool execution for independent tool calls
- Pre-process and cache common queries
- Optimize token counting with pre-computed embedding estimates
