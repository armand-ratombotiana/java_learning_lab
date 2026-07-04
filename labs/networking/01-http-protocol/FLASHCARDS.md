# HTTP Protocol - Flashcards

Q: What is HTTP?
A: Hypertext Transfer Protocol - application-layer protocol for distributed hypermedia systems.

Q: Difference between HTTP/1.1 and HTTP/2?
A: HTTP/1.1 is text-based with HOL blocking; HTTP/2 is binary with multiplexing and header compression.

Q: What is HTTP/3?
A: HTTP/3 uses QUIC (UDP) transport offering 0-RTT and no HOL blocking.

Q: Idempotent HTTP methods?
A: GET, HEAD, PUT, DELETE, OPTIONS.

Q: Difference between 401 and 403?
A: 401=authentication required, 403=authenticated but not authorized.

Q: What is HPACK?
A: HTTP/2 header compression using static/dynamic tables and Huffman coding.

Q: What is Content Negotiation?
A: Client and server agree on response format via Accept headers.

Q: Status code 429?
A: Too Many Requests.

Q: What is HSTS?
A: HTTP Strict Transport Security - tells browsers to always use HTTPS.

Q: Difference between 301 and 308?
A: Both permanent redirects; 301 may change method to GET, 308 preserves method.
