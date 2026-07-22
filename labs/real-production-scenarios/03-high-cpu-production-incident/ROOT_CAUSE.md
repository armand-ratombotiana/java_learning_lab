# Root Cause Analysis: ReDoS-Induced High CPU in Content Moderation

**Incident**: INC-2024-0610-REDOS
**Analyst**: Content AI Team + SRE
**Date of Analysis**: June 11, 2024
**Method**: async-profiler flame graphs, JFR analysis, regex pattern audit

## Executive Summary

The content moderation service experienced global P99 latency spikes from 50ms to 10s due to a Regular Expression Denial of Service (ReDoS) attack. A single user post with a crafted text payload (approximately 40 characters) triggered catastrophic backtracking in Java's NFA-based regex engine when evaluated against a content policy regex pattern. The pattern contained nested quantifiers and alternation, causing an exponential number of backtracking paths. A single request consumed 100% CPU on a thread for 30+ seconds. With a 200-thread thread pool, only 4-5 concurrent ReDoS requests were needed to saturate all threads, causing queue buildup and 10s+ latency for all legitimate requests.

## async-profiler Evidence

The flame graph captured during the incident showed:

```
java.util.regex.Pattern$GroupHead.match  (35% of CPU)
  └─ java.util.regex.Pattern$Branch.match  (28% of CPU)
      └─ java.util.regex.Pattern$GroupTail.match  (12% of CPU)
          └─ java.util.regex.Pattern$Curly.match  (10% of CPU)
```

85% of CPU time was spent inside java.util.regex.Pattern matching methods, confirming the regex engine was the bottleneck.

## The 5 Whys Analysis

### Why 1: Why did CPU spike to 99% globally?

A single regex pattern (content policy rule #1427) consumed 85% of CPU time when evaluated against specific inputs. The java.util.regex.Pattern engine uses an NFA (Nondeterministic Finite Automaton) with backtracking. When the pattern has nested quantifiers and alternation, the backtracking algorithm can explore an exponential number of paths — a condition known as catastrophic backtracking.

The attack input was approximately 40 characters of carefully crafted text that caused the regex engine to explore millions of backtracking paths before determining that no match existed. Each path involved stack operations, character comparisons, and group state management — all CPU-intensive operations in Java.

### Why 2: Why did the regex pattern have catastrophic backtracking?

The vulnerable pattern was:

```
^(?=.*[A-Z])(?=.*[!@#$%^&*()])(?=.*[0-9])((?=.*[a-z])|(?=.*upper_seq))(?=.*(.)\\1{2,}).{8,40}$
```

The critical section was `((?=.*[a-z])|(?=.*upper_seq))`. This is a alternation group inside a larger pattern. When the regex engine tries to match this pattern against input that fails overall, it backtracks through every possible grouping of the alternation across different positions in the input.

The simplified version of the vulnerability:

```java
// Pattern with catastrophic backtracking
Pattern pattern = Pattern.compile("(a|aa|aaa|aaaa)+b");
String input = "aaaaaaaaac"; // Fails overall, but engine backtracks exponentially
```

For this pattern, Java's regex engine tries:
- Treat "a" as the group, match remaining "aaaaaaaa" against `(a|aa|aaa|aaaa)+`
- After failing at 'c', backtrack to try "aa" as the group
- Then "aaa", "aaaa", and all combinations
- Number of paths: O(2^n) where n is the number of 'a' characters

With 40 'a' characters: 2^40 ≈ 1 trillion paths — an impractical computation.

### Why 3: Why was there no protection against ReDoS?

Three protections were absent:

1. **No input length validation**: The service accepted inputs up to 64KB. While the attack payload was only ~40 characters, even an input of 40 characters could trigger exponential backtracking. A simpler defense — limiting input to 1024 characters — would not have stopped this specific attack, but would prevent larger payloads.

2. **No regex timeout**: Java's Pattern.matches() has no built-in timeout. Once started, a regex evaluation runs until completion, even if it takes 30+ seconds. A timeout mechanism (interrupting the matching thread) was not implemented.

3. **No ReDoS testing in CI/CD**: Regex patterns were added to the policy rule database without automated testing for catastrophic backtracking. Tools like ReScue, rxxr2, or custom fuzzing could have identified the vulnerable pattern.

### Why 4: Why did 4-5 concurrent requests saturate the entire service?

The content moderation service used a fixed-size thread pool of 200 threads per node. Each request that triggered the ReDoS would consume one thread at 100% CPU for 30+ seconds. With 200 threads, only 5-6 concurrent ReDoS requests could keep all threads busy (accounting for normal requests completing quickly).

During the incident:
- Attack requests came in at ~10 requests/second (easily achievable from a single client)
- Each attack request held a thread for 30 seconds → 300 "thread-seconds" of consumption per second of attack
- All 200 threads were occupied within seconds
- Normal requests queued up behind the stuck threads

The thread pool acted as an amplifier: a small number of attack requests consumed all threads, creating a denial of service for all users.

### Why 5: Why wasn't this caught in code review or testing?

1. **Regex expertise gap**: The engineer who wrote the regex pattern was not familiar with NFA backtracking behavior. The pattern appeared correct for all tested inputs.

2. **No ReDoS scanning**: The CI/CD pipeline did not include regex vulnerability scanning. Tools like RXXR2 or custom fuzzers can detect catastrophic backtracking patterns automatically.

3. **Performance testing gap**: Load tests used synthetic data that did not include ReDoS exploit payloads. Performance tests focused on throughput under normal conditions, not adversarial inputs.

4. **No regex pattern review**: The policy rule database had 2,000+ regex patterns, added over 5 years. No systematic review process existed for regex quality or security.

## Catastrophic Backtracking Mechanics

```java
// Pattern: (a|aa|aaa|aaaa)+b
// Input: "aaaaaaaaac"

// JVM NFA backtracking engine behavior:
Step 1:  Try group(1) = "a"  → remaining: "aaaaaaaac" → try + → group(1) = "a" ...
         ... after consuming all a's with group(1)="a":
         Match 'b' against 'c' → FAIL
         Backtrack: last iteration was group(1)="a", try group(1)="aa"

Step 2:  group(1) = "aa" → remaining: "aaaaaaac" → try + → group(1) = "a" ...
         ... eventually fail on 'b' vs 'c'
         Backtrack again...

... exponential backtracking through all possible groupings ...
```

## Full Regex Pattern Breakdown

The vulnerable pattern decomposes into these components:

```
^                     — Start of string anchor
(?=.*[A-Z])           — Lookahead: at least one uppercase letter
(?=.*[!@#$%^&*()])    — Lookahead: at least one special character
(?=.*[0-9])           — Lookahead: at least one digit
(                     — Group start (VULNERABLE)
  (?=.*[a-z])         — Alternation branch 1: lowercase letter
  |                   — OR  
  (?=.*upper_seq)     — Alternation branch 2: sequence pattern
)                     — Group end
(?=.*(.)\1{2,})       — Lookahead: character repeated 3+ times
.{8,40}               — Length: 8 to 40 characters
$                     — End of string anchor
```

The problematic section is `((?=.*[a-z])|(?=.*upper_seq))`. This alternation:

1. Is wrapped in a capturing group AND nested inside a larger pattern
2. Both branches are lookaheads, but the group itself is not atomic
3. When the overall pattern fails (e.g., no repeated char), the engine backtracks through the alternation branches at every position in the input
4. Since the branches overlap (both can match at many positions), the backtracking explores all combinations

## Detailed Catastrophic Backtracking Walkthrough

For a worked example with a simplified pattern `(a|aa|aaa|aaaa)+b` and input "aaaaaaaaac":

```
Engine starts at position 0:
  Try group iteration 1 with "a" (1 char) → match, position = 1
  Try group iteration 2 with "a" (1 char) → match, position = 2
  ... (repeat until position = 10)
  Try group iteration 10 with "a" (1 char) → match, position = 10
  Try literal 'b' at position 10 → character is 'c' → FAIL
  Backtrack: undo iteration 10, try "aa" at position 9
    'c' at position 9 → "aa" needs 2 chars, only 1 available → FAIL
  Backtrack: undo iteration 9, try "aa" at position 8
    Need "a" for iteration 9 (re-checking)...
  This continues exponentially through all grouping combinations
```

With atomic group `(?>a|aa|aaa|aaaa)+b`:

```
Engine starts at position 0:
  Try "a" → matches 1 char → COMMIT (never retry other options)
  Try "a" → matches 1 char → COMMIT
  ... (repeat)
  Try literal 'b' at position 10 → character is 'c' → FAIL
  Pattern fails → done (no backtracking into atomic group)
```

## Contribution of Each Defense Gap

| Gap | How it Contributed | Severity |
|-----|-------------------|----------|
| Vulnerable regex | The primary cause — pattern had exponential backtracking | Root cause |
| No input length validation | Longer inputs (up to 64KB) would have made it worse, but attack payload was only 37 chars | Moderate |
| No regex timeout | Without timeout, regex ran for 30+ seconds on a single evaluation | High |
| Fixed thread pool | 5 concurrent attacks saturated 200 threads | Amplifier |
| No ReDoS testing | Pattern was not tested for vulnerability before deployment | Contributing |
| No regex code review | No expert review of regex for performance issues | Contributing |

## Technical Validation of Root Cause

The root cause was validated through:

1. **Flame graph analysis**: 85% of CPU samples in java.util.regex.Pattern methods, specifically GroupHead, Branch, and Curly
2. **Local reproduction**: The exact attack payload caused 30+ seconds of CPU on a local test
3. **Pattern analysis**: The regex had nested quantifiers with overlapping alternation — a textbook ReDoS pattern
4. **Fix verification**: After applying atomic groups, the same payload completed in < 2ms
5. **Regression testing**: All 2,147 existing patterns were tested — 23 additional vulnerable patterns found

### Catastrophic Backtracking Breakdown

For the specific pattern in this incident, the backtracking tree looks like:

```
Input: 37 characters, all matching first three lookaheads
                   ↓
        Alternation at position 0: 2 branches
                   ↓
        Alternation at position 1: 2 branches
                   ↓
        ... (repeated for each position where lookahead matches)
                   ↓
        Total paths: O(2^n) where n ≈ 20 matching positions
                   ↓
        For n=20: ~1,048,576 paths (evaluated in ~30 seconds)
```

With the atomic group fix, the alternation is evaluated once and committed, reducing the paths from O(2^n) to O(n).

### JVM Internals During ReDoS

During catastrophic backtracking, the JVM's behavior is characteristic:

1. **Stack usage**: Deep recursion in Pattern.match methods, approaching but not exceeding the default stack depth
2. **CPU utilization**: 100% on the affected thread, purely in user space (no syscalls)
3. **GC activity**: Minimal (regex evaluation is compute-bound, not allocation-bound)
4. **Memory allocation**: Low (the engine reuses internal data structures)
5. **Thread state**: RUNNABLE (not BLOCKED, not WAITING)

These characteristics make ReDoS distinct from I/O-bound hangs, lock contention, or GC thrashing.

## Regex Vulnerability Patterns

The following regex constructs are most vulnerable to ReDoS:

1. **Nested quantifiers**: `(a+)+b` — exponential backtracking
2. **Overlapping alternations**: `(a|aa|aaa)*b` — polynomial/exponential backtracking
3. **Alternation inside repeated group**: `(\d+|[a-z]+)+` — exponential with crafted input
4. **Optional groups before required**: `(a)?b` — backtracking through optional groups
5. **Lookahead inside quantifier**: `(?=a)+b` — lookaheads evaluated at every position
6. **Capturing groups inside repetition**: `(a|b)*$` — engine explores all group match combinations

## Root Cause Validation Summary

The root cause was validated through:
1. Flame graph: 85% CPU in Pattern.match, GroupHead, Branch methods
2. Local reproduction: attack payload causes 30s+ CPU with original pattern
3. Pattern analysis: nested quantifiers with overlapping alternation = exponential backtracking
4. Fix verification: atomic groups reduce evaluation from 30s to < 2ms
5. Regression testing: 23 additional vulnerable patterns found and fixed

## Alternative Mitigations Considered

| Mitigation | Pros | Cons | Chosen? |
|------------|------|------|---------|
| Switch to RE2/J library | DFA-based, no ReDoS | No backreferences or lookahead support | No |
| Add regex timeout only | Quick to implement | Does not fix root cause | Partial |
| Atomic group fix only | Fixes this specific pattern | Other patterns may still be vulnerable | Yes |
| Input length limit only | Easy to deploy | Does not stop 37-char attack | Yes |
| Switch to substring search | Avoids regex entirely | May not match all patterns | No |
| Rate limiting on endpoint | Limits attack scale | Does not fix underlying vulnerability | Yes (additive) |

## Related OWASP References

| Reference | Description |
|-----------|-------------|
| OWASP ReDoS Guide | Regular Expression Denial of Service (ReDoS) |
| OWASP Input Validation | Validate and sanitize all regex input |
| OWASP Regex Repository | Pre-validated patterns for common use cases |
| CWE-1333 | Inefficient Regular Expression Complexity |
| CWE-400 | Uncontrolled Resource Consumption |

## Defense in Depth Verification

After applying all fixes, the following layers protect against ReDoS:

| Layer | Defense | Bypass Scenario | Residual Risk |
|-------|---------|-----------------|---------------|
| 1 | Input length limit (1000 chars) | Attack with short payload | Medium |
| 2 | Atomic group rewrite | New non-atomic pattern added | Low (code review) |
| 3 | Regex timeout (2s) | Pattern timeout → 500 error | Low (better than hang) |
| 4 | Vulnerability scanning in CI | False negative | Very low |
| 5 | WAF rate limiting | DDoS bypassing WAF | Very low |

The defense in depth ensures that even if one layer fails, subsequent layers provide protection.

## ReDoS Pattern Statistics

| Statistic | Value |
|-----------|-------|
| Total patterns audited | 2,147 |
| Vulnerable patterns found | 23 |
| Vulnerability rate | 1.07% |
| Patterns with overlapping alternation | 14 |
| Patterns with nested quantifiers | 7 |
| Patterns with lookahead in quantifier | 2 |
| Average fix time per pattern | 12 minutes |
| Total fix time | 276 minutes |

