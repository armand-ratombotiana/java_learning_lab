# Math Foundation for Docker

Docker relies on limited mathematical concepts:

## Union-Find / UnionFS Overlay Mathematics
Overlay filesystem merging uses a union operation where lower layers (read-only) are overlaid by upper layers (writable). Resolution follows a simple priority: upper layer wins on conflict.

## Resource Allocation (Cgroups)
- **CPU**: Proportional share scheduling — each cgroup receives CPU time proportional to its `shares` weight.
- **Memory**: Hard/soft limits defined in bytes; OOM killer threshold calculations.
- **Block I/O**: Token bucket algorithm for throttling IOPS/throughput.

## Image Layer Caching
Cache hit determination uses a DAG (Directed Acyclic Graph) of layer hashes. Each layer's cache key = (parent layer hash + instruction + build context hash).

## Network Address Translation
- **Port mapping**: NAT table entries using iptables rules.
- **Subnet calculations**: Docker assigns private IPs (typically 172.17.0.0/16) using CIDR.

No advanced mathematics beyond basic computer science and networking fundamentals.
