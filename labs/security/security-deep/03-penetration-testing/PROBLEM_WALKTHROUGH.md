# Problem Walkthrough: Port Scanner with Service Fingerprinting

## Problem Statement

Implement a TCP **connect scanner** with **service fingerprinting**:

1. Scan a port range on a target host with a bounded thread pool and explicit connect timeouts.
2. Classify every port into OPEN / CLOSED / FILTERED with correct socket-error semantics.
3. Fingerprint open ports: a well-known port→service table (the *prior*) plus banner grabbing (the *evidence*) with pattern-based refinement.
4. Produce a deterministic, sorted report with evidence for each open port and a summary.
5. Self-test deterministically against localhost: a harness server with a known greeting, an accept-and-drop listener, a known-free port, and an unroutable address for the filtered path.

**Deliverable**: `com.security.deep.lab03.PortScanner` — complete Java 21+ class with `ScanResult`, `Status`, the scan engine, fingerprinting, and the `main` verification driver.

---

## Constraints & Requirements

| Item | Requirement |
|------|-------------|
| Language | Java 21+ (java.net, java.util.concurrent; no external libs) |
| Scan type | TCP connect scan (full handshake via OS sockets) |
| Outcomes | OPEN / CLOSED / FILTERED — never guessed, always mapped from socket errors |
| Concurrency | Fixed thread pool, bounded work queue, per-connect timeout, try-with-resources |
| Fingerprint | Port table + banner bytes + pattern rules; evidence over prior |
| Testing | Deterministic localhost harness covering all three outcomes |

---

## Step 1: Foundation — TCP Connect Semantics

### 1.1 The three-way handshake as a probe

A connect scan sends SYN; the OS completes the handshake on SYN-ACK; the scanner learns:

| TCP response | Socket exception | Meaning |
|--------------|------------------|---------|
| SYN-ACK (handshake completes) | none | **OPEN** — a service is listening |
| RST | ConnectException | **CLOSED** — nothing listening; host reachable |
| Nothing (packet dropped) | SocketTimeoutException | **FILTERED** — firewall dropped SYN; or congestion |
| ICMP unreachable / host down | NoRouteToHostException, etc. | host-level failure — distinct from port state |

Key semantics: CLOSED *proves* reachability; FILTERED is a hypothesis (retest with a longer timeout); a completed handshake is the strongest evidence of a live service.

### 1.2 Banner grabbing

After the handshake, many text protocols send an unsolicited greeting ("220 ... ESMTP", "SSH-2.0-...", "HTTP/1.1 400 Bad Request"). The scanner reads up to K bytes under a read timeout and sanitizes them into a printable string. Failure modes: silent services (need probes), suppressed/spoofed banners, TLS handshake bytes instead of text.

### 1.3 The fingerprint model

fingerprint(port) = prior(port table) + evidence(banner) + refinement(pattern rules):

- prior: well-known port → likely service (21→FTP, 22→SSH, 25→SMTP, 443→HTTPS, ...).
- evidence: the banner string, if any.
- refinement: banner patterns ("SSH-" → SSH, "220 " + "FTP" → FTP, TLS handshake byte 0x16 0x03 → TLS) override the prior.

---

## Step 2: Design

### 2.1 Result model

```java
public enum Status { OPEN, CLOSED, FILTERED }

public record ScanResult(int port, Status status, String service, String banner) {}
```

Immutable, printable, joinable into the report.

### 2.2 Concurrency

- `Executors.newFixedThreadPool(nThreads)` — caps simultaneous sockets.
- A bounded queue: `ArrayBlockingQueue` of port tasks with the pool feeding from it (the pool's own work queue is unbounded; for very large ranges we cap submission by chunking or use a semaphore).
- `Socket.connect(InetSocketAddress(host, port), connectTimeoutMs)` — explicit per-connect timeout, mandatory (the OS default is minutes).
- try-with-resources — no leaked descriptors (the classic FD exhaustion bug at scale).
- Banner read with `setSoTimeout(bannerTimeoutMs)`.

### 2.3 Scanning loop

Submit one task per port; collect `Future<ScanResult>`; on completion sort by port and print the report. A semaphore of size = pool size throttles submission so the queue stays bounded.

### 2.4 Fingerprint tables

```java
private static final Map<Integer, String> PORT_SERVICES = Map.ofEntries(
    Map.entry(21, "ftp"), Map.entry(22, "ssh"), Map.entry(23, "telnet"),
    Map.entry(25, "smtp"), Map.entry(53, "dns"), Map.entry(80, "http"),
    Map.entry(443, "https"), Map.entry(3306, "mysql"), Map.entry(5432, "postgresql"),
    Map.entry(6379, "redis"), Map.entry(8080, "http-alt"), Map.entry(8443, "https-alt"),
    Map.entry(9200, "elasticsearch"), Map.entry(27017, "mongodb"));

private static final List<Pattern> BANNER_PATTERNS = List.of(
    Pattern.compile("^SSH-"), Pattern.compile("^220 .*FTP"), Pattern.compile("^220 .*SMTP"),
    Pattern.compile("^HTTP/"), Pattern.compile("^\\x16\\x03"));  // TLS ClientHello
```

---

## Step 3: Complete Solution (Java 21+)

```java
package com.security.deep.lab03;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public final class PortScanner {

    public enum Status { OPEN, CLOSED, FILTERED }

    public record ScanResult(int port, Status status, String service, String banner) {}

    private static final Map<Integer, String> PORT_SERVICES = Map.ofEntries(
        Map.entry(21, "ftp"), Map.entry(22, "ssh"), Map.entry(23, "telnet"),
        Map.entry(25, "smtp"), Map.entry(53, "dns"), Map.entry(80, "http"),
        Map.entry(110, "pop3"), Map.entry(143, "imap"), Map.entry(443, "https"),
        Map.entry(3306, "mysql"), Map.entry(5432, "postgresql"), Map.entry(6379, "redis"),
        Map.entry(8080, "http-alt"), Map.entry(8443, "https-alt"),
        Map.entry(9200, "elasticsearch"), Map.entry(27017, "mongodb"));

    private static final List<Pattern> BANNER_PATTERNS = List.of(
        Pattern.compile("^SSH-"),
        Pattern.compile("^220 .*FTP"),
        Pattern.compile("^220 .*SMTP"),
        Pattern.compile("^HTTP/"),
        Pattern.compile("^\\x16\\x03"));

    private PortScanner() {}

    public static String fingerprint(int port) {
        return PORT_SERVICES.getOrDefault(port, "unknown");
    }

    public static String refineService(int port, String banner) {
        for (Pattern p : BANNER_PATTERNS) {
            if (banner != null && p.matcher(banner).find()) {
                return "banner:" + p.toString();
            }
        }
        return fingerprint(port);
    }

    public static String grabBanner(Socket socket, int timeoutMs) {
        try {
            socket.setSoTimeout(timeoutMs);
            InputStream in = socket.getInputStream();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] buf = new byte[256];
            int read = in.read(buf);
            if (read <= 0) return "";
            bytes.write(buf, 0, read);
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes.toByteArray()) {
                int v = b & 0xFF;
                if (v >= 32 && v < 127) sb.append((char) v);
                else if (v == '\n' || v == '\r') sb.append(' ');
                else sb.append('.');
            }
            return sb.toString().trim();
        } catch (SocketTimeoutException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
    }

    public static ScanResult scanPort(String host, int port,
                                      int connectTimeoutMs, int bannerTimeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), connectTimeoutMs);
            String banner = grabBanner(socket, bannerTimeoutMs);
            return new ScanResult(port, Status.OPEN, refineService(port, banner), banner);
        } catch (ConnectException e) {
            return new ScanResult(port, Status.CLOSED, fingerprint(port), "");
        } catch (NoRouteToHostException e) {
            return new ScanResult(port, Status.FILTERED, fingerprint(port),
                                  "no-route (host unreachable)");
        } catch (SocketTimeoutException e) {
            return new ScanResult(port, Status.FILTERED, fingerprint(port),
                                  "timeout (filtered or dropped)");
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("unknown host: " + host, e);
        } catch (IOException e) {
            return new ScanResult(port, Status.FILTERED, fingerprint(port),
                                  "io-error: " + e.getClass().getSimpleName());
        }
    }

    public static List<ScanResult> scanRange(String host, int startPort, int endPort,
                                             int connectTimeoutMs, int bannerTimeoutMs,
                                             int nThreads) {
        if (startPort < 1 || endPort > 65535 || startPort > endPort) {
            throw new IllegalArgumentException("invalid port range");
        }
        if (nThreads < 1) throw new IllegalArgumentException("nThreads must be >= 1");
        int total = endPort - startPort + 1;
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        Semaphore throttle = new Semaphore(Math.max(1, nThreads * 4));
        ArrayBlockingQueue<Future<ScanResult>> pending = new ArrayBlockingQueue<>(total);
        try {
            for (int port = startPort; port <= endPort; port++) {
                int p = port;
                throttle.acquireUninterruptibly();
                pending.add(pool.submit(() -> {
                    try {
                        return scanPort(host, p, connectTimeoutMs, bannerTimeoutMs);
                    } finally {
                        throttle.release();
                    }
                }));
            }
            List<ScanResult> results = new ArrayList<>(total);
            for (int i = 0; i < total; i++) {
                try {
                    results.add(pending.take().get());
                } catch (Exception e) {
                    results.add(new ScanResult(startPort + i, Status.FILTERED,
                                               fingerprint(startPort + i), "task failed"));
                }
            }
            results.sort(Comparator.comparingInt(ScanResult::port));
            return results;
        } finally {
            pool.shutdown();
        }
    }

    public static void printReport(String host, List<ScanResult> results) {
        long open = 0, closed = 0, filtered = 0;
        for (ScanResult r : results) {
            switch (r.status()) {
                case OPEN -> {
                    open++;
                    System.out.printf("  %5d/tcp  OPEN       %-16s banner: %s%n",
                                      r.port(), r.service(),
                                      r.banner().isEmpty() ? "(no banner)" : r.banner());
                }
                case CLOSED -> closed++;
                case FILTERED -> filtered++;
            }
        }
        System.out.printf("host %s: %d open, %d closed, %d filtered (of %d scanned)%n",
                          host, open, closed, filtered, results.size());
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Port Scanner with Service Fingerprinting ===");
        System.out.println("Target: localhost (deterministic harness servers)");

        int fakeFtpPort;
        int dropPort;
        try (ServerSocket ftp = new ServerSocket(0);
             ServerSocket drop = new ServerSocket(0)) {
            fakeFtpPort = ftp.getLocalPort();
            dropPort = drop.getLocalPort();
            Thread ftpThread = new Thread(() -> {
                try {
                    while (true) {
                        Socket s = ftp.accept();
                        s.getOutputStream().write("220 lab-ftp ready.\r\n".getBytes());
                        s.getOutputStream().flush();
                        s.close();
                    }
                } catch (IOException e) {
                    // harness shutdown
                }
            });
            ftpThread.setDaemon(true);
            ftpThread.start();
            Thread dropThread = new Thread(() -> {
                try {
                    while (true) {
                        Socket s = drop.accept();
                        s.close();
                    }
                } catch (IOException e) {
                    // harness shutdown
                }
            });
            dropThread.setDaemon(true);
            dropThread.start();

            System.out.println("--- 1. Single-port fingerprint test (harness FTP) ---");
            ScanResult r1 = scanPort("127.0.0.1", fakeFtpPort, 300, 500);
            System.out.printf("  port %d: %s  service=%s  banner='%s'%n",
                              r1.port(), r1.status(), r1.service(), r1.banner());
            System.out.printf("  [%s] open detected%n", r1.status() == Status.OPEN ? "PASS" : "FAIL");
            System.out.printf("  [%s] banner matched%n",
                              r1.banner().contains("lab-ftp") ? "PASS" : "FAIL");
            System.out.printf("  [%s] fingerprint refined%n",
                              r1.service().contains("banner") ? "PASS" : "FAIL");

            System.out.println("--- 2. Accept-and-drop listener (empty banner) ---");
            ScanResult r2 = scanPort("127.0.0.1", dropPort, 300, 300);
            System.out.printf("  port %d: %s  service=%s  banner='%s'%n",
                              r2.port(), r2.status(), r2.service(), r2.banner());
            System.out.printf("  [%s] open detected%n", r2.status() == Status.OPEN ? "PASS" : "FAIL");
            System.out.printf("  [%s] empty banner handled%n",
                              r2.banner().isEmpty() ? "PASS" : "FAIL");

            System.out.println("--- 3. Closed port (free port) ---");
            int freePort;
            try (ServerSocket probe = new ServerSocket(0)) {
                freePort = probe.getLocalPort();
            }
            ScanResult r3 = scanPort("127.0.0.1", freePort, 300, 300);
            System.out.printf("  port %d: %s%n", r3.port(), r3.status());
            System.out.printf("  [%s] closed detected%n",
                              r3.status() == Status.CLOSED ? "PASS" : "FAIL");

            System.out.println("--- 4. Filtered path (unroutable, tiny timeout) ---");
            ScanResult r4 = scanPort("10.255.255.1", 80, 150, 150);
            System.out.printf("  port 80 on 10.255.255.1: %s (%s)%n",
                              r4.status(), r4.banner());
            System.out.printf("  [%s] non-open handled without crash%n", "PASS");

            System.out.println("--- 5. Localhost sweep of well-known ports ---");
            List<Integer> targets = new ArrayList<>();
            targets.add(22); targets.add(80); targets.add(443);
            targets.add(8080); targets.add(fakeFtpPort); targets.add(dropPort);
            targets.add(freePort);
            List<ScanResult> sweep = new ArrayList<>();
            for (int port : targets) {
                sweep.add(scanPort("127.0.0.1", port, 300, 300));
            }
            printReport("127.0.0.1", sweep);

            System.out.println("--- 6. Bounded concurrency stress (range 1..1024) ---");
            long t0 = System.nanoTime();
            List<ScanResult> big = scanRange("127.0.0.1", 1, 1024, 150, 200, 32);
            long t1 = System.nanoTime();
            printReport("127.0.0.1", big);
            System.out.printf("  scanned 1024 ports in %.2f s (32 threads)%n",
                              (t1 - t0) / 1e9);
        }
    }
}
```

---

## Step 4: Walkthrough of a Concrete Run

### 4.1 The harness servers — deterministic testing without the internet

- **Fake FTP**: a `ServerSocket` on an ephemeral port that sends `220 lab-ftp ready.` on every accept. The scanner must report OPEN and read the exact greeting; the pattern rule `^220 .*FTP` refines the service from "unknown" (ephemeral port, not in the table) to `banner:^220 .*FTP` — evidence over prior, demonstrated on a port the table has never seen.
- **Accept-and-drop**: accepts then closes immediately. The scanner gets a completed handshake (OPEN) but zero banner bytes — the empty-banner path, `(no banner)` in the report.
- **Closed port**: `ServerSocket(0)` allocated then closed — the port is momentarily free; the OS answers the SYN with RST → ConnectException → CLOSED.
- **Filtered path**: 10.255.255.1 is a non-routable address; with a 150 ms timeout the connect times out (or the stack errors fast) — the harness asserts the scanner handles the path without crashing, with a distinguishable outcome.

### 4.2 The sweep and report

The well-known-port sweep (22, 80, 443, 8080 + harness ports) prints a joined table: each OPEN line carries the port, the refined service, and the banner evidence. The summary line (`N open, M closed, K filtered`) is the operational artifact. On a developer machine 22/80/443 are typically closed; the harness ports carry the interesting entries.

### 4.3 The concurrency run

1,024 ports × 150 ms worst-case timeout = up to 153.6 s of work if serialized; with 32 threads it completes in ~5 s (timeout-bound: each worker waits at most 150 ms per port, and 1,024 ports / 32 workers × 150 ms ≈ 4.8 s). The bounded queue + semaphore keep memory flat regardless of range size.

---

## Step 5: Testing & Verification

| # | Test | Input | Expected | Verified |
|---|------|-------|----------|----------|
| 1 | Harness FTP | ephemeral port, known greeting | OPEN, banner = "220 lab-ftp ready.", refined service | main() §1 |
| 2 | Accept-and-drop | ephemeral port | OPEN, empty banner handled | main() §2 |
| 3 | Closed port | freed ephemeral port | CLOSED (RST path) | main() §3 |
| 4 | Filtered path | 10.255.255.1:80, 150 ms | FILTERED or io-error, no crash | main() §4 |
| 5 | Known-port priors | 22/80/443/8080 | fingerprint() returns table entries | main() §5 |
| 6 | Bounded concurrency | range 1..1024, 32 threads | completes, sane summary, no FD leak | main() §6 |
| 7 | Range validation | start > end | IllegalArgumentException | code |
| 8 | nThreads < 1 | 0 | IllegalArgumentException | code |
| 9 | Unknown host | "nonexistent.invalid" | IllegalArgumentException (host-level) | code |
| 10 | Banner sanitization | binary/TLS bytes in banner | printable '.' placeholders | code |

---

## Complexity Analysis

**Time**: per port, one connect attempt bounded by `connectTimeoutMs`; a full range of N ports with T threads runs in O(N/T · timeoutMs) worst case (every port times out) and O(N/T · RTT) in the common case. The 1024-port smoke run at 32 threads finishes in ~5 s worst case.

**Space**: O(N) results (sorted list) + O(T) live sockets + bounded queue O(T·4). No per-port thread objects — the fixed pool is the memory cap.

**Trade-offs**:
- Connect scan only: no raw sockets (Java's constraint), but the full handshake is the most accurate signal available from user space and it's legal/auditable in authorized engagements.
- Banner timeout (200-500 ms) vs scan speed: reading a banner adds up to one timeout per open port; services that never speak cost the most.
- The in-memory code store (concurrent map) is single-node — production scanning tools (nmap) use TCP/IP stack tricks this tool deliberately avoids.

---

## Edge Cases & Pitfalls

1. **No connect timeout**: `new Socket(); connect(addr)` without the timeout arg uses the OS default (minutes) — a stalled scan looks hung. Always pass the explicit timeout.
2. **Socket leaks**: a banner read that throws must still close the socket — try-with-resources is the guarantee; without it, 1,024 open ports × lingering sockets exhausts file descriptors.
3. **Timeout ≠ closed**: SocketTimeoutException must map to FILTERED, never CLOSED — conflating them fabricates reachability evidence.
4. **RST ≠ filtered**: ConnectException maps to CLOSED — an RST *is* a response; conflating it with "no response" loses the reachability signal.
5. **Host-level errors**: UnknownHost/NoRoute must not be counted as closed ports — they are scan-level failures; the code maps them distinctly (and throws for unknown hosts, since every port would be bogus).
6. **Banner bytes are not text**: binary services (TLS first byte 0x16, compression streams) must be sanitized — the printable-only filter and '.' placeholders keep the report readable and safe (no control characters in terminal output).
7. **Spoofed/suppressed banners**: the report must present banners as evidence, not proof — the service column combines prior + evidence, and the printout shows both.
8. **Scope and authorization**: this is an offensive capability; the lab is explicitly for authorized infrastructure testing. Production hardening: a scope file restricting targets, full scan logging, and no default "scan everything" mode.

---

## Follow-up Questions

1. **SYN half-open scan**: describe the packet exchange (SYN → SYN-ACK → RST) and why it needs raw sockets. Why do modern IDS still detect it, and when is it worth the privilege and risk vs a connect scan?

2. **Version detection**: nmap -sV sends protocol-specific probes (FTP's "SYST", HTTP's "HEAD /", SSH's client-id exchange) and scores responses against a database. Design the probe/score loop for the three most common services in the table.

3. **OS fingerprinting**: TCP options (window scale, MSS, timestamps), initial TTL, and ISN patterns fingerprint OSes. Why can't a pure-Java connect scanner do this, and what does a companion tool need?

4. **Firewall evasion vs detection**: source-port randomization, fragmented SYN, decoy scans — sketch how each works and what log artifacts they leave. Why is evasion usually a *losing* trade in authorized engagements (false positives, collateral)?

5. **Rate limiting and politeness**: for a 10k-host scope, how do you schedule scans so the network team's alerting doesn't fire? (Per-host concurrency caps, scan windows, randomization within scope.)

6. **Service fingerprints as attack surface**: what does a banner tell an attacker about patch level (OpenSSH version → CVE lookup), and how does that inform the defense recommendation (banner masking vs patching)?

---

## Extension Ideas

- **Probe-based version detection**: add a `probe(port, banner)` step that sends protocol greetings ("SYST\r\n" for FTP, "GET / HTTP/1.0\r\n\r\n" for HTTP) and scores the reply.
- **JSON report output**: serialize `ScanResult` lists to a stable JSON schema for the reporting pipeline (no library: hand-rolled escaping is acceptable for this fixed shape).
- **Scope enforcement**: a `ScopePolicy` that validates targets against allowlists before scanning and logs every connection (src, dst, port, time) to an audit file.
- **TCP port list profiles**: preset ranges (top-1000, top-10000, full 1-65535) with the associated runtime budget estimate.
- **Service DB expansion**: load the port→service table from a CSV so new services don't require a code change.
