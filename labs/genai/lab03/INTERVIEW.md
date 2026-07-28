# Lab 03: Interview Questions

## Q1: What is chain-of-thought prompting and when is it effective?
**A:** CoT adds intermediate reasoning steps. Effective for arithmetic, logic, and multi-step reasoning tasks. Improves performance on tasks requiring sequential reasoning.

## Q2: How does few-shot prompting differ from zero-shot?
**A:** Few-shot provides 2-5 example pairs in the prompt; zero-shot gives only the instruction. Few-shot generally improves accuracy but uses more tokens.

## Q3: What is prompt injection and how do you mitigate it?
**A:** Prompt injection is when user input overrides system instructions. Mitigations: input sanitization, delimiter-based separation, instruction hardening.

## Q4: How do you choose the number of few-shot examples?
**A:** Typically 2-5. Too few may not establish the pattern; too many exceed context window or introduce noise. Use validation set performance to tune.

## Q5: Explain structured output parsing and why it matters.
**A:** LLMs output free text. Structured parsing (JSON/XML schemas) enables reliable downstream processing, validation, and integration with type-safe systems.
