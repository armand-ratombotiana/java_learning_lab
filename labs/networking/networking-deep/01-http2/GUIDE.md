# GUIDE — HTTP/2 Deep

## Step 1: Binary Framing
```java
public record Frame(byte type, byte flags, int streamId, byte[] payload) {}
public static final byte DATA = 0, HEADERS = 1, PRIORITY = 2, RST_STREAM = 3, SETTINGS = 4, PUSH_PROMISE = 5, PING = 6, GOAWAY = 7, WINDOW_UPDATE = 8;
```

## Step 2: Stream Multiplexing
- Assign odd/even stream IDs (client-initiated odd, server-initiated even)
- Interleave frames from different streams on single connection
- Handle concurrent stream processing

## Step 3: HPACK Compression
```java
HpackEncoder encoder = new HpackEncoder();
byte[] compressed = encoder.encode(headers); // uses static + dynamic table
```

## Step 4: Server Push
- Server sends PUSH_PROMISE frame before response
- Client can cancel push via RST_STREAM
- Browser caches pushed resources

## Step 5: Stream Prioritization
- Build dependency tree with weights
- Allocate bandwidth proportionally

## Step 6: Exercises
1. Implement header decompression with dynamic table updates
2. Build a flow controller that enforces window limits
3. Create a server push simulator for a web page with CSS/JS resources
