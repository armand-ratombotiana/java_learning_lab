package com.learning.containers;

import java.util.*;
import java.util.concurrent.*;

public class Lab {

    static class Container {
        private final String id;
        private final String image;
        private final int port;
        private volatile boolean running = false;

        Container(String id, String image, int port) {
            this.id = id; this.image = image; this.port = port;
        }

        void start() {
            running = true;
            System.out.println("  [Container " + id + "] Started " + image + " on port " + port);
        }

        void stop() {
            running = false;
            System.out.println("  [Container " + id + "] Stopped");
        }

        boolean isRunning() { return running; }
        String getId() { return id; }
    }

    static class DockerRuntime {
        private final Map<String, Container> containers = new ConcurrentHashMap<>();

        Container run(String image, int port) {
            var id = UUID.randomUUID().toString().substring(0, 8);
            var container = new Container(id, image, port);
            containers.put(id, container);
            container.start();
            return container;
        }

        void stop(String id) {
            var c = containers.get(id);
            if (c != null) c.stop();
        }

        List<Container> list() { return List.copyOf(containers.values()); }
    }

    static class KubernetesPod {
        private final String name;
        private final List<Container> containers = new ArrayList<>();

        KubernetesPod(String name) { this.name = name; }

        void addContainer(Container c) { containers.add(c); }
        void startAll() { containers.forEach(Container::start); }
        void stopAll() { containers.forEach(Container::stop); }
        String getName() { return name; }
        int containerCount() { return containers.size(); }
    }

    public static void main(String[] args) {
        System.out.println("=== Containers Lab ===\n");

        containerBasics();
        dockerfileLayers();
        kubernetesConcepts();
        resourceLimits();
        networking();
        multiStageBuilds();
    }

    static void containerBasics() {
        System.out.println("--- Container Basics ---");
        var docker = new DockerRuntime();

        var web = docker.run("nginx:1.25", 8080);
        var db = docker.run("postgres:16", 5432);
        var cache = docker.run("redis:7", 6379);

        System.out.println("\n  Running containers:");
        for (var c : docker.list()) {
            System.out.println("    " + c.getId() + " " + c.isRunning());
        }

        docker.stop(web.getId());
        System.out.println("\n  Stopped web container.");

        System.out.println("""
  Container = isolated process (cgroups + namespaces)
  Image = read-only template (layered filesystem)
  Registry = image storage (Docker Hub, ECR, GCR)
    """);
    }

    static void dockerfileLayers() {
        System.out.println("\n--- Dockerfile Layers ---");
        System.out.println("""
  FROM eclipse-temurin:21-jre-alpine    # base layer
  WORKDIR /app                           # metadata layer
  COPY target/app.jar app.jar            # application layer
  EXPOSE 8080                            # metadata layer
  ENTRYPOINT ["java", "-jar", "app.jar"] # metadata layer

  Layer caching:
  - Each instruction = one layer
  - ORDER MATTERS: stable instructions first
  - COPY only changes = invalidate subsequent layers
  - Multi-stage builds reduce final image size

  Best practices:
  - Use distroless or alpine base images
  - Combine RUN apt-get update && apt-get install
  - Use .dockerignore for build context
  - Scan with trivy / snyk for vulnerabilities
    """);
    }

    static void kubernetesConcepts() {
        System.out.println("\n--- Kubernetes Concepts ---");
        var pod = new KubernetesPod("web-server-pod");
        pod.addContainer(new Container("c1", "nginx", 80));
        pod.addContainer(new Container("c2", "sidecar-proxy", 9090));

        pod.startAll();
        System.out.println("  Pod '" + pod.getName() + "' with " + pod.containerCount() + " containers");

        System.out.println("""
  Pod: smallest deployable unit (1+ containers)
  Deployment: declarative updates (ReplicaSet + rolling update)
  Service: stable network endpoint (ClusterIP, NodePort, LoadBalancer)
  ConfigMap / Secret: configuration injection
  Ingress: HTTP/HTTPS routing to Services
  PersistentVolume: storage abstraction

  kubectl commands:
  kubectl get pods -w
  kubectl logs -f deployment/my-app
  kubectl port-forward pod/my-pod 8080:80
    """);
        pod.stopAll();
    }

    static void resourceLimits() {
        System.out.println("\n--- Resource Limits ---");
        System.out.println("""
  Docker:
    --memory=512m (hard limit)
    --cpus=1.5   (CPU shares relative to host)

  Kubernetes:
    requests: minimum guaranteed resources
    limits:   maximum allowed (CPU throttled, memory OOM killed)

  Example pod spec:
    resources:
      requests:
        memory: "256Mi"
        cpu: "250m"
      limits:
        memory: "512Mi"
        cpu: "500m"

  Java in containers:
    -XX:ActiveProcessorCount=2 for CPU count
    -XX:MaxRAMPercentage=75.0 for heap
    UseContainerSupport (default since JDK 10)
    """);
    }

    static void networking() {
        System.out.println("\n--- Container Networking ---");
        System.out.println("""
  Docker networking modes:
  bridge:   default, isolated per host
  host:     shares host network stack
  overlay:  multi-host (Swarm / Kubernetes)

  CNI plugins (Kubernetes):
  Calico: network policy + BGP routing
  Flannel: overlay (VXLAN)
  Cilium: eBPF-based, high performance

  Service mesh sidecar:
  Envoy intercepts all pod traffic
  mTLS, traffic routing, observability
    """);
    }

    static void multiStageBuilds() {
        System.out.println("--- Multi-Stage Builds ---");
        System.out.println("""
  # Build stage
  FROM maven:3.9 AS builder
  COPY . /app
  RUN mvn package -DskipTests

  # Runtime stage
  FROM eclipse-temurin:21-jre-alpine
  COPY --from=builder /app/target/app.jar .
  ENTRYPOINT ["java", "-jar", "app.jar"]

  Result: from ~400MB (Maven + JDK) to ~50MB (JRE only)
  Size comparison:
    ubuntu:22.04     ~77MB
    alpine:3.19      ~7MB
    distroless-java  ~120MB (glibc + JRE)
    """);
    }
}
