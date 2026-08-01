# Lab 01: Mock Interview — Senior Protocol/Networking Engineer

**Role**: Senior Networking Engineer | **Topic**: HTTP/2 Frame Multiplexing and Stream Prioritization | **Duration**: 45 minutes

---

## Interview Transcript

**Interviewer**: "Implement HTTP/2 frame multiplexing and stream prioritization. Walk me through what the protocol actually does and where the engineering challenges are."

**Candidate**: "HTTP/2's defining change over HTTP/1.1 is *multiplexing*: a single TCP connection carries many concurrent streams, each representing a request/response exchange, and the byte stream is cut into discrete **frames** that carry stream identifiers. The frame layer is the substrate: a 9-byte frame header (length, type, flags, stream ID, reserved bit) followed by a payload. Frames of different types do different jobs — HEADERS carries compressed header blocks, DATA carries body bytes, SETTINGS negotiates connection parameters, WINDOW_UPDATE carries flow-control credit, RST_STREAM terminates a stream with an error, PING measures latency, GOAWAY starts connection shutdown. The stream layer is a state machine: `idle → open → half-closed (remote) → closed`. The engineering challenge is that the frame stream is *serialized* over TCP but the *logical streams* are concurrent — so the implementation must interleave frames from different streams into one ordered byte stream and reassemble them on the other side."

**Interviewer**: "The famous problem with HTTP/1.1 was head-of-line blocking — one slow response blocked everything behind it. Does multiplexing actually fix that?"

**Candidate**: "It fixes *application-level* head-of-line blocking: a slow response on stream 1 no longer blocks stream 2's response bytes, because their frames interleave. But it does NOT fix *transport-level* head-of-line blocking: if a TCP segment carrying frames for streams 1, 2, and 3 is lost, TCP's retransmission stalls *all three* streams until the segment is recovered — because TCP delivers bytes in order. That's the motivation for HTTP/3/QUIC, which moves multiplexing down into the transport: each stream is its own reliable ordered byte stream inside QUIC, so a lost packet only stalls its own stream. This is the single most important conceptual distinction an interviewer looks for: HTTP/2 multiplexing is a layer-7 fix on a layer-4 bottleneck."

**Interviewer**: "Now the prioritization part. What does the priority tree look like, and what's the actual problem it solves?"

**Candidate**: "Prioritization answers: when the sender has bandwidth for only some streams' frames, *which streams' frames go out first*? The original model is a dependency tree: each stream can depend on another stream and carry a weight. The tree semantics: a parent stream's resources are shared among its children proportional to their weights; a child can only be served after its parent is served (the parent's completion unblocks the children). The classic use: the HTML document depends on nothing and has weight 256; its critical CSS/JS depend on the document; images depend on nothing but have low weight — so the browser gets the document, then CSS, then images, in the right order. In 2022+ the spec moved to a simpler model — the RFC 9218 *extensible prioritization* scheme with a single 'urgency' field and an 'incremental' flag — because the tree proved too complex and under-utilized. Either way, the *implementation* challenge is the same: the multiplexer must maintain an ordered, weighted scheduling of streams, and the scheduler must be fair — a stream that stops sending must not penalize its peers."

**Interviewer**: "How do you actually implement the weighted scheduling — what data structure and fairness model?"

**Candidate**: "The classic answer is *weighted fair queuing* over streams, often implemented with a **deficit round-robin** (DRR) scheduler: each stream has a quantum proportional to its weight; the scheduler serves streams in round-robin order, giving each stream a credit ('deficit') of its quantum per round; a stream that doesn't use its credit accumulates deficit and can send more later. DRR is the workhorse because it's O(1) per stream visit, guarantees proportional fairness in the long run, and handles streams that are momentarily empty without starving others. For the dependency tree, the scheduler walks the tree in dependency order: serve parents before children, distribute the parent's bandwidth to children by weight (recursively). The practical implementation detail: you don't want to hold data for low-priority streams in memory forever — a stream that's scheduled last must still be *flow-controlled*, not buffered unboundedly; the send window is the pressure valve."

**Interviewer**: "Flow control — walk me through WINDOW_UPDATE. What's the per-stream window vs the connection window, and how do they interact?"

**Candidate**: "HTTP/2 flow control is credit-based and layered: a per-stream window and a connection-wide window, both in octets. The sender may only send DATA for a stream up to the stream's current window; the total DATA in flight across all streams is bounded by the connection window. Receivers advertise credit with WINDOW_UPDATE frames — typically after the application consumes data, so the window reflects *true* consumption, not just receipt — which is what makes the protocol memory-safe: a misbehaving sender can never exceed the negotiated windows, so the receiver's buffers are bounded by design. The interaction bug people hit: a connection-level window that shrinks because one stream isn't consumed can starve *other* streams — that's the 'small-window throttling' problem; good implementations set a generous initial connection window (the spec allows up to 2^31-1) and manage per-stream windows finely. And WINDOW_UPDATE is itself a frame on the wire — the receiver must decide *when* to send it: too eagerly (every 4KB) and you eat bandwidth and CPU with frame overhead; too lazily and you throttle throughput. The standard trick is to send WINDOW_UPDATE when the consumed-but-unacknowledged bytes reach half the window."

**Interviewer**: "Head-of-line blocking at the HTTP/2 layer itself — there's another kind that people don't usually mention. Can you find it?"

**Candidate**: "Yes — *header-block* HOL: header compression uses a dynamic Huffman + static/dynamic table (HPACK), and header blocks for *all* streams are decompressed *in order* on a single decoder state. If stream 5's HEADERS frame is missing or corrupted, the decoder state can't advance, and the HEADERS frames of streams 6, 7, 8 can't be decoded even though their DATA frames arrived. HPACK is a serial dependency in a multiplexed protocol. Mitigations: the spec says a sender must re-encode the header block reference set periodically (evicting the table) to bound the damage, and endpoints must tolerate and recover via connection error handling; QUIC's QPACK solves it properly with separate instruction and data streams so decoding is not serialized."

**Interviewer**: "What about GOAWAY and graceful shutdown? And connection coalescing — same connection for multiple origins?"

**Candidate**: "GOAWAY is the graceful-shutdown mechanism: the sender says 'I will process streams with ID ≤ N and reject everything above' — then both sides drain their streams and close the connection cleanly, instead of RST-ing everything mid-flight. The stream-ID space is monotonic and odd-numbered IDs belong to the client (even to the server), which is what makes GOAWAY semantics possible: you can reference the last *processed* stream without ambiguity. Connection coalescing: if two hostnames resolve to the same IP and share the same TLS certificate, a client may reuse one connection for both origins — which is how HTTP/2 makes many requests-per-connection feasible (the 'connection economy'). The failure mode is mixed-content and certificate-pinning mismatches; browsers implement coalescing conservatively. A senior answer ties coalescing to the header table too: shared HPACK state across origins means a cookie leak risk if the encoder doesn't separate state per origin — actually the spec handles this by requiring each origin's authority to be distinct in its own header blocks, but implementations must be careful."

**Interviewer**: "How do you test a multiplexing implementation? What breaks silently?"

**Candidate**: "Three classes of tests. **Protocol conformance** with the official spec test suite — frame validation, error codes, state machine transitions — the classic failures are accepting frames on closed streams and misordering. **Deterministic simulation**: a test harness that delivers frames in scripted orders — interleaved, out-of-order, with delays — and asserts the reassembly is correct; the multiplexer must be a pure function of the byte sequence. **Adversarial tests**: a peer that never sends WINDOW_UPDATE (window exhaustion), a peer that sends DATA on a half-closed stream (protocol error), a peer that floods tiny DATA frames (the '1-byte frame' CPU attack — the reason HTTP/2 servers cap frame sizes and rate-limit small frames). The silent-breakage class is scheduling: a priority bug doesn't error, it just makes pages slow — so the tests must assert *ordering properties*, like 'a high-priority stream's frames appear before a low-priority stream's frames under bandwidth constraints'."

**Interviewer**: "Final question: what would you look at first when a server shows perfect HTTP/2 throughput but terrible page-load times for a specific asset graph?"

**Candidate**: "I'd look at the priority tree the client is building and the server's scheduling of it — specifically whether the server actually honors stream weights or falls back to naive FIFO multiplexing. Then I'd check the header table: if the client's dynamic table keeps getting evicted (SETTINGS_HEADER_TABLE_SIZE too small or the server resets it), every request re-sends full headers — invisible on the wire unless you decode the frames. Then flow control: if the server never raises the per-stream window, a single large response thrashes WINDOW_UPDATE round-trips. Those three — priority honoring, HPACK table dynamics, window sizing — account for most 'HTTP/2 is slower' mysteries."

---

## Wrap-Up

**What the interviewer is looking for**:
- Precise frame taxonomy and the 9-byte frame header layout
- The distinction between app-level HOL (fixed by multiplexing) and transport-level HOL (fixed only by QUIC)
- Understanding of the priority tree / RFC 9218 urgency and *why* the model changed
- Weighted scheduling mechanics: DRR or similar fair scheduling with credits
- Layered flow control (per-stream + connection window) and its memory-safety role
- HPACK serial-decoder HOL and GOAWAY semantics

**Common mistakes candidates make**:
- Claiming HTTP/2 eliminates head-of-line blocking entirely
- Treating prioritization as 'give stream N a bigger buffer' instead of scheduling bandwidth
- Forgetting the connection-level window and only modeling per-stream windows
- Not knowing HPACK is a serial dependency across streams
- Ignoring the small-frame attack surface and frame size caps
