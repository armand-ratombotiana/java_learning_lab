# Network Security — Mock Interview Questions

## Fundamentals (3 questions)

**Q1**: Explain the OSI model layers and common security threats at each layer.

**Expected coverage**: L1 Physical (wiretapping, physical access), L2 Data Link (MAC spoofing, ARP spoofing, VLAN hopping), L3 Network (IP spoofing, route poisoning), L4 Transport (SYN flood, port scanning, session hijacking), L5 Session (session replay), L6 Presentation (SSL stripping, downgrade attacks), L7 Application (SQLi, XSS, CSRF, OWASP Top 10). Defense per layer (encryption, segmentation, firewalls, WAF, patching).

**Q2**: What is the difference between stateful and stateless firewalls?

**Expected coverage**: Stateless (packet filter): inspects each packet individually (source/dest IP, port, protocol), no connection tracking, simple, fast, less secure. Stateful: tracks connection state (SYN→SYN-ACK→ESTABLISHED), allows return traffic automatically, more secure, higher overhead. Also: Deep Packet Inspection (DPI for application-level filtering), Next-Gen Firewall (NGFW adds IPS, identity, app awareness). AWS SG = stateful, NACL = stateless.

**Q3**: What is IPsec? How does it work in tunnel vs transport mode?

**Expected coverage**: IPsec suite for secure IP communication: AH (Authentication Header: integrity + auth, no encryption) and ESP (Encapsulating Security Payload: encryption + auth). Tunnel mode (entire IP packet encrypted → new IP header, VPN use case), Transport mode (only payload encrypted, original IP header, host-to-host). IKE (Internet Key Exchange) for key negotiation, SA (Security Association), SPD (Security Policy Database). Common use: site-to-site VPN.

## Intermediate (3 questions)

**Q4**: Explain common DDoS attack vectors and how to defend against each.

**Expected coverage**: Volumetric (UDP amplification, NTP amplification, DNS amplification → overflow bandwidth, defense: scrubbing, rate limiting, cloud DDoS protection), Protocol (SYN flood, ping of death → exhaust server resources, defense: SYN cookies, connection limits, resource tuning), Application (HTTP flood, slowloris, low-and-slow → exhaust application resources, defense: WAF rate limiting, challenge page, CAPTCHA, anomaly detection).

**Q5**: How do you secure a cloud VPC? Walk through defense in depth for a multi-tier application.

**Expected coverage**: Perimeter (Internet Gateway → ACLs → WAF), VPC-level (NACLs for stateless subnet filtering), Instance-level (Security Groups for stateful filtering per resource), Host-level (iptables/nftables, fail2ban), Application-level (auth JWT, OAuth, API keys, mTLS), Data-level (encryption at rest via AWS KMS, in transit via TLS). VPC endpoints (avoid data exfiltration via internet), flow logs (audit and detect anomalies), network segmentation (public/private subnets).

**Q6**: What is ARP spoofing? How do you detect and prevent it?

**Expected coverage**: ARP spoofing: attacker sends forged ARP replies, maps their MAC to victim's IP, intercepts traffic. Detection: arpwatch (monitor MAC changes), DHCP snooping (validates ARP from trusted DHCP-assigned IPs), port security (limit MACs per port). Prevention: Dynamic ARP Inspection (DAI, validates ARP against DHCP snooping binding), static ARP entries, IEEE 802.1X (port authentication), network segmentation (reduces ARP scope), encrypted communication (makes interception harder to exploit).

## Advanced (2 questions)

**Q7**: Design a network security architecture for a PCI DSS compliant cloud application.

**Expected coverage**: Segmentation (CDE: Cardholder Data Environment isolated from non-CDE, strict firewall between segments), Encryption (TLS 1.2+ for transit, AES-256 for cardholder data at rest), Access control (least privilege, MFA, bastion hosts with audit logs, IAM roles not keys), Monitoring (IDS/IPS, SIEM + log correlation, real-time alerting), Penetration testing (annual + after significant changes), WAF (protect against OWASP Top 10), DDoS protection, file integrity monitoring.

**Q8**: Your company suffered a DNS hijacking attack. Design a defense strategy.

**Expected coverage**: DNSSEC (validate DNS responses, prevent spoofing), registry lock (prevent unauthorized domain transfers), multi-factor auth for DNS registrar, monitoring (DNS query anomaly detection: unusual TTLs, unexpected authoritative servers short-lived), RPKI/ROA (prevent BGP hijacking of your IP space), KSK/ZSK rollover procedures, DNSSEC validation at recursive resolvers. Short TTL for fast recovery but increased query load. Incident response: verify domain registrar, rotate DNSSEC keys, notify upstream DNS providers.
