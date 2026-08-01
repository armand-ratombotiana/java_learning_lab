# Lab 02: Mock Interview — Senior Transport/Networking Engineer

**Role**: Senior Networking Engineer | **Topic**: QUIC-like Connection Migration and Packet Loss Recovery | **Duration**: 45 minutes

---

## Interview Transcript

**Interviewer**: "Implement a QUIC-like connection with connection migration and packet loss recovery. Before we get to the code, tell me how QUIC's transport differs from TCP — what problem is it actually solving?"

**Candidate**: "QUIC is a full transport protocol implemented in user space over UDP, with TLS 1.3 integrated into the handshake. Its three headline properties: *stream multiplexing without head-of-line blocking* — each stream is its own reliable, ordered byte stream, so a lost packet on stream A doesn't stall stream B; *connection migration* — a connection is identified by a 64-bit connection ID, not the 4-tuple, so when the client's IP changes (Wi-Fi to cellular), the connection survives; and *integrated encryption* — every packet is authenticated and encrypted, which also gives protocol ossification resistance since middleboxes can't see or tamper with transport fields. The loss recovery problem is the same as TCP's but with an important advantage: packet *numbers* are per-packet (not per-byte), monotonically increasing, and never retransmitted with the same number — which makes ACK processing and RTT estimation much cleaner, because a delayed ACK can't be confused with an ACK for the original packet."

**Interviewer**: "Let's start with loss recovery. Walk me through the retransmission machinery — timer-based and otherwise."

**Candidate**: "The sender maintains a **sent-packet map**: each packet that carries new data is recorded with its packet number, the range of stream offsets it covers, and the time it was sent. ACKs are cumulative and *range-based*: an ACK frame carries the largest acknowledged packet number plus a set of ranges, so the sender can learn precisely which packets were received even with out-of-order delivery. The recovery algorithm: on ACK, remove acknowledged packets from the map and update RTT estimates — smoothed RTT plus RTT variance give the **RTO** (retransmission timeout) as a dynamic value, and QUIC also uses the **PTO** (probe timeout): after a PTO, the sender transmits probe packets carrying new data (or PINGs if nothing new) to elicit an ACK and detect loss faster. And there's the third mechanism: **ACK-triggered loss detection** — when an ACK arrives that acknowledges packets with numbers *higher* than an unacknowledged packet's number, the gap is declared lost after a threshold (kPacketThreshold, typically 3) even without a timeout — this is TCP's fast retransmit equivalent, but clean because packet numbers are unambiguous."

**Interviewer**: "What about congestion control on top of that? Which algorithm and why?"

**Candidate**: "The modern default is **CUBIC** with hybrid slow start, and the newer contender is **BBR**. CUBIC: after a loss event, the window drops multiplicatively (default 0.7), then grows with a cubic function of the time since the loss — the growth is aggressive when far from the last window size and gentle near it, which gives the classic 'plateau' behavior that's friendly to long fat networks and coexists well with other flows. BBR models the bottleneck bandwidth and round-trip time directly instead of treating loss as the only signal, which is why BBR outperforms on lossy links. The QUIC framing lets you swap congestion controllers as pluggable modules — that's a design property worth copying: the loss-recovery core is algorithm-agnostic; the controller just computes a send window from events (ack, loss, timeout). In a demo implementation I'd implement CUBIC's essence — multiplicative decrease on loss, cubic growth on the clock — with hybrid slow start for the ramp-up."

**Interviewer**: "Now connection migration. What's the mechanism, and what are the security concerns?"

**Candidate**: "The mechanism: the connection is keyed by the 64-bit connection ID, which the client *rotates* for privacy. When the client's address changes, it just keeps sending packets with the same connection ID from the new address. The server must handle two things: **path validation** and **address verification**. Path validation: the server sends a PATH_CHALLENGE frame to the *new* address and requires a PATH_RESPONSE echoing the exact challenge token before trusting the new path — this prevents off-path attackers from hijacking the connection by spoofing the client's IP. Address verification is the anti-amplification property: the server won't send more than a bounded multiple (3×) of what it received from an unverified address, which is the protection against reflection attacks. There's also **NAT rebinding** handling: the server keeps the connection alive on the old address in parallel for a while — a migrate timer — because NATs and middleboxes can cause the apparent source address to change back and forth."

**Interviewer**: "How does the connection survive when the server's data structure is keyed by the 4-tuple? That's the classic TCP mental model."

**Candidate**: "Right — the invariant that must hold in code: *all connection state is keyed by connection ID, never by (src IP, src port, dst IP, dst port)*. The socket-level dispatch is a thin hash table from connection ID to the connection object; the 4-tuple is just the current *path*, one of possibly several. The API-level consequence is that a QUIC server can send a response on a *different path* than the request arrived on — after validation. The demo implementation models exactly this: a `Connection` object owning the send/receive windows and recovery state, with a `currentPath` field that can be swapped on validated migration, and a dispatch table keyed by connection ID."

**Interviewer**: "What happens to in-flight data during migration? The old path's packets may be lost."

**Candidate**: "The connection's loss-recovery machinery doesn't know or care about paths — packets are tracked by packet number, and the ACK/RTO machinery operates on the connection as a whole. On migration, the sender can either keep the existing window (and let normal loss recovery sort out the stragglers) or do a **window reset** — restarting from the current congestion window *at the new path's estimated RTT* — since the new path may have very different characteristics (a 4G path has different RTT and loss than Wi-Fi). QUIC leaves the policy open; the sensible design is: keep congestion state, but re-measure RTT on the new path quickly (the PTO probes do that), and treat the migration moment like a fresh start for pacing. The important correctness property: **packet numbers must remain monotonic across migration** — the receiver's packet-number space is per-connection, and the encrypted packet number makes the stream reassembly unambiguous even as paths change."

**Interviewer**: "What about the handshake interplay — TLS 1.3 integrated? Why does that matter for migration?"

**Candidate**: "TLS 1.3 integration: the handshake is one flight from client (ClientHello with supported versions, keys, and the transport parameters in a special extension) and one from server (ServerHello with the negotiated keys and transport parameters), and 1-RTT application data can start immediately — QUIC's transport parameters (initial windows, max streams, idle timeout, ACK delay) are themselves negotiated in the handshake, which is why the transport is so configurable per-connection. Migration matters here because the TLS keys are bound to the connection ID space, not the IP: the same keys remain valid after migration. 0-RTT is the controversial bit: the client can send application data with the previous session's keys before the handshake completes — the server must treat 0-RTT data as replayable (a duplicate request is possible) unless it implements replay protection with a server-side cache."

**Interviewer**: "How do you test loss recovery deterministically? Real networks are chaotic."

**Candidate**: "A **packet-simulation harness**: a virtual link between client and server where every packet passes through an injectable fault model — drop packets (fixed or random loss rate), delay packets, reorder them, duplicate them, fragment them. The recovery code is then tested against scripted scenarios: 'drop every 10th packet', 'reorder by 2', 'delay by 100ms for 500ms' — and assertions check throughput doesn't collapse, retransmission counts stay bounded, and the reassembly is byte-exact. This is how real QUIC stacks (quiche, ngtcp2) are tested: deterministic simulation plus property tests (never duplicate-deliver, never reorder within a stream, monotonic packet numbers). The walkthrough demo will do exactly this with a scripted drop scenario."

**Interviewer**: "What's the most subtle bug you'd expect in a naive QUIC implementation?"

**Candidate**: "Incorrect RTT estimation. If the implementation doesn't handle **acknowledgment delays** — the receiver's `ack_delay` field tells the sender how long the receiver held the ACK — the RTT estimate gets inflated by exactly that delay, which inflates the RTO, which makes loss detection lazy, which under congestion turns a slight latency hiccup into a throughput collapse. The subtle part: the ack_delay only applies to the *largest* acknowledged packet (earlier packets have their own timing), so the RTT sample is `now - send_time(largest_acked) - ack_delay`. The second candidate: unacknowledged stream data not being reclaimed — if the sent-packet map keeps references to the payload forever, memory grows unboundedly; a QUIC sender must release stream buffers as soon as the covering packet is acknowledged. Third: treating retransmitted data as a new byte-stream — it must go *back into the original stream offset*, never appended."

---

## Wrap-Up

**What the interviewer is looking for**:
- Why per-packet (not per-byte) numbers make loss recovery cleaner
- The full recovery toolbox: RTO/PTO probes, ACK ranges, packet-threshold loss detection
- Migration keyed by connection ID with PATH_CHALLENGE validation and anti-amplification
- Congestion control as a pluggable module (CUBIC/BBR semantics)
- TLS 1.3 integration and the replayable-0-RTT hazard
- Deterministic testing with an injectable packet-simulation link

**Common mistakes candidates make**:
- Keying connection state by the 4-tuple (the TCP habit that breaks migration)
- Confusing packet numbers with stream offsets
- Ignoring ack_delay in RTT samples
- No anti-amplification / path validation on the new address
- Treating retransmitted data as appending to the stream instead of filling the original offset
