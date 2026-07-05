package com.cloud.k8s;

import java.util.*;
import java.util.concurrent.*;

public class PodLifecycle {

    public enum PodPhase { PENDING, RUNNING, SUCCEEDED, FAILED, UNKNOWN }

    public static class ContainerStatus {
        public final String name;
        public final String image;
        public volatile boolean ready;
        public volatile int restartCount;

        public ContainerStatus(String name, String image) {
            this.name = name;
            this.image = image;
            this.ready = false;
        }
    }

    public static class Pod {
        public final String name;
        public final String namespace;
        public final String nodeName;
        public final List<ContainerStatus> containers = new ArrayList<>();
        public PodPhase phase;
        public final Map<String, String> labels = new HashMap<>();
        public final long creationTimestamp;

        public Pod(String name, String namespace, String nodeName) {
            this.name = name;
            this.namespace = namespace;
            this.nodeName = nodeName;
            this.phase = PodPhase.PENDING;
            this.creationTimestamp = System.currentTimeMillis();
        }

        public Pod addContainer(String name, String image) {
            containers.add(new ContainerStatus(name, image));
            return this;
        }

        public Pod addLabel(String key, String value) {
            labels.put(key, value);
            return this;
        }

        public void start() {
            phase = PodPhase.RUNNING;
            containers.forEach(c -> c.ready = true);
            System.out.println("Pod " + name + " -> RUNNING on " + nodeName);
        }

        public void fail() {
            phase = PodPhase.FAILED;
            System.out.println("Pod " + name + " -> FAILED");
        }

        public boolean isReady() {
            return phase == PodPhase.RUNNING && containers.stream().allMatch(c -> c.ready);
        }

        @Override
        public String toString() {
            return name + " (" + namespace + ") " + phase + " on " + nodeName
                + " containers=" + containers.size();
        }
    }

    public static class Kubelet {
        private final String nodeName;
        private final Map<String, Pod> pods = new ConcurrentHashMap<>();

        public Kubelet(String nodeName) { this.nodeName = nodeName; }

        public Pod createPod(String name, String namespace) {
            Pod pod = new Pod(name, namespace, nodeName);
            pods.put(name, pod);
            return pod;
        }

        public Pod getPod(String name) { return pods.get(name); }

        public List<Pod> getPods() { return new ArrayList<>(pods.values()); }
    }

    public static void main(String[] args) {
        Kubelet kubelet = new Kubelet("worker-node-1");

        System.out.println("=== Pod Lifecycle ===");
        Pod webPod = kubelet.createPod("web-app-7f8b9", "default");
        webPod.addContainer("nginx", "nginx:1.25")
              .addContainer("sidecar", "fluentd:v1.16")
              .addLabel("app", "web")
              .addLabel("tier", "frontend");

        System.out.println("Created: " + webPod);
        webPod.start();
        System.out.println("Ready: " + webPod.isReady());

        Pod failedPod = kubelet.createPod("crash-loop-5s", "default");
        failedPod.addContainer("app", "bad-image:v1");
        failedPod.fail();

        System.out.println("\nAll pods on " + kubelet.nodeName + ":");
        kubelet.getPods().forEach(p -> System.out.println("  " + p));
    }
}
