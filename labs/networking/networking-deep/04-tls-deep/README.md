# Lab 04 — TLS Deep

## Overview
Deep dive into TLS 1.3: handshake protocol, cipher suites, certificate validation, OCSP stapling, session tickets, and 0-RTT.

## Prerequisites
- Java 21+ development environment
- Public key cryptography fundamentals
- Basic SSL/TLS knowledge

## What You Will Learn
- Implement TLS 1.3 handshake message flow
- Model cipher suite negotiation
- Build certificate chain validation
- Implement OCSP stapling
- Design session ticket resumption

## Topics Covered
| Topic | Description |
|-------|-------------|
| TLS 1.3 Handshake | 1-RTT and 0-RTT, key schedule, HelloRetryRequest |
| Cipher Suites | AEAD, key exchange, HKDF, signature algorithms |
| Certificate Validation | Chain building, trust store, CRL, OCSP |
| OCSP Stapling | Certificate status extension, freshness |
| Session Tickets | PSK, ticket lifetime, resumption |
| 0-RTT | Early data, replay protection, anti-replay |

## Java Package
`com.networking.deep.lab04`
