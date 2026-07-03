# Module 60: Machine Learning in Java - Edge Cases & Pitfalls

---

## Pitfall 1: JVM Garbage Collection vs Native Memory

### ❌ Wrong
Loading massive datasets using ND4J or TensorFlow Java APIs and assuming the standard Java Garbage Collector will quickly clean up unused Tensors and matrices.

### ✅ Correct
Libraries like ND4J and TensorFlow store their massive multidimensional arrays in **Off-Heap Native Memory** (using C++ under the hood) to bypass JVM limits and communicate directly with GPUs. The Java GC cannot effectively track or aggressively clean this native memory. You must explicitly call `.close()` or use try-with-resources on Tensors and NDArrays to prevent native memory leaks that crash the entire container.

---

## Pitfall 2: Rewriting Python Models in Java

### ❌ Wrong
Taking a massive, highly complex PyTorch neural network written by your data science team and attempting to rewrite the entire training and inference logic line-by-line in Java using DL4J so it fits your tech stack.

### ✅ Correct
Python has an undisputed monopoly on ML research, tooling, and ecosystem. Attempting to rewrite Python ML code in Java is a waste of time and introduces massive bugs. 
Instead, export the Python model to a standardized format (ONNX, PMML, TensorFlow SavedModel) and load the pre-trained binary directly into the Java application purely for **Inference** (making predictions), leaving the **Training** phase entirely in Python.

---

## Pitfall 3: Network Latency in Microservice ML Inference

### ❌ Wrong
Building an ultra-low-latency high-frequency trading application in Java, but making a REST API call over the network to a Python Flask server for every single ML prediction. The network latency (e.g., 50ms per call) destroys the performance requirements of the Java app.

### ✅ Correct
If microsecond latency is required, the ML model must run in the exact same memory space as the application. Use Java libraries (like PMML evaluators or Deep Java Library - DJL) to load the Python-trained model directly into the JVM heap. This allows Java to execute the prediction in sub-milliseconds without network overhead.