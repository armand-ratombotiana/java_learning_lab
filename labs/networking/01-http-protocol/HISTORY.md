# HTTP Protocol - History

## Timeline

### 1989 - The Proposal
Tim Berners-Lee submitted "Information Management: A Proposal" at CERN.

### 1991 - HTTP/0.9 (The One-Line Protocol)
Only GET method, no headers, no status codes. Response was raw HTML.

### 1996 - HTTP/1.0 (RFC 1945)
Added versioning, status codes, Content-Type, caching via Expires, Basic auth.
Limitation: New TCP connection per request.

### 1997 - HTTP/1.1 (RFC 2068, updated by RFC 2616 in 1999)
Major improvements: persistent connections, pipelining, Host header (virtual hosting), chunked transfer encoding, Cache-Control, content negotiation, ETag, range requests.

```java
Socket socket = new Socket("example.com", 80);
PrintWriter out = new PrintWriter(socket.getOutputStream());
out.print("GET /page1 HTTP/1.1\r\nHost: example.com\r\nConnection: keep-alive\r\n\r\n");
out.print("GET /page2 HTTP/1.1\r\nHost: example.com\r\nConnection: keep-alive\r\n\r\n");
out.flush();
```

### 2012 - SPDY (Google)
Experimental protocol: multiplexed streams, header compression, server push.

### 2015 - HTTP/2 (RFC 7540)
Binary protocol, HPACK compression, stream multiplexing, server push, ALPN.

```java
HttpClient client = HttpClient.newBuilder().version(Version.HTTP_2).build();
HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
System.out.println("Version: " + response.version());
```

### 2022 - HTTP/3 (RFC 9114)
QUIC-based: 0-RTT, no HOL blocking, connection migration.

### Java HTTP Support Evolution
| Java | Year | Feature |
|------|------|---------|
| JDK 1.0 | 1996 | URLConnection |
| Java 11 | 2018 | HttpClient (standard, HTTP/2) |
| Java 21 | 2023 | HTTP/3 client finalized |
