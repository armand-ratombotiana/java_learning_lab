# DNS and Load Balancing - Why It Exists

## The Problem

The internet needs:
1. **Human-readable names**: People can't remember 93.184.216.34
2. **Scalability**: Single servers can't handle all traffic
3. **Reliability**: Systems must survive server failures
4. **Geographic distribution**: Users worldwide need fast access

## DNS Benefits
- Maps domain names to IP addresses (directory service)
- Supports multiple IPs per name (round-robin DNS)
- Hierarchical and distributed - no single point of failure
- Caching at multiple levels reduces lookup latency

## Load Balancing Benefits
- Distributes traffic across multiple servers
- Health monitoring and automatic failover
- SSL termination (offloads encryption)
- Session persistence (sticky sessions)
