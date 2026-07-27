# Network Debugging — Interview Guide

> 250+ lines covering traceroute, tcpdump, Wireshark, netstat, ping, mtr, iperf, DNS lookup tools with interview scenarios.

---

## 1. The Debugging Mindset

### Systematic Approach (The "4 Ws")

| Step | Question | Example |
|------|----------|---------|
| **W**hat | What's the problem? | "Users report 30-second page loads" |
| **W**here | Where is the problem? | "Only from EU region, not US" |
| **W**hy | Why is it happening? | "TCP retransmissions due to packet loss" |
| **W**hat now | What's the fix? | "Enable BBR, adjust MTU, move to CDN" |

### Interview Golden Rule

*Every interview answer should follow: Symptom → Hypothesis → Evidence → Conclusion → Action.*

---

## 2. Essential Tools Reference

### ping — Basic Connectivity Checks

```
ping -c 10 8.8.8.8          # Send 10 ICMP echo requests
ping -c 100 -i 0.1 8.8.8.8  # 100 pings, every 100ms
ping -s 1472 8.8.8.8        # Set packet size (for MTU discovery)
```

**What it tells you:**
- Host is reachable (or not)
- Round-trip time (min/avg/max)
- Packet loss percentage
- RTT variance (jitter)

**Limitations:**
- ICMP may be blocked by firewalls
- Doesn't tell you WHERE the problem is (only end-to-end)
- Some hosts deprioritize ICMP over application traffic

### traceroute / tracert — Path Discovery

```
traceroute -n 8.8.8.8        # No DNS lookups (faster)
traceroute -q 5 8.8.8.8      # 5 probes per hop
traceroute -T -p 80 1.1.1.1  # TCP SYN probes (good when ICMP blocked)
traceroute -U -p 53 1.1.1.1  # UDP probes (default on Linux)
```

**What it tells you:**
- Path from source to destination (each hop IP)
- Latency per hop (useful for identifying slow middleboxes)
- Where packets are dropped (stars/asterisks = no response)

**Limitations:**
- Only shows forward path (often asymmetric)
- Load balancers may give confusing results
- Some hops intentionally don't respond

### mtr — The Best of Both (ping + traceroute)

```
mtr -r 8.8.8.8               # Report mode (non-interactive)
mtr -r -c 100 8.8.8.8        # 100 packets per hop
mtr -r -n google.com         # Numeric, no DNS
```

**What it tells you:**
- Path like traceroute, with continuous probes like ping
- Packet loss percentage per hop
- Best/worst/avg/last latency per hop
- Identifies where along the path loss is occurring

### tcpdump — Packet Capture

```
tcpdump -i eth0                         # Capture on interface eth0
tcpdump -i eth0 host 10.0.0.1          # Filter by host
tcpdump -i eth0 port 443               # Filter by port
tcpdump -i eth0 tcp and port 80        # TCP traffic on port 80
tcpdump -i any -c 100 -w capture.pcap  # Capture 100 packets to file
tcpdump -r capture.pcap                # Read from file
tcpdump -n -X                          # Show hex + ASCII
tcpdump -i eth0 'tcp[tcpflags] & tcp-syn != 0'  # SYN packets only
```

**What to look for:**
- TCP retransmissions (dup ACKs, retransmitted sequence numbers)
- TCP zero window (receiver is overwhelmed)
- SYN floods (many SYNs, no SYN-ACKs)
- MTU issues (fragmentation needed but DF set)

### netstat / ss — Connection State

```
netstat -tuln                  # Listening TCP/UDP sockets
netstat -ant | head            # All TCP connections (state, addresses)
ss -tuln                       # Modern version (faster, more info)
ss -i                         # TCP socket info (cwnd, rtt, etc.)
ss -oit state time-wait       # Sockets in TIME_WAIT
```

**What to look for:**
- Many connections in TIME_WAIT (socket reuse issue)
- Connection refused (no listener on port)
- High count of SYN_SENT (network issues)
- Recv-Q / Send-Q > 0 (data queued — possible slowdown)

### iperf / iperf3 — Bandwidth Testing

```
# Server (on receiver):
iperf3 -s -p 5201

# Client (on sender):
iperf3 -c 10.0.0.1 -p 5201
iperf3 -c 10.0.0.1 -u -b 100m  # UDP test, 100 Mbps target
iperf3 -c 10.0.0.1 -P 4        # 4 parallel streams
iperf3 -c 10.0.0.1 -R          # Reverse mode (download test)
```

**What it tells you:**
- Maximum achievable throughput
- Is the bottleneck bandwidth or latency?
- Jitter and packet loss (UDP mode)
- TCP congestion control effectiveness

### dig / nslookup — DNS Debugging

```
dig google.com                              # Standard A record lookup
dig google.com AAAA                         # IPv6 record
dig @8.8.8.8 google.com                     # Use specific resolver
dig google.com NS                           # Name server records
dig google.com MX                           # Mail exchange records
dig google.com +trace                       # Trace resolution path
dig -x 8.8.8.8                              # Reverse DNS (PTR)
dig google.com ANY +noall +answer           # All records, just answers
nslookup -type=any google.com 1.1.1.1       # Any record via Cloudflare
```

**What to look for:**
- Non-authoritative vs authoritative answers
- DNS query time (indicates resolver latency)
- TTL values (too short = many queries, too long = slow change propagation)
- Missing records (NXDOMAIN vs NOERROR with empty answer)

---

## 3. Interview Scenarios

### Scenario 1: "Website is loading very slowly"

**Initial Symptom**: Users report page load times > 10 seconds.

**Hypothesis 1**: DNS resolution is slow.
- Action: `dig google.com` — check query time
- Evidence: Query time > 1 second indicates resolver issue
- Action: `dig @8.8.8.8 google.com` — compare with Google DNS
- Conclusion: If 8.8.8.8 is fast, upstream resolver is the problem
- Fix: Change DNS resolver, implement local caching

**Hypothesis 2**: CDN/Origin is overloaded.
- Action: `curl -w "%{time_total}" -o /dev/null -s https://example.com`
- Evidence: High `time_total` but low `time_connect`, suggests server processing
- Action: `tcpdump -i any port 443 -c 100`
- Evidence: Look for TCP window updates, zero windows
- Conclusion: Server processing time is bottleneck
- Fix: Add more origin servers, enable caching

### Scenario 2: "Users in one region can't connect"

**Initial Symptom**: APAC users get connection timeout. US users are fine.

**Hypothesis**: BGP routing issue (path too long or blackholed).
- Action: `mtr -r -c 50 apac-server.example.com` from a US host
- Evidence: Show high latency or packet loss at specific AS hop
- Action: Check looking glass from multiple vantage points
- Evidence: Route leak or de-peering event between ISPs
- Fix: Contact ISP, adjust BGP communities to prefer different path

### Scenario 3: "Intermittent packet loss"

**Initial Symptom**: Occasional timeouts, retransmissions, VoIP quality issues.

**Hypothesis**: MTU mismatch causing fragmentation problems.
- Action: `ping -M do -s 1472 8.8.8.8` (DF bit set, 1472 payload = 1500 MTU)
- Evidence: Larger pings fail, smaller pings succeed
- Action: `ping -M do -s 1450` — find working MTU
- Fix: Set `ip path MTU discovery` or adjust MSS clamping

**Hypothesis**: Full switch buffer causing microburst drops.
- Action: `ss -i` looking for high retransmission rates
- Action: Check switch interface counters (CRC errors, drops, pauses)
- Fix: Implement flow control, rate limiting, increase buffer capacity

### Scenario 4: "DDoS attack in progress"

**Initial Symptom**: Service latency spikes, error rate 50%, unusual traffic patterns.

**Immediate Actions:**
1. `tcpdump -i any -c 1000 -n src net 10.0.0.0/8` — identify source IPs
2. `ss -ant | awk '{print $5}' | sort | uniq -c | sort -rn | head -10` — top source IPs
3. `iptables -A INPUT -s ATTACKER_IP -j DROP` or similar via cloud WAF
4. Enable rate limiting / challenge page for suspicious traffic

**Post-incident:**
- Analyze captured traffic to identify attack vector
- Implement permanent filtering and WAF rules
- Enable DDoS protection service (Cloudflare, AWS Shield, Azure DDoS)

### Scenario 5: "SSL/TLS handshake failing"

**Initial Symptom**: Users get SSL_ERROR_HANDSHAKE_ALERT.

**Hypothesis**: TLS version mismatch.
- Action: `openssl s_client -connect example.com:443 -tls1_3` — check supported
- Action: `openssl s_client -connect example.com:443 -tls1_2`
- Evidence: One works, the other fails. Server doesn't support modern TLS.
- Fix: Update server TLS configuration, enable 1.2/1.3

**Hypothesis**: Certificate expired or invalid.
- Action: `openssl s_client -connect example.com:443` — check certificate dates
- Evidence: "certificate has expired" or "self-signed certificate"
- Fix: Renew certificate from CA, update trust store

---

## 4. Debugging Scripts for Interviews

### Quick Network Health Check

```bash
#!/bin/bash
# network_health.sh — run on first suspicion

TARGET="${1:-8.8.8.8}"
PORT="${2:-443}"

echo "=== DNS Resolution ==="
dig +short $TARGET

echo "=== Ping (10 packets) ==="
ping -c 10 $TARGET | tail -3

echo "=== MTR (path + loss) ==="
mtr -r -c 20 $TARGET | tail -20

echo "=== TCP Connection Test ==="
time nc -zv $TARGET $PORT

echo "=== Open Connections ==="
ss -ant | awk '{print $6}' | sort | uniq -c | sort -rn

echo "=== Interface Stats ==="
netstat -i
```

### tcpdump HTTP Analysis

```bash
# Capture HTTP request/response
tcpdump -i eth0 -A 'tcp and port 80 and (tcp[((tcp[12:1] & 0xf0) >> 2):4] = 0x47455420)'
# Explanation: matches "GET " at start of TCP payload
```

### TCP Retransmission Detection

```bash
# Watch for TCP retransmissions in real-time
tcpdump -i eth0 'tcp[tcpflags] & (tcp-retrans) != 0'
# Or with more detail
tcpdump -i eth0 -v 'tcp and (tcp[13] & 8 == 8)'  # TCP flags with RST
```

---

## 5. Interview Walkthrough Template

### How to Structure Any Debugging Answer

1. **Clarify the problem**
   - "What does 'slow' mean exactly? What metric?"
   - "When did it start? Is it all users or specific ones?"
   - "What changed recently?"

2. **Gather data**
   - Start broad: ping, DNS check, curl timing
   - Narrow: mtr (path), tcpdump (packets), netstat (connections)

3. **Form hypothesis**
   - "I suspect it's [DNS / routing / server load / congestion] because..."

4. **Test the hypothesis**
   - "To confirm, I'll run [specific command] and look for [specific output]."

5. **Conclusion and action**
   - "The evidence confirms [root cause]. I will [fix, mitigate, escalate]."
   - "After the fix, I'll confirm with [re-run tool]."

6. **Prevent recurrence**
   - "To prevent this, I'll add monitoring for [indicator] and alert at [threshold]."

---

## 6. Tool Selection by Problem Type

| Problem | Primary Tool | Confirm With |
|---------|-------------|--------------|
| No connectivity | ping | traceroute / mtr |
| Slow connection | mtr | iperf, tcpdump |
| Intermittent drops | mtr (continuous) | ping -c 1000 |
| DNS issue | dig +trace | nslookup |
| Application slowness | curl -w | tcpdump |
| SSL/TLS issue | openssl s_client | curl -v |
| Bandwidth | iperf3 | tcpdump + throughput calc |
| Connection overflow | ss / netstat | tcpdump SYN rate |
| Packet loss | ping -c 1000 | tcpdump retransmissions |
| Path asymmetry | traceroute forward + reverse | Looking glass |

---

## Quick Reference: TCP Flags in tcpdump Filters

| Flag | Filter | Description |
|------|--------|-------------|
| SYN | `tcp[tcpflags] & tcp-syn != 0` | Connection start |
| SYN-ACK | `tcp[tcpflags] & (tcp-syn|tcp-ack) == (tcp-syn|tcp-ack)` | SYN + ACK |
| FIN | `tcp[tcpflags] & tcp-fin != 0` | Connection close |
| RST | `tcp[tcpflags] & tcp-rst != 0` | Connection reset |
| PSH | `tcp[tcpflags] & tcp-push != 0` | Push data immediately |
| ACK | `tcp[tcpflags] & tcp-ack != 0` | Acknowledgment |
| URG | `tcp[tcpflags] & tcp-urg != 0` | Urgent data |

---

*"A well-structured debugging process beats two decades of intuition."*
