# Lab 03 — High CPU Production Incident: Interview Questions

**Q1: What is catastrophic backtracking in regex and how does it cause a ReDoS attack?**

**Answer:** Catastrophic backtracking occurs when a regex pattern with nested quantifiers (e.g., `(a|aa|aaa)+`) causes the NFA engine to try an exponential number of alternative paths. Each time the engine fails to match, it backtracks and tries the next combination. With 40 characters, there can be millions of paths. This causes 100% CPU on the evaluating thread for seconds or minutes, effectively a denial-of-service (ReDoS).

**Q2: How do you identify a ReDoS attack using async-profiler?**

**Answer:** Run `profiler.sh -d 60 -e cpu -f flame.html <pid>`. The flame graph will show 85%+ of CPU samples in java.util.regex.Pattern methods (GroupHead.match, Branch.match, Curly.match, etc.). The stacks will be deep with recursive calls, and no other significant CPU consumers will be visible. Compare to a normal flame graph where regex is < 5% of samples.

**Q3: What three defenses should every regex-heavy service implement?**

**Answer:** 1) Input length validation — limit to 1024 characters or less. 2) Regex timeout — wrap regex evaluation in a timeout mechanism (e.g., Future with timeout, or watchdog thread interrupt). 3) Atomic groups / possessive quantifiers — rewrite patterns as `(?>...)` or `*+`/`++`/`?+` to prevent backtracking into already-matched groups.

**Q4: Why does Java's regex engine use NFA instead of DFA?**

**Answer:** NFA engines support advanced features like backreferences, lookahead/lookbehind, capturing groups, and atomic groups. DFA engines (like RE2) process each character exactly once (O(n) time) but cannot support these features. Java's design prioritizes feature completeness over worst-case performance. The trade-off is exponential worst-case time for complex patterns.

**Q5: How do atomic groups prevent catastrophic backtracking?**

**Answer:** An atomic group `(?>...)` tells the regex engine: "once you match this group, commit to it — never backtrack into it." If the engine tries different alternatives within the group and finds a match, it saves state. If the overall pattern later fails, the engine cannot try different alternatives inside the atomic group — it must fail. This reduces the search space from exponential to linear.

**Q6: Design a regex firewall for a content moderation service.**

**Answer:** 1) Input validation layer — max 1024 chars, reject binary/unusual characters. 2) Pre-screening — run input through a fast DFA to detect obvious ReDoS patterns. 3) Regex evaluation — use RE2/J (DFA-based) instead of java.util.regex for non-critical patterns; use java.util.regex with TimeoutPattern wrapper for critical patterns. 4) Rate limiting — per-IP and per-user rate limits on content submission. 5) Thread pool isolation — separate thread pool for regex evaluation to prevent thread pool saturation. 6) Monitoring — CPU per thread, regex evaluation time per request, thread pool queue depth.

**Q7: How would you implement a regex timeout in Java since Pattern doesn't support it natively?**

**Answer:** Create a wrapper method that: 1) Creates a Callable<Boolean> that runs the regex matching. 2) Submits it to a separate thread pool with a timeout (e.g., 100ms, TimeUnit.MILLISECONDS). 3) If timeout occurs, the Future throws TimeoutException. 4) Interrupt the worker thread to stop the regex evaluation (important — regex evaluation respects thread interrupts). 5) Log the timeout event. The implementation requires careful thread management to avoid thread leaks from interrupted matchers.

**Q8: How does a ReDoS attack differ from other DDoS attacks?**

**Answer:** A ReDoS attack is application-layer (Layer 7), targeting the regex engine specifically. It uses a small request (as few as 30 characters) to cause disproportionate CPU consumption. Traditional DDoS attacks use volume (millions of requests) to overwhelm network, connection, or CPU capacity. ReDoS is harder to detect with traditional rate limiting because the request looks valid — only the regex evaluation time differs.

**Q9: Your production CPU spiked to 100% across 500 nodes. Thread dumps show most threads in java.util.regex.Pattern. What do you do?**

**Answer:** 1) Immediately disable the specific policy rule via feature flag. 2) CPU will drop to normal within 1-2 minutes. 3) Identify the attack payload from request logs. 4) Add the payload to blocklist (temporary). 5) Fix the regex pattern with atomic groups. 6) Add input length validation. 7) Add regex timeout wrapper. 8) Add ReDoS vulnerability scanning to CI/CD. 9) Perform post-mortem on detection gaps.

**Q10: What are possessive quantifiers and how do they differ from greedy quantifiers?**

**Answer:** Greedy quantifiers (`*`, `+`, `?`) match as much as possible but give up characters during backtracking. Possessive quantifiers (`*+`, `++`, `?+`) match as much as possible and NEVER give up characters during backtracking. This prevents backtracking into the quantified group, which prevents catastrophic backtracking. Example: `a++b` will never give up characters matched by `a++`, unlike `a+b` which will backtrack.

**Q11: How would you audit 2,147 regex patterns for ReDoS vulnerabilities?**

**Answer:** 1) Automated scanning: run each pattern against known ReDoS payloads (OWASP list + common patterns). 2) Timing test: test each pattern against 1,000 random inputs, flag any that take > 10ms. 3) Static analysis: parse patterns and flag those with nested quantifiers or overlapping alternation. 4) Manual review: two engineers independently review flagged patterns. 5) Documentation: record performance characteristics for each pattern.

**Q12: You notice P99 latency increased from 50ms to 10s but P50 is normal. What does this suggest?**

**Answer:** This suggests a subset of requests are taking very long while most are normal. Classic sign of head-of-line blocking or thread pool saturation. The suspicious requests (long-tail) are likely ReDoS payloads consuming regex threads. Once thread pool saturates, other requests queue up. The P50 is normal because it reflects requests that happen to get a free thread. Fix: isolate regex evaluation to a separate thread pool.

**Q13: Tell me about a time you debugged a high-CPU production issue. (STAR)**

**Answer:** Situation: Content moderation service CPU spiked to 100% across 500 nodes, P99 latency from 50ms to 10s. Task: As the on-call SRE, I needed to identify and mitigate the CPU storm. Action: I ran async-profiler on a sample node and saw 85% of CPU in java.util.regex.Pattern. I correlated with request logs and found one specific policy rule with a ReDoS-vulnerable pattern. I disabled the rule via feature flag — CPU dropped to 30% in 2 minutes. I then rewrote the pattern with atomic groups and added input validation and regex timeout. Result: CPU and latency returned to baseline, no recurrence.

**Q14: How would you set up monitoring to detect ReDoS attacks in real-time?**

**Answer:** 1) Per-thread CPU monitoring — alarm if any thread exceeds 80% CPU for > 10 seconds. 2) Regex evaluation time per request — monitor P99 and max. 3) Thread pool metrics — active threads, queue depth, rejection rate. 4) Pattern-level metrics — track evaluation time per pattern ID. 5) Flame graph capture on CPU alert — trigger async-profiler automatically when CPU > 90%. 6) Synthetic monitoring — send known ReDoS payloads as health checks.

**Q15: Your team owns 2,147 regex patterns. How do you prioritize fixing them for ReDoS vulnerabilities?**

**Answer:** 1) Critical (fix in 24 hours): patterns used in request-processing path with high throughput. 2) High (fix in 1 week): patterns used in request-processing but lower throughput. 3) Medium (fix in 1 month): patterns used in batch/offline processing. 4) Low (fix in 3 months): patterns used in internal/admin tools with low usage. For each pattern, apply the three defenses: input validation, regex timeout, atomic groups. Prioritize by traffic volume and pattern complexity.
