# Networking LeetCode — Pattern Cheatsheet

> 200+ lines covering socket programming, HTTP client/server, DNS resolution, network flow algorithms, IP addressing.

---

## 1. Socket Programming Patterns

### TCP Echo Server

```python
import socket

def tcp_echo_server(host='0.0.0.0', port=8080):
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server.bind((host, port))
    server.listen(5)
    while True:
        client, addr = server.accept()
        data = client.recv(1024)
        client.send(data)
        client.close()
```

### TCP Echo Client

```python
def tcp_echo_client(host='127.0.0.1', port=8080, message='hello'):
    client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client.connect((host, port))
    client.sendall(message.encode())
    response = client.recv(1024)
    client.close()
    return response
```

### UDP Echo Server

```python
def udp_echo_server(host='0.0.0.0', port=8080):
    server = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    server.bind((host, port))
    while True:
        data, addr = server.recvfrom(1024)
        server.sendto(data, addr)
```

### Common Interview Variations

| Pattern | Technique | Example |
|---------|-----------|---------|
| Connection pooling | Reuse sockets, timeouts | HTTP connection pool |
| Non-blocking I/O | select/poll/epoll/kqueue | Chat server |
| Async socket | asyncio | WebSocket server |
| Socket options | SO_KEEPALIVE, TCP_NODELAY | Real-time apps |
| Multiplexing | selectors module | Handle many clients |

### Key Socket Options

| Option | Purpose | Code |
|--------|---------|------|
| TCP_NODELAY | Disable Nagle's algorithm | `s.setsockopt(IPPROTO_TCP, TCP_NODELAY, 1)` |
| SO_KEEPALIVE | Detect dead connections | `s.setsockopt(SOL_SOCKET, SO_KEEPALIVE, 1)` |
| SO_REUSEADDR | Reuse port in TIME_WAIT | `s.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)` |
| SO_LINGER | Control close behavior | `s.setsockopt(SOL_SOCKET, SO_LINGER, l_onoff=1, l_linger=0)` |

---

## 2. HTTP Client/Server

### Minimal HTTP Server

```python
from http.server import HTTPServer, BaseHTTPRequestHandler

class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.end_headers()
        self.wfile.write(b'Hello, world')

server = HTTPServer(('', 8080), Handler)
server.serve_forever()
```

### HTTP Request Parsing

```python
# Parse raw HTTP request
def parse_http_request(data):
    lines = data.split(b'\r\n')
    method, path, version = lines[0].decode().split(' ')
    headers = {}
    body = b''
    i = 1
    while lines[i]:
        key, val = lines[i].decode().split(':', 1)
        headers[key.strip()] = val.strip()
        i += 1
    if b'Content-Length' in data:
        body = b'\r\n'.join(lines[i+1:])
    return method, path, headers, body
```

### HTTP Client (raw socket)

```python
def http_get(host, path='/'):
    client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client.connect((host, 80))
    request = f'GET {path} HTTP/1.1\r\nHost: {host}\r\nConnection: close\r\n\r\n'
    client.sendall(request.encode())
    response = b''
    while True:
        chunk = client.recv(4096)
        if not chunk: break
        response += chunk
    client.close()
    return response
```

### Common HTTP LeetCode Problems

| Problem Type | Key Skill | Approach |
|-------------|-----------|----------|
| Web crawler | HTTP parsing, BFS/DFS | Queue of URLs, dedup visited |
| Rate limiter | Token bucket, sliding window | Redis sorted sets per client |
| URL shortener | Hash generation, storage mapping | Base62 encoding, distributed ID |
| HTML parser | Regex, DOM traversal | Stack-based tag matching |
| Proxy server | Request forwarding, connection reuse | Forward/Reverse, caching |

---

## 3. DNS Resolution (implemented)

### Simple DNS Query (Python)

```python
import socket
import struct

def build_dns_query(domain):
    id = 0x1234
    flags = 0x0100  # standard query
    qdcount = 1
    header = struct.pack('>HHHHHH', id, flags, qdcount, 0, 0, 0)
    question = b''
    for part in domain.split('.'):
        question += bytes([len(part)]) + part.encode()
    question += b'\x00'  # end of domain
    question += struct.pack('>HH', 1, 1)  # type A, class IN
    return header + question

def resolve_dns(domain, dns_server='8.8.8.8'):
    query = build_dns_query(domain)
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.settimeout(5)
    sock.sendto(query, (dns_server, 53))
    response, _ = sock.recvfrom(512)
    sock.close()
    return response
```

### DNS Response Parsing

```python
def parse_dns_response(response):
    # Skip header (12 bytes)
    idx = 12
    # Skip question section
    while response[idx] != 0:
        idx += 1
    idx += 5  # null byte + QTYPE + QCLASS
    # Parse answer section
    answers = []
    while idx < len(response):
        # Name (pointer or sequence)
        if response[idx] & 0xC0 == 0xC0:  # pointer
            ptr = struct.unpack('>H', response[idx:idx+2])[0] & 0x3FFF
            idx += 2
        else:
            while response[idx] != 0: idx += 1
            idx += 1
        rtype, rclass, ttl, rdlength = struct.unpack('>HHIH', response[idx:idx+10])
        idx += 10
        if rtype == 1:  # A record
            ip = socket.inet_ntoa(response[idx:idx+4])
            answers.append(ip)
        idx += rdlength
    return answers
```

---

## 4. Network Flow Algorithms

### Ford-Fulkerson (Max Flow)

```python
def ford_fulkerson(graph, source, sink):
    # graph: adjacency matrix, returns max flow
    n = len(graph)
    residual = [row[:] for row in graph]
    parent = [-1] * n
    max_flow = 0

    def bfs():
        visited = [False] * n
        queue = [source]
        visited[source] = True
        while queue:
            u = queue.pop(0)
            for v in range(n):
                if not visited[v] and residual[u][v] > 0:
                    queue.append(v)
                    visited[v] = True
                    parent[v] = u
        return visited[sink]

    while bfs():
        path_flow = float('inf')
        v = sink
        while v != source:
            u = parent[v]
            path_flow = min(path_flow, residual[u][v])
            v = parent[u] if u != source else source
        v = sink
        while v != source:
            u = parent[v]
            residual[u][v] -= path_flow
            residual[v][u] += path_flow
            v = parent[u] if u != source else source
        max_flow += path_flow

    return max_flow
```

### Other Flow Algorithms

| Algorithm | Complexity | Use Case |
|-----------|------------|----------|
| Edmonds-Karp | O(VE²) | Simple implementation |
| Dinic | O(EV²) | Dense graphs |
| Push-Relabel | O(V³) | Parallel computation |
| Min-Cut Max-Flow | O(F·E) | Cut identification |

### Networking Applications

- Bandwidth allocation between nodes
- Traffic engineering / load balancing
- Determining max data transfer between endpoints
- Finding critical links (min-cut analysis)

---

## 5. IP Addressing & Subnetting

### IPv4 to Binary

```python
def ip_to_binary(ip_str):
    octets = [int(x) for x in ip_str.split('.')]
    return ''.join(f'{o:08b}' for o in octets)

def binary_to_ip(binary):
    octets = [int(binary[i:i+8], 2) for i in range(0, 32, 8)]
    return '.'.join(str(o) for o in octets)
```

### CIDR Calculation

```python
def cidr_range(cidr):
    ip_str, prefix = cidr.split('/')
    prefix = int(prefix)
    ip_bin = ip_to_binary(ip_str)
    network_bin = ip_bin[:prefix] + '0' * (32 - prefix)
    broadcast_bin = ip_bin[:prefix] + '1' * (32 - prefix)
    network = binary_to_ip(network_bin)
    broadcast = binary_to_ip(broadcast_bin)
    return network, broadcast
```

### Subnet Calculation

```python
def subnet_info(cidr):
    ip, prefix = cidr.split('/')
    prefix = int(prefix)
    hosts = (2 ** (32 - prefix)) - 2  # exclude network and broadcast
    netmask = '.'.join(str((0xFFFFFFFF << (32 - prefix) >> (8*i)) & 0xFF) for i in range(3, -1, -1))
    return {'subnet': cidr, 'netmask': netmask, 'usable_hosts': max(0, hosts)}
```

### Common IP Patterns

| Pattern | Problem | Approach |
|---------|---------|----------|
| Validate IP | IPv4/IPv6 validation | Regex, octet range check |
| CIDR merge | Merge overlapping CIDRs | Sort by prefix, greedy merge |
| IP to CIDR | Group IPs into smallest CIDR | Binary tree / trie |
| Subnet membership | Check if IP x in subnet y | Bitwise AND with netmask |
| DNS resolver | Resolve domain to IP | Socket DNS query |
| CDN origin selection | Route user to nearest PoP | Geo-IP, latency-based |

---

## Quick Reference: Common Network Data Structures

| Data Structure | Network Use |
|----------------|-------------|
| Trie | CIDR prefix matching, routing table lookup |
| Graph (adjacency) | Network topology, BGP path analysis |
| Queue | Packet queueing, traffic shaping |
| Hash Table | Connection tracking, NAT table, ARP cache |
| Bloom Filter | Packet dedup, DAG (distributed aggregation) |
| Min-Heap | QoS priority scheduling, retransmission timeout |
| Circular Buffer | Packet reassembly, ring buffer in NIC |
| Consistent Hash Ring | Load balancer distribution, CDN cache routing |

---

*"Know your data structures — a trie routes IPs, a graph routes traffic, a queue shapes packets."*
