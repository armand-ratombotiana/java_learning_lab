# Lab 03: Mock Interview — Senior Systems/Networking Engineer

**Role**: Senior Networking Engineer | **Topic**: Recursive DNS Resolver | **Duration**: 45 minutes

---

## Interview Transcript

**Interviewer**: "Implement a recursive DNS resolver with caching and TTL. Walk me through the resolution path from a user typing a domain to the answer arriving, and where the engineering subtleties are."

**Candidate**: "The resolution path: the client sends a query for, say, `api.example.com` to the resolver. The resolver first checks its **cache** — if it has a non-expired record, it answers from cache and stops there (that's the 90% case). On a miss, the resolver does *iterative resolution*: it starts at the **root servers** — a fixed set of 13 root server identities (the infamous root hints file) — and asks 'who is authoritative for .com?', getting back a *referral* with the .com TLD server names and, ideally, their glue records. Then it asks a .com server 'who is authoritative for example.com?', then asks example.com's authoritative server for the actual record, e.g., an A record. Each step the resolver walks down the DNS hierarchy: root → TLD → second-level domain → answer. The records it collects along the way — the referrals and glue — are cached too, because the cache is the entire performance story of DNS."

**Interviewer**: "Caching is where most of the money is. What do you cache, for how long, and what are the failure modes?"

**Candidate**: "You cache every record type that passed through the resolution: the answer records (A/AAAA), the referrals (NS records for each zone), the glue (addresses of the authoritative servers, so you don't need a second resolution to find the resolver), and **negative answers** — NXDOMAIN responses get cached too (with the SOA minimum TTL), which is the *negative caching* that stops hammering dead names. The TTL is the lifetime: each record has a TTL in seconds, decremented in cache, and expired at zero — and the authoritative server can also return the *SOA minimum* which matters for negative caching. The failure modes: TTL too long → stale answers (a service that moved IPs keeps pointing at the old address — the classic 'my DNS hasn't updated' complaint); TTL too short → the cache hit rate collapses and the authoritative servers get hammered, which is the mechanism behind DNS-amplification stress. The right answer is TTL hygiene: records that change rarely get hours-to-days TTLs; records that must rotate fast (load balancer targets) get 30-60 seconds — and the cache must handle the TTL *edge* correctly: a record with TTL 0 is never cached, and a query arriving exactly at expiry must go to the network, not serve a technically-expired record."

**Interviewer**: "What about cache poisoning and the security side? This is where DNS gets spicy."

**Candidate**: "The attack is **Kamino-style poisoning** — an attacker injects a forged response with a spoofed source address and a guessed transaction ID. The defenses, in order of modern importance: **DNSSEC validation** — the resolver verifies the RRSIG chain from the root trust anchor down to the answer; a response that fails validation is *rejected outright*, regardless of where it came from. Second, **0x20 randomization** — randomly mixed case in the query name (e.g., `ExAmPlE.com`) — a forged response must match the case exactly, multiplying the guess space by 2^(labels × letters). Third, **transaction ID and source-port randomization** — 16-bit IDs and a random ephemeral port (up to 2^32 combined) make the chance of a successful blind guess ~2^-32, vs 2^-16 in the 1990s. Fourth, **bailiwick checking** — a response for `evil.com` received while asking about `example.com` is discarded unless it's in scope; this is the rule that blocks out-of-bailiwick injection, and it's cheap so it's always on. A senior answer also mentions the **Kamino attack variant** (2024): an attacker on-path during a *cache-miss* can poison records for OTHER names inside the same cache by racing the authoritative responses — mitigated by query-name scoping of received data and aggressive validation."

**Interviewer**: "Let's talk about the iterative walk in more depth — the referral handling. What's the subtlety in following referrals?"

**Candidate**: "When a root server replies with a referral to the .com TLD, it contains NS records for the TLD and usually **glue** — A/AAAA records for those NS names *in the parent zone* — because the TLD's own servers' addresses can't be found by asking the TLD itself (that would be circular). The resolver must follow the referral, and the *deep* subtlety is: glue records are not authoritative for the NS names themselves — they're in the parent's zone — so a cache must store them with appropriate trust and must not serve glue as an authoritative answer for `ns1.com's address` in a way that confuses validation. The second subtlety: a referral may return NS names *with* glue or *without* (lame delegation); without glue, the resolver must recursively resolve the NS name first — an extra resolution step that costs RTT and can be abused to slow resolvers (that's why resolvers cap the number of outstanding lookups per client query). The third: **lame delegations** — a referral points at servers that don't actually serve that zone; the resolver must mark them as lame, try the next NS in the set, and eventually give up with SERVFAIL rather than looping."

**Interviewer**: "How do you handle the case where the client is recursive — a stub resolver — versus iterative? And what's the resolver's relationship with upstream?"

**Candidate**: "The stub client sends one query and expects the complete answer — the *recursive* role is exactly what we implement: walk the tree for the client, cache, and return the final answer. Upstream, the resolver talks to authoritative servers *iteratively*. Two architectures beyond that: a **forwarding resolver** — instead of walking the tree itself, it forwards queries to an upstream resolver (ISP's or a public one like 1.1.1.1) and caches the results — cheaper to run, but you inherit the upstream's behavior and privacy exposure (this is why public resolvers pushed DoH/DoT: the *last mile* between stub and resolver is encrypted too). The protocol detail: the resolver sets the RD (recursion desired) flag when talking to an upstream recursive resolver, and clears it when talking to authoritative servers. A robust implementation supports both modes — the demo does the authoritative walk itself."

**Interviewer**: "How do you decide between UDP and TCP for the transport? And what's the EDNS/truncation dance?"

**Candidate**: "DNS historically runs over UDP:53 with a 512-byte limit; larger answers come back **truncated** (the TC bit set), and the client must retry over TCP. The modern standard is EDNS0: the resolver advertises a larger UDP payload size (typically 1232 bytes — the value tuned to fit inside most networks' MTU without IP fragmentation, which is the subtle part: big fragmented DNS packets get dropped and cause silent timeouts). If the response exceeds the advertised size, TC is set and the fallback is TCP. The full answer for DNSSEC-heavy responses: they're large (multiple RRSIGs), so resolvers must be comfortable with TCP — and DoH/DoT replace both with HTTP/3 or TLS on 443. The demo models the UDP-vs-TCP fallback: an oversized response triggers the truncation path and the resolver retries over the reliable channel."

**Interviewer**: "How do you test a resolver? DNS has 40 years of accumulated weirdness."

**Candidate**: "Start with a **zone-file fixture**: a mini hierarchy — a fake root, a fake .com, a fake example.com — served by an in-process authoritative server; the resolver is then tested against a fully controlled world. Test the cache semantics: TTL expiry boundaries, negative caching, glue caching, and the NXDOMAIN path. Then the adversarial suite: forged responses (wrong source port, wrong ID, wrong case), out-of-bailiwick answers, malformed names (length > 63 per label, total > 255), compression-pointer loops — a classic vulnerability class in parsers — and answer counts that don't match the question. Property tests: for any zone configuration, resolution must either succeed with a DNSSEC-validated chain or fail with SERVFAIL/NXDOMAIN — never return a wrong-but-plausible answer. The demo's walkthrough does the fixture approach: three in-memory zones and scripted adversarial responses."

**Interviewer**: "What's the performance model? How many queries per second can a cache handle, and what's the bottleneck?"

**Candidate**: "Cache hit path: hash lookup on (name, type, class) — the cache is a hash map with a lock; the bottleneck is contention, so production resolvers shard the cache by name hash (per-shard locks) or use a lock-free structure like a striped hash map. The miss path: the resolver bounds *outstanding* in-flight queries — typically 200-500 concurrent upstream queries per CPU — with a cap on total client queries per second (rate limiting protects the upstreams AND the resolver itself from being a reflection amplifier). Memory is dominated by cache size: entries with short TTLs churn; production systems cap cache size with a low-priority eviction (TTL-aware LRU) rather than letting the cache grow unboundedly with junk — especially hostile names from malware queries. The one number people underestimate: cache hit rates of 85-95% for a well-populated resolver are normal, and every percent of miss is expensive — it's a full walk of the tree with several network round trips."

**Interviewer**: "Final: what's a DNS bug you've seen that took a long time to find?"

**Candidate**: "The TTL-multiplied-by-request-rate one: a popular record with a 60-second TTL cached *per unique query name suffix* — a service that generated unique hostnames per request (`req-{uuid}.app.example.com`) blew the cache to hundreds of GB and hammered the authoritative servers, because every unique name is a separate cache entry with the full TTL. The fix was recognizing the pattern — unbounded-cardinality names — and handling it at the client or with a wildcard-aware cache policy. The lesson: cache *shape* matters as much as cache size; a resolver must know its name distribution, or it gets DDoSed by its own workload."

---

## Wrap-Up

**What the interviewer is looking for**:
- The iterative walk: root → TLD → authoritative, with referral and glue handling
- Cache semantics: per-type TTLs, negative caching, TTL-0 and expiry-edge correctness
- Security depth: DNSSEC validation, 0x20, source-port/ID randomization, bailiwick, Kamino
- UDP/EDNS/truncation → TCP fallback mechanics
- Operational performance: cache sharding, outstanding-query bounds, rate limiting
- Testability with fixture zones and adversarial response suites

**Common mistakes candidates make**:
- Forgetting the root-servers step in the walk
- Not caching referrals/glue and negative answers
- No bailiwick/out-of-zone answer filtering
- Ignoring the TC-bit/EDNS fallback path
- Treating the cache as a simple map without TTL-aware eviction and sharding
