# Mental Models for Docker

## 1. Shipping Container Analogy
- **Image** = Shipping container blueprint (standardized, reusable)
- **Container** = Loaded shipping container on a truck (running instance)
- **Registry** = Shipping port / warehouse hub
- **Dockerfile** = Packing list / assembly instructions
- **Layer** = Each packing step (add items, seal, label)

## 2. Layered Cake Model
Images are like layered cakes: common base layers (OS, runtime) are shared, and custom layers (app code, config) sit on top. Pulling an image downloads only new layers.

## 3. Immutable Infrastructure
Treat containers as disposable. Never SSH into a running container. Instead, rebuild with changes. This ensures consistency and reproducibility.

## 4. Process vs Machine
Think of containers as isolated processes, not lightweight VMs. Containers areephemeral - they should be created and destroyed easily.
