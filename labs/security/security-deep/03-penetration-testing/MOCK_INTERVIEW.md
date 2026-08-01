# Mock Interview: Port Scanner with Service Fingerprinting

> Coding mock interview — 45-minute session, Java 21+, whiteboard style.

---

## Interview Setup

**Role**: Offensive Security Engineer (Red Team / AppSec)
**Candidate Level**: Senior Engineer
**Focus Area**: Network reconnaissance, socket programming, scanning methodology, ethics & scope
**Problem**: Design and implement a TCP port scanner with service fingerprinting (banner grabbing), with correct concurrency, timeout handling, and interpretation of results.
**Language**: Java 21+ (java.net sockets, records, executor framework)

---

## Interviewer Cheat Sheet (Prepared Questions)

1. What does a TCP connect scan actually do at the socket level, and what are its signatures?
2. SYN scan vs connect scan — why would a red team prefer SYN? What does that require?
3. What can make a port appear closed when it's filtered? How do you tell?
4. How does banner grabbing work, and why is it unreliable?
5. How do you scale a scanner — threads, limits, timeouts — without DoS-ing the target or yourself?
6. Follow-up: OS fingerprinting, service version detection (nmap -sV style), and the legal/scope constraints.

---

## Transcript

### Part 1: Problem Clarification (5 minutes)

**Interviewer**: "We do authorized penetration tests of our own infrastructure. I need a TCP port scanner with service fingerprinting — the recon stage. Scope the problem."

**Candidate**: "Three scoping questions. First, scan type: a full TCP **connect scan** via the OS socket API is the right default for an in-house tool — it's the only type available in pure Java, it's accurate for our own infrastructure, and it doesn't need raw sockets or root. If we needed SYN stealth later, that's a separate engine. Second, fingerprinting depth: banner grabbing (read what the service sends on connect) plus a well-known port→service table — that's the reliable, dependency-free core; nmap-style version detection with probe scripts is the extension. Third, the operating constraints: a default timeout and a concurrency cap, so the scanner is polite to both the target and the network team's alerts."

**Interviewer**: "Good. Connect scan + banners + the port table. What's your first clarification about the protocol itself?"

**Candidate**: "The three-way-handshake semantics: I'll treat a completed connect as 'open', a connection-refused (ECONNREFUSED) as 'closed', and a timeout as 'filtered or dropped' — I'll report it as such rather than guessing. Unreachable-host errors are distinct and shouldn't be counted as closed."

### Part 2: Theory — Scan Types & Interpretation (10 minutes)

**Interviewer**: "Connect scan vs SYN scan — explain the trade-off."

**Candidate**: "A connect scan performs the full TCP three-way handshake: SYN → SYN-ACK → ACK. It's the most reliable and requires no privileges — the OS completes the handshake. The downside: it's *loud* — every open port creates a completed connection that the target's logs and IDS will record, and on some systems it fills the listen queue. A SYN (half-open) scan sends only the SYN and tears down on SYN-ACK — the connection is never completed, so application logs often miss it. That requires raw socket access (root) or a library like Npcap, which is why I'd keep the Java tool to connect scans and note the option."

**Interviewer**: "How do you interpret the three outcomes — open, closed, filtered?"

**Candidate**: "Open: handshake completed — a service is listening. Closed: RST received — nothing is listening *at this moment*, and the port is reachable. Filtered: no response within timeout — a firewall dropped the packet; importantly, **filtered is a hypothesis, not a fact**: it could also be a congested network or a rate-limited host, so I report it with the timeout used, and a re-scan with a longer timeout is the standard confirmation. The three-way distinction matters operationally: a 'closed' result proves reachability, which is itself reconnaissance information."

**Interviewer**: "And banner grabbing — how does it work, and when does it fail?"

**Candidate**: "After connecting, most text protocols (FTP, SMTP, HTTP, SSH) send a greeting: 'SSH-2.0-OpenSSH_9.3', '220 host ESMTP', 'HTTP/1.1 400'. The scanner reads up to N bytes with a short read timeout and matches the result against fingerprint patterns. It fails in three common ways: (1) binary protocols that send nothing until spoken to first — you need to send a probe; (2) banners deliberately suppressed or spoofed — SSH banners are often rewritten by the admin; (3) TLS-wrapped services that send a TLS handshake instead of a text banner — that's why the fingerprint table should also note 'tls' indicators. So banners are *evidence*, not proof — the table plus banner plus version patterns together form the fingerprint."

### Part 3: Design (8 minutes)

**Interviewer**: "Design the concurrency model."

**Candidate**: "A fixed thread pool with the work items in a bounded queue: one task per (host, port) — for a single-host scan that's just the port range. The fixed pool caps concurrency (I'll default to 32-64 threads), the bounded queue prevents unbounded memory on big ranges, and each task enforces its own timeout via `Socket.connect(addr, timeoutMs)`. I'll also add a per-scan total deadline. The key engineering detail: never create a thread per port — for a /24 with 65,535 ports that's 16.7M threads and instant death; and never use a raw `new Socket(); connect()` without the explicit timeout, because the default connect timeout is OS-level minutes."

**Interviewer**: "What's the result model?"

**Candidate**: "An immutable `ScanResult(port, status, service, banner)` — status being an enum {OPEN, CLOSED, FILTERED} — collected into a list and sorted by port for the report. The report prints open ports with their fingerprint evidence, plus a count summary: X open, Y closed, Z filtered. I'd also always print the fingerprint rationale — '443/tcp OPEN https (banner: TLS handshake bytes)' — because a finding without evidence is useless to the report the client reads."

### Part 4: Implementation (18 minutes)

**Interviewer**: "Code the core scan task."

**Candidate**:

```java
public static ScanResult scanPort(String host, int port, int connectTimeoutMs, int bannerTimeoutMs) {
    try (Socket socket = new Socket()) {
        socket.connect(new InetSocketAddress(host, port), connectTimeoutMs);
        String banner = grabBanner(socket, bannerTimeoutMs);
        return new ScanResult(port, Status.OPEN, fingerprint(port), banner);
    } catch (SocketTimeoutException e) {
        return new ScanResult(port, Status.FILTERED, fingerprint(port), "");
    } catch (ConnectException e) {
        return new ScanResult(port, Status.CLOSED, fingerprint(port), "");
    } catch (IOException e) {
        return new ScanResult(port, Status.FILTERED, fingerprint(port), "");
    }
}
```

**Interviewer**: "Why the try-with-resources and the catch mapping?"

**Candidate**: "try-with-resources guarantees the socket closes even if the banner read times out — leaked sockets are the classic scanner bug, they exhaust file descriptors on a big scan. The catch mapping is the correctness core: SocketTimeoutException → filtered; ConnectException → closed; other IOExceptions (unreachable host, reset) → filtered with the host-level ones distinguishable at a higher level. Every outcome is a value, never an exception."

**Interviewer**: "Show the banner grab."

**Candidate**:

```java
public static String grabBanner(Socket socket, int timeoutMs) {
    try {
        socket.setSoTimeout(timeoutMs);
        InputStream in = socket.getInputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[256];
        int read = in.read(buf);
        if (read <= 0) return "";
        out.write(buf, 0, read);
        byte[] bytes = out.toByteArray();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            if (b >= 32 && b < 127) sb.append((char) b);
            else if (b == '\n' || b == '\r') sb.append(' ');
            else sb.append('.');   // non-printable -> placeholder
        }
        return sb.toString().trim();
    } catch (SocketTimeoutException e) {
        return "";   // service sent nothing within the window
    } catch (IOException e) {
        return "";
    }
}
```

**Interviewer**: "And the fingerprint table?"

**Candidate**: "A `Map<Integer, ServiceInfo>`: 21 FTP, 22 SSH, 23 Telnet, 25 SMTP, 53 DNS, 80/443 HTTP(S), 3306 MySQL, 5432 PostgreSQL, 6379 Redis, 8080/8443 HTTP(S) alt, 9200 Elasticsearch, 27017 MongoDB. The report joins the table with the banner — and I'd add pattern rules ('SSH-' prefix → SSH, '220 ' → SMTP/FTP by follow-up) so the banner *evidence* can override the *prior* from the table. That split — prior from port, evidence from banner — is exactly how nmap's service detection reasons."

### Part 5: Testing (5 minutes)

**Interviewer**: "How do you test a scanner without scanning the internet?"

**Candidate**: "Deterministically, on localhost. The harness starts a `ServerSocket` on an ephemeral port with a known greeting — a fake FTP that sends '220 lab-ftp ready' — then scans that exact port and asserts OPEN + the banner match. A second test connects-and-closes instantly (a listener that accepts and drops) to test the empty-banner path. A third test asserts CLOSED on a port known to be free, and FILTERED is tested by pointing at a non-routable address like 10.255.255.1 with a tiny timeout — that gets us the no-response path. All three outcomes get deterministic coverage, plus a localhost sweep (say 1-1024 with a short timeout) as a smoke run — localhost has no firewall by default, so results are interpretable."

### Part 6: Follow-ups (5 minutes)

**Interviewer**: "Where would you take this next for a real red-team engagement?"

**Candidate**: "Three upgrades. (1) **Version detection**: send protocol probes and match responses against a fingerprint DB — the nmap -sV model. (2) **OS fingerprinting**: TCP/IP stack quirks (TTL, window size, segment ordering) identify the OS family — requires raw packets, so it lives in the companion scanner, not this Java tool. (3) **Output integration**: export results as JSON for the reporting pipeline. And the discipline point: every scan in a real engagement runs against an authorized scope with a written rulebook — the tool should enforce a scope file and log everything, because the artifacts are the deliverable."

---

## Scoring Rubric

| Area | Excellent (3) | Good (2) | Needs Work (1) |
|------|---------------|----------|----------------|
| Protocol | Explains handshake, RST vs timeout semantics, SYN trade-off | Knows open/closed | No interpretation |
| Concurrency | Fixed pool, bounded queue, explicit timeouts, no FD leaks | Thread per port | Blocking single-thread |
| Fingerprint | Banner + table + pattern rules, evidence vs prior | Banner only | Port table only |
| Error mapping | Typed outcomes for timeout/refused/other IO | Try/catch with generic error | Swallows exceptions |
| Testing | Deterministic localhost harness for all 3 outcomes | Happy path | None |
| Ethics | Scope enforcement, logging, authorization framing | Mentions scope | No operational context |

## Red Flags
- No connect timeout (OS-default minutes) or thread-per-port.
- Treating FILTERED as CLOSED.
- Reporting banners as proof without noting spoofing/suppression.
- No scope/authorization awareness — this is an offensive tool.

## Key Takeaways
- Connect scan = completed handshake; RST = closed; no response = filtered (a hypothesis).
- Fixed pool + explicit connect timeout + try-with-resources = safe scaling.
- Fingerprint = port prior + banner evidence + pattern rules.
- Test deterministically on localhost with a harness server for all three outcomes.
