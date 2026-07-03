# Module 43: Service Mesh - Mini Project

**Project Name**: Zero-Trust Microservices with Sidecars  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Understand the mechanics of a Service Mesh (Data Plane vs Control Plane) by manually deploying a microservices architecture to a Kubernetes cluster using Istio to handle mTLS encryption and traffic routing (Canary deployments).

## 📝 Requirements

### Core Features

1. **The Application Setup**:
   - Write two simple Spring Boot REST APIs: `FrontendService` and `BackendService`.
   - The `FrontendService` makes a plain HTTP call to `http://backend-service:8080/data`.
   - **Crucially**: Neither application should contain any SSL/HTTPS certificates, circuit breakers, or retry logic in the Java code.

2. **Kubernetes Deployment**:
   - Write standard `Deployment` and `Service` YAML files for both applications.
   - Install a Service Mesh (e.g., Istio or Linkerd) on your local cluster (Minikube / Docker Desktop).
   - Enable **automatic sidecar injection** for the namespace (e.g., `kubectl label namespace default istio-injection=enabled`).
   - Deploy the applications. Use `kubectl get pods` to verify that each Pod has **2/2** containers running (your Java app + the Envoy sidecar proxy).

3. **Mutual TLS (mTLS)**:
   - Create an Istio `PeerAuthentication` policy to enforce `STRICT` mTLS across the entire namespace.
   - Prove that the `FrontendService` can still talk to the `BackendService`. 
   - *Concept*: Even though your Java code is sending unencrypted HTTP traffic, the Envoy sidecar intercepts it, encrypts it via mTLS, sends it to the Backend's sidecar, which decrypts it and passes it as HTTP to the Backend Java app.

4. **Traffic Routing (Canary Deployment)**:
   - Create a `BackendService-V2` deployment (returns different data).
   - Create an Istio `VirtualService` and `DestinationRule`.
   - Configure the routing so that exactly **90%** of traffic goes to V1, and **10%** of traffic goes to V2.
   - Create a script to hit the `FrontendService` 100 times and print the results to verify the 90/10 split.

---

## 💡 Solution Blueprint

1. **Enable Sidecar Injection**:
   ```bash
   kubectl label namespace default istio-injection=enabled
   ```

2. **Strict mTLS Policy (`mtls.yaml`)**:
   ```yaml
   apiVersion: security.istio.io/v1beta1
   kind: PeerAuthentication
   metadata:
     name: default
     namespace: default
   spec:
     mtls:
       mode: STRICT
   ```

3. **Canary Routing (`routing.yaml`)**:
   ```yaml
   apiVersion: networking.istio.io/v1alpha3
   kind: VirtualService
   metadata:
     name: backend-route
   spec:
     hosts:
     - backend-service
     http:
     - route:
       - destination:
           host: backend-service
           subset: v1
         weight: 90
       - destination:
           host: backend-service
           subset: v2
         weight: 10

   ---
   apiVersion: networking.istio.io/v1alpha3
   kind: DestinationRule
   metadata:
     name: backend-destination
   spec:
     host: backend-service
     subsets:
     - name: v1
       labels:
         version: v1
     - name: v2
       labels:
         version: v2
   ```