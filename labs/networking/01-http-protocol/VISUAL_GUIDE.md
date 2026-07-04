# HTTP Protocol - Visual Guide

## HTTP Request/Response Flow

```
+--------+                    +--------+
| Client |                    | Server |
+--------+                    +--------+
     |                             |
     |--- DNS Lookup ------------->|
     |<-- IP Address --------------|
     |--- TCP SYN ---------------->|
     |<-- TCP SYN-ACK -------------|
     |--- TCP ACK ---------------->|
     |--- TLS ClientHello -------->|
     |<-- TLS ServerHello + Cert --|
     |--- HTTP Request ----------->|
     |<-- HTTP Response -----------|
```

## HTTP Message Structure

```
POST /api/users HTTP/1.1
Host: api.example.com
Content-Type: application/json
Authorization: Bearer <token>
Content-Length: 42
X-Request-Id: 7a8b3c2d-1e4f

{"username":"john","email":"john@example.com"}
```

## HTTP/2 Stream Multiplexing

```
TCP Connection
  Stream 1 (HEADERS)  |  :method=GET  |  :path=/a
  Stream 3 (HEADERS)  |  :method=GET  |  :path=/b
  Stream 2 (HEADERS)  |  :method=GET  |  :path=/c
  Stream 1 (DATA)     |  payload for /a
  Stream 4 (HEADERS)  |  :method=GET  |  :path=/d
```

## Content Negotiation

```
Client                          Server
  |                               |
  | Accept: application/json      |
  | Accept-Language: en-US        |
  | Accept-Encoding: gzip         |
  |                               |
  |<------------------------------|
  | Content-Type: application/json|
  | Content-Language: en-US       |
  | Content-Encoding: gzip        |
```
