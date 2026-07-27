# Mock Interview: Prompt Engineering

## Question 1: Prompt Design Principles
**Q**: Design prompts for specific tasks (summarization, extraction, reasoning). Explain key principles.

**A**: Key principles:
1. **Be specific**: "Summarize the following article in 2-3 sentences focusing on key findings"
2. **Provide examples**: Few-shot prompting for complex tasks
3. **Format output**: "Output as JSON: {"summary": "...", "key_points": [...]}"
4. **Chain-of-thought**: "Let's think step by step" for reasoning tasks
5. **Role assignment**: "You are an expert data scientist..."

```python
# Task: Information extraction
prompt = """Extract company name, revenue, and date from the text.
Format as JSON.

Text: {text}

Output:
{
  "company_name": "...",
  "revenue": "..." ,
  "date": "..."
}"""
```

## Question 2: Chain-of-Thought Prompting
**Q**: Explain chain-of-thought (CoT) prompting. How does it improve reasoning?

**A**: CoT prompts the model to show intermediate reasoning steps before giving the final answer.

**Zero-shot CoT**: Add "Let's think step by step" or "Let's work this out in a step-by-step way"
**Few-shot CoT**: Provide examples with reasoning chains

Example: 
```
Q: If there are 3 cars and each has 4 wheels, how many wheels total?
A: Each car has 4 wheels. There are 3 cars. So total = 3 * 4 = 12. The answer is 12.
```

**Follow-up**: What are limitations of CoT? Increases token usage, works best for models > 70B, can produce plausible but incorrect reasoning.

## Question 3: Structured Output
**Q**: How do you ensure the LLM produces structured, parseable output?

**A**: Techniques:
1. **Explicit format specification**: "Output as JSON with fields: name, age"
2. **JSON mode**: Many APIs support json_object response_format
3. **Function calling**: Define tool/function schemas for structured extraction
4. **Constrained decoding**: grammar-based sampling (e.g., lm-format-enforcer, guidance)
5. **Output parsing**: Use PydanticOutputParser (LangChain) or Instructor library

```python
from pydantic import BaseModel

class ExtractInfo(BaseModel):
    name: str
    age: int
    occupation: str

# Prompt with structured output specification
response = llm.chat(messages=[
    {"role": "user", "content": f"Extract info from: {text}"}
], response_format={"type": "json_object"})
```

## Question 4: Advanced Prompting
**Q**: Compare few-shot, chain-of-thought, and tree-of-thought prompting.

**A**:
| Technique | Description | Best For | Token Cost |
|-----------|-------------|----------|------------|
| Few-shot | Provide examples in prompt | Classification, extraction | Medium |
| CoT | Step-by-step reasoning | Math, logic, multi-step | High |
| Self-consistency | Multiple CoT paths, majority vote | High-stakes reasoning | Very high |
| Tree-of-Thought | Explore multiple reasoning branches | Complex planning | Very high |
| ReAct | Reasoning + Acting (tool use) | Agents, interactive tasks | High |
| Reflexion | Self-evaluate and improve | Code generation, tasks | Very high |

## Question 5: Prompt Optimization
**Q**: How do you systematically optimize prompts?

**A**: 
1. **Prompt versioning**: Track prompt changes with metrics
2. **A/B testing**: Compare prompt variants on eval set
3. **Automated optimization**:
   - DSPy: Programmatic prompt optimization
   - OPRO: LLM-based prompt improvement
   - Autoprompt: Gradient-based prompt search
4. **Metrics-driven**: Define evaluation criteria, iterate
5. **Adversarial testing**: Edge cases, failure analysis
6. **Cost/quality trade-off**: Shorter prompts vs detailed prompts

**Example workflow**: 
- Start with zero-shot -> test on eval set
- Add few-shot examples for edge cases
- Add CoT for reasoning tasks
- Use self-consistency if accuracy critical
- Prune examples that don't help to reduce cost
