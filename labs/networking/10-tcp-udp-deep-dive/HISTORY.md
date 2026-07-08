# TCP/UDP Deep Dive — History

## 1. The Birth of TCP/IP (1970s)

### ARPANET and the Need for a Protocol
In the late 1960s, the ARPANET connected research institutions using the Network Control Protocol (NCP). By the early 1970s, NCP's limitations became apparent: it couldn't handle packet loss across heterogeneous networks, and it required all connected hosts to be peers (no router concept).

### 1973: The TCP Design
Vint Cerf and Bob Kahn published "A Protocol for Packet Network Intercommunication" in May 1974. Their design described a single protocol (TCP) that would handle both reliable stream transport and datagram routing.

**Key innovations:**
- End-to-end principle: reliability at the edges, not the network core
- Sliding window flow control (window size = 8 bits = 256 bytes initially)
- Sequence numbers for ordering and retransmission
- Destination address for routing

### 1978: Splitting TCP and IP
The original monolithic TCP was split into two protocols:
- **IP (Internet Protocol)** — routing and addressing
- **TCP (Transmission Control Protocol)** — reliable stream delivery

This separation allowed other transport protocols (like UDP) to operate over IP, enabling more diverse applications.

## 2. The Evolution of TCP (1980s)

### 1981: TCP Standardized (RFC 793)
Jon Postel edited RFC 793, which defined TCP as we know it today. The header layout, state machine, and basic algorithms were codified.

### 1983: TCP/IP Becomes Mandatory
On January 1, 1983, ARPANET switched from NCP to TCP/IP. This "flag day" is considered the birth of the modern Internet.

### 1984: Nagle's Algorithm (RFC 896)
John Nagle identified the "small packet problem" in RFC 896. He observed that interactive Telnet sessions produced many tiny packets (41 bytes overhead for 1 byte of data = 4100% overhead). His algorithm coalesces small writes to reduce packet count.

### 1986: Congestion Collapse
In October 1986, the Internet experienced its first congestion collapse. The link between Lawrence Berkeley Lab and UC Berkeley (a short 400-yard hop) dropped from 32 Kbps to 40 bps — a 99.9% throughput reduction.

### 1988: TCP Tahoe
Van Jacobson published "Congestion Avoidance and Control" (SIGCOMM 1988), introducing:
- Slow Start (exponential probing)
- Congestion Avoidance (AIMD)
- Fast Retransmit (triple duplicate ACK triggers retransmit)

These algorithms saved the Internet from congestion collapse.

## 3. The Rise of UDP (1980s-1990s)

### 1980: UDP Standardized (RFC 768)
David Reed defined the User Datagram Protocol in RFC 768. It was intentionally minimal — just 8 bytes of header, no reliability, no flow control.

### 1983: DNS Uses UDP
The Domain Name System (RFC 882/883) chose UDP as its primary transport. A single DNS query typically fits in one 512-byte datagram, making connection overhead unnecessary.

### 1990s: UDP for Real-Time Applications
As multimedia grew, UDP became the transport of choice for:
- RTP (Real-time Transport Protocol) for VoIP and video
- Streaming media (RealAudio, QuickTime)
- Online gaming (latency-sensitive, loss-tolerant)

## 4. TCP Refinements (1990s)

### 1990: TCP Reno
Added Fast Recovery to Tahoe. Instead of going back to slow start after fast retransmit, Reno sets cwnd to ssthresh (halved cwnd) and continues in congestion avoidance.

### 1992: Window Scaling (RFC 1323)
The original 16-bit window field limited windows to 64KB. For high-BDP links (e.g., satellite, fiber), this wasn't enough. Window scaling added a scaling factor (shift count) negotiated during the SYN handshake, enabling windows up to 1GB.

### 1994: TCP Vegas
Brakmo and Peterson proposed delay-based congestion control. Vegas uses RTT increases (not packet loss) to detect congestion before buffers fill. Despite better performance, Vegas never gained wide deployment because it's less aggressive than Reno and gets fewer resources when competing.

### 1996: Selective ACK (RFC 2018)
SACK allows the receiver to acknowledge non-contiguous blocks of data. Without SACK, a single lost packet forces retransmission of all following data (Go-Back-N). SACK enables selective retransmission.

## 5. The Modern Era (2000s-Present)

### 2005: TCP Cubic
Developed by Sangtae Ha, Injong Rhee, and Lisong Xu at North Carolina State University. Cubic uses a cubic (third-degree polynomial) growth function that is more aggressive for large windows but fair to Reno for small windows. Became the Linux default in 2006.

### 2013: Google's QUIC
Google designed QUIC (Quick UDP Internet Connections) to reduce HTTP latency. QUIC runs over UDP and provides:
- 0-RTT connection establishment (when previously connected)
- Multiplexed streams (no head-of-line blocking)
- Built-in encryption (similar to TLS 1.3)
- Connection migration (survives IP address changes)

QUIC was standardized as RFC 9000 in May 2021 and became the basis for HTTP/3.

### 2016: TCP BBR
Google's Neal Cardwell and team developed BBR (Bottleneck Bandwidth and Round-trip propagation time). Unlike loss-based algorithms, BBR is model-based:
- Estimates available bandwidth from delivery rate
- Estimates minimum RTT from timing
- Paces at the estimated bandwidth

BBR achieves higher throughput than Cubic on lossy links and avoids bufferbloat. It became the Linux default congestion control in 2024 (kernel 6.8+).

## 6. Key Milestones Timeline

| Year | Event | Impact |
|------|-------|--------|
| 1974 | Cerf & Kahn publish TCP design | Foundation of internetworking |
| 1980 | UDP standardized (RFC 768) | Connectionless transport |
| 1981 | TCP standardized (RFC 793) | Reliable stream service |
| 1983 | TCP/IP mandatory on ARPANET | Birth of modern Internet |
| 1984 | Nagle's algorithm (RFC 896) | Solved small-packet problem |
| 1986 | First congestion collapse | Motivated congestion control |
| 1988 | Jacobson's congestion control | Saved the Internet |
| 1990 | TCP Reno (fast recovery) | Faster loss recovery |
| 1992 | Window scaling (RFC 1323) | Enabled high-speed TCP |
| 1996 | Selective ACK (RFC 2018) | Efficient loss recovery |
| 2005 | TCP Cubic | High-BDP performance |
| 2013 | Google QUIC | HTTP/3 foundation |
| 2016 | TCP BBR | Model-based congestion control |
| 2021 | QUIC standardized (RFC 9000) | Modern transport protocol |

## 7. The Never-Ending Evolution

TCP and UDP continue to evolve:

**TCP improvements:**
- PLB (Proactive Load Balancing) for multi-path
- TCP-NV (Netflix's Vegas variant for datacenters)
- DCTCP (Data Center TCP) for low-latency DC networks

**UDP innovations:**
- QUIC (HTTP/3, already deployed at Google, Facebook, Cloudflare)
- SRT (Secure Reliable Transport for video)
- WebRTC (browser-based real-time communication)

The transport layer remains an active research area, with modern needs (low latency, high throughput, mobility, security) driving continued innovation.
