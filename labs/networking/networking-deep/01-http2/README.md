# Lab 01 — HTTP/2 Deep

## Overview
Deep dive into HTTP/2 protocol features: binary framing, multiplexing, HPACK compression, server push, stream prioritization, and flow control.

## Prerequisites
- Java 21+ development environment
- HTTP/1.1 protocol knowledge
- Basic networking concepts

## What You Will Learn
- Implement HTTP/2 binary framing and frame types
- Model stream multiplexing with concurrent request handling
- Implement HPACK header compression with static/dynamic tables
- Simulate server push scenarios
- Build stream prioritization and flow control mechanisms

## Topics Covered
| Topic | Description |
|-------|-------------|
| Binary Framing | Frame types, length, flags, stream identifier |
| Multiplexing | Concurrent streams over single TCP connection |
| HPACK | Static table, dynamic table, Huffman encoding |
| Server Push | Push promise, pushed response, cancellation |
| Stream Prioritization | Dependency tree, weight-based allocation |
| Flow Control | Window update, connection-level, stream-level |

## Java Package
`com.networking.deep.lab01`
