# Common Service Mesh Mistakes

1. **Enabling mTLS STRICT without readiness** — services that don't have sidecars can't communicate.
2. **No sidecar injection on all relevant namespaces** — mesh has blind spots.
3. **Forgetting Gateway CRD** — mesh internal traffic works, but ingress doesn't.
4. **Overly permissive AuthorizationPolicy** — no actual security; default-deny required.
5. **Complex routing rules** — hard to debug; start simple and iterate.
6. **No resource limits on sidecar** — Envoy can consume significant CPU/memory.
7. **Blindly trusting mTLS** — mTLS authenticates services, not users.
8. **Missing VirtualService for every service** — Envoy drops traffic without matching route.
9. **Not monitoring mesh health** — mesh problems manifest as application issues.
10. **Upgrading Istio without testing** — breaking changes between versions.
11. **Ignoring sidecar startup delay** — application starts before sidecar; use `holdApplicationUntilProxyStarts`.
12. **Too many Envoy filters** — performance degradation from complex filter chains.
