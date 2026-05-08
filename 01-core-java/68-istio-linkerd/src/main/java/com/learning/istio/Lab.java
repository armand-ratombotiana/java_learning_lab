package com.learning.istio;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class Lab {

    record Pod(String name, String version, boolean healthy) {}
    record TrafficSplit(String destination, int weight) {}

    static class VirtualService {
        private final String name;
        private final List<TrafficSplit> splits = new ArrayList<>();
        private final List<String> retryOn = new ArrayList<>();
        private int timeoutMs = 30000;

        VirtualService(String name) { this.name = name; }

        VirtualService addSplit(String dest, int weight) {
            splits.add(new TrafficSplit(dest, weight));
            return this;
        }

        VirtualService setTimeout(int ms) { this.timeoutMs = ms; return this; }
        VirtualService addRetry(String condition) { retryOn.add(condition); return this; }

        String route() {
            return name + " -> " + splits.stream()
                .map(s -> s.destination() + "(" + s.weight() + "%)")
                .reduce((a, b) -> a + ", " + b).orElse("");
        }
    }

    static class DestinationRule {
        private final String name;
        private final String lbAlgorithm;

        DestinationRule(String name, String lb) { this.name = name; this.lbAlgorithm = lb; }
        public String toString() { return name + " (load balancer: " + lbAlgorithm + ")"; }
    }

    static class ServiceMesh {
        private final List<VirtualService> virtualServices = new ArrayList<>();
        private final List<DestinationRule> destinationRules = new ArrayList<>();

        void addVirtualService(VirtualService vs) { virtualServices.add(vs); }
        void addDestinationRule(DestinationRule dr) { destinationRules.add(dr); }

        void report() {
            System.out.println("  Virtual services:");
            virtualServices.forEach(vs -> System.out.println("    " + vs.route()));
            System.out.println("  Destination rules:");
            destinationRules.forEach(dr -> System.out.println("    " + dr));
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Istio & Linkerd Lab ===\n");

        serviceMeshOverview();
        trafficManagement();
        securityMtls();
        observability();
        resiliency();
    }

    static void serviceMeshOverview() {
        System.out.println("--- Service Mesh Overview ---");
        var mesh = new ServiceMesh();

        var vs = new VirtualService("reviews")
            .addSplit("v1", 90)
            .addSplit("v2", 10)
            .setTimeout(2000)
            .addRetry("connect-failure");
        mesh.addVirtualService(vs);

        var dr = new DestinationRule("reviews-dr", "LEAST_REQUEST");
        mesh.addDestinationRule(dr);

        mesh.report();

        System.out.println("""
  Service mesh = dedicated infrastructure layer for:
  - Traffic management (routing, splitting, mirroring)
  - Security (mTLS, RBAC)
  - Observability (metrics, tracing, access logs)
  - Resiliency (retries, timeouts, circuit breaking)

  Istio: Envoy sidecar + Istiod control plane
  Linkerd: Rust-based "linkerd-proxy" + control plane
    """);
    }

    static void trafficManagement() {
        System.out.println("\n--- Traffic Management ---");
        System.out.println("""
  Istio VirtualService:
    apiVersion: networking.istio.io/v1beta1
    kind: VirtualService
    metadata:
      name: reviews
    spec:
      hosts:
      - reviews
      http:
      - match:
        - headers:
            end-user:
              exact: "test-user"
        route:
        - destination:
            host: reviews
            subset: v2
      - route:
        - destination:
            host: reviews
            subset: v1
          weight: 80
        - destination:
            host: reviews
            subset: v3
          weight: 20
      timeout: 3s
      retries:
        attempts: 3
        perTryTimeout: 1s

  Canary release: 80% v1, 20% v3
  Header-based routing: test-user -> v2
  Traffic mirroring: copy traffic to v3 (shadow)
    """);
    }

    static void securityMtls() {
        System.out.println("\n--- Security (mTLS) ---");
        System.out.println("""
  Mutual TLS (mTLS) in Istio:

  PeerAuthentication:
    apiVersion: security.istio.io/v1beta1
    kind: PeerAuthentication
    metadata:
      name: default
      namespace: istio-system
    spec:
      mtls:
        mode: STRICT  # REQUIRE mutual TLS

  Modes:
  DISABLE: no mTLS
  PERMISSIVE: accept both mTLS and plaintext (migration)
  STRICT: only mTLS allowed

  How it works:
  1. Envoy sidecars get certificates from Istiod (CA)
  2. All traffic between sidecars is encrypted (mTLS)
  3. Application code sees plain HTTP (zero change)
  4. Spire/SPIFFE for workload identity

  Linkerd: similar mTLS with auto-injection
  Both handle certificate rotation automatically
    """);
    }

    static void observability() {
        System.out.println("\n--- Observability in Service Mesh ---");
        System.out.println("""
  Istio telemetry (Envoy + Mixer/Metrics):

  Metrics (Prometheus):
    istio_requests_total{source, destination, response_code, ...}
    istio_request_duration_milliseconds_bucket{p50, p95, p99}
    istio_tcp_sent_bytes_total

  Grafana dashboards:
    - Service dashboard: success rate, latency, traffic
    - Workload dashboard: per-pod metrics
    - Control plane dashboard: Istiod health

  Distributed Tracing:
    - Envoy propagates trace headers (x-request-id, b3, w3c)
    - Jaeger/Zipkin integration (no app changes!)
    - Trace sampling: adaptive, head-based, tail-based

  Access logs:
    - Envoy access logs (JSON format)
    - Log to stdout -> Fluentd -> Elasticsearch -> Kibana

  Linkerd: similar metrics + tap (live request inspection)
    """);
    }

    static void resiliency() {
        System.out.println("\n--- Resiliency Features ---");
        System.out.println("""
  Timeouts:
    http:
    - timeout: 3s  # per-request timeout

  Retries:
    retries:
      attempts: 3
      perTryTimeout: 1s
      retryOn: connect-failure,refused-stream,503

  Circuit Breaking (DestinationRule):
    trafficPolicy:
      connectionPool:
        tcp:
          maxConnections: 100
        http:
          http1MaxPendingRequests: 10
      outlierDetection:
        consecutive5xxErrors: 5
        interval: 30s
        baseEjectionTime: 60s

  Circuit breaker states:
  - Normal: requests pass through
  - Eject: after 5 errors, pod removed from pool for 60s
  - Recovery: after ejection time, probe with requests
  - Remove: if recovery fails -> ejected again

  Istio vs Linkerd resiliency:
  Both support timeouts, retries, circuit breaking
  Linkerd: simpler API (ServiceProfile)
  Istio: more granular controls (per-route, per-weight)
    """);
    }
}
