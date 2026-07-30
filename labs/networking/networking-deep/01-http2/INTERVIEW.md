# Interview Questions — HTTP/2 Deep

## Beginner

Q: What are the main features of HTTP/2?
A: Binary framing, multiplexing, HPACK compression, server push, stream prioritization, flow control.

Q: How is HTTP/2 different from HTTP/1.1?
A: Binary instead of text, multiplexed instead of sequential, compressed headers, server-initiated pushes.

## Intermediate

Q: How does HPACK compression work?
A: Static table (61 predefined headers), dynamic table (recently seen headers), Huffman encoding for string literals.

Q: What problem does HTTP/2 multiplexing solve?
A: Eliminates HOL blocking at application layer where one slow response blocks others in HTTP/1.1.

## Advanced

Q: How does HTTP/2 flow control work at stream and connection level?
A: WINDOW_UPDATE frames adjust flow control window; both stream-level and connection-level windows apply; prevents receiver from being overwhelmed.

Q: Explain stream prioritization in HTTP/2 and its use cases.
A: Dependency tree where each stream has a parent and weight. Browser signals critical resources first; server allocates bandwidth accordingly.
