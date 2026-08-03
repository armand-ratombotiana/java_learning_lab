# Lab 05: Mock Interview — LLM Agent Frameworks

**Role**: LLM Engineer / GenAI Engineer
**Duration**: 60 minutes
**Focus**: ReAct loop, tool registry, multi-step reasoning, agent observability, tool-call parsing, agent failure modes

---

**Interviewer**: "Walk me through the ReAct pattern as the lab implements it."

**Candidate**: "ReAct interleaves reasoning and action: the agent forms a thought, executes
a tool, observes the result, and loops. The lab's `ReActAgent.run(goal)` holds a
`maxSteps` budget and a `trace` list; each iteration logs the thought, executes either
the search or calculator tool through the registry, records the observation, and
decides the next thought. The key design choice is that the agent never talks to the
LLM mid-loop in the demo — the thoughts are scripted — but the *skeleton* is exactly
the production pattern: in a real deployment the LLM generates the next action from
the accumulated trace, which is why the trace must contain every thought, action, and
observation. The trace is both the agent's memory and its debuggability."

**Interviewer**: "Why a `ToolRegistry` instead of hard-coding tool calls in the agent?"

**Candidate**: "Because tools are a plugin surface. The `Tool` interface — `getName`,
`getDescription`, `execute` — lets the registry hold any number of tools, and
`register`/`get` make the agent code tool-agnostic: the agent asks the registry for a
tool by name and calls `execute(args)`. That mirrors how real frameworks work — the
LLM sees `listDescriptions()` as the prompt's tool list, picks a tool name and
arguments, and the runtime looks it up. New tools ship without touching the agent
loop, and each tool carries its own description, which is exactly the information the
model needs to choose correctly."

**Interviewer**: "The demo runs the agent on 'What is the capital of France?' and it fails —
it ends with 'No answer found.' Can you explain what happened, from the trace?"

**Candidate**: "The trace shows the failure precisely. Step 1's thought is the generic
'I need to find information step by step.', which contains neither 'search' nor
'calculate', so the else branch replaces it with 'I should search for the answer I
need.' Step 2 then tries to parse the query: the code does
`thought.substring(thought.indexOf(\"search\") + 7)`, which assumes the word 'search'
is immediately followed by the query — but the actual thought is 'I should search for
the answer I need.', so it strips seven characters and searches for the phrase 'for
the answer I need.' — which isn't in the `SearchTool.knowledge` map. It gets 'No
results found for: for the answer I need.' and the loop repeats the same broken
pattern until `maxSteps = 5` runs out. Two bugs compounding: the thought vocabulary
doesn't match the parser, and the parser assumes a fixed offset instead of a
structured format."

**Interviewer**: "How would you fix the parsing so the agent actually works?"

**Candidate**: "Make the action format explicit and parse it structurally. Instead of
scanning prose for the word 'search', the thought must be a fixed contract —
`action: tool_name(args)` — and the parser splits on the colon and the parentheses.
My walkthrough implements exactly that: the thought 'search: transformer book price in
dollars' becomes `search(\"transformer book price in dollars\")` with a
`substring(\"search:\".length())` parse, and the agent completes the goal — looking
up the price, then issuing 'calculate: 100 * 0.9', and producing 'The Transformer
book costs 90.0 euros.' in two steps instead of burning five. Production frameworks
formalize the same contract as JSON tool calls, but the lesson is identical: the
model must emit a structured action, and the parser must not guess."

**Interviewer**: "What are the common failure modes of LLM agents, and how do you defend
against each?"

**Candidate**: "Infinite loops — the demo's repeat-until-maxSteps cycle is a mild case —
defended by `maxSteps` budgets, per-action retry caps, and loop detection on repeated
(thought, observation) pairs. Hallucinated tool calls — the model invokes a tool that
doesn't exist or passes nonsense args — defended by validating against the registry
before execution and feeding the validation error back as an observation. Wrong
parameters — same fix: schema validation of arguments. Context overflow — the trace
grows unboundedly, so cap the trace length and summarize old steps. Tool errors —
catch exceptions in `execute` and return them as observations, which is exactly what
the lab's `CalculatorTool` does with its try/catch returning 'ERROR: ...' strings. The
recurring theme: every failure becomes an *observation*, not a crash, so the loop can
recover."

**Interviewer**: "The lab's `CalculatorTool` only handles single '+' expressions and
returns 'ERROR: unsupported expression' otherwise. Is that acceptable in production?"

**Candidate**: "No — a production calculator must handle the real expression grammar
(operator precedence, parentheses, decimals) or use a battle-tested evaluator. But the
lab's design decision is still right: fail with a *structured error message* rather
than throwing, because the error text flows back to the loop as an observation and the
agent can reformulate. My walkthrough extends it minimally — the `*` case in addition
to `+` — and keeps the same contract. The important production rule is that tool
output is untrusted data and must be validated at the boundary, especially if a tool
returns content that gets embedded into the next prompt — that's a prompt-injection
vector via indirect injection, which lab 10 covers."

**Interviewer**: "How do you make an agent observable? What would you log per step?"

**Candidate**: "The lab's `trace` is the right artifact: each step logs the thought, the
exact action with its arguments, the raw observation, and the final answer. In
production you'd persist that trace with a request id, add timestamps and token
counts per step, record which tool took how long, and log the intermediate prompts
sent to the LLM — you need the full transcript to debug why an agent went off the
rails. This connects to lab 14's `RequestTracer` with parent/child spans: an agent run
is a root span with one child span per tool call. You also want cost per run —
tool-call-heavy runs are the expensive ones — and a step budget alert when runs
consume most of `maxSteps`."

**Interviewer**: "Single-agent versus multi-agent — when do you split into multiple agents?"

**Candidate**: "A single agent is simpler: one loop, one context, one trace. It breaks
when the task needs conflicting capabilities — a researcher that must read many
documents and a coder that must hold a long plan — because a single context fills up
and the tool list gets unwieldy. Multi-agent splits by role — planner, researcher,
writer — each with its own context and tool subset, communicating through a shared
task list. The costs: coordination overhead, duplicated infrastructure, and failure
propagation across agents. The lab's `ReActAgent` is the single-agent skeleton; a
multi-agent system is several of these loops wired together, which is why starting
with one well-observed agent and splitting only when the context or tool-list limits
bite is the pragmatic path."

**Interviewer**: "How do you evaluate an agent's quality?"

**Candidate**: "Task success rate on a labeled scenario set — did the agent reach the
correct final answer; that's the headline number. Then process metrics: steps to
completion (fewer is better), tool-call accuracy (did it pick the right tool), success
rate on first attempt versus after retries, and the loop-failure rate — how often did
it exhaust `maxSteps`. Then cost and latency: tokens per run and wall time per run,
because a 'better' agent that costs 5x is not a win. The walkthrough's before/after is
a miniature version of this: the naive parser fails 5 of 5 steps, the fixed agent
succeeds in 2 — measurable, reproducible, and attributable to one code change."

**Interviewer**: "What would you change about this lab's framework to ship it?"

**Candidate**: "Four things. First, structured actions: replace substring parsing with a
formal `action: tool(args)` or JSON contract and schema-validate arguments. Second,
real LLM integration: the thought generation must come from a model call over the
accumulated trace, with the tool descriptions from `listDescriptions()` in the prompt.
Third, safety rails: cap trace length, deduplicate repeated states, validate tool
outputs before they re-enter the context, and gate dangerous tools. Fourth,
observability and evaluation: persist traces with ids, add spans per tool call, and
run a labeled scenario set in CI so every change to the loop or the tool list is
measured. The architecture is right; production is about making the boundaries strict
and the behavior measured."
