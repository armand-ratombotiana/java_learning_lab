# Lab 03 — DNS Deep

## Overview
Deep dive into DNS: resolution process, authoritative vs recursive resolvers, DNSSEC, DNS over HTTPS/TLS, zone transfers, and anycast routing.

## Prerequisites
- Java 21+ development environment
- Basic networking concepts
- Understanding of DNS fundamentals

## What You Will Learn
- Model DNS message format and resource records in Java
- Implement recursive and iterative resolution algorithms
- Build DNSSEC validation chain
- Implement DNS over HTTPS (DoH) and DNS over TLS (DoT) clients
- Model zone transfer and anycast routing

## Topics Covered
| Topic | Description |
|-------|-------------|
| DNS Resolution | Recursive vs iterative, caching, TTL |
| Authoritative vs Recursive | Authoritative servers, recursive resolvers, stub resolvers |
| DNSSEC | RRSIG, DNSKEY, DS, trust anchor, validation |
| DNS over HTTPS/TLS | Encrypted DNS, HTTP/2 transport, TLS connections |
| Zone Transfers | AXFR, IXFR, NOTIFY, secondary DNS |
| Anycast | BGP anycast, load distribution, DDoS mitigation |

## Java Package
`com.networking.deep.lab03`
