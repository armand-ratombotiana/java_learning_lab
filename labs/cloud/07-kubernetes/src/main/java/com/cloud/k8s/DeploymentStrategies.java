package com.cloud.k8s;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class DeploymentStrategies {

    public enum StrategyType { ROLLING_UPDATE, BLUE_GREEN, CANARY, RECREATE }

    public static class Deployment {
        public final String name;
        public final String image;
        public final int replicas;
        public final StrategyType strategy;
        public final List<PodLifecycle.Pod> pods = new CopyOnWriteArrayList<>();
        public volatile int availableReplicas;
        private final AtomicLong podCounter = new AtomicLong(1);

        public Deployment(String name, String image, int replicas, StrategyType strategy) {
            this.name = name;
            this.image = image;
            this.replicas = replicas;
            this.strategy = strategy;
        }

        public void deploy() {
            System.out.println("\nDeploying " + name + " using " + strategy + " strategy");
            switch (strategy) {
                case RECREATE -> recreateDeploy();
                case ROLLING_UPDATE -> rollingUpdate();
                case BLUE_GREEN -> blueGreenDeploy();
                case CANARY -> canaryDeploy();
            }
        }

        private void recreateDeploy() {
            pods.clear();
            for (int i = 0; i < replicas; i++) {
                PodLifecycle.Pod pod = new PodLifecycle.Pod(
                    name + "-" + podCounter.getAndIncrement(), "default", "node-1");
                pod.addContainer("app", image);
                pod.start();
                pods.add(pod);
            }
            availableReplicas = replicas;
            System.out.println("  All old pods terminated, new pods created");
        }

        private void rollingUpdate() {
            for (int i = 0; i < replicas; i++) {
                PodLifecycle.Pod pod = new PodLifecycle.Pod(
                    name + "-" + podCounter.getAndIncrement(), "default", "node-1");
                pod.addContainer("app", image);
                pod.start();
                pods.add(pod);
                availableReplicas = pods.size();
                System.out.println("  Rolling: pod " + (i + 1) + "/" + replicas + " started");
                try { Thread.sleep(100); } catch (InterruptedException e) {}
            }
        }

        private void blueGreenDeploy() {
            System.out.println("  Blue (current): " + pods.size() + " pods");
            List<PodLifecycle.Pod> greenPods = new ArrayList<>();
            for (int i = 0; i < replicas; i++) {
                PodLifecycle.Pod pod = new PodLifecycle.Pod(
                    name + "-green-" + podCounter.getAndIncrement(), "default", "node-1");
                pod.addContainer("app", image);
                pod.start();
                greenPods.add(pod);
            }
            pods.clear();
            pods.addAll(greenPods);
            availableReplicas = replicas;
            System.out.println("  Green environment promoted to active");
        }

        private void canaryDeploy() {
            int canaryCount = Math.max(1, replicas / 10);
            System.out.println("  Canary: deploying " + canaryCount + " pod(s) with new version");
            for (int i = 0; i < canaryCount; i++) {
                PodLifecycle.Pod pod = new PodLifecycle.Pod(
                    name + "-canary-" + podCounter.getAndIncrement(), "default", "node-1");
                pod.addContainer("app", image);
                pod.start();
                pods.add(pod);
            }
            availableReplicas = pods.size();
        }

        public void printStatus() {
            System.out.println("\n" + name + " status: " + availableReplicas + "/" + replicas + " available");
            pods.forEach(p -> System.out.println("  " + p));
        }
    }

    public static void main(String[] args) {
        Deployment rolling = new Deployment("web-app", "nginx:1.25", 3, StrategyType.ROLLING_UPDATE);
        rolling.deploy();
        rolling.printStatus();

        Deployment blueGreen = new Deployment("api-gateway", "envoy:latest", 2, StrategyType.BLUE_GREEN);
        blueGreen.deploy();
        blueGreen.printStatus();

        Deployment canary = new Deployment("recommendation", "ml-model:v2", 10, StrategyType.CANARY);
        canary.deploy();
        canary.printStatus();
    }
}
