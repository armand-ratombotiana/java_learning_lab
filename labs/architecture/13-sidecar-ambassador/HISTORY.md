# History: Sidecar and Ambassador Patterns

## Origins
The Sidecar pattern originated from the proxy pattern used in network architecture. It became prominent with the rise of service mesh technologies.

## Evolution

### 2010-2014
Netflix developed Prana as a sidecar for their microservices. The pattern was used to decouple infrastructure concerns from application code.

### 2015
Lyft released Envoy, a high-performance proxy designed for service mesh architectures. Envoy popularized the sidecar pattern.

### 2017
Istio launched as the first comprehensive service mesh platform using Envoy sidecars. Linkerd followed as a lighter-weight alternative.

### 2020s
Sidecar and ambassador patterns become standard for service mesh. Cloud providers offer managed service mesh solutions.

## Key Milestones
- 2014: Netflix Prana sidecar
- 2015: Envoy proxy released by Lyft
- 2017: Istio service mesh announced
- 2018: Linkerd 2.0 rewritten in Rust
- 2019: AWS App Mesh launched
- 2021: Istio graduated in CNCF
