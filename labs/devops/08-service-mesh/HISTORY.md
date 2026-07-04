# History of Service Mesh

- **2016 (May)**: William Morgan and Oliver Gould coin "service mesh" term; Linkerd v0.1 released (first service mesh, built on Twitter Finagle).
- **2016 (Sep)**: Envoy proxy open-sourced by Lyft (high-performance C++ proxy).
- **2017 (May)**: Istio v0.1 announced by Google, IBM, and Lyft (uses Envoy as sidecar).
- **2017 (Sep)**: Linkerd joins CNCF as incubation project.
- **2018 (Feb)**: Istio v0.8 — mTLS, traffic management, telemetry.
- **2018 (Jul)**: Istio v1.0 — production-ready; CNCF acceptance.
- **2019 (Mar)**: Linkerd 2.0 (Rust-based "linkerd-proxy" replaces Finagle).
- **2020**: Istio v1.5 — simplified control plane (single binary: istiod); Linkerd 2.8 — mTLS GA.
- **2021-2022**: Istio adds Ambient Mesh (sidecar-less mode); Linkerd graduates CNCF.
- **2023-2024**: Istio v1.20+ — Gateway API GA; Ambient Mesh beta; Cilium service mesh (eBPF).

The service mesh evolved from simple proxy-based routing to full-featured infrastructure layer with sidecar and sidecar-less modes.
