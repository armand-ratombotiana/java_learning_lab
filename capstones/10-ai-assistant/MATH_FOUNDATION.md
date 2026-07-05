# Math Foundation: AI Assistant

## Token Counting
- 1 token ~= 0.75 words (English)
- Context window sizes: 4K (GPT-3.5), 8K-128K (GPT-4), 100K (Claude), 1M (Gemini)
- Token counting is critical to stay within context window and manage costs

## Cost Estimation
- Cost = input_tokens * price_per_input_token + output_tokens * price_per_output_token
- Example: GPT-4: $10/1M input, $30/1M output tokens
- A typical conversation (10 turns): ~2000 input, ~500 output = $0.035

## Sampling Parameters
- Temperature: 0.0 (deterministic) to 2.0 (creative). Default: 0.7
- top_p (nucleus sampling): cumulative probability threshold. Default: 0.9
- top_k: only sample from top K tokens. Default: 40

## Context Window Management
- Sliding window: keep last N messages (N = floor(context_window / avg_tokens_per_message))
- Token truncation: oldest messages dropped first
- Summary compression: summarize old messages, replace with summary
