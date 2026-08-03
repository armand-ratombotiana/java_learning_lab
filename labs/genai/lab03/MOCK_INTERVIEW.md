# Lab 03: Mock Interview — Prompt Engineering Patterns

**Role**: LLM Engineer / GenAI Engineer
**Duration**: 60 minutes
**Focus**: prompt templates, few-shot learning, chain-of-thought, structured output parsing, prompt versioning

---

**Interviewer**: "The lab has a `PromptTemplate` that renders `{{variable}}` placeholders.
How is it implemented, and what does it do when a variable is missing?"

**Candidate**: "The constructor scans the template with the regex `\\{\\{(\\w+)}}` and
collects every placeholder into a `variables` list. `render(values)` then walks that
list and calls `values.getOrDefault(var, \"MISSING\")` — so an unsubstituted variable
becomes the literal string `MISSING` rather than staying as `{{text}}` or throwing.
That choice is deliberate: it makes broken prompts *visible* — the word MISSING is easy
to spot in logs and tests — instead of silently passing an unrendered template to the
model. In production I'd add a hard validation pass before any prompt leaves the
service, because a `MISSING` token in a production prompt is a quality incident."

**Interviewer**: "Why is programmatic prompt construction better than pasting strings in a
config file?"

**Candidate**: "Three reasons. First, correctness: templates with explicit variable
extraction can be unit-tested — the lab's `PromptTemplate` discovers its own variables,
so a test can assert every declared variable is always provided. Second, versioning:
`PromptVersion` gives every content string a hash identity, so you can track exactly
which prompt produced which model output; when quality changes, you know the diff. The
demo shows `PromptVersion` hashing "Translate {{text}} to {{lang}}" to id `ffe80e72`
and stamping `createdAt`. Third, scaling: an SDK-style layer lets you inject few-shot
examples, system messages, and structured-output constraints uniformly, instead of
each service hand-rolling strings."

**Interviewer**: "The `FewShotPrompt.build` output appends 'Input: <query>' and ends with
'Output:' with nothing after it. What's the trick there?"

**Candidate**: "You're leaving the completion open so the model continues exactly where the
examples left off. Each example is an `Input:`/`Output:` pair, and the final line has
the new input with `Output:` dangling — the model's job is to complete that last line
in the same format. This is the classic few-shot structure: the model imitates the
pattern rather than having the format explained. The lab stores examples as
`Map.entry(input, output)` pairs added with `addExample`, and `build` numbers them
'Example 1', 'Example 2' in order. One production subtlety: the examples must be
representative of the real distribution and ordered so the most relevant ones come
last, because recent examples influence output more than early ones."

**Interviewer**: "How does chain-of-thought differ from few-shot, and when would you use
`CoTPrompt.build`?"

**Candidate**: "Few-shot teaches *format* through examples; CoT teaches *process* by asking
for intermediate reasoning before the final answer. The lab's `CoTPrompt.build` is
minimal — it appends 'Let's think step by step.' to the question — and `extractAnswer`
takes the last line of the response as the answer, which is the standard assumption
that the model puts its final answer last. CoT shines on arithmetic, logic, and
multi-step reasoning where a single-shot answer is error-prone; it's a prompt-level
technique that can be folded into few-shot too. The cost is tokens — reasoning traces
are verbose — and on tasks that don't need reasoning it adds latency and can
overthink simple questions."

**Interviewer**: "The lab's `StructuredParser.parseJson` is a regex over
`\"(\\w+)\"\\s*:\\s*\"([^\"]+)\"`. What are its limitations in production?"

**Candidate**: "It's a *toy* JSON parser: it only matches string values, one level deep,
with no whitespace tolerance beyond `\\s*` around the colon, no numbers, booleans,
arrays, or nesting — the demo response `{\"name\": \"Alice\", \"role\": \"engineer\"}`
parses fine to `{name=Alice, role=engineer}`, but a real model output with a newline
inside a value or a nested object would silently drop fields. Production pipelines
need a real JSON parser plus schema validation, and — increasingly — constrained
decoding, where the model's token probabilities are restricted to valid JSON grammar,
so invalid output is impossible rather than detected. The lab's parser is right as a
*pattern*: parse, validate, extract, and fail loudly on missing keys."

**Interviewer**: "What is the purpose of `PromptVersion` and hash-based identity
specifically?"

**Candidate**: "Hash-based identity gives you deduplication and diffing for free: identical
content always produces the same id, so storing a hundred identical templates collapses
to one row, and any edit produces a new id — the walkthrough shows the same content
yielding `63e826f0` twice while a one-phrase edit yields `8c2365d1`. Combined with
`createdAt`, you get a linear history: every prompt that ever produced output can be
looked up by version id, which is the foundation for regression testing — run the
golden eval set against the new version, compare scores, promote or roll back. The
hash is content-addressed, so you can also verify no one silently edited a prompt in
the database after release."

**Interviewer**: "Walk me through how you'd A/B test two prompt templates in production."

**Candidate**: "Same rigor as model A/B testing: define the metric first — exact match on
structured tasks, rubric or LLM-as-judge on open-ended ones — then split traffic by
stable user or request hash so one user isn't flip-flopping between templates, run
both versions long enough to reach significance, and gate promotion on both the quality
delta and the cost delta, since a 'better' prompt that costs 2x tokens may not win.
Version tracking is non-negotiable: every sample logged must carry its `PromptVersion`
id so the analysis attributes results to the exact content, not to 'the prompt'."
That's exactly what the lab's versioning machinery enables."

**Interviewer**: "How does prompt injection interact with template-based prompts?"

**Candidate**: "The `{{variable}}` substitution is the attack surface: user text is dropped
into a slot, and if the user text contains 'Ignore previous instructions' or its own
instructions, the model may follow the injected text instead of the template. The
lab's template engine is a great illustration because the boundary between the fixed
template and the variable content is explicit in the code — which is the correct
mindset. Mitigations: treat user content as data, delimit it clearly (quoted,
tagged), validate it against injection patterns like lab 10's `InjectionDetector`
regexes, and never concatenate untrusted text into privileged instruction blocks.
For sensitive flows, constrain decoding or use a separate instruction-following model
as a classifier."

**Interviewer**: "How do you decide between zero-shot, few-shot, and CoT for a new task?"

**Candidate**: "Start zero-shot with a precise instruction — it's cheapest in tokens and
fast to iterate. If accuracy is below target, add few-shot examples: pick 2-5
representative pairs that cover the common failure modes, and validate on a held-out
set because more examples aren't always better — they burn context and can introduce
noise, as the INTERVIEW guide notes. If the task is multi-step reasoning, add CoT and
measure whether the trace actually improves the final answer; if it does, consider
distilling the reasoning into the model via fine-tuning (lab 06 territory) so you get
the quality without the per-call token cost. Always measure both quality and token
cost — prompt engineering is a cost-quality optimization, not just accuracy."

**Interviewer**: "The walkthrough scores templates structurally — instruction present,
query present, examples, reasoning trigger. Why structural scoring instead of real
model evaluation?"

**Candidate**: "Structural checks are the cheap, deterministic gate that runs in CI on every
commit, catching rendering bugs — unrendered `{{text}}`, missing examples, truncated
prompts — before any expensive model evaluation. My `PromptRegistry` gives plain a
score of 2/4 — it has the instruction and the query but no examples and no reasoning
trigger — while few-shot gets 3/4. Then, on top of the structural gate, you run the
real evaluation: golden set, human labels, and cost accounting. The layered approach
matters because model evaluation is slow and noisy; you want it only for prompts that
are already structurally sound. A production registry runs both layers in sequence."

**Interviewer**: "The few-shot prompt costs 34 tokens versus 13 for the plain template.
When does that extra cost pay off?"

**Candidate**: "When format fidelity is the dominant failure mode — classification into a
fixed label set, JSON schema adherence, translation style. The examples are teaching
behavior the instruction alone doesn't convey, and on high-value, low-volume traffic
like agent tool-calling or extraction, the extra tokens are trivial compared to the
cost of a malformed output going downstream. But for high-volume, simple tasks —
sentiment on a support ticket, topic tagging — 2.6x token cost compounds, and the
savings path is: prove the examples help, then fine-tune a small model on those exact
example pairs so inference drops back to zero-shot pricing. That's the lifecycle
prompt engineering feeds into."

**Interviewer**: "If you could change one thing about this lab's prompt framework for
production, what would it be?"

**Candidate**: "Real typed validation on every boundary. `PromptTemplate.render` uses
`getOrDefault` with a `MISSING` sentinel — fine for demos, but production needs
fail-fast: missing variables should block deployment. `StructuredParser` should be a
schema validator that rejects responses that don't conform, not a lenient regex.
And `PromptVersion` should record provenance — who changed it, why, which eval it
passed — because a hash id alone tells you a prompt changed, not whether it was a
sanctioned change. The pieces are all here; production is about making each boundary
strict instead of lenient."
