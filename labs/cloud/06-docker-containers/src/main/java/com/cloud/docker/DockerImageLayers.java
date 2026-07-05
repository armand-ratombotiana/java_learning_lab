package com.cloud.docker;

import java.util.*;
import java.util.concurrent.*;

public class DockerImageLayers {

    public static class Layer {
        public final String id;
        public final String command;
        public final long sizeBytes;
        public final String digest;

        public Layer(String id, String command, long sizeBytes) {
            this.id = id;
            this.command = command;
            this.sizeBytes = sizeBytes;
            this.digest = "sha256:" + UUID.randomUUID().toString().replace("-", "").substring(0, 32);
        }

        @Override
        public String toString() {
            return String.format("Layer %s: '%s' (%d KB) %s",
                id.substring(0, 12), command, sizeBytes / 1024, digest.substring(0, 19));
        }
    }

    public static class DockerImage {
        private final String name;
        private final String tag;
        private final List<Layer> layers = new ArrayList<>();
        private long totalSize;

        public DockerImage(String name, String tag) {
            this.name = name;
            this.tag = tag;
        }

        public DockerImage addLayer(String command, long sizeBytes) {
            String layerId = "sha256:" + UUID.randomUUID().toString().replace("-", "").substring(0, 32);
            layers.add(new Layer(layerId, command, sizeBytes));
            totalSize += sizeBytes;
            return this;
        }

        public long getTotalSize() { return totalSize; }

        public void printLayers() {
            System.out.println("\n" + name + ":" + tag + " (" + layers.size() + " layers, "
                + String.format("%.2f", totalSize / (1024.0 * 1024.0)) + " MB)");
            for (int i = 0; i < layers.size(); i++) {
                System.out.println("  " + (i == 0 ? "BASE " : "    ") + layers.get(i));
            }
        }

        public boolean hasLayer(String layerId) {
            return layers.stream().anyMatch(l -> l.id.equals(layerId));
        }
    }

    public static class DockerRegistry {
        private final Map<String, DockerImage> images = new ConcurrentHashMap<>();
        private final Map<String, Layer> layerCache = new ConcurrentHashMap<>();

        public void push(DockerImage image) {
            images.put(image.name + ":" + image.tag, image);
            for (Layer layer : image.layers) {
                layerCache.putIfAbsent(layer.digest, layer);
            }
            System.out.println("Pushed " + image.name + ":" + image.tag);
        }

        public DockerImage pull(String name, String tag) {
            return images.get(name + ":" + tag);
        }

        public int getCachedLayerCount() { return layerCache.size(); }
    }

    public static void main(String[] args) {
        System.out.println("=== Docker Image Layers ===");
        DockerImage appImage = new DockerImage("my-app", "latest")
            .addLayer("FROM ubuntu:22.04", 72_000_000)
            .addLayer("RUN apt-get update && apt-get install -y python3", 180_000_000)
            .addLayer("COPY app.py /app/", 1_500)
            .addLayer("RUN pip install flask", 45_000_000)
            .addLayer("EXPOSE 8080", 0)
            .addLayer("CMD [\"python3\", \"/app/app.py\"]", 0);

        appImage.printLayers();

        DockerImage pythonImage = new DockerImage("python", "3.11-slim")
            .addLayer("FROM debian:bullseye-slim", 45_000_000)
            .addLayer("RUN apt-get install -y python3", 90_000_000);

        DockerRegistry registry = new DockerRegistry();
        registry.push(appImage);
        registry.push(pythonImage);

        System.out.println("\nCached layers in registry: " + registry.getCachedLayerCount());
    }
}
