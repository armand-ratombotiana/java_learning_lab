package com.cloud.docker;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class ContainerLifecycle {

    public enum ContainerState { CREATED, RUNNING, PAUSED, STOPPED, REMOVED }

    public static class Container {
        public final String containerId;
        public final String image;
        public final String name;
        public ContainerState state;
        public final long createdAt;
        public final Map<String, String> ports = new HashMap<>();
        public final Map<String, String> env = new HashMap<>();

        public Container(String containerId, String image, String name) {
            this.containerId = containerId;
            this.image = image;
            this.name = name;
            this.state = ContainerState.CREATED;
            this.createdAt = System.currentTimeMillis();
        }

        public void start() {
            if (state == ContainerState.CREATED || state == ContainerState.STOPPED) {
                state = ContainerState.RUNNING;
                System.out.println("Started: " + name + " (" + containerId.substring(0, 12) + ")");
            }
        }

        public void stop() {
            if (state == ContainerState.RUNNING) {
                state = ContainerState.STOPPED;
                System.out.println("Stopped: " + name);
            }
        }

        public void pause() {
            if (state == ContainerState.RUNNING) {
                state = ContainerState.PAUSED;
            }
        }

        public void unpause() {
            if (state == ContainerState.PAUSED) {
                state = ContainerState.RUNNING;
            }
        }

        public void remove() {
            state = ContainerState.REMOVED;
            System.out.println("Removed: " + name);
        }

        public boolean isRunning() { return state == ContainerState.RUNNING; }

        @Override
        public String toString() {
            return containerId.substring(0, 12) + " " + name + " " + image + " " + state;
        }
    }

    public static class DockerDaemon {
        private final Map<String, Container> containers = new ConcurrentHashMap<>();
        private final AtomicLong counter = new AtomicLong(1);

        public Container createContainer(String image, String name) {
            String id = UUID.randomUUID().toString();
            Container container = new Container(id, image, name);
            containers.put(id, container);
            System.out.println("Created: " + name + " from " + image);
            return container;
        }

        public Container getContainer(String id) { return containers.get(id); }

        public List<Container> listContainers(ContainerState filter) {
            return containers.values().stream()
                .filter(c -> filter == null || c.state == filter)
                .toList();
        }

        public void cleanupStopped() {
            containers.values().removeIf(c -> c.state == ContainerState.STOPPED
                || c.state == ContainerState.REMOVED);
        }
    }

    public static void main(String[] args) {
        DockerDaemon docker = new DockerDaemon();

        System.out.println("=== Container Lifecycle ===");
        Container web = docker.createContainer("nginx:latest", "web-server");
        web.start();
        web.pause();
        web.unpause();
        web.stop();
        web.remove();

        Container db = docker.createContainer("postgres:15", "database");
        db.start();

        System.out.println("\nRunning containers:");
        docker.listContainers(ContainerState.RUNNING).forEach(c -> System.out.println("  " + c));
    }
}
