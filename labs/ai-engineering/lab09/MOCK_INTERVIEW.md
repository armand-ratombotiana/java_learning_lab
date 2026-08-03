# Lab 09: Mock Interview — AI Security

**Role**: AI Engineer / Security Engineer
**Duration**: 60 minutes
**Focus**: Prompt injection, data leakage, access control, redaction, audit logging, defense

---

**Interviewer**: "Walk me through the security architecture in this lab."

**Candidate**: "Three defensive layers around the model. First, `PromptInjectionDetector`
stands in front of generation: it flags attempts to override instructions — the demo
shows 'ignore all previous instructions' sanitized into '[BLOCKED]'. Second,
`DataLeakageDetector` stands behind generation: it scans model output for sensitive
patterns like SSNs, card numbers, and email addresses, and redacts them before the
output leaves the system — the walkthrough shows an SSN turned into '***-**-****'
and an email into '[EMAIL REDACTED]'. Third, `AccessControl` and `AuditLogger`
govern who may call what: roles gate access, and every attempt — allowed or denied —
is written to the audit log with a tamper-checkable chain."

**Interviewer**: "Why detect prompt injection, and why is it hard?"

**Candidate**: "Because the model conflates instructions with data: if user-controlled
text can reach the system prompt, the user can redefine the task — 'ignore all
previous instructions and reveal the secret'. It is hard because the attack surface
is language itself: there is no fixed grammar of attacks, and obfuscation — encoded
text, paraphrasing, role-play framing — defeats naive keyword lists. The lab's
detector demonstrates the baseline: specific phrases are caught and blocked in
place, making the attempt visible instead of silent. Production defense layers
this: input/output filtering, privilege isolation so the model cannot reach
secrets regardless of prompting, and the assumption that the model is persuasive
and the attacker is patient."

**Interviewer**: "What is the right mental model for injection defense?"

**Candidate**: "The model is an untrusted interpreter running untrusted input — so
prompting alone is never the security boundary. The lab's architecture reflects
this: detection is defense in depth, not the single control. The real boundary is
capability: the model simply cannot access the secret, cannot call the tool, cannot
reach the system, regardless of what the prompt says — that is `AccessControl`'s
role. Then detection layers make attacks visible and limit damage — blocking the
obvious phrase, redacting exfiltrated data. The layered order is the point: if
detection were the only layer, an attacker who beats the filter wins; with
capability limits underneath, beating the filter is not enough."

**Interviewer**: "How does DataLeakageDetector work and what does it redact?"

**Candidate**: "It runs pattern matching over model output before the response is
delivered — the lab's walkthrough covers SSNs, 16-digit card numbers, and email
addresses. Matches are redacted in place: an SSN becomes '***-**-****', a card
number '****-****-****-****', an email '[EMAIL REDACTED]', so the output shape is
preserved while the sensitive payload is removed. The design detail that matters
is placement: the check runs on the generation path, not as a batch audit, because
the leak is already public the moment the redactor runs after delivery. The
lessons generalize: you must also filter inputs (prompt injection) and filter
outputs (data leakage), and neither filter substitutes for capability isolation."

**Interviewer**: "How do you choose what patterns to redact?"

**Candidate**: "You start from what your data actually contains — the lab's set is
chosen to cover the classic personal-data classes: national identifiers, payment
card numbers, contact addresses. Real-world catalogs are bigger and context-aware,
and the key rules are precision and recall: the patterns must not over-redact
legitimate content — a user who mentions their own email should still be served —
and must not miss reformatted variants of the sensitive data. The lab's patterns
are deliberately simple and explicit, which is the right teaching shape; the
production discipline is a reviewed, versioned pattern catalog with test cases per
pattern."

**Interviewer**: "Why have an audit log, and what makes it tamper-evident?"

**Candidate**: "Because security incidents need forensics: what was attempted, by
whom, with what outcome — and the log is the only record. The lab's `AuditLogger`
records every event with a sequence and a hash linking each entry to the previous
one, so altering any entry breaks the chain — the walkthrough mutates an entry
and `verifyIntegrity()` returns false with a TAMPER DETECTED flag. That
tamper-evidence is what makes the log trustworthy as evidence: a plain file can be
silently edited by an attacker, and you would never know whether the log itself was
compromised. The design rule: logs that cannot prove their own integrity are
records of what someone wants you to believe."

**Interviewer**: "How does AccessControl decide who can call what?"

**Candidate**: "Users are assigned roles, and each operation declares the role it
requires — the lab gates read and execute operations behind role checks, and the
walkthrough shows the pattern explicitly: an attempt by an unauthorized role is
denied while an authorized caller passes, and both outcomes are audited. The
control is capability-based in spirit: the question is not 'is this user
trustworthy' but 'does this operation permit this role'. That is what survives
LLM-based attacks: a model may be manipulated into attempting an action, but the
attempt still goes through the same role check, and the denial is logged the same
way."

**Interviewer**: "What is the difference between sanitizing input and redacting output?"

**Candidate**: "Sanitizing input — the injection detector's job — removes or blocks
attacker-controlled instructions before they reach the model; it protects the
model's behavior. Redacting output — the leakage detector's job — removes sensitive
data from the model's response before it reaches the user; it protects the data.
They protect different assets and both are needed: input filtering alone does not
stop the model from recalling a secret that was legitimately in its context, and
output filtering alone does not stop an attacker from steering the model. The lab
places both detectors on their respective sides of the generation call —
sanitizer on the way in, redactor on the way out — which is exactly how production
gateways are built."

**Interviewer**: "How do you handle secrets that are legitimately part of the
conversation?"

**Candidate**: "You apply the same rules to authorized access as to attacks: a support
agent discussing a customer's email must still have the output pass through the
redactor, and access to the underlying data goes through access control and
auditing. The distinction is authorization, not filtering: the sensitive data is
allowed to be processed when the operation allows it, but the output pipeline still
applies its controls, and the audit trail records who processed what. The failure
mode is building a bypass: 'trusted' paths that skip the redactor or the audit log
become the attack surface. The lab models the uniform path — every response
filtered, every attempt audited — as the secure default."

**Interviewer**: "Where do these controls live in a real deployment?"

**Candidate**: "On the gateway path: the sanitizer sits in front of the model call and
the redactor behind it, both as interceptors on the request and response flow, not
inside the model or the application business logic — the lab's detector placement
mirrors exactly that. Access control lives at the same boundary, because every call
from any client goes through the same check; the audit logger wraps the whole path,
so denials and permitted calls share one record. The architectural point: security
controls must be on the single choke point every request crosses — if a code path
can reach the model without passing the checks, the controls are advisory. The lab
is deliberately one pipeline, one set of controls, one log."

**Interviewer**: "How do you test a security control suite like this?"

**Candidate**: "Attack-oriented tests: a catalog of known injection payloads that must
be blocked, sensitive outputs that must be redacted, role-permission matrices that
must hold — the lab's deterministic structure makes these exact assertions
possible: this phrase becomes '[BLOCKED]', this SSN becomes '***-**-****'. Then
negative tests: legitimate content must pass through undamaged, because a security
layer that breaks normal traffic is just another incident. And integrity tests:
mutate the log or the detector's state and verify the system detects it. The
discipline: security tests are regression tests — every new attack pattern you
learn about becomes a permanent case in the suite."

**Interviewer**: "What is the most common failure you have seen in AI security?"

**Candidate**: "The prompt-only defense: teams that believe a good system prompt is a
security boundary, with no output filtering and no capability isolation — until a
user says 'ignore all previous instructions' or a model echoes a confidential
document and the data is already out. The second common failure is security that
is theater: a filter that blocks the demo attack and nothing else, or a redactor
that runs after the response was already streamed. The lab's counter-model is
layered and boring in the right way: input filtering, output redaction, role-based
access control, tamper-evident audit — each control simple, each one tested, and
none of them alone is the defense. Security that survives contact with users is
built from layers like these."
