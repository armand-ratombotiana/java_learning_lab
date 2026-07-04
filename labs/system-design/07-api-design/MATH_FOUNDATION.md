# API Design - MATH FOUNDATION

## Pagination Math

### Offset vs Cursor Pagination
```
Offset: O(n) queries at deep pages
  SELECT * FROM products LIMIT 20 OFFSET 10000;
  Database must scan 10020 rows, skip 10000.

Cursor: O(log n) queries
  SELECT * FROM products WHERE id > 'cursor' LIMIT 20;
  Database uses index, reads 20 rows directly.
```

### Page Number Calculation
```
total_pages = ceil(total_elements / page_size)
offset = page × size

For 1000 elements, page=50, size=20:
  offset = 50 × 20 = 1000 (already past end → empty)
```

## API Response Size

### Payload Optimization
```
Full response  : { id, name, description, price, stock, category, createdAt, updatedAt, reviews }
Partial response: { id, name, price }

Reduction: 400 bytes → 80 bytes (5x smaller)

At 1000 RPS with pagination=20:
  Full: 1000 × 20 × 400B = 8MB/s API traffic
  Partial: 1000 × 20 × 80B = 1.6MB/s API traffic
```

## Rate Limiting Math

### Token Bucket
```
capacity = 100 tokens
refill_rate = 10 tokens/second

Client sends 15 requests at t=0: 85 tokens remaining
t=1: 10 tokens added → 95 tokens
After 5 seconds of max rate:
  Tokens depleted in 100/15 = 6.67 seconds
  Stable rate: 10 RPS, burst: 100 requests
```

## Latency Budget

### API Request Latency Breakdown
```
Total Latency = DNS + TCP + TLS + Server Processing + Serialization + Data Transfer

Typical breakdown (p95):
  DNS:         5ms
  TCP connect: 10ms
  TLS handshake: 30ms
  Request upload: 5ms
  Server processing: 20ms
  Response download: 5ms
  Total: ~75ms

With keep-alive (no TCP/TLS per request):
  Total with keep-alive: ~30ms (60% reduction)
```

## Caching Impact

### HTTP Cache Hit Ratio
```
Cacheable: GET /products (changes hourly)
  Cache-Control: max-age=3600
  With 1000 requests in 1 hour, 999 hits:
  Hit ratio: 99.9%
  Server load reduction: 1000x
```

## API Versioning Cost

| Strategy | Client Impact | Maintenance |
|----------|--------------|------------|
| URL path (/v1/, /v2/) | Max complexity | Two controllers |
| Header (Accept: vnd.api.v2) | Simple | Header negotiation |
| Parameter (?version=2) | Simple | Parameter parsing |
| Media type versioning | Moderate | Complex configuration |
