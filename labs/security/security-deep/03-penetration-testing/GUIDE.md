# Penetration Testing — Study Guide

## Core Concepts

### Reconnaissance
- **Passive**: OSINT, DNS records, WHOIS, Shodan, Google dorking
- **Active**: direct interaction with target (nmap, netcat)

### Scanning & Enumeration
- **Port scanning**: TCP SYN scan, TCP connect scan, UDP scan
- **Service enumeration**: version detection, banner grabbing
- **Vulnerability scanning**: Nessus, OpenVAS, Nikto for web

### Exploitation Phases
1. Gain access (exploit vulnerability)
2. Escalate privileges (vertical/horizontal)
3. Maintain access (persistence, backdoors)
4. Move laterally (pivot to other systems)

### Risk Classification
- **Critical**: CVSS 9.0-10.0
- **High**: CVSS 7.0-8.9
- **Medium**: CVSS 4.0-6.9
- **Low**: CVSS 0.1-3.9

## Implementation Checklist
1. Obtain written authorization before testing
2. Define scope and rules of engagement
3. Use safe scan options to avoid service disruption
4. Document every finding with reproducibility steps

## Common Pitfalls
- Scanning without authorization (illegal)
- Aggressive scanning crashing services
- Credential dumping in customer environment without approval
- Incomplete reporting (missing remediation steps)
