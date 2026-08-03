# Lab 04: Mock Interview — AI Agent Frameworks

**Role**: AI Engineer / LLM Engineer
**Duration**: 60 minutes
**Focus**: ReAct loop, tool use, orchestration, multi-step planning, safety, evaluation

---

**Interviewer**: "Walk me through the agent loop in this lab."

**Candidate**: "The core is `ReActAgent`: it interleaves reasoning and acting. The agent
receives a task, calls the model to produce a thought and a tool call, executes the
tool, and feeds the observation back into the loop until it reaches a final answer —
with a step budget so the loop cannot spin forever. Tools implement a common `Tool`
interface — `CalculatorTool`, `SearchTool`, `WeatherTool` — so the agent dispatches by
name and never hardcodes tool logic. `AgentOrchestrator` sits above it: it manages
the agent, the available tools, and the run lifecycle, which is what makes the demo
composable instead of a single hardwired script."

**Interviewer**: "Why interleave reasoning and acting instead of planning first?"

**Candidate**: "A plan-first agent writes a complete plan against assumptions, then
executes it blindly; if step two reveals the world is different than planned, the
rest of the plan is garbage. ReAct recomputes what to do after every observation — the
next step is conditioned on the actual tool result, not on a prediction. The lab
demonstrates this when the agent must chain tools: the output of one call feeds the
input of the next, and the reasoning text explains the choice. For tool-using agents
this feedback is the whole point: the environment is the source of truth, and the
loop is how the agent stays honest with it."

**Interviewer**: "How does the Tool interface keep the agent decoupled?"

**Candidate**: "Every tool exposes a uniform contract — a name, a description of what it
does and its parameters, and an execute method — and the agent dispatches by name.
The decoupling matters for two reasons. First, adding a tool is pure addition: a new
implementation plugs in without touching the agent logic, which is how the lab adds
weather and search alongside calculator. Second, it makes tools inspectable: the
agent's calls are structured, so you can log and audit what was invoked. In
production this same contract becomes the security boundary — tools are the only
place the agent touches the outside world."

**Interviewer**: "How does the agent decide which tool to call?"

**Candidate**: "The model chooses, using the tool descriptions as the signal — that is
why descriptions are part of the prompt and why they must be accurate. The lab's
calls show the pattern: a math task produces a calculator call with arguments, a
lookup task produces a search call. The framework's job is to make the choice
reliable: strict argument schemas so malformed calls fail fast, and an execution
result that comes back as an observation the model can actually reason over. If the
model picks the wrong tool or wrong arguments repeatedly, the fix is usually the
description or the schema, not more prompt scolding."

**Interviewer**: "What happens when a tool call fails?"

**Candidate**: "The failure is converted into an observation and returned to the loop —
the agent sees 'calculator returned an error: division by zero' and can react: reformat
the arguments, pick another tool, or explain the limitation. Crucially, the loop does
not crash; the step budget still applies, and the agent must either recover or
produce a final answer that acknowledges the failure. The lab shows this contract —
tools return structured outcomes, not exceptions the agent never sees. In production
this is also a safety feature: tool failures are logged, and repeated failures should
bounce the task to a human rather than silently looping."

**Interviewer**: "What is the role of AgentOrchestrator versus the ReActAgent itself?"

**Candidate**: "The ReActAgent owns the reasoning loop — thought, action, observation,
step budget. The orchestrator owns everything around it: which tools are registered,
how the agent is instantiated, how the run is executed and how its result is
reported. Separation of concerns: the agent logic stays reusable and testable, while
policy — tool availability per task, budgets, logging — lives in the orchestrator.
In a production system the orchestrator is where you would attach tracing, rate
limits, and human-in-the-loop checkpoints, because none of those belong inside the
reasoning loop."

**Interviewer**: "How does the agent handle a task that needs several tools in sequence?"

**Candidate**: "It chains them through observations: the search result becomes the
input context for the next reasoning step, which may trigger a calculator call on a
number pulled from the search, and so on. Nothing in the loop special-cases the
chain — each step is just another thought/action/observation turn, and the
transcript records the dependency between calls. The failure modes are the
interesting part: a chain can break on a malformed intermediate value, and the agent
must detect that instead of plowing forward with garbage. The lab's transcripts make
these dependencies visible, which is how you debug a long chain."

**Interviewer**: "How would you add memory to this agent?"

**Candidate**: "Two kinds: within-run and across-runs. Within-run memory is the
conversation itself — the ReAct transcript of thoughts, tool calls, and observations
is the working context, and the step budget is what keeps it bounded. Across-run
memory is a store the agent consults between tasks — embeddings of past sessions or
persisted facts — which the lab leaves out deliberately: it is an external concern,
not part of the loop. The production question is never 'does the agent have memory'
but 'what memory, scoped how, and who can see it', because unbounded memory is both a
correctness and a privacy hazard."

**Interviewer**: "What are the safety concerns specific to tool-using agents?"

**Candidate**: "The agent can now cause effects: it can mutate state, call external
services, spend money. The lab's design puts the safety levers in the right places —
tools are the boundary, so every external effect happens through a well-defined,
loggable call; the step budget bounds runaway loops; and the orchestrator scopes
which tools a task may use. Production layers on top: read-only tools by default,
permission prompts for mutations, and human approval for high-impact actions. The
principle: an agent is only as safe as its tool contract plus its call budget — the
model's judgment is not a control."

**Interviewer**: "How do you evaluate an agent?"

**Candidate**: "You evaluate outcomes and process separately. Outcomes: task success
against a benchmark of tasks with known answers — did the calculator task produce the
right number, did the search task land on the right result. Process: how many steps,
how many failed tool calls, did it stay in budget, did it hallucinate a tool result
instead of calling one. The lab's demo output supports the outcome side directly —
the transcript shows the trajectory — and the process side falls out of the same
transcript. The key discipline: agents are stochastic, so evaluation is a batch over
many runs, not a single encouraging demo."

**Interviewer**: "How do you bound an agent's autonomy?"

**Candidate**: "Three budgets: steps, tools, and actions. The step budget caps the loop —
the lab's agent terminates when it either reaches a final answer or exhausts its
steps, so a confused agent fails closed. A tool budget caps the total calls, which
bounds cost and side effects per task. An action budget scopes what the tools can do —
read-only by default, destructive or external effects gated behind approval. All three
are set by the orchestrator per task, not by the model. Autonomy is a dial you turn,
not a property the agent decides for itself."

**Interviewer**: "How do you stop an agent from repeating the same failed action?"

**Candidate**: "The transcript is the memory of what was tried: a loop that calls the
same tool with the same arguments twice is visible in the step history, and the
loop logic can enforce a no-repeat rule — an action identical to a previous one is
blocked or re-planned, and the step budget is the backstop either way. The lab's
structure makes the check possible because tool calls are structured records, not
free text: comparing arguments is exact, not heuristic. In production you add a
novelty budget too — an agent that burns twenty calls on a failing path is a cost
incident even if every call differs slightly, and capping calls per tool or per
outcome class is the same discipline the budget enforces on the loop itself."

**Interviewer**: "What is the most common failure mode for these agents?"

**Candidate**: "The silent loop: the agent takes many tool calls, all of them look
busy, and the final answer is wrong or empty — no error, no refusal, just a fluent
hallucinated completion after expensive work. The lab's countermeasures are the step
budget, which forces termination, and the structured transcript, which makes the
trajectory reviewable: you can see where it went off the rails. The second common
failure is tool-call malformation — arguments that do not fit the schema — which the
lab avoids with strict interfaces. In production, both point to the same rule: agent
behavior must be observable and budgeted, or it is a black box that spends money."
