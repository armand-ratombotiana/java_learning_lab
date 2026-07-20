# gRPC Networking -- Interview Questions
## Common Interview Questions

### Q1: What is the TCP three-way handshake?
A: SYN -> SYN-ACK -> ACK sequence that establishes a TCP connection.

### Q2: What is the difference between TCP and UDP?
A: TCP is connection-oriented, reliable, ordered. UDP is connectionless, unreliable, unordered.

### Q3: How does non-blocking I/O differ from blocking I/O?
A: Blocking I/O waits for the operation to complete. Non-blocking returns immediately.

### Q4: What is the C10K problem?
A: Handling 10,000 concurrent connections. Thread-per-connection fails; event loop solves it.

### Q5: How does HTTP/2 multiplexing work?
A: Multiple streams over a single TCP connection, eliminating head-of-line blocking.

### Q6: What is backpressure and why is it important?
A: Backpressure regulates data flow to prevent consumers from being overwhelmed.

### Q7: Explain the Reactor pattern.
A: Event demultiplexing and dispatch: select() then handle events for each ready channel.

### Q8: How do you debug network issues in production?
A: Use thread dumps, JFR, Wireshark captures, connection metrics, and distributed tracing.
