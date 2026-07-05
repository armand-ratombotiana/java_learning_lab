package com.cloud.k8s;

import java.util.*;
import java.util.concurrent.*;

public class ServiceDiscovery {

    public static class K8sService {
        public final String name;
        public final String namespace;
        public final String clusterIp;
        public final Map<String, Integer> ports = new HashMap<>();
        public final Map<String, String> selector = new HashMap<>();
        public final List<PodLifecycle.Pod> endpoints = new CopyOnWriteArrayList<>();

        public K8sService(String name, String namespace, String clusterIp) {
            this.name = name;
            this.namespace = namespace;
            this.clusterIp = clusterIp;
        }

        public K8sService addPort(String name, int port) {
            ports.put(name, port);
            return this;
        }

        public K8sService addSelector(String key, String value) {
            selector.put(key, value);
            return this;
        }

        public void addEndpoint(PodLifecycle.Pod pod) {
            if (pod.isReady() && matchesSelector(pod)) {
                endpoints.add(pod);
                System.out.println("Endpoint added: " + pod.name + " -> " + name);
            }
        }

        public void removeEndpoint(PodLifecycle.Pod pod) {
            endpoints.remove(pod);
        }

        private boolean matchesSelector(PodLifecycle.Pod pod) {
            for (Map.Entry<String, String> entry : selector.entrySet()) {
                if (!entry.getValue().equals(pod.labels.get(entry.getKey()))) {
                    return false;
                }
            }
            return true;
        }

        public List<PodLifecycle.Pod> getEndpoints() {
            return endpoints.stream().filter(PodLifecycle.Pod::isReady).toList();
        }

        public String getDnsName() {
            return name + "." + namespace + ".svc.cluster.local";
        }

        @Override
        public String toString() {
            return "Service " + name + " ClusterIP=" + clusterIp + " DNS=" + getDnsName()
                + " endpoints=" + endpoints.size();
        }
    }

    public static class K8sClient {
        public String callService(K8sService svc, String path) {
            List<PodLifecycle.Pod> endpoints = svc.getEndpoints();
            if (endpoints.isEmpty()) {
                return "ERROR: No available endpoints for " + svc.name;
            }
            PodLifecycle.Pod target = endpoints.get(0);
            return svc.name + ":" + svc.ports.values().iterator().next() + path
                + " -> " + target.name;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== K8s Service Discovery ===");
        K8sService svc = new K8sService("web-service", "default", "10.100.0.1");
        svc.addPort("http", 80);
        svc.addSelector("app", "web");

        PodLifecycle.Pod pod1 = new PodLifecycle.Pod("web-pod-xyz", "default", "node-1");
        pod1.addContainer("nginx", "nginx:1.25");
        pod1.addLabel("app", "web");
        pod1.start();

        svc.addEndpoint(pod1);
        System.out.println("  " + svc);
        System.out.println("  DNS: " + svc.getDnsName());

        K8sClient client = new K8sClient();
        System.out.println("  Call: " + client.callService(svc, "/api/health"));
    }
}
