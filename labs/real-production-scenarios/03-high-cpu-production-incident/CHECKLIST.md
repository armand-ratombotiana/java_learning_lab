# Incident Response Runbook: High CPU / ReDoS Attack

**Incident Type**: High CPU (Potential ReDoS)
**Severity**: P0-P1 (depends on scope)
**Response Time**: < 2 minutes for initial triage

## 1. DETECTION AND TRIAGE

### 1.1 Verify the Alert
- [ ] Confirm alert: "CPUUsageCritical" or "RegexEvaluationTime" or latency SLO breach
- [ ] Check CPU utilization: `top` / `htop` / CloudWatch / Atlas
- [ ] Check thread states: `jstack <pid> | grep "java.lang.Thread.State" | sort | uniq -c`
- [ ] Check if threads are stuck in `java.util.regex` methods
- [ ] Check if service is still accepting requests (health endpoint)
- [ ] Check incident history: has this happened before?

### 1.2 Assess Impact
- [ ] Which nodes/regions are affected?
- [ ] What is the latency impact (p50, p99)?
- [ ] Are requests queuing or timing out?
- [ ] Are there cascading failures to downstream services?
- [ ] Notify on-call incident manager
- [ ] Declare incident severity level (P0/P1)

### 1.3 Initial Mitigation (Emergency)
- [ ] Collect CPU flame graph: `profiler.sh -d 30 -e cpu -f flame.html <pid>`
- [ ] Identify the hot method (likely `java.util.regex.Pattern$GroupHead.match`)
- [ ] Identify the specific regex pattern from the stack trace
- [ ] Disable the offending regex pattern via feature flag / configuration toggle
- [ ] After disabling: CPU should drop within 1-2 minutes
- [ ] If CPU does not drop: restart affected nodes

## 2. DATA COLLECTION

### 2.1 CPU Profiling
- [ ] async-profiler (CPU): `profiler.sh -d 60 -e cpu -f /data/flame_cpu.html <pid>`
- [ ] async-profiler (Wall): `profiler.sh -d 60 -e wall -f /data/flame_wall.html <pid>`
- [ ] top: `top -H -p <pid>` (identify hot threads)
- [ ] Check if regex-related methods dominate the flame graph

### 2.2 Gather Input Data
- [ ] Find the request that triggered the ReDoS (from application logs)
- [ ] Save the input payload for reproduction and testing
- [ ] Check if the input is a known ReDoS pattern
- [ ] Check if multiple inputs from same source (IP, user) are triggering the issue

### 2.3 Collect JVM Artifacts
- [ ] JFR recording: `jcmd <pid> JFR.start name=diagnostic settings=profile duration=120s`
- [ ] Thread dump (3 samples): `jstack -l <pid> > /data/dumps/td_1.txt`
- [ ] GC logs: check for GC pressure from string allocations
- [ ] Heap histogram: `jcmd <pid> GC.class_histogram`
- [ ] Check string allocation rate: `jstat -gcutil <pid> 1s`

### 2.4 Check System Logs
- [ ] Application logs for regex timeout errors
- [ ] System logs: `dmesg | tail -50`
- [ ] Load balancer logs for suspicious IPs
- [ ] WAF/security logs for attack patterns

## 3. ROOT CAUSE ANALYSIS

### 3.1 Identify Vulnerable Regex
- [ ] Extract the specific regex pattern from source code / config
- [ ] Load the pattern into a local test harness
- [ ] Test with known ReDoS inputs
- [ ] Confirm catastrophic backtracking with timing tests

### 3.2 Analyze Regex Structure
- [ ] Check for nested quantifiers: `(a+)+`, `(b*)*`, `(c+)+$`
- [ ] Check for alternation with overlapping branches: `(a|aa|aaa)`
- [ ] Check for lookaround nesting: `(?=.*(.)\1{2,})` with other groups
- [ ] Check for possessive quantifier usage (should be used but isn't)
- [ ] Check for atomic group usage (should be used but isn't)

### 3.3 Check Defense Gaps
- [ ] Is there input length validation? What is the limit?
- [ ] Is there a regex timeout configured? What is the timeout?
- [ ] Is there a thread pool? Size and behavior?
- [ ] Are there rate limits on the endpoint?
- [ ] Are there WAF rules for ReDoS patterns?

### 3.4 Test Fix
- [ ] Rewrite pattern with atomic groups / possessive quantifiers
- [ ] Test fix against known ReDoS inputs — should be fast
- [ ] Test fix against normal inputs — should work correctly
- [ ] Add input length validation if missing

## 4. FIX AND VERIFICATION

### 4.1 Apply Fix
- [ ] Rewrite vulnerable regex pattern (atomic groups / possessive quantifiers)
- [ ] Add input length validation (max 1024 characters)
- [ ] Add regex timeout wrapper (TimeoutPattern class)
- [ ] Build and deploy to canary

### 4.2 Verify Fix
- [ ] Run ReDoS payload against fixed service — should complete in < 100ms
- [ ] Run normal traffic through fixed service — no regression
- [ ] Verify regex timeout fires for excessive evaluations
- [ ] Verify input validation rejects oversized payloads

### 4.3 Deploy to Production
- [ ] Canary (10% traffic, 2 hours)
- [ ] Regional (50% traffic, 4 hours)
- [ ] Full rollout (100%, 12 hours monitoring)
- [ ] Re-enable the disabled regex pattern

## 5. PREVENTIVE MEASURES

### 5.1 Monitoring
- [ ] Add regex evaluation time metrics and alerts
- [ ] Add CPU utilization alerts with pattern identification
- [ ] Add regex timeout counter alerts
- [ ] Deploy async-profiler for on-demand profiling

### 5.2 Code Quality
- [ ] Add ReDoS scanner to CI/CD pipeline
- [ ] Review all regex patterns for ReDoS vulnerabilities
- [ ] Add Checkstyle/ErrorProne rule for nested quantifiers
- [ ] Mandate input length validation for all text endpoints

### 5.3 Security
- [ ] Add WAF rules for ReDoS patterns
- [ ] Add rate limiting on content submission endpoints
- [ ] Implement request size limits at load balancer level
- [ ] Consider using a regex replacement (RE2/J, GraalVM)

### 5.4 Training
- [ ] Train developers on regex backtracking mechanics
- [ ] Train developers on atomic groups and possessive quantifiers
- [ ] Share ReDoS prevention guidelines with all teams

## 6. POSTMORTEM

- [ ] Complete INCIDENT_REPORT.md with timeline
- [ ] Complete ROOT_CAUSE.md with 5 Whys
- [ ] Complete PREVENTION.md for future incidents
- [ ] Share ReDoS case study with security team
- [ ] File follow-up tickets for regex audit

## Key Metrics Reference

| Metric | Healthy | Warning | Critical | Action |
|--------|---------|---------|----------|--------|
| CPU Utilization | < 50% | 70-90% | > 90% | Profile with async-profiler |
| Regex Eval Time (P99) | < 10ms | 100-1000ms | > 1000ms | Potential ReDoS |
| Regex Timeout Count | 0 | > 0 | > 10/min | Active ReDoS attack |
| Thread Pool Active | < 50% | 50-80% | > 80% | Check for stuck threads |
| Input Length (P99) | < 256 | 256-1024 | > 1024 | Block oversized inputs |
| StackOverflow Errors | 0 | > 0 | > 0 | Deep regex backtracking |

## Tools Reference

| Tool | Command | Purpose |
|------|---------|---------|
| async-profiler | `profiler.sh -d 60 -e cpu <pid>` | CPU flame graph |
| async-profiler | `profiler.sh -d 60 -e wall <pid>` | Wall-clock flame graph |
| jstack | `jstack <pid>` | Thread dump |
| jcmd | `jcmd <pid> Thread.print` | Thread dump (preferred) |
| top | `top -H -p <pid>` | Per-thread CPU |
| Java Mission Control | `jmc` | JFR analysis |
| ReScue | ReDoS detection tool | Scan regex patterns |
| regex101.com | Online regex tester | Test pattern backtracking |

