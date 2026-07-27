# HTTP Protocol — Mock Interview Questions

## Fundamentals (3 questions)

**Q1**: Walk through what happens when a user types a URL in a browser and presses Enter.

**Expected coverage**: DNS resolution, TCP 3-way handshake, TLS handshake, HTTP request/response, browser rendering pipeline.

**Q2**: Explain the difference between HTTP/1.0 and HTTP/1.1.

**Expected coverage**: Persistent connections (keep-alive), host header, chunked transfer encoding, caching headers (If-Modified-Since, ETag), OPTIONS method.

**Q3**: What are the 5 classes of HTTP status codes and examples of each?

**Expected coverage**: 1xx (101 Switching Protocols), 2xx (200 OK, 201 Created, 204 No Content), 3xx (301 Moved, 302 Found, 304 Not Modified), 4xx (400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 429 Too Many Requests), 5xx (500 Internal Server Error, 502 Bad Gateway, 503 Service Unavailable, 504 Gateway Timeout).

## Intermediate (3 questions)

**Q4**: How does HTTP caching work? Explain Cache-Control headers in detail.

**Expected coverage**: Cache-Control: public/private/no-cache/no-store/max-age/s-maxage/must-revalidate, ETag/If-None-Match, Last-Modified/If-Modified-Since, Vary header, cache tiers (browser → proxy → CDN → origin).

**Q5**: What is HTTPS? How does TLS provide security for HTTP?

**Expected coverage**: TLS handshake (1.2 vs 1.3), certificate chain, asymmetric vs symmetric encryption, cipher suites, forward secrecy, HSTS.

**Q6**: Explain HTTP pipelining. Why was it never widely adopted?

**Expected coverage**: FIFO response ordering, head-of-line blocking, proxy complexity, browser adoption challenges, HTTP/2 multiplexing as the replacement.

## Advanced (2 questions)

**Q7**: Design a REST API for a file-sharing service. Discuss versioning strategies, pagination, error handling, and rate limiting.

**Expected coverage**: URI versioning (/v1/) vs header versioning (Accept: application/vnd.api+json;version=1), cursor vs offset pagination, structured error responses (RFC 7807 Problem Details), token bucket vs sliding window rate limiting.

**Q8**: You are debugging a slow HTTPS API. Walk through all the tools and techniques you would use.

**Expected coverage**: curl -w timing, Chrome DevTools Network tab, Wireshark/tcpdump TLS handshake analysis, OpenSSL s_client, HTTP/2 frame inspection, connection reuse analysis, DNS resolution time.
