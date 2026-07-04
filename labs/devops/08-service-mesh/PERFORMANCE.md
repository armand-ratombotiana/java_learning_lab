# Service Mesh Performance

## Sidecar Performance Overhead
- **Latency**: 1-5ms added per hop (Envoy processing).
- **CPU**: 50-200m per sidecar (varies with request rate and filter complexity).
- **Memory**: 50-200MB per sidecar (Envoy configuration, connections, statistics).
- **Network**: Small overhead from TLS encryption/decryption.

## Optimization Tips
- **Resource limits**: Set CPU/memory limits on istio-proxy container.
- **Concurrency**: Envoy worker thread count (`concurrency: 2`).
- **Access log filtering**: Log only errors to reduce I/O.
- **TLS acceleration**: AES-NI hardware acceleration for mTLS.
- **Protocol selection**: Use HTTP/2 or gRPC for better performance.

## Istio Performance Tuning
- **Discovery polling**: Reduce `PILOT_DISCOVERY_REFRESH_INTERVAL` for larger clusters.
- **Egress filtering**: Disable egress traffic capture if not needed (`meshConfig.outboundTrafficPolicy.mode: REGISTRY_ONLY`).
- **Mixer deprecation**: Since Istio 1.5, Mixer is deprecated; use in-proxy telemetry.
- **Ambient Mesh**: Sidecar-less mode reduces resource overhead (alpha/beta in 1.20+).

## Benchmarking
- Istio can handle 10,000+ requests/second per sidecar on modern hardware.
- Linkerd 2.x generally has lower latency and resource overhead than Istio.
- Cilium (eBPF-based) can achieve near-zero overhead for some operations.
