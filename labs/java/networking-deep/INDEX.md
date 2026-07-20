# Networking Deep - Micro-Labs

## Overview
This module contains 5 deep-dive micro-labs covering socket programming, NIO selectors, HTTP clients, Netty framework, and gRPC networking.

## Lab Index
| # | Lab | Description |
|---|-----|-------------|
| 01 | [Socket Programming](./01-socket-programming/) | ServerSocket/Socket, TCP client-server, thread-per-connection, connection pool, NIO SocketChannel |
| 02 | [NIO Selectors](./02-nio-selectors/) | Selector, SelectionKey, OP_ACCEPT/OP_READ/OP_WRITE, non-blocking IO, Reactor pattern, single-threaded event loop |
| 03 | [HTTP Clients](./03-http-clients/) | HttpClient (Java 11+), HTTP/2, WebSocket, reactive client, connection pooling, timeout/retry configuration |
| 04 | [Netty Framework](./04-netty-framework/) | ChannelHandler pipeline, EventLoopGroup, ChannelFuture, ByteBuf, Netty HTTP server, TCP server, codecs |
| 05 | [gRPC Networking](./05-grpc-networking/) | gRPC HTTP/2 framing, flow control, deadline propagation, keepalive, load balancing, reflection |

## How to Use
Each micro-lab is self-contained with 24 markdown files, Java source code, JUnit 5 tests, and 7 subdirectories for hands-on work. Start from lab 01 and progress sequentially.
