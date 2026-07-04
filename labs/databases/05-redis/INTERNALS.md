# Internals: Redis

## Memory Layout

### Redis Object (robj)
```
typedef struct redisObject {
    unsigned type:4;       // STRING, LIST, SET, ZSET, HASH, STREAM
    unsigned encoding:4;   // INT, EMBSTR, RAW, HT, SKIPLIST, etc.
    unsigned lru:24;       // LRU/LFU time
    int refcount;          // shared object reference count
    void *ptr;             // pointer to actual data
} robj;
```
- Size: 16 bytes per object
- Reference counting: shared small integers, string dedup

### String Encoding
- **INT**: Values representable as 64-bit signed integer (8 bytes)
- **EMBSTR**: Strings ≤ 44 bytes (embedded with robj in one allocation)
- **RAW**: Strings > 44 bytes (separate allocation)

### Hash Encoding
- **ziplist**: Small hashes (< 512 entries, values < 64 bytes)
- **hashtable**: Large hashes

### List Encoding
- **quicklist**: Linked list of ziplist nodes (since 3.2)
- Memory efficient, supports head/tail operations

### Sorted Set Encoding
- **ziplist**: Small sorted sets (< 128 entries)
- **skiplist + dict**: Large sorted sets (O(log N) operations)

## Network Protocol (RESP3)

```
Request:   *3\r\n$3\r\nSET\r\n$3\r\nkey\r\n$5\r\nvalue\r\n
Response:  +OK\r\n
```
- Simple String: `+OK\r\n`
- Error: `-ERR message\r\n`
- Integer: `:1\r\n`
- Bulk String: `$5\r\nhello\r\n`
- Array: `*2\r\n$3\r\nfoo\r\n$3\r\nbar\r\n`
- Null: `$-1\r\n`
- Push (RESP3): `>...` for pub/sub or client pushes

## Cluster Internals

### Hash Slot Mapping
```
CRC16(key) mod 16384 → slot → cluster node
```
- Gossip protocol: nodes exchange state via PING/PONG
- Cluster bus: separate TCP port (base + 10000) for inter-node communication
- Configuration epoch: versioned cluster configuration

### MOVED Redirect
```
GET key → -MOVED <slot> <node-ip>:<port>
```
Client rehashes and directs request to correct node

## Sentinel Internals

- **Sentinel nodes**: Monitor master/replica health via PING
- **Quorum**: Minimum Sentinels agreeing a master is down
- **Election**: Majority-based leader election for failover
- **Configuration propagation**: Rewrites `redis.conf` on failover
- **SDOWN vs ODOWN**: Subjectively down (one Sentinel) vs Objectively down (quorum)
