# HTTP Protocol - Interview Questions

## Beginner

Q: Explain the HTTP request-response cycle.
A: Client opens TCP connection, sends request (method, path, headers, body), server processes and returns response (status code, headers, body), connection is closed or kept alive.

Q: What are main HTTP methods?
A: GET (retrieve), POST (create), PUT (replace), PATCH (update), DELETE (remove), HEAD (headers only), OPTIONS (capabilities).

## Intermediate

Q: How does HTTP/2 multiplexing work?
A: Multiple streams interleave frames over single TCP connection. One slow response doesn't block others.

Q: How does content negotiation work?
A: Client sends Accept headers with quality values, server selects best representation.

## Advanced

Q: How does HPACK compression work?
A: Static table (61 predefined headers), dynamic table (recently used), Huffman encoding.

Q: How does QUIC solve TCP HOL blocking?
A: QUIC multiplexes streams at transport layer. Packet loss on one stream doesn't affect others.
