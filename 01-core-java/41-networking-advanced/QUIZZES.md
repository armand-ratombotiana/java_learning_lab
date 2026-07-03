# Quizzes: Advanced Networking

Test your knowledge of TCP, UDP, Socket Options, and Multicast.

## Quiz 1: TCP vs UDP

**Q1: Which of the following statements about UDP is TRUE?**
- A) It guarantees that packets are delivered in the exact order they were sent.
- B) It requires a three-way handshake before data can be transmitted.
- C) It does not guarantee delivery; packets can be silently dropped if the network is congested.
- D) It automatically retransmits lost packets.
*Answer: C*

**Q2: If you are building a live, real-time multiplayer action game, which protocol is generally preferred for sending player movement coordinates, and why?**
- A) TCP, because you cannot afford to lose any movement data.
- B) UDP, because it is connectionless and lightweight. If a movement packet is lost, it doesn't matter, because a newer movement packet will arrive a millisecond later. TCP would pause the game to retransmit the old, useless data.
- C) TCP, because it supports encryption natively.
- D) Multicast, because it is more secure.
*Answer: B*

## Quiz 2: Socket Options

**Q1: What happens if a client unplugs their network cable while your server is blocked on `inputStream.read()`, and you have NOT set `SO_TIMEOUT`?**
- A) The `read()` method throws an `IOException` immediately.
- B) The server thread blocks forever in a "half-open" state, causing a thread leak.
- C) The server automatically closes the socket after 30 seconds.
- D) The JVM crashes.
*Answer: B*

**Q2: What does `socket.setTcpNoDelay(true)` do?**
- A) It disables TCP flow control.
- B) It disables Nagle's Algorithm, forcing the OS to send small packets immediately rather than buffering them to save bandwidth. This reduces latency.
- C) It converts a TCP socket into a UDP socket.
- D) It bypasses the OS firewall.
*Answer: B*

## Quiz 3: Multicast

**Q1: How does Multicast differ from standard UDP Unicast?**
- A) Multicast uses TCP instead of UDP.
- B) Multicast requires a dedicated server to route all messages.
- C) Multicast allows one sender to transmit a single packet to a specific group IP address, and the network routers duplicate the packet to deliver it to all clients who have "joined" that group.
- D) Multicast encrypts the payload automatically.
*Answer: C*