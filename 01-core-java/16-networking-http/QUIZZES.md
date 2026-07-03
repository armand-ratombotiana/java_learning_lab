# Module 16: Networking & HTTP - Quizzes

---

## Q1: Sockets vs ServerSockets
What is the primary difference between a `Socket` and a `ServerSocket` in Java?

A) A `Socket` is used for UDP, while `ServerSocket` is used for TCP.
B) A `Socket` represents a client endpoint, while a `ServerSocket` waits for client connections.
C) A `ServerSocket` can only run on localhost.
D) There is no difference.

**Answer**: B
**Explanation**: A `ServerSocket` listens on a specific port for incoming connection requests. Once a connection is made, it returns a `Socket` object to communicate with the client.

---

## Q2: Timeouts
Which method is used to set the timeout for reading from a traditional Java `Socket`?

A) `setConnectionTimeout()`
B) `setSoTimeout()`
C) `setReadTimeout()`
D) `setTimeout()`

**Answer**: B
**Explanation**: `setSoTimeout(int timeout)` enables/disables `SO_TIMEOUT` with the specified timeout in milliseconds, ensuring `read()` operations don't block forever.

---

## Q3: Java 11 HttpClient
Which of the following classes is required to construct an HTTP request using the Java 11+ HttpClient API?

A) `HttpURLConnection`
B) `HttpRequest.Builder`
C) `SocketRequest`
D) `ClientRequest`

**Answer**: B
**Explanation**: The `HttpRequest` objects are immutable and created using the builder pattern via `HttpRequest.newBuilder()`.