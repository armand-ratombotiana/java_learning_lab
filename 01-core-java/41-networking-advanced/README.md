# Module 41: Networking Advanced

## Overview
This module explores the transport layer of network programming in Java. Moving beyond simple HTTP requests, you will learn how to manipulate raw TCP and UDP sockets, tune socket options for high-performance applications, and implement one-to-many broadcasting using UDP Multicast.

## Learning Objectives
By the end of this module, you will be able to:
1. Compare and contrast the architectural guarantees of TCP vs. UDP and choose the correct protocol for a given domain.
2. Configure critical Socket Options like `SO_TIMEOUT` to prevent thread leaks and `TCP_NODELAY` to minimize latency.
3. Implement raw UDP communication using `DatagramSocket` and `DatagramPacket`.
4. Implement one-to-many network broadcasting using `MulticastSocket` or `DatagramChannel`.
5. Identify and prevent severe network bugs including half-open connections, UDP fragmentation, and `TIME_WAIT` port exhaustion.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on TCP vs UDP, Socket Options (Nagle's Algorithm), Datagrams, and Multicast routing.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a real-time UDP Multicast Ticker Feed with a Publisher and multiple independent Subscribers.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about half-open connection thread leaks, UDP packet truncation, and Multicast TTL restrictions.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of protocol guarantees, socket timeouts, and multicast mechanics.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding Nagle's Algorithm, `TIME_WAIT` exhaustion, and the $O(1)$ efficiency of Multicast.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of basic I/O (Module 07).
*   Understanding of NIO Channels and Buffers (Module 39).
*   Basic understanding of IP addresses and ports.