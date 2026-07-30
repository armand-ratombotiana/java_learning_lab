# Lab 02 — HTTP/3 and QUIC

## Overview
Deep dive into QUIC transport protocol and HTTP/3: 0-RTT handshake, connection migration, stream multiplexing, TLS 1.3 integration, and performance comparison with HTTP/2.

## Prerequisites
- Java 21+ development environment
- TCP/UDP fundamentals
- HTTP/2 knowledge helpful

## What You Will Learn
- Model QUIC connection establishment and 0-RTT handshake
- Implement stream multiplexing over single QUIC connection
- Simulate connection migration across network paths
- Understand TLS 1.3 integration in QUIC
- Compare performance characteristics with HTTP/2

## Topics Covered
| Topic | Description |
|-------|-------------|
| QUIC Features | Connection IDs, streams, reliable delivery |
| 0-RTT Handshake | Early data, replay protection, address validation |
| Connection Migration | Path change detection, continuation without re-handshake |
| Stream Multiplexing | Independent streams, no HOL blocking |
| vs HTTP/2 | Transport-layer HOL blocking elimination |
| TLS 1.3 Integration | Handshake encryption, ALPN, key update |

## Java Package
`com.networking.deep.lab02`
